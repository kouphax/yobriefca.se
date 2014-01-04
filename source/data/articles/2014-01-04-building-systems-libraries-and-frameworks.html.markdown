---
title:      "Building Systems: Libraries and Frameworks"
published:  true
layout:     post
date:       2014-01-04
categories: [Architecture]
slug:       "My original Micro Service Architecture post has been by far my most popular post and has generated a lot of discussion.  A recurring theme across these discussions is that of using small “fit for purpose” libraries or micro frameworks over larger frameworks.  So I wanted to capture some of my thoughts on this."
---

My [Micro Service Architecture post](http://yobriefca.se/blog/2013/04/29/micro-service-architecture/) has been by far my most popular post and has generated a lot of discussion.  A recurring theme across these discussions is that of using small “fit for purpose” libraries or micro frameworks over larger frameworks.  So I wanted to capture some of my thoughts on this.

## What is Small/Large?

My view, as already expressed in the original post, is that smaller frameworks and libraries should be favoured over larger frameworks when building micro services.  In fact thats my general rule of thumb when building systems of any scale.  But of course this is entirely subjective - what I define as small is probably considerably large in some other peoples views (especially as we are being completely language and platform agnostic here).  But there are a few things I consider when deciding if a library/framework is small or large,

- __The goal of the library/framework__.  Small things set out to fix a single problem, big things aim to be a panacea.  [jBcrypt](http://www.mindrot.org/projects/jBCrypt/) does strong password hashing, [Apache Commons](http://commons.apache.org/) does everything.  [ANORM](http://www.playframework.com/documentation/2.0/ScalaAnorm) gives you some simple sugar over JDBC, [Hibernate](http://hibernate.org/) has its own flipping query language for crying out loud. 
- __Prefer libraries over frameworks__.  The difference being you call libraries whereas frameworks call you - frameworks are Soviet Russia.  This definition makes libraries inherently more composable than frameworks.  Libraries are often less prone to abstraction distraction (or at the very least their abstractions are rather specific to their goal).  __Proper__ usage of libraries allows you to roll your own framework and conventions rather than workaround generic conventions baked into frameworks.
- __Feature count__.  Kitchen-sink libraries or frameworks are often just full of features you don’t need.  “Well don’t use them then” - ah if it were that simple.  The unix philosophy should apply to services and libraries (do one thing and do it well).  I mean, Apache Commons?  C'mon Really?  Why not just use a language with a decent core library instead?
- __Useful/Redundant Feature Ratio__.  If your framework/library has more features I don’t need than ones I do, or if I need to use a feature I don't need or want just to use the other features - thats big.  This is one reason why I consider Spring to be “big”.  It’s entire premise is built around a big IoC container.  The way I build systems I simply don’t need an IoC container<a name="_1"></a>[<sup>1</sup>](#1).  So being forced to use something I don’t need to gain other features I can get elsewhere without the overhead - well thats big<a name="_2"></a>[<sup>2</sup>](#2)
- __Tooling support__.  If your library or framework needs a special IDE or admin application or other 3rd party installs - its big.  Not necessarily “bad” but “big".

## Why Small?

There are a million analogies you can use here, like “I don’t buy a taco kit just to get taco shells” but lets try and stick to the reality here.  I’m also going to avoid the obvious stuff like "frameworks lead to leaky abstractions”, “frameworks lead to compromise or extra work” - there are plenty of articles about that stuff and YMMV anyway.

Firstly there is the obvious point that if you running a large number of micro services on a small number of machines then footprint matters, extra stuff loaded into a services memory allocation that aren’t used creates a lot of waste but this doesn’t really apply to most small/medium sized systems in reality.

Secondly design.  Large frameworks are designed with rather monolithic architectures in mind.  Modular service based systems have significantly different design needs that are orthogonal to the feature sets of large frameworks.  This leads to questions like “Why not just use Spring for this instead of Dropwizard, I know Spring and you can do the same thing with them?”.  Ultimately it leads to design decisions that are more suited to monolithic architectures resulting a sort of schizophrenic architecture that becomes difficult to reason about.  You’ll have to actively work against other peoples design decisions instead of establishing your own.

Finally there is an argument that frameworks reduce the LOC metric which I think is 

1. a terrible metric in the first place, and,
2. just plain wrong.  

Just because you’re taking other peoples work, neatly tucked away in a jar or package, and writing some of your own code around it doesn’t mean you’ve reduced the LOC metric in any meaningful way.  Yeah you've written less but if thats all you care about... mon dieu!  

In fact the minute you take on other peoples code you’ve become responsible for it, you need to have some understanding about it, you need to maintain it (security holes etc.) and one day you’ll stepping through that code to understand why things behave the way they do.  Working with libraries in this context is actually a lot easier than frameworks, they typically have a much smaller surface area.  When it comes to upgrades as well libraries can be significantly easier to manage without affecting other aspects of the system.  The quick win you get with frameworks when you start a project will need to be repaid and you better be capable to explaining how your system works - the customers don’t care that its a known bug in Rails - it’s a bug in the system you delivered to them, you need to fix it.  Starting with nothing and knowingly justifying every library inclusion you make will make the process of delivering and maintaining systems much more fluid instead of the promise a huge productivity spike at the start<a name="_3"></a>[<sup>3</sup>](#3).

Oh, and don't get me started on the fact that many developers see unused features as a challenge.

I’ve had much better __long term__ success and predictability by avoiding large frameworks and instead composing solutions using only what we needed even if it requires a bit of extra work up front or convincing.  Admittedly a frameworks promise of productivity is a difficult thing to move people away from, especially if they married to the framework or don’t really think beyond their own role. 

<hr/>
<sup><a name="1"></a>[1](#_1): IoC containers are suited to special types of systems - ones that have very large, deep or complex dependency graphs.  In other words poorly designed systems.</sup>

<sup><a name="2"></a>[2](#_2): I know of a project that uses Spring inside their Dropwizard services.  It’s like people don’t realise that you can actually call constructors using `new`. Not to mention the fact the `initialize()` method in a Dropwizard service is essentially an IoC container. If you’re putting a container inside a container you probably need help.</sup>

<sup><a name="3"></a>[3](#_3): This sort of thinking is articulated really well by [Dan Bodart](http://dan.bodar.com/2012/02/28/crazy-fast-build-times-or-when-10-seconds-starts-to-make-you-nervous/) in his [Crazy Fast Build Times talk](http://www.infoq.com/presentations/Crazy-Fast-Build-Times-or-When-10-Seconds-Starts-to-Make-You-Nervous).  His main argument is the tightening up of feedback loops but there are plenty of points that apply here too.</sup>