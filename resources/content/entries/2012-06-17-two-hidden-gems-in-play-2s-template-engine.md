---
date: 2012-06-16T23:00:00Z
title: Two Hidden Gems in Play 2's Template Engine
published: true
categories: [Scala]
type: article
external: false
---
Poking around some [Play 2](http://www.playframework.org/) [samples](http://www.playframework.org/documentation/2.0.1/Samples) today I came across some little bits of sugar.  I haven't seen mention of them in other places (they must be well hidden) but they are two very common things I've implemented in less elegant ways in the past.  That was reason enough for me to throw a quick post up about them.

Prepare to have your minds blown.  Well not blown - gently nudged probably.

## String.when

`"somestring".when(predicate: => Boolean)` allows you conditionally spit out the value of the string it is called on (otherwise returning and empty string).

```scala 
"returned_string".when(true)      // returns "returned_string"
"nonreturned_string".when(false)  // returns "" (empty string)
```

So in the context of views what is this good for?  Lots of things but the most clever use for this little function is conditional css.  Imagine you've got a block of HTML that represents a message area. These messages can be either good or bad and so we need to add the css class of `message-bad` depending on an argument passed in to the view.  A very quick and dirty way of handling this would be as such

```scala 
@(message: String, successful: Boolean)

<div class='message @if(!successful){ "message-bad" }'>
  @message
</div>
```

Grand, fine.  It's a bit noisy but it works. `when()` makes this a bit neater IMHO.

```scala 
@(message: String, successful: Boolean)

<div class='message @("message-bad".when(!successful))'>
  @message
</div>
```

Yep the line is the same length but it looks more like a single block of conditional code, it flows much better and, I think at least, is much more readable.

## Date.format("&lt;date-format>")

`new java.util.Date().format(pattern: String)` allows you to specify how a date should be formatted in the rendered HTML.  It's essentially shorthand for the noise-fest that is `SimpleDateFormat`

```scala 
new java.text.SimpleDateFormat(pattern).format(date)
```

In the past I've added transient properties to models for displaying the date, or written my own methods on those models.  Both of those approaches should have had me taken out into a field and shot.  `format` makes things much more straightforward,

```scala 
@(importantDate: java.util.Date)

<p>
  @importantDate.format("dd-MM-yyyy")
</p>
```

And the output will be along the lines of "29-01-2013" or whatever the date was.

OK so neither of these are ground breaking or earth shattering (wait they mean the same thing really dont they???) but they are neat to enough to warrant a shout-out here.  Anyone else have any little nuggets in Play 2 they want to share?