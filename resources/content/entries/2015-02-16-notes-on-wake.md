---
date: 2015-02-16T00:00:00Z
title: "Notes on Wake"
published: true
categories: [Wake]
type: article
external: false
---

I've spent the morning tinkering with [Wake](http://www.wakelang.com/) because I had time and it popped up on Twitter at the right time.

It's an interesting language that aims to be

> _a fast, expressive, type safe language that gives you testability from the ground up._

What follows is a write up of my thoughts from the short time I've spent with [Wake](http://www.wakelang.com/).

## Baked in Dependency Injection

I have plenty of experience in Java and have seen my fair share of codebases that make extensive use of dependency injection.  The verbosity of Java has created many a monster.  `Providers`, `Factories`, `FactoryProviders` and `ProviderFactoryProviders` fill project hierarchies all in an attempt to make the language bend to our will.

The main problem, aside from the obvious verbosity of Java, is its lack of syntax to support dependency injection.  Vast technologies have been built around this simple concept alone.  However without the language level support you are forced to go for either wizardry (see `@Inject`) or verbosity (through additional annotations) - neither of which are [desirable](http://yobriefca.se/blog/2014/08/25/invasion-of-privacy/).

One of [Wake](http://www.wakelang.com/)s fundamental features is the use of [provisions](http://wakelang.com/#provisions) which is essentially a means to bind dependencies to particular behaviour.  Classes define exactly what they `need` and `provide` in order to get the job done.

```java
import Printer;

every Main is:

  needs Printer out;

  main() {
    out.printLine("Hello");
  }
```

In the example above we can see that Main explicitly `needs` `Printer`. When the application is executed an instance of `Printer` will be injected into our `Main`.  In this case Wake knows how to inject `Printer` as it is from the standard library.  We can also `provide` our own dependencies including any rules required to construct them.

```java
import Printer;

every Main is:

  needs
    Printer;

  provides
    Person <- Person(Printer, ?Text),
    Printer;

  main() {
    (Person("James") from this).sayHi("Internet");
  }

every Person is:

  needs
    Printer out,
    Text    me;

  sayHi(Text otherPerson) {
    out.printLine("Hello " + otherPerson + " I'm " + me + "!");
  }
```

There is a bit more code here so let me break down some of the important lines.

- We have a `Person` class.  This class `needs` both a `Printer` and some `Text` that represents the persons name.
- The `Person` class has a `void` method that takes a single `Text` argument and uses the injected `Printer` to print a greeting.
- The `Main` class, as before, `needs` a `Printer`
- This time the `Main` class also `provides` a few things.  It `provides` a `Printer` which I will expand on shortly and it also `provides` a `Person` type.  More so it specifies a way to construct a `Person` instance without having to specify a `Printer` each time - `Person(Printer, ?Text)`.  In essence this is saying _when Main is asked to provide and instance of Person always use this particular `Printer` instance and take a `Text` argument_
- Our `Main` class is called via the `main()` entry point and at this point creates a new `Person` instance using the `Person` provider defined in `Main` (hence the use of `this` - `(Person("James") from this)`)
- We call `sayHi` on this provided instance.

It may seem initially weird for an object to define its own `needs` and `provides` but this need not be the case.  It is possible to create discrete provider classes that encapsulate construction behaviour which can be injected into our target class.  So instead of `from this` we could say, for instance, `from PersonProvider`.

This syntax allows us to create defined contracts for our classes without a lot of extra verbosity that could mask intent.   It also gives us certain guarantees around type safety and compile time checking the our object graphs are correctly defined.  Finally, as an added bonus, this syntax gives us a rather clean way of wiring up stubs and mocks during testing by simply swapping out our providers with stubbed providers.

## Testing

On the subject of testing I was a little surprised to read _"gives you testability from the ground up"_ and to not see any examples of how to write tests.  Of course the language is still quite young and things are changing.  After a bit of poking around I discovered tests are annotation based,

```java
import Asserts;

@TestClass
every MyFirstTest is:

  @Test
  itIsWhatItIs(Asserts) {
    var Text = "test";
    Asserts.that(Text)Equals("test");
  }
```

Running `make` will run all the discovered tests in the `tests/` folder.  Fairly straightforward.

## Other Nuances

There are few things in Wake that exist in an attempt to make code more concise, more readable and potentially drive design down a particular route.

### Naming Variables

Most variables do not need to be given a name.  Our `Person` class above could be written in a more concise manner,

```java
every Person is:

  needs Printer, Text;

  sayHi($Text) {
    Printer.printLine("Hello " + $Text + " I'm " + Text + "!");
  }
```

- We have one instance of `Printer` so we don't need to give it a variable name and can refer to it directly by its type.
- The same applies for `Text` however as we have 2 `Text` instances we can make use of the `$` symbol to avoid [conflicts](http://www.wakelang.com/#shadowing).

The argument here is that the type of a variable is often the most meaningful name.  This is easily a very contentious point which makes sense in some situations but not necessarily in others.  Thankfully its possible to give variables names which I  did in the first instance in order to avoid introducing too many new concepts in one go.  This approach can take you down the "thinking in types" path where you make the types more rich rather than just place holders for rich data.

### Method Names

Like in Swift and Objective-C, Wake methods names don't need to follow the <name>(<args>) structure.  Arguments can in fact be spread throughout the name.  While it may seem odd at first it can make complex methods a bit clearer.

```java
sayHelloTo(Text)As($Text) {
  Printer.printLine("Hello " + Text + " I'm " + $Text + "!");
}

main() {
  sayHelloTo("Emma")As("James");
}
```

## Wrapping Up

I've spent more time putting this article together than I have spent with the language but all in all there are some compelling features in [Wake](http://www.wakelang.com/).

The contract based approach of `needs` and `provides` makes the structure of your application very clear and aids testing

The ability to reduce the volume of code by eliminating unnecessary cruft (variable names for example), __where it fits__, goes a step further to allowing us to create more maintainable code.

As of right now the documentation has some holes in it (for example around testing) and the language is still very young but I think its a language worth keeping an eye on.

I've only scratched the surface of the language and its future plans so I recommend, if this is the first time you're reading about Wake, you check out the site for more information - http://www.wakelang.com/

