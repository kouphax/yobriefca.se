---
date: 2014-05-30T00:00:00Z
title: "Basic ClojureScript Setup"
published: true
categories: [Clojure, ClojureScript]
type: article
external: false
---

ClojureScript is a bit of an awkward one to get into.  If Clojure isn't your natural language then you may well find the clojurarian setup and the long compilation times a bit of a frustration.  Even if you're accustomed to the REPL based development approach that fits so well with Clojure there is still a certain amount of friction to be had with ClojureScript.

Recently a strong desire to play with and better understand [`core.async`](https://github.com/clojure/core.async) in ClojureScript I decided to try and put together a lightweight workflow.  In doing so I ended up producing a Clojure library ([primrose](http://yobriefca.se/primrose/)) and a Leiningen plugin ([lein-cooper](http://yobriefca.se/lein-cooper/)) to better support this workflow.

It's probably a sure sign that you are doing something wrong when you feel you need to create a bunch of developer focused tools to support a workflow for an already established ecosystem but you gotta do, what you gotta do.

Here goes.

## Starting point

ClojureScript projects are still Clojure projects and so there are few bits and pieces that you need to wire up a basic empty project.  You've got your `project.clj`, the [`lein-cljsbuild`](https://github.com/emezeske/lein-cljsbuild) plugin for compiling ClojureScript to JavaScript and (at the very least) an `index.html` for bringing everything together.

Thankfully David Nolen has you covered.  [Mies](https://github.com/swannodette/mies) is a very basic ClojureScript project template so getting up and running is a matter of,

```bash
lein new mies <project name>
cd <project name>
```

At this point you are good to go.  To compile the ClojureScript you can run,

```bash
lein cljsbuild once
```

If you open `index.html` and view the developer console you should see some predictable output.  Alternatively if you run,

```bash
lein cljsbuild auto
```

The plugin will watch your project sources and recompile everything when changes are made.  This is significantly faster that manually compiling every time as compilation is incremental.  So instead of 10s of seconds for each compile you'll be getting sub-second compilations.

## Ajax

When you view HTML files via `file://` you won't be able to, at least by default, make ajax calls (due to security concerns such as [Same-origin policy](https://en.wikipedia.org/wiki/Same-origin_policy)).

To fix this you'll want to serve your `index.html` from a web server.  You could go with `python -m simplehttpserver` as many often do but if you want to keep everything under the same roof (Clojure/Leiningen) then I'd recommend [`lein-simpleton`](https://github.com/tailrecursion/lein-simpleton) - a Leiningen plugin for serving static files from the current directory.  If you add `lein-simpleton` to the projects `project.clj` file,

```clojure
:plugins [[lein-cljsbuild "1.0.2"]
          [lein-simpleton "1.3.0"]]
```

You can now run `lein simpleton <port>` to start serving static files (your `index.html`).  Alternatively if you have actual server side code in the same project you can replace `lein-simpleton` with [`lein-ring`](https://github.com/weavejester/lein-ring).

## Two for One

So now if you run both

1. `lein cljsbuild auto`
2. `lein simpleton <port>`

You have a dynamic(ish) environment for working and testing your ClojureScript work.

Problem is I don't like having multiple windows open for long running processes and we now have 2.  Ruby has a nice answer for this which plays very well with [Heroku](http://heroku.com) - [Foreman](https://github.com/ddollar/foreman).  Foreman takes a file (`Procfile`) that contains a list of named command line processes and runs them.  These processes are supposed to be long running (like `simpleton` and `cljsbuild auto`) and Foreman merges the output of these files into a single stream that makes development and debugging a bit easier.

Again if you want to keep this sort of thing all under one roof I've thrown together a simple plugin for ingesting a `Procfile` and doing what Foreman does - in Leiningen.

## lein-cooper

[`lein-cooper`](https://github.com/kouphax/lein-cooper) gives you that Foreman feeling.  So lets add this as a plugin.

```clojure
:plugins [[lein-cljsbuild "1.0.2"]
          [lein-simpleton "1.3.0"]
          [lein-cooper "0.0.1"]]
```

Now we can create a `Procfile` at the root of our project to run our two commands.

```
web: lein simpleton
cljs: lein cljsbuild auto
```

Now if we run `lein cooper` we will have single call to start up our dynamic(ish) environment for working with ClojureScript.  Once you are done just `CTRL-C` the terminal and everything will shutdown cleanly.

## Summing Up

So we now have a very basic project setup that will allow us to tinker with ClojureScript, make Ajax calls, edit code and cleanly automate away all the stoping and restarting and problematic debugging.  It's not quite the REPL driven style of a typical Clojure project but it's minimal enough to try a few smaller scale things.
