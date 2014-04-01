---
date: 2014-01-18T00:00:00Z
title: Adventures in Clojure Land
published: true
categories: [Clojure]
type: article
external: false
---
Over the last few evenings I've built a very simple application using an exclusivley Clojure stack.  This is my attempt to reason about that experience.  It's not supposed to be a tutorial/how-to and should be taken as "here a some things I looked at and found interesting/useful, look at them too".  If you want to see the resultant code you can get it via the [depression-test repo](github.com/kouphax/depression-test).


I've dabbled in [Clojure](http://clojure.org/) a few times in the past but nothing even slightly serious and usually going no further than some REPL tinkering so most of this was still very new to me.  In fact my previous SSD died a horrible death so I even started on a fresh machine.  I decided to approach the experience by intentionally over-engineering the solution, I included libraries that I really didn't need for such a small scale solution.  I did this because there is an associated cost of entry for each new library you use and the increased surface area I was exposing myself to meant I'd feel pain that basic "Hello World" examples don't really give you.

## Tech Stack

In terms of the tech stack I ended up using I had,

- [Leiningen](http://leiningen.org/) - the build to of the Clojure world.  Also included a few plugins,
  - [lein-ring](https://github.com/weavejester/lein-ring) - start/stop your ring based apps
  - [lein-cljsbuild](https://github.com/emezeske/lein-cljsbuild) - compile your ClojureScript into JavaScript
- [Ring](https://github.com/ring-clojure/ring) - Web server/middelware library (like `connect` in the node.js world)
- [Compojure](https://github.com/weavejester/compojure) - Routing library that works well with Ring
- [Hiccup](https://github.com/weavejester/hiccup) - DSL for building HTML pages/snippets in Clojure
- [Liberator](http://clojure-liberator.github.io/liberator/) - library for building RESTful web apps.
- [ClojureScript](https://github.com/clojure/clojurescript) - I decided to build the front end interactions with ClojureScript and a few libraries,
  - [react.js](http://facebook.github.io/react/) - shiny new JS framework for building UIs
  - [cloact](http://holmsand.github.io/cloact/) - a clojurescript-ified library on top of react.js
  - [cljs-ajax](https://github.com/yogthos/cljs-ajax) - a neater ajax library

### Leiningen & Plugins

Lein appears to be the defacto build tool for Clojure projects.  Yes, you can use Maven or Gradle or whatever but there are certain benefits to using Lein.

1. Its written with Clojure projects in mind so fits better
2. The build file `project.clj` is just a Clojure data structure and incredibly easy to grok when in the Clojure mindset
3. It feels, in my limited exposure, really simple.  Dependency resolution, adding new plugins, using custom templates or generators to structure new apps - all of it "just works".  Admitedly I didn't do an awful lot with it but having done plenty of work with `SBT` and Maven it was incredibly simple but no less powerful to use.

[lein-cljsbuild](https://github.com/emezeske/lein-cljsbuild) feel essential when working with ClojureScript.  It seems to be the only tools that transpiles ClojureScript in JavaScript.

[lein-ring](https://github.com/weavejester/lein-ring) is nice for getting up and running quickly but having just took a stab at deploying my app to [Heroku](http://www.heroku.com) it appears to be a bit troublesome down the line (though most likely down to my inexperience).

### Ring, Compojure & Hiccup

I'd not be suprised to find that this trio underpinned most Clojure based web apps.  Seems like a brilliant combination that works really well together.  While my app doesn't use ring directly it's no less essential and as your app grows you'll invariably find yourself adding ring middleware directly to customise your app.  Compojure is excellent for succintly expressing the routes in your app.

```clojure
(defroutes app-routes
  (GET "/" [] views/index)
  (ANY "/questions" [] resources/questions)
  (route/resources "/")
  (route/not-found "Not Found"))
```

  In this small sample alone I'm using the `defroutes` macro, `GET` & `ANY` verbs, the `route/resources` handler for serving static assets and the `route/not-found` handler for dealing with 404's in a custom way.  Even with my novice mind this was incredibly easy to construct and easy to read.  These routes are then turned into just another bit of ring middleware and added to the stack,

```clojure
(def app
  (handler/site app-routes))
```

The `app` here referes back to the configuration in my `project.clj` that the `lein-ring` plugin uses to mount our application in the web server

```clojure
:ring { :handler depression-test.core/app }
```
The `handler/site` part simply wraps our routes in more ring middleware that are common to web sites (e.g. middleware for extracting HTTP post form parameters etc.)

Finally hiccup gives use a Cloure DSL for building HTML blocks and pages.  The `index` view is built with it,

```clojure
(def index (html5
             [:head
              [:title "depression-test"]]
             [:body
              [:div#stage]
              (include-js "/app/app.js")]))
```

While this page is essentially static an culd easily be represented as an embedded resource you can see that this sort of expressive DSL allows us to dynamically construct views because, as is incredibly common in Clojure, it's just data.

### Liberator

The inclusion of Liberator here was overkill but it was extracted from a slightly larger project I started at the same time as this.  Liberator sits on to top of ring and focuses on allowing you to build RESTful APIs using a ridiculously easy to reason about structure.  Usually when I work with frameworks or libraries that attempt to model RESTful APIs in a generic way theyend up being incredibly complex.  Liberator is quite the opposite,

```clojure
(defresource questions
  ;; return all the people in the application in json format
  :available-media-types ["application/json"]
  :handle-ok (fn [_] data/questions))
```

`defresource` is a macro used to generate a resource that can be used in a route.  In fact my `(ANY "/questions" [] resources/questions)` route is basically taking any request to the `/questions` URI and letting liberator deal with it.  Our resource definition simply specifies that this endpoint can deal with JSON media types and will coerce our `questions` list into a JSON response.  It does this thanks to a bunch of defaults and an incredibly predicatable decision tree. If you want to accept different verbs, handle authentication etc. the approach to doing it is straight forward.  Liberator have went to the effort of mapping the decision required to the turn our above resource into a JSON reponse via the [mind boggilingly awesome decision graph](http://clojure-liberator.github.io/liberator/assets/img/decision-graph.svg) which will show you what hooks and behaviours you can work with to make the magic happen in your RESTful API.  I feel Liberator is one of those important technologies.  I've built a lot of APIs in recent times and the sheer "obviousness" of Liberator is the polar opposite of the unpredictable magic in many other frameworks.

### React.js

Everyone is talking about react.js - the new hotness from Facebook.  I'll not dwell to much on it.  I recommend to you check out [this video](http://www.youtube.com/watch?v=x7cQ3mrcKaY) around the design decisions of react and how its avoiding the usual terribleness of two-way data binding and state management.  While my resultant app doesn't use react directly I did originally write the UI in Javascript/React before re-writing it in ClojureScript/cloact.

### Cloact

One of many ClojureScript wrappers around React.  This one appeared the most straight forward.  It uses it's own `atom` implementation to manage state and change propogation

```clojure
(ns depression-test.core
  (:require [cloact.core :as cloact :refer [atom]]))

(def selections (atom [1 2 3 4]))
...
(swap! selections (fn [_] [9 7 6 3]))
```

By declaring `selections` as a cloact `atom` then later using the atom api (`swap!`) to change its value any cloact component that uses the selections atom will get re-rendered throught the usual react method.  The `render-component` method of cloact kicks everything into action,

```clojure
(cloact/render-component
  [:div.questions
    [:h1 "Could you be depressed?"]
    [:p "This test will help you to assess whether you could be suffering from depression."]
    [:p "Answer the questions based on how you've been feeling during the last two weeks."]
    [score]]
  (.-body js/document))))
```

### cljs-ajax

  Nice little shim over the normal ClojureScript ajax stuff.  Not much else to say,

```clojure
(defn load-questions [callback]
  (GET "/questions" {:response-format :json
                     :keywords? true
                     :handler #(callback %)}))
```

## IDE/Editor

I ended up writing most of the code in Sublime Text 2.  I've never quite got my Vim setup right on this new machine and I've no clue about Emacs and ST2 did the job.  I installed a few plugins to make working with a LISP a bit nicer.

### [SublimeREPL](https://github.com/wuub/SublimeREPL)

Interactive REPL for Sublime that supports, among many other things, Clojure and ClojureScript.  The REPL is where you end up doing most of the work in Clojure eventually pasting the same code into a file when you are done.  SublimeREPL supports leiningen so you have access to your dependencies and app code when working in the REPL.

### [Paredit](https://github.com/odyssomay/paredit)

Paredit mode comes from Emacs and makes working with LISPs parenthesis a bit easier by making sure you can unbalance your braces.

### [lispindent](https://github.com/odyssomay/sublime-lispindent)

Provides more lispified logic for dealing with indenting.

All of these are installed via the Sublime Text package manager.

## Thoughts & Comments

### Clojure

Clojure is a LISP and immediatley feels different to every other language I commonly use, but regardless, when you actually work with it there is a certain feeling of "naturalness" about the whole thing.  The homoiconic nature, where "everything is just data" means that solving problems is suprisingly intuative and this applies to using libraries and frameworks.  Most things are just lists and maps and you can inspect these things easily in the REPL.  The amount of experimental spikes that work more or less first time is incredibly high and the final code is usually very terse.  I'm by no means an expert, not even "average" at Clojure but it's a joy to use thanks to its well thought out foundation.

Another thing I've noticed is that Clojure libs dont suffer from the same API volatility that you see in likes of Node and Ruby.  For example some popular libraries haven't had commits in 7 months - and it's not because they have stagnated or been neglected - it's because they're done.  IMHO thats the sign of an incredibly mature, design concious community.

That said - some documentation leaves a lot to be desired if it even exists.  I did often find myself searching to try and find what I wanted to know and usually resulted in just reading the code.

### ClojureScript

I enjoyed working with ClojureScript but I didn't quite get my workflow right.  ClojureScript takes a while to compile but this can be automated.  Problem was I couldn't get this automation to work with `lein-ring` and so I found my self waiting ~20 seconds to try each change - it's cool though, I write Scala regularly.  That said ClojureScript intrigues me more than any of the other JS transpilers around - I very quickly stopped seeing the point of CoffeeScript for example.  The fact it works well with node.js (ClojureScript all the way down) is equally interesting.

## Onward

Clojure, which was/is, very new to me helped me put together a simple application rather quickly as to how it stands up against larger projects remains to be seen.  I'm going to try and see if I can improve my ClojureScript workflow as its a rather compelling technology but right now its a bit slow to use (e.g. I got better results using [meis](https://github.com/swannodette/mies) and `lein cljsbuild auto` but that skipped the server stuff).  I also need to re-work this app to work on heroku without sacrificing the ease of `lein ring server`.

Its worth caveating that the [code](github.com/kouphax/depression-test) should in no way be assumed to be best practise, in fact its probably awful.