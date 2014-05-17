---
date: 2014-05-17T00:00:00Z
title: "A Brief Overview of BaseX"
published: true
categories: [Database]
type: article
external: false
---

There are technologies that I have used to solve problems that just make me happy while using them.  They make solving the problem so easy that I don't have to burn time getting them to work or try bending them to my needs.

[BaseX](http://basex.org), an [XML database](http://en.wikipedia.org/w/index.php?title=XML_database), is one of those.  Yes it's an XML database, yes XML is, like, totally lame and all but trust me, it exists and there are contexts it works.

So I wanted to write about BaseX because while it may not be shiny it makes up for it in pure practicality.  One day you may need to interrogate XML and maybe BaseX will save your sanity as it did mine. Also XQuery is rather wonderful.

## BaseX

[BaseX](http://basex.org) is an XML database written in Java.  That may sound like "the worst" but let me tell you why it is rather the opposite.

So yeah it's cool because it's __NoSQL__.  It stores XML documents so it's doubly cool because it's a __document store__.  Not only that but it doesn't care about DTDs and XML schemas so, as long as they are well formed, it can store any XML document. That makes it triply cool because it's __schemaless__.  Heavens, it's basically MongoDB for XML.  Well not quite<a name="_1"></a>[<sup>1</sup>](#1) - the design goals are not the same.

So what is BaseX useful for?  Well one of the areas that it shines is Open Data.  There is a lot of publicly available data locked up in static XML documents.

One example is [IATI](http://iatiregistry.org/) - the International Aid Transparency Initiative.  IATI is many things,

- an organisation that promotes transparency of foreign aid spend,
- an XML standard for publishing data consistently, and,
- a registry to access this open data

Many organisations and governments that spend money on foreign aid publish data via IATI. Data is usually published as many XML files and according to the registry there are 241 organisations publishing 3,343 datasets (XML files). Thats gigabytes of data.  Not exactly __big data__ but still a respectable volume of XML.

To top it all off the registry only provides a centralised lookup for datasources, pointers to XML files, via metadata.  It __does not__ extract the data from within the XML files.  So the data itself is still locked up in static XML files spread across the globe.

None of this is specific to IATI.  Like it or not there is a lot of data exactly like this.

Now imagine you want to ask a question of this type of distributed XML data and you've discovered the sweet spot for BaseX.  You feed a BaseX database a batch of XML files, it indexes the contents and allows you to query over the top of it as if it was a single root document.

## Querying

If there is one thing that can be said about XML - it's really well thought out. Yeah yeah there are aspects of the ecosystem that could be described as __over thought__ but as a data format its not half bad.  Thanks to this "thoughtout-ed-ness" BaseX doesn't need to invent any new concepts to support querying. XML has already got its own query language, well actually two, XPath and XQuery.  Both are mature, well documented, well understood query languages.

### XPath

XPath is like the CSS selectors of the XML world.  A bit more verbose than CSS selector syntax but essentially the same idea.  In fact there are some features, such as selecting nodes based on conditions of their children, that CSS selectors could do with.  Take this rather contrived example,

```xml
/repository/projects/project[@title="My Blog"]/releases/release[@type="beta"]/release-date/text()
```

This would return the `release-date` of all the `beta` releases of the project titled `My Blog`.  I did say contrived didn't I?  So XPath is useful for __selecting__ things but not manipulating or transforming anything.

### XQuery

XQuery is useful when you want to do more than just select nodes in a document.  In fact XQuery is kind of an amazing language.  Seriously.  Its based on a SQL-like expression set called [FLWOR](http://en.wikipedia.org/wiki/FLWOR) (FOR, LET, WHERE, ORDER BY and RETURN) that when mixed with XPath you have a nice functional language.  Everything is a sequence making it conceptually rather LISPy yet the syntax is much like ML.

Here is a slightly more complex, real life authenti-genuine, example featuring list manipulation, dynamic XPath execution, external parameter passing and other typical XQuery stuff,

```xquery
declare variable $start external;
declare variable $limit external;
declare variable $reporting-org external;

let $filter := "$db//iati-activity"
let $filter := concat($filter,
  switch($reporting-org)
    case "" return ""
    default return "[reporting-org/@ref=$reporting-org]")

let $db         := db:open('iati')
let $bindings   := map { '$db' := $db,  '$reporting-org' := $reporting-org }
let $activities := for $activity in xquery:eval($filter, $bindings)
           order by $activity/iati-identifier
           return $activity
return <result>
  <ok>true</ok>
  <iati-activities generated-datetime="{ current-dateTime() }">
    <query>
      <total-count>{ count($activities) }</total-count>
      <start>{ $start }</start>
      <limit>{ $limit }</limit>
    </query>
    { subsequence($activities, $start, $limit) }
  </iati-activities>
</result>
```

### Clients

I've already said BaseX is written, unsuprisingly, in Java but as with most data stores use is not limited to the JVM.  BaseX runs as a server that you send commands to over TCP and, as you'd expect, there are many [clients available](http://docs.basex.org/wiki/Clients) to abstract this lower level communication.

I've used the Java, Scala and Ruby client and they are all pretty much the same.  I'm also currently in the process of building a Clojure based one that wraps the standard Java client.  Again the API is very similar to the other clients.

BaseX doesn't have an extensive feature set and so the clients aren't exactly complicated.

The clients follow a similar pattern of having two __modes__.  A __standard mode__ that provides functionality  for connecting to a server and sending arbitrary commands and a __query mode__ that allows you to create a query on the server, bind external values to the query and stream the results to the client.  You can think of queries in __query mode__ as similar to SQLs PreparedStatements.

In reality __query mode__ is just a bit of rich sugar to make the common task of querying a bit less bloaty and there isn't anything in it you can't achieve in __standard mode__ - its all just sending a basic set of commands over a socket.  The power lives within the server.

Some clients (Java, Scala, C# and Clojure for example) also support the notion of events.  With these clients you can ask sessions to `watch` and `unwatch` a special database type of `event`.  The typical signature for `watch` looks like this,

```java
session.watch("my-event", new EventNotifier() {
  @Override
  public void notify(final String value) {
    System.out.println(value);
  }
});
```

On the database side you need to have created an event called `my-event` for this to succeed

```sql
CREATE EVENT my-event
```

Then at some point you can fire an event and pass values to it.

```sql
db:event("my-event", "1 to 4")
```

Each event can be passed an XQuery expression that will be evaluated and passed to any registered handlers on the client side.  This can be one or more opened session as long as they have `watch`ed the named event.

### Modules

BaseX can be extended via modules too.  These can add extra features to the database itself as well as enhance  XQuery itself.  There are a [heap of ready-rolled](http://docs.basex.org/wiki/Module_Library) modules for all your needs.  Just to call a few out,

- [JSON Module](http://docs.basex.org/wiki/JSON_Module) - extend XQuery to support reading and writing JSON.  Yep JSON in and out of an XML database. Bananas.
- [Higher Order Functions Module](http://docs.basex.org/wiki/Higher-Order_Functions_Module) - get some monads in your XQuery
- [Full Text Module](http://docs.basex.org/wiki/Full-Text_Module) - Every database needs full text search capabilities these days it seems and BaseX need not be an exception.
- [Unit Testing Module](http://docs.basex.org/wiki/Unit_Module) - Testing is dead but don't let that hold you back.
- [Geospatial Module](http://docs.basex.org/wiki/Geo_Module) - another must have trend in databases these days.

There are a heap of them allowing you to customise BaseX to your needs.

### Other Notable Things

#### Admin GUI

One of the things I miss with a lot of the NoSQL databases is a good Admin app.  These apps let me poke around the data in my database, model solution and generally visualise all those bits and bytes.

SQL Server got this bit right.  SQL Server Management Studio is a really handy tool for understanding your data and its container a bit better.

BaseX has you covered.  It has a rather nice client that lets you write XPath/XQuery, manage documents and visualise not only the data but also profiles queries and shows how they can be optimised.

![BaseX Admin GUI](/images/blog/basex.png)

Its written in some Java based desktop technology so the UI itself is a bit basic but given what you can do with it, thats more than forgiven.

#### Binary Data

BaseX lets to read and write binary data as well.  Being binary you can't do much more than read and write it but it means that you can, if needs be, store binary blobs alongside your XML.

#### Scaling

At one point I was using BaseX to query across a large amount of XML data.  Not a __huge__ amount but large, about 13GB worth.  The query I was running, while it wasn't basic wasn't exactly complicated either but it did result in a large result set and the query times where not suitable for real time applications (single queries running 3-6 seconds).  In my case that was fine.  I suspect there are very few cases where you'd genuinely need real time responses from a large blob of XML but still.

BaseX doesn't provide any sort of answer for distributed environments, you'd need to provide some sort of basic sharding strategy yourself if that is what you are after.  Perhaps if someone gets drunk enough they'll write some sort of Hadoop integration for offline querying of massive XML datasets. Perhaps.

### In a Nutshell

BaseX fulfils a need.  Not a need many people will have but a need nonetheless.  It does it well and it does it without inventing a new query language or offering 1000 shiny features.  It uses XQuery which is, quite honestly, a wonderful language to work with.  It's functional, minimal, expressive and powerful.  The Admin GUI offers some nice insight into your databases and the clients offered across most common and current languages make working with BaseX straightforward.

While I am on this subject BaseX is of course not the only XML database.  You've got others like eXist, MarkLogic and Sedna.  I've never ventured into other systems (save for a brief look at eXist) so can't comment on them but in terms of feature set and licensing BaseX [looks](http://en.wikipedia.org/w/index.php?title=XML_database&section=5#Language_features) to be one of the better implementations.

<hr/>
<sup><a name="1"></a>[1](#_1): I'd imagine, though, if a Java Certified Enterprise Architect decided to write a document store from scratch they'd probably end up with an XML database har! har!.
