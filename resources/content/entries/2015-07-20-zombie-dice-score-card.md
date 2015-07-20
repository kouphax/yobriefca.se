---
date: 2015-07-20T00:00:00Z
title: "Zombie Dice Score Card with Reagent"
published: true
categories: [Clojure, ClojureScript]
type: article
external: false
---

Over the weekend I built a [Zombie Dice Score Card](/zombie-dice).  I built it because I keep forgetting to get pen and paper for keeping score but  mostly because I wanted to build something simple in ClojureScript again.

![Zombie Dice Score Card](/images/blog/zombie-dice.png)

The app is pretty basic, no server side interaction, no local storage interaction and only a few basic actions/features.  You simply,

1. Add players
2. Play zombie dice as usual, rewarding points by clicking the necessary brain on the screen.
3. When someone reaches 13 brains you'll be told to finish the round (all remaining players yet to go on this round have a final chance at stealing the win)
4. Hitting 'Finish Game' will reward the person with the highest number of brains a gold brain and the next game begins.
5. At any time you can reset the current game scores or clear the entire board.

For the curious the stack used is as follows,

- ClojureScript
- [Tenzing](https://github.com/martinklepsch/tenzing) - Leiningen template for creating Boot powered client side only ClojureScript applications.
- [Boot](http://boot-clj.com/) - The new build system on the Clojure(Script) block offering a more programatic approach to build systems (i.e more like Rake than Maven)
- [Reagent](http://reagent-project.github.io/) - A simple ClojureScript wrapper around [React](http://facebook.github.io/react/).
- [cljs-uuid-utils](https://github.com/lbradstreet/cljs-uuid-utils) - ClojureScript library for generating and working with UUID's

## Tenzing

This is a nice little template for prototyping with ClojureScript or in situations where you can integrate with external services for your server side needs.  It's a conditional template that lets you add technologies that you think you'll need.  For example I generated the Zombie Dice Score Card project with,

```
lein new tenzing zombie-dice +sass +reagent +divshot
```

This template gives me an app with `SASS`, `Reagent` and [Divshot](https://divshot.com/) configuration and all the necessary build pipeline stuff set up to compile CLJS and SASS when files change.  I didn't use [Divshot](https://divshot.com/) this time around but it's nice to know the option was there.

On another note I did find it a bit weird using Leiningen to generate a Boot application but I guess most Clojure(Script) developers are going to have Leiningen available and Boot doesn't provide templating support.

## Boot

I didn't need to dive into Boot too much for this tiny project but I do like what I see.  The way tasks are defined in Clojure and composed into pipelines using standard Clojure idioms makes creating build pipelines really rather nice.

```clojure
(deftask build []
  (comp (speak)
        (cljs)
        (sass :output-dir "css")))

(deftask run []
  (comp (serve)
        (watch)
        (cljs-repl)
        (reload)
        (build)))
```

You see `run` composes a pipeline of other tasks for serving the application, watching for filesystem changes, connecting to a ClojureScript REPL and reloading the application.  It then composes another defined task for building the ClojureScript and SASS.

## Reagent

Aside from a name change Reagent (formerly Cloact) hasn't really change that much since I talked about in [January 2014](https://yobriefca.se/blog/2014/01/18/adventures-in-clojure-land/).  Sure they've added some new features and refined the API a bit but its approach is still the same.  It provides  simple approach to using React in a ClojureScript app offering us hiccup style syntax for declaring components along side atoms for state management.

I've never really looked into the other React wrappers so I can't speak to whether Reagent is the best but I can say it's certainly been a pleasure to use on small scale apps.

## Plans

I think I'm going to write this in Elm just to see what all the fuss is about. Maybe add some local storage for saving historical games and players. Truth be told I'll probably never touch it again but a man can dream.

## Footnote

Have you ever played [Zombie Dice](http://www.sjgames.com/dice/zombiedice/)?  If not you should give it a go it's a simple, quick dice based game that you can explain in about 3 minutes and have a full game done in about 15 or 20 minutes.  The aim is simple, try and collect/eat 13 brains by rolling dice over a series of rounds.  Once someone hits 13 brains everyone finishes that round and the person that has the highest score wins that game.  Typically you play best of 3 or 5 games.