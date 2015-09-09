---
date: 2015-05-13T00:00:00Z
title: "More Betterness"
published: true
categories: [Craftsmanship]
type: article
external: false
---

<style>
  body {
    background-color: #f5f7f1;
    color: #424242;
  }

  a {
    color: #93bdc2;
  }

  .row h1 {
    display:none;
  }

  .homer a {
    color: #424242;
  }
</style>

> This article is adapted from a presentation I gave on team work and understanding

![More Betterness](/images/morebetterness/morebetterness.001.png)

Hi. This wont take very long so just sit back and enjoy yourself.  I want to start with a quote.

![More Betterness](/images/morebetterness/morebetterness.002.png)

Have a read of that, and let it sink in for a moment before we move on.  We'll come back to it later.

OK? Good.

I'd like to introduce you to a few people.

![More Betterness](/images/morebetterness/morebetterness.005.png)

This is Alice.  Typical hacker, Alice rarely writes tests.  Never mind Test Driven Development there is literally no tests.  Talk about pushing more broken garbage out in to the world.  Boo Alice.  Boo.

![More Betterness](/images/morebetterness/morebetterness.008.png)

Here's Bob. Bob [bought a book](http://shop.oreilly.com/product/9780596809492.do) recently a learned the "Boy Scout Rule".  Since then he has been helping his team out by making old code more concise.  Bob, you're awesome.

![More Betterness](/images/morebetterness/morebetterness.011.png)

Hi Caitlin.  Caitlin's team are a bit peeved at Caitlin because even though everyone has moved to a more functional style of writing JavaScript Caitlin still insists on writing old-style imperative `for` loops which are super noisy.  Get with the program Caitlin.

![More Betterness](/images/morebetterness/morebetterness.014.png)

Dales team are on fire!  Not literally but they did just knock out a really robust, fault tolerant data pipeline project in just shy of a week. Mind. Blown.  Gold stars all round folks.

![More Betterness](/images/morebetterness/morebetterness.017.png)

Finally we have Eda.  Eda is like a magpie.  She just loves kicking off projects in whatever shiny new technologies are at the top of Hacker News. The problem is she gets bored and distracted by other new things.  Eventually she just moves on to another project.  For pity sake Eda, sort it out.

Now these are interesting stories and I'm sure you can identify various people in these personas but there's a problem here.  A few problems in fact which we will get to later.  For now lets focus on one problem.  All these stories are missing something.  Context.

![More Betterness](/images/morebetterness/morebetterness.019.png)

Context is to understanding like oxygen is to a fire.  Without context it is impossible to fully understand any situation.  Without context we are forced to make assumptions and judgements, we are more easily coerced into believing things about the situation and ultimately make wrong decisions.

So let me provide you a bit of context for the previous case studies.

![More Betterness](/images/morebetterness/morebetterness.022.png)

Back to Alice - the dirty little hacker.  Turns out Alice is working on a series of really rapid prototypes to help a client understand some of their internal requirements and discover what actually needs to be built.  These prototypes are all throwaway and Alice knows that codifying assumptions through tests would be a pointless goal.

We're sorry Alice we were wrong about you.

![More Betterness](/images/morebetterness/morebetterness.025.png)

Now Bob. Bob. Bob. Bob. Bobs nobel application of the "Boy Scout Rule" has really mucked things up for the support team.  You see Bob had a goal of making code more concise but he ignored the fact that someone needs to maintain this code and so Bob set about code golfing perfectly readable, nicely structured code into a tangle of chained functional style calls.  Bob made the support team cry.

![More Betterness](/images/morebetterness/morebetterness.028.png)

Ah yes Caitlin.  Going against the grain with coding style, using old style for loops in JavaScript when there is a perfectly good functional approach available.  Well... Caitlin is right.  You see Caitlin is working in a very performance sensitive part of the application and, well, old-style for loops are often much faster than their functional cousins.  She used data to measure performance and came to this conclusion using her findings.  Caitlin you used evidence based decision making instead of just falling in line.  You rock.

![More Betterness](/images/morebetterness/morebetterness.031.png)

Oh look it's Dales elite team of rockstar ninjas. Thanks for the robust data pipeline folks but I only wanted you to scrape a couple of web pages and extra some figures.  In fact while I waited for you I wrote a little shell script using `curl`, `sed` and `awk` to do it for me. Your entire team just burnt nearly a week of funding due to over-engineering.  Thanks for nothing Dales Team.

![More Betterness](/images/morebetterness/morebetterness.034.png)

Finally Eda.  Eda likes to remain current as it helps her make more informed decisions.  So in her down time she works on hobby projects and code katas.  She rarely finishes them because there is so much churn in the world of technology that she get distracted.  Thats cool, she learnt stuff, she was under no obligation to actually release anything.  Eda makes better decisions in work because of her tinkering and is well respected.  Have a big fat pay rise Eda.

![More Betterness](/images/morebetterness/morebetterness.035.png)

So you can see with more context we can actually alter the entire narrative of a story.  What was once a bad thing becomes a good thing and vice-versa.  Of course it may not.  Eda could well have been one of those people that likes to start projects in work, complicate matters and then leave.  But she wasn't.

And that is the point of this entire talk. With enough context we can make better decisions, with better decisions we can be more successful and start moving toward more betterness.

But it's not just context other things where at play such as bias and influence.  So in order to be better we need to understand these things as well.

![More Betterness](/images/morebetterness/morebetterness.036.png)

First and foremost establish as much context as possible.  Context is king after all.  You can see how without context I was able to alter the narrative of a story. Know that there may always be more to it.  Use discovery to improve understanding of the context.  There is a reason why a great discovery session starts with as many diverse stakeholders as possible - everyone has a slightly different view of the overall context.

Don't walk into any situation with rigid assumptions, don't “solutionize" before you know enough. Listen.

![More Betterness](/images/morebetterness/morebetterness.037.png)

I'm sure as I was reading out the case studies some of you had some reaction to the stories I was telling.  Perhaps Alice and her lack of tests made you roll your eyes.  Thats bias at play.  Bias actually reinforced the story irrespective of the situational context.

We all have bias and bias exists without context.  Bias affects your understanding of the context and so can adversely affect decision making.  While we can't necessarily change our bias we can be aware of it.  This helps us better understand our decision making.

In fact not being aware of a bias you may hold can lead to unintended steering of decision making.

![More Betterness](/images/morebetterness/morebetterness.038.png)

One thing I did during the original case studies was, at least attempt to, influence your feelings for the person.  I intentionally used negative words and tone to make you think the worst.

Influence may know about situational context but doesn't care.  The loudest voice in the room may get their way but they may not be right.  Influence can be either internal or external.  That echo chamber of a Twitter stream that tells you how OO is bad and functional programming is the one true way can affect the decisions you make because of the influence it has over you.  The thing is no-one wants to blog about daily maven usage when you get more social engagement and karma from writing about SBT internals.

![More Betterness](/images/morebetterness/morebetterness.039.png)

And this brings me on to the team.

When I say team I mean a proper team not a group of people thrown together because the resourcing pipeline spat out a few available candidates.  Some may have started out that way of course.  I mean a group of people that have come together under a common cause. A team of people, not resources.

The great thing about a good team is that it normalises everything.  Biases balance out, influences are moderated and everyone works together to form the truest view of the given problem context.  The team establish context, the team make decisions.  

A good team will do wonderful things. A bad team can lay the groundwork for years of pain.  A good team will coalesce on an ideal.  A bad team will bicker.  A good team will make informed decisions quickly.  A bad team will flounder.  A bad team will cost you reputation and money.  A good team will address problems upfront and openly.  A bad team will let things fester until peer review time.  A good team is diverse as it naturally allows for a broader range of thinking.  A bad team is a monoculture.

![More Betterness](/images/morebetterness/morebetterness.041.png)

So remember this.

Your team is your (work) family and as such you should love your team.  Give a damn about the people and leave your ego at the door.

![More Betterness](/images/morebetterness/morebetterness.043.png)

And of course I wasn't quoting some horrible Harry Potter erotic fanfic describing some carnal act between Harry Potter characters.  Snape needed medical attention.  Shame really.

The right context really changes the way a mind thinks.
