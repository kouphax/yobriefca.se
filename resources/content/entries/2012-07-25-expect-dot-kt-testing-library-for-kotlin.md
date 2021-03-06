---
date: 2012-07-24T23:00:00Z
title: expect.kt - Testing Library for Kotlin
published: true
categories: [Kotlin]
type: article
external: false
---
In a bid to delve into the [language](http://kotlin.jetbrains.org/) a bit further than a rake of tiny console apps I decided to do something "real" with Kotlin.  With that in mind - welcome [expect.kt](https://github.com/kouphax/expect.kt).

It started purely as a learning venture but I think there is a certain usefulness in the library so its probably worth sharing :).  Now it's worth noting that kotlin already has a number of testing statements that you can achieve a lot of this stuff with - this library gives you a few things over and above these statements 

1. Alternative syntax.  Testing is quite a personal thing and some people like to write things in different ways.
2. Descriptive assertions.  Some would say `expect(2).toBePositive()` reads better than `assertTrue(2 > 0)` or `expect(2 > 0) { true }`.  Again its all about taste, and variety is the spice of life.
3. Chained assertions.  See later

Rather than some campaign to replace the inbuilt expectations and assertions this library intended to complement them.

## So what is expect.kt?

Expect.kt is a set of (growing) assertions/expectations that you can use in your kotlin based tests.  It gives you a number of ways to assert your expectations and gives you quite descriptive methods in which to do it.

### Expect Syntax

The  expect syntax is the default way to assert things,

```java 
package tests

import org.junit.Test as test
import kotlin.expectations.*

class StringExpectationTests {
    test fun testHelloWorld () {
		expect("Hello World").toStartWith("Hell")
	}
}
```

Most of that is boilerplate JUnit testy things but the key line is

```java 
expect("Hello World").toStartWith("Hell")
```

Reads quite nicely IMHO.

###Should Syntax

The should syntax offers a more fluent syntax if you want to call it that, if we wanted to write the same test above using the should syntax we could do this,

```java 
"Hello World".should.startWith("Hell")
```

So rather than wrap your target with an expect you get and extension function to fluently assert it.

###Chaining

Sometimes making a single assertion on a result isn't enough and rather than write `assertTrue(...)` multiple times you can use extpect.kt's chaining support.

```java 
expect("Hello World").toStartWith("Hell").and.toEndWith("rld")
```

Or, with the should syntax,

```java 
"Hello World".should.startWith("Hell").and.endWith("rld")
```

It's a bit more readable than expect after expect, right?

So thats it for now - there are a number of assertions implemented already on the useful types (Strings, Ints, Doubles, Dates, Booleans for example) and there are more planned on the way.  Currently you'll be wanting to build from source (or copy/paste what you need).

Finally - A shout out to .NETs [FluentAssertions](http://fluentassertions.codeplex.com/) where a lot of the assertions found their origin!

[expect.kt](https://github.com/kouphax/expect.kt).