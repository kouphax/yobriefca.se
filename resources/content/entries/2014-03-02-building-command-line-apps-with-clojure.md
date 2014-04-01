---
date: 2014-03-02T00:00:00Z
title: Building Command Line Apps with Clojure
published: true
categories: [Clojure]
type: article
external: false
---
I recently read an article by [Ryan Neufeld](http://rkn.io) around [parsing command line arguments with Clojure](http://www.rkn.io/2014/02/27/clojure-cookbook-command-line-args/). It's a good article and you should read it if the idea of building command line utilities in Clojure floats your boat.

To complement that article I've decided to take the building of the command line app one step further and support running your creation as a standalone command.  Of course its possible, as demonstrated in the article, to run the app using Leiningen directly but you typically want to be bundling your app into a self conatined package (minus any necessary runtimes - in our case the JVM) for deployment to other environments.

So lets start were Ryans article left off, a simple command line app that can be run via Leiningen. The `core.clj` looks like this,

```clojure
(ns runs.core
  (:require [clojure.tools.cli :refer [cli]])
  (:gen-class))


(defn -main [& args]
  (let [[opts args banner] (cli args
                                ["-h" "--help" "Print this help"
                                 :default false :flag true])]
    (when (:help opts)
      (println banner))))
```

And the `project.clj` looks like this

```clojure
(defproject runs "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.cli "0.2.4"]]
  :main runs.core)
```

As Ryans article mentions we can run this via `lein run` and pass our arguments in.

```bash
lein run -- -h
```

## Uberjar

First thing we want to do is take Leiningen out of the equation and bundle the app as a standalone JAR.  Unlike other JVM build tools that need to be extended with plugins such as [shade](http://maven.apache.org/plugins/maven-shade-plugin/), [onejar](http://one-jar.sourceforge.net/) and [assembly](https://github.com/sbt/sbt-assembly) Leingingen comes with `uberjar` prebundled.  To create a basic uberjar, a JAR containting your application code and all the necessary dependencies, we can call `lein uberjar`.  There are also a bunch of configuration options that go along with uberjar but what we have specified is sufficient to build our jar.  The JAR will be output to `target` dir appended with `-standalone`.  We can run this JAR using via the usual Java way

```bash
java -jar target/app-0.1.0-SNAPSHOT-standalone.jar -h
```

## lein-bin

I don't know about you but I'm lazy and I like my commands names to represent what they do (roughly) so the thought of running `java -jar blahblah.jar -h` every time I want to run the command is just "meh".  We could write a shell script to run the more verbose command but thats less than ideal (it's messy and on top of my laziness I'm a tad OCD about carting around wrapper scripts).

Enter [lein-bin](https://github.com/Raynes/lein-bin) a Leiningen plugin that utilises the fact ZIP files and therefore JARs can have stuff prepended to the front of it.  What it does is prepend shell commands to your JAR making it executable in and of itself.

So to use `lein-bin` we need to add the plugin to our projects `project.clj` or the global Leiningen profile (`~/.lein/profiles.clj`)

```clojure
{ :plugins [[lein-bin "0.3.4"]] }
```

We can also add some `lein-bin` related configuration like changing the output name (there are some other options as well, all noted in the `lein-bin` [README](https://github.com/Raynes/lein-bin/blob/master/README.markdown))

```clojure
:bin { :name "runs" })
```

So running `lein bin` now leaves us with another file in `target` called  `runs` that is executable.

```clojure
target/runs -h
```

Now we've got a command line util called `runs` (yeah a terrible name) rather than some verbose Java command or being force to use a build tool on all environments.  It's the little things.