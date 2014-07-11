---
date: 2014-07-11T00:00:00Z
title: "Working with Play apps in the Console"
published: true
categories: [Scala]
type: article
external: false
---

Having spent a fair amount of time working with Clojure in the last 6 months I've become rather accustomed to the REPL driven development approach.  If you're unfamiliar with this approach I recommend reading/viewing Jay Fields [post on the topic](http://blog.jayfields.com/2014/01/repl-driven-development.html).  Long story short RDD involves writing a small bits of code and executing them immediately.  This gives you immediate feedback without having to write tests or start an entire system each time.  You can of course make your REPL based experimentation more concrete by saving your work off as a set of tests once your code has settled down a bit.

Like Clojure, and unlike Java, Scala supports the concept of REPL driven development and I've been using it more often since my exposure to its charms in Clojure.  Of course Scala syntax is a lot meatier (and I do mean a lot) than Clojure syntax so there is a bit more friction but every now and again its faster when you just want to poke around a concept or idea.  

A common example when working with [Play](http://playframework.com) is querying the application database with Anorm.  The one problem here is that to use Anorm and the Play provided extensions you need to have a running Play instance that it can extract configuration values from.  

For example if we tried to get all the users from a database we could write something like this via the console (`sbt console` or `activator console` in the root of our project)

```scala
scala> import play.api.db._
scala> import anorm._
scala> DB.withConnection { implicit connection => SQL"SELECT * FROM users".map(mapUser).list() }
```

But this would result in an error.

```scala
java.lang.RuntimeException: There is no started application
  at scala.sys.package$.error(package.scala:27)
  at play.api.Play$$anonfun$current$1.apply(Play.scala:71)
  at play.api.Play$$anonfun$current$1.apply(Play.scala:71)
  at scala.Option.getOrElse(Option.scala:120)
  at play.api.Play$.current(Play.scala:71)
  ... 43 elided
```

Bummer.  Thankfully Play provides a quick way to stand up an running instance which can be used in the console

```scala
scala> new play.core.StaticApplication(new java.io.File("."))
[info] play - database [default] connected at jdbc:h2:file:./data/db
[info] play - Application started (Prod)
res18: play.core.StaticApplication = play.core.StaticApplication@22a730f2
```

Now if we try our query again

```scala
scala> DB.withConnection { implicit connection => SQL"SELECT * FROM users".map(mapUser).list() }
res19: Seq[scala.collection.immutable.Map[String,String]] = List(Map(username -> kouphax, password -> JHGW$FSWF$KJJK$3231))
 ```

To be super clean we can even stop the running instance

```scala
scala> play.api.Play.stop()
```

`StaticApplication` isn't perfect.  For instance it will start your application in production mode but for my needs this has been acceptable.  If you do want to provide greater control over how your application is started then look no further than the `StaticApplication` source.

```scala
class StaticApplication(applicationPath: File) extends ApplicationProvider {

  val application = new DefaultApplication(applicationPath, this.getClass.getClassLoader, None, Mode.Prod)

  Play.start(application)

  def get = Success(application)
  def path = applicationPath
}
```

Using this knowledge you could create your own `application` instance using `DefaultApplication` and ask Play to start it using `Play.start(application)`. 





