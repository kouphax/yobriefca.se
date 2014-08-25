---
date: 2014-08-26T00:00:00Z
title: "Simple stubbing with lein-cooper and lein-oneoff"
published: false
categories: [Clojure]
type: article
external: false
---

Work on [lein-cooper](https://github.com/kouphax/lein-cooper) has been pretty quiet.  It's not because it has been abandoned but because it doesn't enough for my needs.  As and when it starts getting issues or pull requests coming in I can start adapting it to fit the wider needs of others.

Recently I came across a lovely Leiningen plugin called [`lein-oneoff`](https://github.com/mtyaka/lein-oneoff). `oneoff` allows you to specify dependencies for a clojure script at the top of a clojure script (a bit like what [Grape](http://groovy.codehaus.org/Grape) does in the Groovy world I believe).  This means you don't have to spin up all the project infrastructure to run a simple script.  So instead of `lein new server` and editing the `core.clj` file to mock up a simple "Hello World" sample you could do something like this,

```clojure
(defdeps
  [[org.clojure/clojure "1.6.0"]
   [ring/ring-jetty-adapter "1.3.0"]
   [ring "1.3.1"]])

(ns server
  (:require [ring.adapter.jetty :refer [run-jetty]]))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World"})

(run-jetty handler { :port 3000 })
```

If this were saved as `server.clj` we could use `lein-oneoff` (declared in the global `~/.lein/profiles.clj`) to run it like so,

```shell
> lein oneoff server.clj
```

Notice the `defdeps` form at the top of the file, this will be used to grab dependencies and set up the appropriate classpath for running the script.

Pretty neat.

I also noticed that this would be a perfect accompaniment for a typical usage of `lein-cooper` stubbing out external services.  If you're building a system that integrates with other services (either internal or external) you introduce a bit of friction when it comes to developing and testing (at the integration level) your system.  Either you have to build and run dependent services or rely on external services that may or may not be available. A simple solution is to develop against simple stubs for these services.  This is what `coop-off` (the new power couple portmanteau I've given to the `lein-cooper`/`lein-oneoff` combination) can make easier.

Just create a `stubs` folder in your project, create standalone service stubs (like the example below that also demonstrates the use of `oneoff`s `*command-line-args*` for accessing arguments passed with the command),

```clojure
(defdeps
  [[org.clojure/clojure "1.6.0"]
   [ring/ring-jetty-adapter "1.3.0"]
   [org.clojure/data.json "0.2.5"]
   [ring "1.3.1"]])

(ns server
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [clojure.data.json :as json]))

(defn handler [request]
  { :status 200
    :headers {"Content-Type" "application/json"}
    :body (json/write-str { :success true :data [1 2 3 4] }) })

(def port
  (or (Integer. (first *command-line-args*)) 3000))

(defn start-server
  [port]
  (run-jetty handler { :port port }))

(start-server port)
```

And you can integrate the running of this and other stubs alongside using `lein-cooper`.  Just create a `Procfile` with commands to run everything,

```
web: lein ring server
data: lein oneoff stubs/data.clj 3000
auth: lein oneoff stubs/auth.clj 3002
```

Finally run the whole thing with `lein cooper`.  The neatly isolated stubs will be executed alongside your own service without the overhead of having to manage separate projects or integrate test code into your own project.
