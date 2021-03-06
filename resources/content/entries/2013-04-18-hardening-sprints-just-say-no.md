---
date: 2013-04-17T23:00:00Z
title: 'Hardening Sprints: Just Say No?'
published: true
categories: [Agile]
type: article
external: false
---
I was recently asked by a PM in my company if "hardening sprints" are allowed in agile projects and I gave the very pragmatic answer of "you do what needs to be done".  I left the conversation not feeling right though and I've been thinking about the question ever since.  To be honest my gut instinct is that, with certain exceptions, these sort of sprints are a smell and their origins are firmly rooted in phased/waterfall delivery.  I'll address the _"with certain exceptions"_ caveat a bit later but lets first dive into the idea of hardening sprints.

## What is a hardening sprint?

![We'll Just Squeeze the other bricks in when we're done with the house](/images/blog/hardening-sprints.png)

A hardening sprint is a timebox or sprint reserved at the end of a group of sprints (usually prior to release) to allow testing or hardening of the release.  This could mean extra testing, different types of testing, refactoring, reviews etc.  It brings with it the rather bizarre concept of "Done Done" - i.e. features that have been delivered in previous sprints may be "Done" but not "Done Done".  So it may be coded and "kind-of" working and "kind-of" tested and people are "kind-of" happy with it and yeah its "kind-of" done but we'll wait until the end to know if we are really done.

## So What?

It strikes me as "kind-of" odd that this sort of thing happens without people raising an eyebrow.  For one thing there is no such thing as "Done Done" and if we don't admit it I fear that we'll see "Done Done Done" pushing its way into our process vocabulary.  

The next thing that strikes me - there is absolutely no data available to predict how long this "hardening" process should take.  If we take it at face value the term implies a single sprint.  How can we be sure whatever unpredicatable stuff comes up during that sprint can actually be addressed and adequatley resolved in that sprint?  Or, taking it to the other extreme (indefinite amount of sprints) how do we know when to stop - afterall nothing is ever perfect.

## Undoing All The Good Work?

So we've spent X amount of sprints refining our approach to delivery, continually improving and learning and now we've thrown the project into this huge dark pit of uncertainty and "hardening".  Surely I'm not alone in thinking this sounds somewhat wrong.

Another risk is that this kind of strucutre will create is a potential reduction in ongoing quality.  It could be argued that hardening sprints, much like the old phased/waterfall approach to delivery, removes a certain amount of responsibility from the delivery team.  Now there is less of a desire for developers to apply as much rigour to their code as they may have - afterall _"the testers will find the bugs so why waste my time being thorough?"_.  But if you are deferring your user or penetration testing a few months down the line there is always this notion that the team can just _"throw it in and see what comes out in the wash"_. 

## "With Certain Exceptions"

Of course, like every rule, there may be exceptions.  One obvious exception is external penetration testing.  There can be considerable cost and time assoicated with getting a third party specialist in and its certainly not viable to do it every sprint.  So defering a full pen testing cycle until near the release is an acceptable and often necessary exception.  But that doesn't mean the team should throw all care about security out the window - the goal of the penetration testing should be to validate that there are no vulnerabilities not to discover them.

## Next Time Gadget... Next Time

So are "hardening sprints" a good idea?  I'm inclined, as negative as it may be, to start with "no" and take it from there, afterall its better to start with discovering __why we can't__ do certain things inside of a sprint rather than assume __we can't__ and carry on ignorant of the potential benefits.