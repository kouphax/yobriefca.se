---
date: 2013-11-08T00:00:00Z
title: Web Summit
published: true
categories: [Craftsmanship]
type: article
external: false
---
Last week, along with 9999 other people, I attended the [Web Summit](http://www.websummit.net/) in Dublin.  The Summit is touted as being
Europes answer to [SXSW](http://sxsw.com/) and honestly it's certainly not my usual type of conference but, hey, when someone offers you a
free media pass you don't turn it down.  And so this post exists because it's the least I could do.

So was it the European answer to SXSW? Pretty damn close in both spirit and execution.  It was only 2 days which is much shorter than the
SXSW marathon but still by the end of the second day I was nearly burnt out.

I spent most of my time in the Developer Stage/Tent - a stage curated by Eamon Leonard of [Engine Yard](https://www.engineyard.com/)
and a few other Engine Yarders.  Key takeaways for me included -

- __(Micro) Service Architecture__ is here in force.  Essentially taking the Unix command philosophy of "Do one thing and do it well" and apply it
to service oriented architectures.  This leads to resilient self healing systems.  In fact [Chad Fowler](https://twitter.com/chadfowler) made the
excellent analogy of systems being like the human body where cells (services) are constantly dying and being replaced yet the whole system
continues to run.
- __Immutability doesn't just apply to code__.  At [Wunderkit/6wunderkinder](http://www.6wunderkinder.com) they have an excellent story around
immutable infrastructure.  They practise it so much that their approach to spinning up new machines is to "throw away the SSH keys" so that the
only thing you can do is create or destroy machines.
- __Testing is overrated__ - super contentious but the general arguments seem to be around _typical_ testing techniques such as unit and integration
testing.  This idea fits well with MSA because if it is extremely quick to spin up new services or multiple version of the same service then
business and operational metrics with automatic rollback to a "last known good version" trumps any form of extensive testing.  I'm not saying
DON'T TEST but you need to remember unit/integration tests for a volatile service are just extra work.  When the service becomes more stable, maybe then
think about how you wrap it in a safety net of extra unit tests?  I dunno... we could write books on that debate but they've echoed many of the
questions I've had myself.

The rest of the conference I floated around, seen many startups, shook MANY MANY hands.  The evening events where nothing short of epic.  Dame Lane
was closed off and Summiters could wander between bars getting bathed in food, drink and music.

As I said at the start this wasn't my typical event as evidenced by the fact I spent most of it geeking out over technical things rather than
taking in the Digital Marketing &  Cloud stages.  It's cool they had something for everyone in a city that I missed (which made it a welcome visit).

So great thanks and applaud go out [Paddy Cosgrave](https://twitter.com/paddycosgrave) and co. for a well put together event.