---
date: 2012-04-01T23:00:00Z
title: Setting the Selected Option in a Select with Selenium in Scala
published: true
categories: [Scala, Testing]
type: article
external: false
---
Almost a perfect bit of alliteration there in that title - right?  Anyway I've been doing a bit of work with bare-metal Selenium stuff in Scala recently and one of the things that kept me stumped for a while was setting the selected `option` for a particular `select` element.

I looked around the interwebs and didn't really find anything - now I'm not saying it isn't out there, I am certain it is, but I couldn't find it.  So I decided to document how I did it in case any lone sole is stuck in a similar situation...

```scala
  driver.findElement(By.id("year")).
    findElements(By.tagName("option")).
    get(2).
    setSelected()
```

So here I am simply finding the second `option` of the "year" `select` element and setting it selected.  I know it looks obvious now but trust me I was scratching my head for an age on this one.

I hope it helps some one, eventually :)