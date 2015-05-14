---
date: 2015-05-13T00:00:00Z
title: "The Builder Pattern"
published: true
categories: [Craftsmanship]
type: article
external: false
---

<style>
  body {
    background-color: #253239;
    color: #cfd8dc;
  }

  .row h1 {
    display:none;
  }

  .homer a {
    color: #cfd8dc;
  }

  blockquote, p code, li code {
    background-color: #314249;
    color: #cfd8dc;
    border: none;
  }
</style>

> This article is adapted from a presentation I gave on the Builder Pattern for Deloitte Digital on Friday the 15th May 2015.

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.001.png)

Lets talk a bit about the Builder Pattern.  But why the builder pattern in particular?  Well being a bit of a technical butterfly jumping around from language to language, technology to technology, there are a surprisingly small amount of patterns that port well.  So I tend to fall __into__ patterns when writing code more than actually thinking "oh this could be solved using the XYZ pattern to decouple the doo-hickey from the weehoo".  The builder pattern, however, I've always found to be very portable across technology and language domains.  It may not exist in most domains to solve the original problem it was created for but it is really rather versatile and can be used to create cleaner code.  It is also a very applicable when constructing representations of your data such as HTML, XML, JSON etc. and this is an area you're unlikely to avoid in any project regardless of technology or language.

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.002.png)

Originally the builder pattern was created to find a solution to the problem of the telescoping constructor anti-pattern but it has grown beyond this use over time.  That said defining the original scope of the problem will help us understand the builder pattern better.

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.003.png)

Here we have the definition of `Reading` class.  It represents some sort of discrete reading from a sensor or something similar.  It has a number of immutable properties and a single constructor for creating an instance of a `Reading`.

Now lets imagine all of these `Reading` properties are optional and can be defined with default values.  So we can begin by filling in constructors to support easy construction of this class with defaults.

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.004.png)

With this constructor we call the previous "full" constructor passing a default value in. This is the first link in a chain of constructor calls.

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.005.png)

Our next constructor calls the previous one passing a default date which results in that constructor calling its previous one passing in it's default value as well.  We now have our second link.

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.006.png)

And so this continues on for a while as we gradually create a longer chain of constructor calls

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.007.png)

Successively adding another constructor to the chain of constructors.

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.008.png)

You'll notice that only a single default value is passed to the next constructor so we aren't duplicating construction logic and default values.

If we zoom out and look at an outline of our class constructors we can get a better picture of what has happened.

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.009.png)

If you've ever owned or just seen a collapsible telescope you can see where the telescoping constructor anti-pattern gets its name.

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.010.png)

So at this point we have a few minor problems

1. We have taken a simple class and filled it full of boilerplate.
2. We have coupled all the constructors into a chain which makes the class rather brittle which can make refactoring slow and prone to error.
3. We've scattered our default values across the class.  While each value only has a single point of definition the declarations are decentralised.

If you think about it for a while you'll likely see a few simple ways to remove these issues without having to resort to nothing more than a bit of tweaking and refactoring of the current solution.

But lets consider some other scenarios.

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.011.png)

So what happens when we want to support constructing a `Reading` and we only want to set the `time` to a non-default value?

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.012.png)

Well that was fairly easy - but we have created __yet another constructor__.  Not the end of the world but we have diverted slightly from our original constructor chain creating another branch of behaviours to test.  We've also duplicated default value logic across more than one constructor. Again not the end of the world and we could pull the values out into constants but we've certainly made everything less clean.

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.013.png)

What happens then if we want to introduce a constructor that only sets the `sourceId` property? Now we have a problem,

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.015.png)

We already have a constructor that takes a single `int` for `id`. Working around this may require exposing the knowledge of default values outside the class and have the constructing object know to pass in `-1` to the 2 arg constructor but then we have externalised coupling and what's the point of having a nice encapsulated OO design if you end up doing something like this?

> Those among you who use __sensible__ languages that have features such as default parameter values and copy constructors will find this isn't as much of a problem but even in those languages its possible to tie yourself up in knots.

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.016.png)

<div class="vimeo"><iframe src="https://player.vimeo.com/video/127740033" width="700" height="439" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe></div>

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.017.png)

As I originally mentioned the builder pattern is remarkably versatile and isn't just used to solve this one anti-pattern.  There are a number of benefits to be gained by applying the builder pattern to code.

1. In many situations you achieve more concise, expressive code which can aid readability and maintenance of the code
2. You can use it to compose complex objects without having to fully understand how the object is constructed internally
3. You can optimise object creation.   The builder pattern can be both immediate and lazy (performing the building steps only when needed) which allows you to defer potentially costly steps to the last minute, or pipeline and batch certain steps where it gives a benefit.

To demonstrate this lets take a look at a method for building a customer order.

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.018.png)

Here we have a fairly procedural view of creating an order, adding some line items and marking it for priority shipping.

There is a lot going on and it's up to this method to know exactly how all these bits and pieces fit together.  Some things could go wrong,

1. We may forget to call `.addLine()` for an item creating a bug
2. `Product.find` could be an expensive operation and we are doing it a few times.

An alternative, employing the builder pattern, could look similar to this,

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.019.png)

There is much less code here, its subjectively easier to read and understand and there are a few more benefits.

1. You don't need to understand exactly how line items are added and you can't accidentally miss out an `addLine` call.
2. We don't call `Product.find` ourselves, instead the builder knows to call it and it may be possible for the builder to selectively batch these calls to reduce the overhead.

One more very common use of the builder pattern is through the construction of representations of our data and communication across domains.

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.020.png)

To demonstrate this overly wordy phrase lets take a look at a few examples.

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.021.png)

Here is one example of a Java DSL that is used to construct a SQL query.  The fluent syntax gives you a consistent language to express your intent and, once complete, the builder can take all the knowledge and convert it into a SQL dialect.  A huge benefit to this approach is that it removes the need to manually guard against SQL injection attacks.

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.022.png)

Another example, this time form Scala, uses the builder pattern, albeit rather minimally to allow us to build a JSON representation of a piece of data.

Although it's not wise to end on a low note it is worth mentioning that as with all things in this world the builder pattern has its own share of issues.

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.023.png)

The 2 biggest causes for concern are,

1. Using the builder pattern to create a DSL such as the SQL example above can result in rather complex builder code.  Typically builders have their fair share of boilerplate to support their API, however, this is an acceptable trade off because we've can reduce boilerplate and complexity in our __business logic__ but edge cases can a desire to make the perfect DSL can cause complexity creeep. Ensuring you're not generating unnecessary complexity is important when creating a builder.
2. As we've witnessed in our very first example (Lomboks builder didn’t set the default values for our optional fields) it is sometimes possible to use a builder and yet create "incomplete" objects missing values that need to be set. If your builder is part of a library that is used across projects then this could cause frustration.  Documentation and correct handling of invalid states is essential.

![The Builder Pattern](/images/thebuilderpattern/thebuilderpattern.001.png)

And that is the builder pattern in a nutshell.  It is very likely you'll have already come across uses of the builder pattern in the wild.  It is a very simple and flexible pattern but should, as with all things, be treated with respect.
