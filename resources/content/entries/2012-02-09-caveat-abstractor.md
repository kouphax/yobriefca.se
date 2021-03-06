---
date: 2012-02-09T00:00:00Z
title: Caveat Abstractor
published: true
categories: [Design]
type: article
external: false
---
_Ramblings from 30,000ft.  The word abstraction is starting to sound odd._

Every time you create an abstraction there is a very good chance you've introduced unnecessary complexity into a solution.  It should never be done alone - it should be a team decision.  The goal of an abstraction is to simplify but in its untamed natural form an abstraction will only add complexity.

<!-- more -->

As programmers it's easy to fall into the trap whereby we want the code that we write to be the last time anyone ever has to write that code again.  We dream of creating reusable works of art that ever other developer will use and never be concerned with the low level implementation ever again.

This is wrong and impossible.  An anti-pattern even!

It baffles me that anyone would create an abstraction that they didn't have a need for at the time. It's wasteful and degrades the readability, grokability and all those other good "ilities" we use.  Of course I say this like i don't do it myself but I'd be lying to you and myself.

An abstraction, by definition, is an intentional hiding of implementation and knowledge but surely understanding of these things will lead to a higher chance of success in a project?  The person that created the abstraction has that understanding but it's like they dont want anyone else to know how it works.  They're ready to take that secret to their grave.

When you start to abstract you need to remind yourself you are building a specific solution and not a framework (unless you are of course).  Abstractions should be done only within the scope of the project and you should have a strong case to back up its creation (make the decision a team effort).  This may sound like overhead but if you find it difficult to justify the creation odds are the abstraction is unnecessary.

Abstractions should not be grand, they should be light and convey their intent clearly by other people (pair programming and peer review will help weed out any verbose abstractions).

If you are creating an abstraction for anything other than DRY, odds are youre probably creating it for YAGNI.  You'll start considering irrelevant use cases and having to cater for insane edge cases.  This will happen even if you are taking a strong test driven approach - you'll concern youself with the workings of the abstraction rather then the problem you are solving and begin creating endless "what if" tests to cater for all those "0.00000001% chance of happening" edge cases. When this happens youre going to end up in mediocrity.  Mediocre abstractions attract more abstraction.  Even in a shared codebase bad abstractions create a certain amount of implied ownership to the person that created it. Inevitably someone will come along and create an abstraction around your abstraction (even if they have access to the original source).  This in turn will likely become YAMA (Yet Another Mediocre Abstraction) attracting yet more abstraction.... You can guess where I am going with that one... Its abstractions all the way down and everytime you abstract you've decreased system comphension and likely destroyed another future developers soul.

In the same way guns dont kill people, people do - Abstraction dont kill projects, developers do.  You've only got yourself to blame (been there done that, will be again).  A nice simple abstraction, at the right time (e.g. When needed) can be expressive and helpful but too often we put the abstraction before the need.

So consider abstractions as "guilty until proven innocent" not the other way around.  Solve YOUR problem - not every other problem, not until you need to.