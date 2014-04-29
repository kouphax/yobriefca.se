---
date: 2014-04-29T00:00:00Z
title: "Managing Environment Variables in Clojure"
published: true
categories: [Clojure]
type: article
external: false
---

> If you've never read the [Twelve Factor App](http://12factor.net/) methodology I strongly recommend you do, it's a great guide born from real life experience of building many, many web applications.

One of the rules in the [Twelve Factor App](http://12factor.net/) methodology is that you ["Store config in the environment"](http://12factor.net/config) or, more specifically, store config that is likely to change __across environments__ in the environment.  It makes sense as it ties the target environment and its necessary configuration together instead of having to juggle `if ENV=test|dev` style flags around your code base or swap out different config files per build.

One other positive side effect of this approach is that secure configuration doesn't accidentally get checked into source.  Every one needs database credentials or client secrets but no-one want to share them with the world.

Supporting this approach in Clojure is fairly simple and there are a few ways to do it.

## System/getenv

Clojure sits on the JVM and the JVM has the `System` namespace which provides utilities and classes for accessing the running systems features.  [`System.getenv`](http://docs.oracle.com/javase/7/docs/api/java/lang/System.html#getenv()) and [`System.getenv(String)`](http://docs.oracle.com/javase/7/docs/api/java/lang/System.html#getenv(java.lang.String)) give us access to environment variables from Clojure.

```clojure
(System/getenv "DATABASE_URL")
;=> http://user:pass@dburl/mydb
```

We can also provide a fallback for environment variables that don't exist

```clojure
(or (System/getenv "NOEXIST_DATABASE_URL") 
    "http://localhost/mydb")
;=> http://localhost/mydb
```

In this case `NOEXIST_DATABASE_URL` isn't set as an environment variable so we fallback to our hardcoded version.  

This isn't uncommon and often its done to support development environments.  For example [Heroku](http://heroku.com) (where the original 12 factor app methodology arose) add environment variables for any addons you may activate such as `DATABASE_URL` for the Postgres DB addon.  So rather than write different handlers per environment many people would either.

1. Use the fallback URL to represent their development environment
2. Add the environment variable to their shell

Both of the approaches have problems.  Using a fallback URL can lead to problems where an environment isn't correctly configured and starts using the wrong database.  If it can write to it there will be trouble, if it can't you'll be scratching your head wondering whats going wrong for longer then necessary.  Setting a project specific environment variable in your shell can lead to pain when things get overwritten across projects and pollution if you're setting it in an automated fashion (`.bashrc`) or you'll simply forget to set it per use when doing it manually.

## weavejester/environ

A neater solution is available in the form of [`environ`](https://github.com/weavejester/environ) - a simple library for managing environment variables.  The clever thing [`environ`](https://github.com/weavejester/environ) does is merge environment variables from multiple sources into a single map.

So by adding it to our `project.clj`

```clojure
[environ "0.5.0"]
```

We can import and use it where we need,

```clojure
(require [environ.core :refer [env]])

(env :database-url)
;=> http://localhost/mydb
```

So how are the environment variables sourced?  Well environ looks in a number of areas in order,

1. `~/.lein/profiles.clj`
2. A `.lein-env` file in the project directory
3. Environment variables
4. Java system properties

It also keywordises the variable names as you can see from the example above.

Great, so why is this more useful?  The big benefit I've discovered is the `.lein-env` file in the root of my project.  This file holds a map of environment values that can be useful during development.

```clojure
{ :env { :database-url "http://localhost/mydb" 
         :client-token "QWERTY12345" } }
```

You create and populate the `.lein-env` file on your machine with development specific values and you forget about fallbacks and setting environment variables and the potential conflicts with other projects.  Then on your production and test systems you can source values from actual environment variables without having to change your strategy or use `(if (= "test" (:env config)))` style checks.

Its worth noting that `.lein-env` is already added as a match in the default `.gitignore` from `lein new ...` so wont be checked into `git` - __BUUUUUUT...__ if you do manage to check in and push your `.lein-env` file and it does happen to contain secure tokens and passwords (hashed or otherwise) you must assume those values are already compromised even if you manage to purge them from history.
