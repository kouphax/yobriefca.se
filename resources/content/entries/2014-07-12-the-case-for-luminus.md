---
date: 2014-07-21T00:00:00Z
title: "A Case for Luminus"
published: true
categories: [Clojure]
type: article
external: false
---

First and foremost - frameworks.  I view frameworks suspiciously and prefer to [build my systems][1] out of a core set of libraries and allow it to evolve and breathe a little without the artificial constraints of a framework.  There is a time and place for frameworks, but that time and place never seems to be here and now were my work is concerned.  This may be one of the many reasons I find Clojure so enjoyable.

[Luminus][0] is a "Clojure web framework".  It says so right on the the tagline of the [Luminus homepage][0].  This single line has meant I have hesitated diving into Luminus since I starting breaking the back of my Clojure dabbling.  I wanted to avoid it until I felt it useful or necessary to invest my time in it, a framework, after-all, needs to be understood as a whole unlike its modular library counterparts.

So my first foray into building web apps in Clojure meant building them out from scratch using a foundation of Ring, Compojure, Hiccup - the usual suspects.  Data access meant looking at a few possibilities , assessing each one and making a decision. Various authentication libraries needed thorough inspections. This, as you can expect, leads to a tremendous amount of yak-shaving and time spent satisfying curiosity.

So I had a look at [Luminus][0].  I wanted to see what decisions a framework had made and possibly steal some of the better ideas.  I used [Luminus][0] on my next project.  I didn't steal the good ideas for my own work, I didn't need to - the image I have in my head when someone mentions framework is simply not what [Luminus][0] is.  

In its most basic form a framework is a collection of 3rd party and bespoke libraries tied together with some code that abstracts the library specifics into a more cohesive whole.  [Luminus][0] doesn't really have this.  There is no bespoke modules or libraries, there is no abstraction.  [Clojars][2] has no `luminus` library.  It does have a Leiningen [template][3] that gives you a decent starting point but even that isn't even a heavily opinionated starting point and can be [customised][4] to suit your own needs.

So what is Luminus?  IMHO Luminus is what you'd end up with if you wanted to personally build a curated list of recommended libraries to build web applications in Clojure.  It's what would happen if you bothered to document that process and offer alternatives.  In fact while the curated set of libraries makes for a productive development process with reduced decision anxiety it's the documentation that launches it out of the park.  Rather than "Do it like this", the documentation offers recommendations and guides for using alternative libraries such as [migrations][5], data access, [HTML templating][6] and [authentication][7].  [Luminus][0] gives you enough to get started and generously gives you a foot up to the next level.  

At no point in time does [Luminus][0] hide anything from you, it wears its lineage as a badge of honour and won't get offended if you decide it's wrong in places.  

I can respect that.

[0]: http://www.luminusweb.net/
[1]: http://yobriefca.se/blog/2014/01/04/building-systems-libraries-and-frameworks/
[2]: https://clojars.org/search?q=luminus
[3]: https://clojars.org/luminus/lein-template
[4]: http://www.luminusweb.net/docs/profiles.md
[5]: http://www.luminusweb.net/docs/migrations.md
[6]: http://www.luminusweb.net/docs/html_templating.md
[7]: http://www.luminusweb.net/docs/security.md
