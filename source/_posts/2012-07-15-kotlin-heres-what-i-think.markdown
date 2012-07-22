---
layout: post
title: "Kotlin: Here's What I Think (For Now)"
date: 2012-07-15 16:43
comments: true
categories: ["Kotlin", "Miscellaneous"]
---

> Before I go shooting my mouth off here - a word of warning - I haven't really done anything with Kotlin.  I've read the docs, played with the [Kotlin 101 samples](https://github.com/dodyg/Kotlin101) and written a few small console type apps.  So take what you want from this post.

Want to hear something zany?  There actually are people - real living people, with a fully working brain, many much smarter than me - that are "happy" with Java.  

Seriously.

I made a move from Java to .NET about 5 years ago and have dipped back in a few times over that period.  Trying new languages is a sure fire way, for some at least, to make you realise Java is not a language that makes coding fun, or productive.  Java put me right of the JVM.  Any language that makes woking with collections (something you'll do alot) so long winded and noisy deserves to be shunned.  But this isn't a Java rant - the JVM is great but it's just marketed badly because of Java.

But all the JVM-hate that Java instilled in me 5 years ago; the last 6 months of working with Scala has replaced with love.  So I've been looking around the JVM world and discovered [Kotlin](http://kotlin.jetbrains.org/) a new JVM based language from JetBrains.  Its much closer to Java than what Scala or Clojure is (intentionally).

One main thing that Kotlin brings to the table is pragmatism.  Without trying to belittle Kotlin - it's almost like CoffeeScript for Java (yep kind of like what [Xtend](http://www.eclipse.org/xtend/) is.  Kotlin removes all those annoying necessities in Java that make your code verbose (checked exceptions, semi-colons, null reference checks etc.).  It also adds things that make your life much easier (extension methods, functional collection manipulation etc.).  

When you write Ruby for the first time you notice that many things just work out like you'd expect, the syntax is quite natural.  The same cannot be said for Java - there is just way too much boilerplate.  Kotlin helps reduce that boilerplate.  So, by extension, it could be said that Kotlin makes you more productive.  A person who knows Java will find Kotlin no challenge to start using - the syntax is nicely aligned, just without the noise.

Many people, when trying to get their team to adopt Scala, use the "you can use Scala like Java til you get used to it" mantra.  I don't agree with that.  Academically it's true but the minute you start working with other peoples Scala code - you've just gotta know Scala and when they have a slew of Implicits thrown in your Scala as Java stuff goes out the window.  Kotlin, on the other hand, has some of the nice functional patterns from Scala but none of the "magic-to-a-newcomer" features that can make grown men weep.  Now the Kotlin docs themselves even go as far as stating 

> 	If you are happy with Scala, you probably don't need Kotlin.

But I think they are selling themselves short - I think plenty of people use Scala but could easily use Kotlin in its place and find themselves in a better place (easier upskilling, less cryptic codebase).

Anyway this is a bit of a ramble, what I'm saying is - Kotlin has some really rather nice features (Groovy Like DSL Builders, Pattern Matching, the `when` expression, lambdas, Kotlin to JavaScript compilation(!) etc.) and people happy with either Java OR Scala should give it a go.
