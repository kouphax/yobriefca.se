---
title:      "Activate on Heroku"
published:  true
layout:     post
date:       2013-12-11
categories: [Scala]
slug:       "How to use the Activate Framework on Heroku"
---

[Activate](http://activate-framework.org/) is an object persistence framework for Scala.  Unlike an ORM that openly tries to map DB concepts to objects [Activate](http://activate-framework.org/) goes up a further level making the persistence aspect almost completely transparent.  You work with objects and in doing so persistence happens as a side effect.  For example given a class that extends the `Entity` trait,

```scala
class Person(var name: String, var age: Int)
```

We can create __and persist__ this instance like so

```scala
transactional {
  new Person("James", 33)
}
```

Updating it will result in the persisted state of the instance being mutated as well.  [Activate](http://activate-framework.org/) uses the concurrency model of [Software Transactional Memory](http://en.wikipedia.org/wiki/Software_transactional_memory) at its core.  As there is often some discrepencies between the conceptual model the database domain and objects [Activate](http://activate-framework.org/) uses `Dialects` that handle the translation between the two (for example persisting an object with a `List` of `Int`s in a relational database that doesn't support array columns).  In fact persistence is abstracted suitably that it becomes completely pluggable - use a document store like Mongo or a relational store like Postgres.  [Activate](http://activate-framework.org/) attempts to make this irrelevant.

I want to caveat all of this with the fact I'm not an [Activate](http://activate-framework.org/) expert.  I've succesfully used it in small volume projects and it's allowed me to be pretty productive.  Which leads me to onto using it with [Heroku](http://www.heroku.com/).

## Adapting Activate for Heroku

I have used Activate on 2 projects hosted on Heroku recently and I wanted to share a snippet that I wrote for the first one that I've found useful in the second.  When you use Activate you need to create a `Context` that configures Activate for your application domain.  One thing you need to set is the `storage` mechanism.  On Heroku I used the provided Postgres instance which is made available to your application via the `DATABASE_URL` environment variable.  So in JDBC based applications you need to coerce this into a JDBC connection string.  To do this in Activate I used the code below which may come in useful for people wanting to do the same thing.

```scala
object PersistenceContext extends ActivateContext with Config {
  val storage = new PooledJdbcRelationalStorage {
        val uri        = new URI(System.getenv("DATABASE_URL"))
        val jdbcDriver = "org.postgresql.Driver"
        val user       = uri.getUserInfo.split(":").head
        val password   = uri.getUserInfo.split(":").last
        val url        = s"jdbc:postgresql://${uri.getHost()}:${uri.getPort()}${uri.getPath()}?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory"
        val dialect    = postgresqlDialect
      }
}
```
