---
date: 2016-01-01T00:00:00Z
title: "On Maintenance"
published: true
categories: [Craftsmanship]
type: article
external: false
---

All software we release needs to either be maintained or killed off.  Now yes, you could argue that certain libraries, the ones that do one thing and do it very well are complete but that ignore the fact it is part of a bigger bustling ecosystem that is still evolving.  There is no guanrantee that the tooling and runtime required to utilise your little library will remain compatible in a year or two years.

A quick side note about open source.  Thousands of people fill their githubs with v0.0.0.0.1-early-alpha versions of projects, they push them to whatever dependency manager linked repository they need to and then they abandon the project.  To the superficial recruiters this sort of behaviour looks great but in the real world you look like to have serious commitment issues and how does that look tp a company that runs porject that last longer than 3 months.  So if you publish, that is you go to the effort of making it publically available for people to include in projects, you need to commit.  If not EOL it.

Now lets move away from simple libraries towards more complex products or applications.  Not 

Software has a kind of life cycle.  Yes it's not a living thing in the same way biological entites are but it is created, it matures and eventually it is replaced or turned off.

Maintenance isn't just about fixing bugs - you want to actively improve the service as well or it will stagnate, cost you more money than it makes and you'll be forced to kill it before its time.

## Language Choice

People often talk about learning curves of languages and how "language XYZ may have an initial steep learning curve but after you reach the peak its all so productive and powerful and other positive adjectives" or "language ABC has a really small feature set that you can learn in 6 hours and be productive from the outset".  All these discussion center make an asusmption that the project you start learing with is greenfield.

There is another learning curve - the learning curve for a language when dropping into a medium/large legacy codebase.  All those "powerful" features of the language are thrown in your face up front and it can be overwhelming, all those "limitations" force workarounds an additional complexity.

Take something like Scala - it's a veritable smorgasbord of features and I've had pletny of experience working in teams that have picked up other people Scala code.  Even codebases ugins "the good parts" of scala there are still dark areas of code in legacy systems that just baffle the keenest minds.  Macros, Implicit conversions, hell implicits in general permeate a lot frameworks and code and create a lot of spooky action at a distance.

Take Java - an enerprise main stay.  It's type system not quite dynamic enough to use the power of dynamics and not quite typed enough to use the power of types.  Java is full of limitations and limitation lead to workarounds.  Classes wrapping classes wrapping proxies wrapping factories for repositories that talk to DAOs.  Even the best teams end up creating some degree of indirection - it is almost necessary to make the code anything but an utter mess.  But each indirection creates more complexity more work for a maintenance team to discover and remember.

Take dynamic languages such as Rubyb of Clojure. We codify assumptions about our types and interactions as tests.  We create 1000s of discrete clean little tests.  The tests of course don't live in the production code they are elsewhere and rarely actually explain WHY something should be doing something - it's usually the WHAT.

We are in a time of heavy churn new and rediscovered languages are coming and going every week.  We have the developers announcing leaving ruby for node only to announce they are leaving node for Go.  The Javaists going to Scala and back again. Haskell, Elm, Go, Rust, Elixir and Erlang.  We break up our systems into mincroservices and write each one in a different language and we walk away to our next gig leaving someone else to pick up the pieces.

- Powerful language features increase the support learning curve
- More languages means more up front cost for support
- 

## Framework Strata

Old code layered ontop, fraemworks and libraries used and abandoned.

Convention based libraries and frameworks.  AOP, code generation, Automatic resolution of dependencies and how all this stuff makes debugging - an essential action for legcay support - almost impossible.  Loads of frameworks.  Each one with dependcies to manage, tools to install to make work properly, quirks and workarounds and learning curves.

## Architecture

Monoliths, micro services, N-tier

## Automation

Automate things to make thing easier but it distances you from a lot of architectural and implementation details that means improving a live service requires extra work.

## The People Problem

A lot of people in our industry have never done any real maintenance.  OSS authors write v0.1-v1.0 of a library and hand it over to the community.  In commercial projects people join and leave when the work gets "boring" and we establish "support teams" - people who specifically look after your broken all day

People have little to know understanding of what it means to create something and look after it past live services.  This is literally the model in a services company. Build stuff and walk away, try and maybe get a good deal on a support contract but thats looked after by support people because apparently they are different.  Projects that have an incremental rollout give you exposure to live service improvement and maintenance.