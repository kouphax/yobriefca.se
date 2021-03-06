---
date: 2012-04-28T23:00:00Z
title: The Little Sacrifices
published: true
categories: [Random]
type: article
external: false
---
Late night ramblings... 

Productivity can plateau very quickly - especially in a technical process.  No matter how good you get at the process or approach, if you keep doing the same thing you can only get so good at it or do it so fast.  It's possible, however, by sacrificing some of your precious time to go beyond that plateau.  There are things we, the developers, do as part of our development process that are so small and insignificant that we do them without thinking

- Clearing down the values in LocalStorage when debugging a web app
- Copying assets from one folder to another (aka the macgyver deploy)
- Navigating to `http://localhost:8000` or `9000` or some other port
- Typing `rake generate preview` into the terminal about 20 times an hour
- These are but a few things I've had to consider today alone

Generally these things are so small we'd just do them by hand, right?  But if you take the time to automate these processes or even just refine them in some way it can do wonders for you productivity going forward.

For example the clearing of LocalStorage in Chrome - spent a few minutes writing a bookmarklet that you can execute with 1 click.  The macgyver deploy?  Write a rake/make/jake/psake/cake task for it.  Better yet use [Guard](https://github.com/guard/guard) or [Watchr](https://github.com/mynyml/watchr) and perform the deploy on save!  Navigatin to various different localhost ports?  Try my [alfred script](http://yobriefca.se/blog/2012/04/03/alfred-hack-for-web-developers/) to minimise the keystrokes. `rake generate preview` - alias that bad boy to `rgp`!  Save yourself those extra keystrokes - you might need them later.

Listen - specifics aren't important here.  You'll have your own small repetitive tasks and you'll be able to find your own ways to automate, improve or refine them - but this is the point.  You probably don't.  At least not as often as you should.  Losing a second here and there on one task means very little in the small.  In the large, across many of these tasks, well thats a different story.  It's hard to justify spending 10 or 20 minutes on something to automate a task that takes 5 seconds to run but even if the time doesn't repay for a while then the fact you have less of a cognitive shift between your primary task and this secondary one should allow you stay in the highly effective, productive mindset to get the job done.  

So give it a go, even as a thought experiment - think about the habitual development tasks you do and how you could automate them - you could well suprise yourself (like I have been recently).