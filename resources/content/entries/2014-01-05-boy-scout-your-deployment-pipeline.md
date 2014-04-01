---
date: 2014-01-05T00:00:00Z
title: Continuous Delivery & Boy Scouting Your Deployment Pipeline
published: true
categories: [Architecture, Agile]
type: article
external: false
---
Talking to teams and stakeholders within organisations over the last few years I've seen very strong concerns that [continuous delivery](http://en.wikipedia.org/wiki/Continuous_delivery) was not for them.  Usually it was because there was already a process in place - a process that involved lead times in the weeks or months, a lot of repeated manual work and many phases of QA prior to any release being deployed by some third party vendor.  They worry, especially, that they would have very little chance of automating everything in the process.

## Throwing the baby out with the bath water

The goal of continuous delivery isn't actually about automating everything (it just so happens that in many cases automation has significant benefits).  Continuous delivery is about reducing the time to deliver a single change to production so to establish a tighter feedback loop between your company or product and your users.  

However a lot of the time taken to deploy a release may well be out of your control, usually for the reasons quoted above.  The thing is though, things change.  It takes time but things always change.  To quote Dr. Ian Malcolm - "[Life finds a way](http://www.youtube.com/watch?v=SkWeMvrNiOM)".  For example - your QA department might well suddenly discover the joys of automating a lot of their manual regression tests, your service management contract may expire and some new executive might suggest you try this new fangled "agile cloud as a service stuff" on the back of promises like 10x productivity<a name="_1"></a>[<sup>1</sup>](#1).  There are lots of reasons, and most of them a blessing in disguise even if they are a bit ill-informed.  __Being ready allows you to capitalise on this organistational change__ and make it last.

## Visualise your Pipeline

Draw a diagram or outline the steps that a change or single commit goes through to get onto a production box.  This isn't done enough in projects, there are fragments of it in peoples heads and there are foggy areas of uncertainty.  Drawing a pipeline, one that covers multiple deparments is actually quite an eye opener.  

Another thing that this task does is help you realise that deployment isn't a single big black box that is always as slow as the slowest step.  Its a series of steps and by visualising it you can simulate changes to it.  That  1/2 day it takes to deploy to a preview environment for developer sanity checking may pale in comparison to the 4 week period that you wait to see if the production deployment was successful but bringing that 1/2 day down to 10 minutes for a team of, say, 6 developers has a more long term benefit.  Even more, if you get to extend the process for deploying to preview into the QA environments at a later date - the work is mostly already done<a name="_2"></a>[<sup>2</sup>](#2).

So you may not have hugely reduced the time to a __production__ release but you've helped tighten up the development feedback and prepared yourself for a future where these changes can be reused further down the pipeline.

## Boy Scouting

But a lot of this work is far from trivial and right now it might be hard to sell it to the people holding the purse strings and Gantt charts so what can you do?  Well, a few years ago Robert C. Martin (Uncle Bob) suggested that you can [apply the Boy Scout rule to code bases](http://programmer.97things.oreilly.com/wiki/index.php/The_Boy_Scout_Rule).

> The Boy Scouts have a rule: "Always leave the campground cleaner than you found it." If you find a mess on the ground, you clean it up regardless of who might have made the mess. You intentionally improve the environment for the next group of campers.

Applying this rule to a code base means that every check in you should, 

- tidy up a little bit of extra code,
- add another useful unit test, 
- refactor a tiny bit of code, or,
- tidy up a bit of formatting

Anything to have a positive impact on the quality of code in the project. 

I proffer that __the same rule can be applied to your deployment pipeline__.  Things like - introduce a script that starts up a simple vagrant environment, add a little bit of Puppet/Chef/Salt to create a production-like environment on developers machines, automate that file copy/paste process that Steve in Team B does during every release with a script, document the deployment process, publish the deployment pipeline, add a bit more validation to your CI server.  Anything that makes the pipeline that tiny bit less painful to flow through.

In time these small incremental changes have a compound positive effect that results in fewer issues with deployment (and on-boarding) and acts as an example to people involved downstream in the deployment pipeline.  

Ultimately the technical change required to move towards continuous delivery takes time and effort but it is nothing compared to the cultural and organisational changes.  Proactively addressing the technical changes that can be made now can benefit your current process and also prepare you for a more effective delivery model in the future.

<hr/>
<sup><a name="1"></a>[1](#_1): Both of these things have happened in recent years to projects I've been involved in.</sup>

<sup><a name="2"></a>[2](#_2): We haven't even pointed out that this developer deployment is going to be of immediate benefit for on-boarding new team members as well.</sup>