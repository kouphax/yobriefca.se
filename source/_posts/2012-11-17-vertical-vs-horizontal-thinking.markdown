---
layout: post
title: "Vertical vs Horizontal Thinking"
date: 2012-11-17 22:48
comments: true
categories: [Miscellaneous]
---

I'm not quite sure were this ramble is going to lead, it's been a while since I've felt like writing something that hasn't been clearly defined by my half conscious brain but lets take the journey anyway shall we?

This is going to be loosely related to agility, not necessarily the "agile method or process" but more simply about adapting to change and embracing transparency in a software project.  More often than not this can be simply referred to as "common sense" and it's a great thing.  

But I digress,

In a vertical world you deliver features by implementing vertical spikes through each layer of the system architecture.  It doesn't have to be right, but it does need to be good enough for now.  Sure it may not deliver the best user experience or maybe there are more HTTP requests or database calls than necessary but it's useable.  Its shippable.  

In the horizontal world layers of the architecture are tackled by (possibly pseudo) specialists and delivered en masse.  This is very typical of the Big Design Upfront, Plan Based, Waterfall approach to delivery but its visible in a lot of projects in various guises and it's rather natural to many people.

Now, on the surface, getting specialists to deliver what they know best and then smash them altogether actually sounds great.  You know your DB person is going to produce a wonderful hyper-normalised schema for you, your UI/UX person will deliver a gorgeous mockup of the site and your middle layer app people will deliver the best truly ReSTful service layer known to man.  But then you spend an insane amount of time (or many many little periods of time) doing the actual integration and making this vision a reality.  The testing, the regressions, the edge cases the performance hits and sacrifices through integrating perfect works of horizontal art.  Best case you get it done eventually, worst case someone else gets it done by layering hack upon hack at integration points just to get it working.  

Now imagine your DB person gets eaten by a badger, or your UI person decides they've had enough and goes to live in a free-love commune and drink the spiked kool-aid?  I've overly dramatised the [Bus Factor](http://en.wikipedia.org/wiki/Bus_factor) here but the point is still made.  You've lost siloed knowledge forever and you need to spend time building it up again, and time and quality slip.

Finally - projects that deliver horizontally are, more often than not, projects that have limited customer/stakeholder visibility.  Sometimes even less for end-users.  So delivering a mockup of the UI and a DB schema that can be exported as a nice UML diagram is enough to satisfy people that work is on-going and going well.  Even if it isn't.  It's not a lie but it is a false impression, and it's an impression that doesn't work for long especially if you have shorter release cycles with ongoing customer interaction.  A page of beautiful buttons that don't do anything will create an impression that you are nearly done and can easily generate disappointment at subsequent demos.  Is it worth it?

Don't get me wrong it is very possible to successfully deliver software in this manner, on time and on budget but so many things must fall into place and all the stars must be aligned that it's a rather big risk, but a risk that people seem to ignore.

So I'm saying vertical is better and there are many reasons for that and I'll touch on some of them here but there is one very fundamental reason it's better.  Working and thinking vertically allows you to create a shippable product early.  It's not all done, far from it.  It probably looks like ass as well but still it's usable enough to get the job done, right?  Imagine using a new product that looked amazing but only had 3 working features and the rest of the buttons and knobs didn't do anything.  Wow factor is for kids and sales men.

Why else is vertical thinking better?

For one everyone on the team works across all layers of the solution.  You don't have huge silos of knowledge contained in a single brain of some crazy bus magnet.  Sure you may involve specialists to ensure you're not doing it totally wrong but at least you'd be working WITH them and picking up parts of the vast knowledge they may have.

By delivering something "good enough for now" you actually reduce waste.  True you may re-write a few things to get it right but you haven't spent hours crafting perfection only to tear it apart.  Even better the first iteration may make users realise - "Actually you know what?  That super whizzy button thing - we don't need it".  Waterfall attempts to "manage change" because change in waterfall is scary expensive.  Embrace it early and often and it much less costly.

Finally, the solution evolves rather than being dragged into existence from a 189 page document of business rules and textual assumptions.  This allows you to experiment and try things, throw them away or discover a better way of doing things.  You can adapt the solution at anytime to suit the changing nature of the environment it lives in.

Vertical thinking is a proven pattern in many other aspects of the software world.  TDD for example. Rather than writing reams of code and testing it manually later, you create a solution bit by bit and adapt it fearlessly as you go.  UI designers never just bang out one design and leave it there.  Smaller feedback loops are essential to flow and success and vertical thinking gives you this.  Break the habit of wowing people and replace it with the habit of delivering - it's a better, longer lasting feeling of win.
