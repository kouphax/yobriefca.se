---
layout: post
title: "Technology in the Cloud"
date: 2012-07-12 13:59
comments: true
categories: [Cloud, Miscellaneous]
---

The Cloud.  As much as the term has been pummelled into meaningless marketing sputum by the guys in expensive suits and fake smiles it does exist and is distinct from a typical "throw all your stuff in a data center" setup.

The Cloud is distinct, as well, in terms of how you think about your solution architecture.  The typical old school approach would be to just create big monoliths to process your data but that doesn't work out so well in the cloud.  Of course the considerations for the cloud aren't new, hell no - things like SOA have been around for an age, but if you want to harness the benefits of the cloud as the internet intended you will be narrowing your options.  No more just cobbling common bits together and crossing your fingers.  

So fine - architecting for the cloud has some special considerations but thats not what I want to talk about - there are better people than me to cover that.

What I want to talk about is use of technology within that architecture.  I've never been a fan of the one-size-fits-all-painful-or-not technology stack driven almost solely by a need to avoid unnecessary education of developers.  And, now I feel the cloud is strengthening my view that a default stack is more trouble than it's worth.  More importantly it's really adding momentum to the whole polyglot architecture, or simply using the right tools for the job.

Let me explain.  Any default stack, by definition, needs to cater to many user cases and as such brings a certain amount of sacrifice with it.  Sacrifices come in many forms but usually in performance, memory footprint and/or verbose configuration.  In the cloud you are, in essence, in a Pay-As-You-Go model, every clock cycle and every bit of storage costs.  Now that heavy ORM with its less than ideal queries and slow relationship mapping is starting to cost you money EVERY request.  {% pullquote %} In fact, it's probably backed by that storage heavy relational database for all your non-relational data.  Same for that rather large web framework you've got there.... every time to deal with a spike and scale that badboy out and you're having to use medium instances rather than small.  That SOAP based webservice that requires triple the bandwidth to service a request... ugh you get the point. {" "In the cloud the default stack can easily bleed money." "}
{% endpullquote %}

I guess this is less about the default stack and more about just using the right tools to get the job done in the most effective manner - afterall you're paying for this stuff now.  You kind of were before but in such a coarse grained manner it didn't matter - it does now.

In summary - the cloud has started to make technology decisions more accountable and we need to ensure that those decisions aren't costing our stakeholders more money than necessary.

PS.

I'm not going to sit here and preach about the which specific technologies to use but come on - if your storing Session State in a Oracle database, switch to Riak, or Redis.  If you're building a nice scalable API then use something like Scala which promotes immutable state and concurrency out of the box over something like vanilla Java (or perhaps just a better framework Spring-WS vs Dropwizard for example).  JSON over XML or a binary format (Protobuf for example) for machine to machine communication.  A small node.js web site over ASP.NET Webforms for simple sites.  The list goes on.  That is to say consider this advice against the other factors driving technical decisions - dont JUST go for the smallest, lightest tools because it MIGHT be cheaper.  Do the research.

Finally, perhaps you've got another IT team needing to support this new solution?  Bring them along this journey too, educate them as you educate yourself, dont just throw the finished thing over the wall with a 1 week handover and a pat on the back.

Don't be afraid - it's a super fun world we live in!