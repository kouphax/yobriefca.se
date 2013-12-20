---
title:      Micro Service Architecture
published:  true
layout:     post
date:       2013-04-29
categories: [Architecture]
slug:       "Micro Service Architecture is an architectural concept that aims to decouple a solution by decomposing functionality into discrete services"
---

Micro Service Architecture is an architectural concept that aims to decouple a solution by decomposing functionality into discrete services.  Think of it as applying many of the principles of [SOLID](http://en.wikipedia.org/wiki/SOLID_(object-oriented_design\)) at an architectural level, instead of classes you've got services.

![Micro Service Architecture](/images/blog/micro-service-architecture.png)

Conceptually speaking MSA is not particularly difficult to grasp but in practice it does raise many questions.  How do these services communicate? What about latency between services? How do you test the services?  How do you detect and respond to failure? How do you manage deployments when you have a bunch of interdependencies?  So lets expand on some of these throughout this post and see if MSA really is worth the effort.

## Anatomy of a Micro Service

First things first what actually __is__ a micro service?  Well there really isn't a hard and fast definition but from conversations with various people there seems to be a consensus that a micro service is a simple application that sits around the 10-100 LOC mark.  Now I realise that line count is an atrocious way to compare implementations so take what you will from that.  But they are small, micro even. This means you're not going to find hundreds of tiny services built on top of large frameworks, it's simply not practical.  No, simplicity and lightweightyness <sup>(not a real word)</sup> is the order of the day here.  Small frameworks like [Sinatra](http://www.sinatrarb.com/), [Webbit](https://github.com/webbit/webbit), [Finagle](http://twitter.github.io/finagle/) & [Connect](http://www.senchalabs.org/connect/) do just enough to allow you to wrap your actual code in a thin communication layer.

In terms of a footprint these services will be small, you're potentially going to run a lot of them on the same machine so you don't want to be holding on to memory or resources that you aren't intending to use.  Once again simple libraries over large frameworks will win out, you'll also find less of a reliance on 3rd party dependencies.

This decoupling at a service level also offers another interesting option.  We've pushed a lot of the old application complexity down to infrastructure level.  We are no longer bound to a single stack or language.  We can play to the strengths of any stack or language now.  It's entirely possible to have a system built out with a myriad of languages and libraries, though as we will touch on later this is a double edged sword.

You're also not going to find any true micro service based architectures that are hosted in an application server, that kinds of defeats the point.  To this end micro services self host, they grab a port and listen.  This means you'll lose any benefits your typical enterprise application server may bring and your service will need to provide some of the more essential ones (instrumentation, monitoring etc.).

## Communication

This is an interesting one.  How do your services communicate?  There really isn't a single answer for this, even in a single solution.  The most basic approach across the board would be to expose all services over HTTP and pass JSON back and forth.  Service discovery (how one service knows where to find another) can be as simple as putting the endpoint details into a configuration file (or simply hardcoded).  

You may discover, in certain circumstances, the cost of serialising and deserialising JSON payloads through an entire _transaction_ is causing bottlenecks.  Perhaps JSON isn't suitable at all (binary formats) and so you'll want to look at different protocols, like [Protobuf](https://code.google.com/p/protobuf/)

But hardcoded urls kind of imply a certain strict coupling and A-B-C workflow.  In a layered application this makes sense but moving into a service based architecture means you're not bound by this (potential) constraint.  Some communications between services can be completely decoupled, in fact some services could publish events or data blindly.  They can just throw it into the ether (or at least a message bus) and maybe one day some service can come along and start listening.  Alternatively maybe parts of your system could operate in a batch/offline process, feeding off a queue and flipping bits many hours later.  

![Micro Service Architecture Comms](/images/blog/micro-service-architecture-comms.png)

A micro service approach gives you this kind of flexibility without major architectural rework.

## Monitoring & Metrics

Components of a monolithic, layered solution don't typically fail silently - it either fails to compile or throws exceptions when something is wrong (unless you go out of your way and silently swallow exceptions like some insane person).  In a service based approach a service might drop off the face of the earth and it's very easy for no other service to notice (especially in a pub/sub model).  This means it's imperative that services are properly monitored and orchestrated.  In fact just knowing a service is alive is seldom good enough.  Is it providing enough business benefit? Is it even being used anymore? Is it acting as a bottleneck for reliant transactions?

Monitoring is always important but more so in a service based architecture where failure is often less obvious.  Good examples of this from the JVM world are [Metrics](http://metrics.codahale.com/) and [Ostrich](https://github.com/twitter/ostrich) these libraries not only allow for collection of metrics but provide a means of integrating and reporting that data into other services like [Nagios](http://www.nagios.org/) or [Ganglia](http://ganglia.sourceforge.net/).

## Testing 

There is nothing particularly special about testing services in a micro service based system but the point I want to raise here is that you've reduced the need to have a full suite of tests for every service.  Because a service does one thing, and should do it well the risk of introducing a system breaking bug is significantly reduced thanks to the natural behavior of a service based system.  Now writing tests for the sake of it, or just in case, holds little to no value but still require the same amount of care or attention during refactoring.

I'm not saying _don't_ test but I am recommending you think before you use the old "No. of Tests" as a value of code quality.

In fact lets go one step further and change the way we test entirely.  You've got monitoring in place, at any given time you can know what services are broken and recover.  But also why not monitor key business metrics on each service and use them as indicators to know if we've broken something.  This is commonly known as a production immune system.  If conversions are dropping, or sales have halted then you can make it so your system rolls back to a known stable state and gives you nasty looks until you fix the problem.  The same approach can be used for A/B testing and Dark Launching new features and services.

Micro Service Architecture actually makes a whole lot of sense when it comes to ensuring you have a stable system at any scale.

##  Applications of Micro Service Architecture

MSA has a number of interesting applications.  Obviously green field solutions of a given size is an obvious use of this approach.  Though I say "given size" because the cost incurred at an infrastructure level required to support micro services could outweigh the benefits and, for small systems, a simple web app would be totally acceptable.

Micro Services also have an interesting application in large legacy systems.  Working with legacy code is risky at best.  For systems that have been running for decades there is a chance there is limited knowledge in how the systems actually works internally.  Working with this code can be like working with a house of cards and a simple mistake in one place can have adverse affects elsewhere.  These systems are typically mission critical so mistakes can be costly.  So just don't touch it.  The micro service approach lets you do this.  Rather than diving into the legacy code base, write a small service that does what you need (yes you may be duplicating legacy code) and proxy service calls through it (e.g. via nginx/apache etc or purely within code).  

![Micro Service Architecture Proxy](/images/blog/micro-service-architecture-proxy.png)

Now imagine doing this regularly as part of your maintenance and support program.  You'll be turning off the old system bit by bit and perhaps eventually be able to turn it off completely.

## Issues

Micro Service based systems aren't without their problems of course.  For one there is a greater need to better understand non-development areas.  In days of old before DevOps was a broadly misunderstood marketing term developers would deliver working systems over the wall to operations who would deploy it to production and everything would fail.  Of course this was the ops team fault, don't they know anything?  Jeez.  It was entirely possible for a developer to know only code, running the entire application from their IDE without thinking about how this would work in real life.  MSA forces the developers hand to actually be more responsible and actually get involved in scripting of deployments and understanding integration requirements etc.

Another issue that I touched on earlier is the fact, left unchecked you could have hundreds of small services written in hundreds of languages.  Simply managing that sort of estate of languages and platforms with their different installations, package managers and requirements can stop a project dead in its tracks.

An anti-pattern has emerged from the MSA approach - often called the Nano-Service architecture.  These are systems built on services that are so small that duplication of effort is prevalent and it becomes almost impossible to reason about any particular area of a system easily.

## Macro Post for Micro Services

The irony of a large wall of text to introduce the concept of micro services is not lost on me but there is lots to consider.  Micro Service based systems can produce scalable, fault tolerant systems on fairly low cost hardware.  MSA promotes good practices of keeping things lightweight, emerging systems and business monitoring but as with everything should be approached with a rational mindset and solid understanding of what you __need__ to achieve.