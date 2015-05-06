
---
date: 2015-05-06T00:00:00Z
title: "Not All Frameworks"
published: true
categories: [Craftsmanship]
type: article
external: false
---

It's interesting, to me at least, that many people in the software industry have very black and white opinions.  Maybe its the fact we wrangle binary (albeit at a very very high level these days) on a daily basis that gives rise to such binary views, or perhaps its reflective of the relative young age of the industry (and the mentality this fosters).  Who knows.  

Sadly it does reflect, often negatively, in the solutions we produce.  I've worked in legacy apps that are littered with the snake oil covered spent cases of silver bullets and read my fair share of "`Why we are using <%=arbitrary_new_shiny_thing%>`" posts followed, in time, by "`Actually were going back to an older reliable tech because <%=reasons%>`".  Then of course there is the wasteland of abandoned open source projects written in yesterdays techs while the sole creator is belligerently re-writing everything in todays tech because it's going to change the shape of IT.

I know I've been guilty of many of these things (and probably will be again) but in my older age I've started to care less about arguments that are, more or less, irrelevant.  For example any argument that ignores the most important aspect of technology application, that of context, immediately has me thinking `[CITATION NEEDED]`.  Without context its very easy to validate your own argument like so...

1. Find the __worst__ examples of the things you disagree with. Hearsay and rumour is also acceptable.
2. Counter this information with the __best__ examples of the things you agree with. Drop in words like __performant__, __webscale__ and __intuitive__ and you're bound to walk away feeling self-satisfied.

Your "opponent" can do exactly the same thing and achieve the same results.  It's like magic.

This seems to be the current state of affairs when it comes to talking about __frameworks vs libraries__.  In fact I think it always has been.  The __frameworks vs libraries__ debate is one of those topics that is always very black and white, usually focused on specific technologies.  The general theme is that frameworks are bad and libraries are good and therefore anything that could be categorised as a framework is quickly blacklisted in favour of a few libraries no matter how mature the framework is or how broken the library is.

"But frameworks don't compose"  - You know what bucko, neither does most of your crappy code. "Libraries give me flexibility" - yes they do but can you say with confidence that you know what you are doing?  Libraries aren't as ready to deal with cross cutting concerns in your solution and lets be honest do we really want to be sitting around designing login/registration/forgot password workflow every time we create an app?  The yaks have been built and the bike sheds shaved.

But the biggest problem I see with peoples "frameworks are evil" mindset is that the definition is far from perfect.  The definition I like the most is very coarse grained and open to interpretation which isn't ideal but it feels acceptable,

> With a library your code is responsible for calling the library whereas with a framework your code is executed by a framework __within__ the context of the framework.

Good enough.

But as I alluded to - there are libraries that are __clearly__ libraries and there are frameworks that are __clearly__ frameworks - but the other 90% live in this amazing fuzzy area I typically call practicality.  With a  dash of context many of these technologies can be viewed in a different light.  In fact we've even invented this term, one that over the course of my time in IT I've started to really dislike, __micro-frameworks__.  We've been told that frameworks are pure evil and will ruin your software project (when in fact its usually people) but, you know, as a starting point for a project or service libraries don't quite have enough opinion so instead we'll create a term that makes us feel better about the whole thing - a __micro-framework__.  It's like a library but with opinion.  One of the greatest examples of this abuse of verbage is [Spring Boot](http://projects.spring.io/spring-boot/) - it's a fine framework that helps hide a lot of friction and complexity that comes with the Spring web stack.  It has been tagged with the __micro-framework__ label more times than I cared to count.  But thats the thing, almost nothing has been taken away from it's core functionality as a Spring MVC framework - it has simply chosen to hide it behind carefully constructed opinion and default behaviour but it is still hiding the behemoth that is Spring.  

So [Spring Boot](http://projects.spring.io/spring-boot/) takes a framework and makes it a __micro-framework__ (it doesn't really of course but there you go).  What about the other end of the spectrum?  Lets say you just started a nice shiny greenfield project and people are all up in your face about microservices so you start out building some of these fancy microservices.  In time you'll start to notice common usage patterns, common behaviour or technical requirements.  Being the clever spod that you are you'll start extracting these patterns and behaviours out so they can be reused across your services.  Eventually you'll refine a lot of it so that parts of the lifecycle of your services are completely managed by these patterns.  Now you've only gone and built a framework haven't you?  Clearly that was bad design, back to the drawing board old bean eh?  No.  Look at [Dropwizard](http://www.dropwizard.io/) as another example.  A nicely curated set of well documented and tested libraries thrown together with a bit of glue code (again opinion) - all in all you end up at the same place you did with Spring Boot (again, not quite but superficially speaking you have).  One is a set of curated libraries and the other is a single mass of curated classes - it's all in the packaging.

A framework gives you consistency and predictably - these are often good things. Decisions you shouldn't waste time making are made for you, stuff you aren't aware of yet are being dealt with while you remain blissfully unaware, security is a big one, frameworks often deal with matters of security quite well.  Libraries, not so much.  Its all in the wrist, it's how you use it that makes the difference.  Depending on any 3rd party code too much creates nasty coupling and you should avoid this.  

A wonderful example of avoiding 3rd party coupling is shown in [The Destroy All Software](https://www.destroyallsoftware.com/) episode [#10: Fast Tests with and Without Rails](https://www.destroyallsoftware.com/screencasts/catalog/fast-tests-with-and-without-rails) by Gary Bernhardt.  In the episode he shows how you can get faster test cycles by isolating model code within Rails.  This isolation means the model itself has no reliance on Rails (making the startup times for tests much faster) and yet can still utilise the powerful features of Rails when necessary.

In any project of sufficient scale you'll likely end up creating something akin to a framework anyway.  It could be loving handcrafted from libraries over time or you could get immediate productivity by adopting a suitable ready made framework.  The choice is yours because you have the context available to make an informed decision. So look - saying frameworks are bad and libraries good is missing the point.  It's missing context.  It's also missing the fact that many __good__ frameworks are __good__ because they give flexibility similar to libraries but without having to make common decisions over and over.  That sort of quality isn't easy to develop so don't go judging all frameworks as if they were evil.  Attempting to pigeon hole any particular technology is also a wasted effort because its so easy to subvert the standard definitions.

In short - use what works, don't tightly couple yourself with __any__ third party code (library or framework), don't be quick to judge, listen to the biased nonsensewaffle that most people speak with skepticism and think. Please, please, above all else, __think__.