---
title:      The Old "Agile is for Developers" Myth
published:  true
layout:     post
date:       2013-05-08
categories: [Agile]
slug:       "There is a common misconception among people who are just starting out on the agile path that the agile model favors developers and downplays other roles.  While it is true that agile puts a greater focus and added responsibility on developers it also broadens the definition of developer"
---

There is a common misconception among people who are just starting out on the agile path that the agile model favors developers and downplays other roles.  While it is true that agile puts a greater focus and added responsibility on developers it also broadens the definition of developer from the niche of "people who write code" role to a more umbrella term of "people who deliver software".

Another comment I heard being made, admittedly in jest, is that agile is "Disneyland for Developers" and honestly this couldn't be further from the truth. In fact agile methods typically expect people involved in delivery to be MORE responsible for delivery.  Developers are no longer just mindless code monkeys banging out tasks against pseudo-code written in a document.  No, developers are expected to __DEVELOP a working solution__.  This includes analysis, design, architecture, development & testing.  All of this is expected to be done with one eye on the "big picture", they are expected to understand WHY they are doing something from, both, a technical and business perspective, they are expected to be at least understand how their code is deployed and often expected to be able to deploy it.  You don't have _the Java guys_, _the Web guys_ or _the DBAs_ - no, a single person in a team should at least be capable of driving out a vertical slice of functionality from infrastructure to database to services to HTML and JavaScript.  Of course the slice doesn't have to be, nor will be, perfect right now. There will always be space for specialists to come in and make their areas better, more secure, faster, whatever needs done.  But at the very least when all but one person on the team catches some rare exotic disease on a team bonding night out that person can still deliver __valuable__ working software until the rest of the team recovers.

Ultimately though the agile methods that are technically focused are about ensuring quality of delivery.  Delivered features should be __"done"__ rather than thrown together an handed over a test team to let them find the issues - a common side effect of time constrained, fixed scope, phased delivery.  That practice is unacceptable even in plan based or phased models and regardless of the project approach a development team can and should be making use of practices often associated with agile.

But other roles necessary in the delivery of a project also benefit greatly from agile approaches.  Just think of some of the things these other roles would do within more traditional project delivery models,

- _Who has to spend days or even weeks manually running the same few hundred scripted tests every few months to avoid regressions and assert correctness of the system?_  Not the developers thats for sure.  Testers are often required to execute manual scripted tests which are slow, prone to error and have a relatively low ROI compared to other testing techniques.  Manual tests check for issues you know will break your system, but its the unknown issues that typically kill your solution.  Automation of these tests (by developers and testers) gives faster more frequent feedback and allows testers to actually do interesting, rewarding, more valuable testing such as Exploratory Testing.  Testers should not be there to break things but to ensure we build the right thing right in the first place.
- _Who spends weeks going through multiple versions of documents that seldom get updated and go out of date shortly after sign off?_  Again, not typically the developers.  Business Analysts and Solution Architects spend a long time producing documents that create a contract for all parties involved.  Often, though, they are only pulled out during "disputes" around agreed functionality (between either developers & tester or customers & analysts).  If that happens though, then who is actually reading these things properly?  No matter how rigorous you make documents there will always be the problem of interpretation.  You can't misinterpret a running solution.
- _Who is expected to "manage expectations" when delivery is slipping beyond the scheduled dates?_.  Long running projects that pin all their plans on up front estimates will slip.  It's inevitable unless you've managed to overestimate everything but then I'd wonder why you are doing the work and not your more aggressive competitor.  When this happens it's typically the job of the manager and senior technical folks to inform the customer in the best way possible, to "manage their expectations" (shudder).  No one wants to do that.  In typical agile models visibility is a key differentiator (another way to ensure quick and frequent feedback) which removes the need to for big bang disappointment.  When things change or slip you know about them much quicker and can course adjust, the impact is significantly reduced.

All these (arguably wasteful) practices are typically the responsibility of people in non-developer roles.  Now that we have reduced or removed these practices does that mean we've made the roles redundant?  Not at all - as with the developer role increasing in scope so does the manager, analyst and tester.  Testers no longer just "test", they have a more important function - assuring that what they test is the right thing, they've become crucial to analysis.  Analysts also need to understand aspects of project management and managers themselves can move to a more hands-off sales and account management role as delivery is managed by the team itself.  What we will see is a reduction of specific titles on projects, most people no longer fit those niche titles.

## Facilitators & Implementors

So, yes, agile approaches certainly favour working software over volumes of documentation describing what the system __should__ do but heck - so does common sense.  

Agile approaches flatten the role structure across the delivery of a project.  You end up with umbrella terms like facilitators (who ensure the right thing is being built) and implementors (who ensure the thing is being built right).  

You go from this,

![Traditional View](/images/blog/traditional-view.png)

To something more like this,

![High-level Agile View](/images/blog/agile-view.png)

If you wanted to map these intentionally nebulous terms back to typical roles in a more traditional model you have,

- __Facilitators__ - Business Analysts, Solution Architects, Customers, Stakeholders, Users, Testers, Project Managers, Legal etc.
- __Implementors__ - Developers, Testers, DBAs, Designers 

Its clear to see where the commonplace agile roles fall as well - Product Owner and Scrum Master would be __facilitators__ and the team would be the __implementors__.

Already, even at this high level you're getting overlaps and in many ways everyone plays their part as both a __facilitator__ and an __implementor__ but its generally obvious what a persons primary function is and you can plan around that accordingly.

You don't need fancy, lofty titles because you have a single purpose. Titles only force attribution of responsibility to particular people, they enforce specialism and defensive attitudes.  Titles shouldn't affect the delivery of a solution.

So, no, "agile" is not just for developers, it's for the facilitators, the stakeholder & the implementors, it's an all-encompassing first step on the road to refocusing our goals from following a process to delivering the right thing quickly and painlessly. 
