---
date: 2011-12-27T00:00:00Z
title: Code Coverage and the Forces of Good
published: true
categories: [Craftsmanship]
type: article
external: false
---
Code coverage often does more harm than good.  It removes the focus from testing the expected behaviour of the code to just writing tests to make sure all your code is hit (gamifiying the process to its own detriment).  People will bend tests, often in a way that no typical use case could, just to make them run as they want.  This is unrealistic and paints a false picture of the behaviour of your code.

That being said - code coverage is a tool and it shouldn't be blamed for enabling bad habits (a bad workman always blames hi... well, you know the rest).  So can code coverage be applied in a less obvious way?  Indeed it can.

Business users invariably end up using things like [Google Analytics](http://google.com/analytics), [Gauges](http://gaug.es) or [Flurry](http://flurry.com) to discover the most valuable areas of the solution and tailor accordingly.  Makes perfect sense, right?  So why not use code coverage, during development, as a mechanism to identify redundant areas of code, or discover feature hotspots that could benefit from refactoring or performance tweaking?  Why not indeed?

As any system evolves there will be behaviours that simply don't need to exist to satisfy the overall behaviour of the system.  Some code and it's related tests will still hang around.  It happens and with the right tools you'll be able to see those areas - MightyMoose will show you hit counts for lines of code for example.  So by ensuring tests are reviewed as part of your code review process (or refactored during pairing) you'll also get reporting of redundant code or feature hotspots for free.

So if your project is already reporting on code coverage thanks to some great architectural mandate why not at least use it for something more positive?