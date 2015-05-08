---
date: 2015-05-08T00:00:00Z
title: "Less Java with Lombok"
published: true
categories: [Java]
type: article
external: false
---

So Java, you've heard of it right?  It's the language thats used to [convert XML into stack traces](https://twitter.com/avalanche123/status/7062890318143488).  You know the one... its [really popular](http://www.tiobe.com/index.php/content/paperinfo/tpci/index.html) and has a massive ecosystem of powerful open source tools and techs but everyone says its the worst.  Sadly making fun of Java lacks any real challenge because, if I can be blunt, Java makes it easy to do so (see I just did it without thinking).

There are a lot of things that make Java an easy target but in my frequent exposure to Java there are 3 things that really wind me up.

1. Type erasure & Non-reified generics lead to a whole host of frustrating problems when you're trying to build nice clean composable solutions,
2. Following on from #1 the type system in general is a real pain.  It's neither powerful enough to use it in any real meaningful way nor is it loose enough to allow dynamic typing. It's basically an annoying landlord who happens to live with you, in your bed, and likes farting and being the little spoon.
3. Finally the sheer verbosity of the whole thing.  When 90% of your code is boilerplate classes and methods required to work around limitations in the language you really need to work to make the simplest of code expressive and maintainable.

But I've already said it is popular and interestingly its popularity has been growing.  Probably in part due to the power of the JVM and the huge powerful sea of tooling and tech that exist for it.  That said I have a theory that it also acts as a sort of natural constraint that in some ways help prevent language astronauts code golf functionality into an unreadable one line functional expression that looks like a computer novice tried to quit Vim.  This is all a personal theory formed after reviewing a fairly substantial amount of Scala code, YMMV.

Any way... Without switching to another language there is very little we can do about #1 & #2 so at the very least addressing #3 could make your time in Java more tolerable.  Java 8 added a few features that can improve the expressiveness of your code including [Functional Interfaces](https://leanpub.com/whatsnewinjava8/read#leanpub-auto-functional-interfaces) and [Default Methods](https://leanpub.com/whatsnewinjava8/read#leanpub-auto-default-methods) but by and large we are still producing a large volume of boilerplate.  These are the challenges with having a great backward compatibility story.

## I put on my robe and wizard hat

Fear ye not you beautifully desperate people there are things you can do. A word of caution first - there is a certain amount of "magic" coming up.  Yes magic is a bit dodgy because it requires wizards to control but there are levels of magic and a lot of it is just quick slight of hand.  Once you're shown the reveal you can't unsee it.  Admittedly I was a skeptic of what I'm about to write about but after spending a bit of time with it I approve of considered and careful wielding of this magic.

## Lombok

So lets remove the boilerplate from Java without having to rewrite our solutions in a new language.  To do this we are going to use [Lombok](https://projectlombok.org/).  Now the website is very light on explanation but [Lombok](https://projectlombok.org/) is an annotation based code generation library that focuses on removing the noise and boilerplate from your code without the need for actual authentigenuine magic.  So while it may feel like magic being released from the shackles of Javas boilerplate oppression it is just a bunch of common patterns rolled into annotations.  Oh and yes, if you've read any of my older posts, annotations are a pet hate of mine so you'll understand I'm not saying any of this lightly.

So whats the elevator pitch for Lombok (after I've spent over 500 words meandering over nonsense)?  OK lets go.  

Assume we have a `Character` (in the RPG sense not the textual sense) class that has just 2 properties - a `name` and a `level`.  Now the `name` is set once and never changes and the `level` fluctuates as this character kills dragons, marries trolls and harvests magic mushrooms (usually it goes up but you can never really tell with magic mushrooms).

Got it?  Good.

This `Character` class is, and you'll forgive me for using such a stupid term - a DTO.  It's a bag of state that gets passed around your application.  This happens A LOT in Java applications because of a crappy type system.  If you know Java well enough you'll understand that a big-bag-o-state/DTO needs decent `toString`, `equals` and `hashCode` implementations because that thing is going to get logged and compared like a demon (surprisingly demons get compared and logged more than you'd think - I guess hell really is full of Java developers).  The implementation of such a class would look like this.... brace yourself.... POJO is coming...

```java
public class Character {
  private final String name;
  private int level;

  public Character(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public int getLevel() {
    return this.level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Character)) return false;
    final Character other = (Character) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$name = this.name;
    final Object other$name = other.name;
    if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
    if (this.level != other.level) return false;
    return true;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $name = this.name;
    result = result * PRIME + ($name == null ? 0 : $name.hashCode());
    result = result * PRIME + this.level;
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof Character;
  }

  public String toString() {
      return "se.yobriefca.tinkering.Character(name=" + this.name + ", level=" + this.level + ")";
  }
}
```

Now as a developer I care about maybe 2 or 3 of those __50__ lines of code. With Lombok you can do this...

```java
import lombok.Data;

@Data
public class Character {
  private final String name;
  private int level;
}
```

Oh look, squint a bit and we have a Scala `case class` or Kotlin `data class` but we are still writing the Javas.  Neat right<a name="_1"></a>[<sup>1</sup>](#1)?  One annotation, completely constrained to that class and we've eliminated a lot of boilerplate and sprinkled in some "best practise" (the `hashCode` and `toString` stuff is often sadly neglected by many developers).

But wait!  Maybe we want to support creating a `Character` with some sort of initial level as well as a name.  OK, here goes.

```java
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Character {
  private final String name;
  private int level;
}
```

What I've done is, via annotations, add both an `AllArgsConstructor` and a `RequiredArgsConstructor`.  The names are fairly self explanatory but we can now instantiate our instances like so,

```java
Character c1 = new Character("James", 50);
```

Or,

```java
Character c2 = new Character("James");
```

A HA! But what if we have a more complicated object with a plethora of required and optional fields?  Well Lombok also gives you an annotation that generates a fluent builder interface for your object.

So this,

```java
@Builder
public class Character {
  private final String name;
  private int level;
}
```

Gives you this,

```java
Character c3 = Character.builder()
  .level(50)
  .name("James")
  .build();
```

Of course you can mix and match these annotations to a certain extent to achieve exactly what you need to build your lovely little property bag.  As an unsurprising bonus this is all done in a consistent and predictable way that works very well with most 3rd party libraries such as serialisation libraries or data access libraries.

But wait - there's more - it's not all about them POJOs/DTOs/State Bags lets look at some of the interesting things you can also do with Lombok.

## Type Inference

Declaring variables in Java can be an exercise in repeating yourself multiple times.  For example,

```java
final ValueOutputterProvider<String> valueOutputterProvider = new ValueOutputterProvider<String>();
```

It's kind of obvious what this is because it says so three times which is not only wasteful and slow to type but its yet more unnecessary noise that reduces readability.  Lombok allows us to shorten this declaration making it look more like a Scala declaration

```java
val valueOutputterProvider = new ValueOutputterProvider<String>();
```

Not exactly earth shattering but it certainly reduces the noise we have to tune out to see the important code.

## @Log (inc. @Slf4j @Log4j etc.)

You know that line you write every time you create a new class?  You know the one that allows you to log things?

```java
public class WorkerThing {
  private static final Logger log = LoggerFactory.getLogger(WorkerThing);

  public void doWork(){
    log.info("Doing work");
  }
}
```

That one on the second line.  We can make it go away with Lombok,

```java
@Slf4j
public class WorkerThing {
  public void doWork(){
    log.info("Doing work");
  }
}
```

We've traded a single long line of declaration for a single annotation.  Clean.  There is an annotation for each of the common logging libraries.

## Cleanup

You know those pesky `try`/`finally` blocks that we wrap around resources to ensure we don't leak connections or keep streams open unnecessarily?

```java
DBConnection connection = new DBConnection();
try {
  connection.execute("DROP TABLE users");
} finally {
  connection.close();
}
```

If we are using Java 1.7 onwards we could use the [`try-with-resources`](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html) statement to make it cleaner,

```java
try(DBConnection connection = new DBConnection()) {
  connection.execute("DROP TABLE users");
}
```

But Lombok gives us another option - the `@Cleanup` annotation,

```java
@Cleanup DBConnection connection = new DBConnection();
connection.execute("DROP TABLE users");
```

The `@Cleanup` annotation generates the necessary boilerplate to ensure we are dutifully closing any resource we create even when something goes wrong.

## Experimental

Lombok has a bunch of features that are still tagged as __experimental__.  These features either have known issues or have yet to prove themselves thoroughly in the field of Java-ing.  I'll demonstrate one of them now - `@ExtensionMethod`.

I really like C# extension methods, Kotlins extension methods, Scalas extension methods (`implicit def`'s) and so on.  `@ExtensionMethod` gives you this feature for Java.

First we create a static suite of helper methods,

```java
public class StringExtensions {
  public static String makeMusical(String in) {
    return "¸¸♬·¯·♩¸¸♪·¯·♫¸¸"+ in + "¸¸♬·¯·♩¸¸♪·¯·♫¸¸";
  }
}
```

Then we annotate that class we want to utilise the new extension methods in,

```java
@ExtensionMethod({StringExtensions.class})
public class App {
  public static void main(String[] args) {
  }
}
```

Finally we call the static methods within this class as if they were instance methods of the type that the method accepts.

```java
  public static void main(String[] args) {
    System.out.println("EXTEEEEEENSION METHODSSSSSS".makeMusical());
  }
```

Under the hood the call simply gets translated to

```java
StringExtensions.makeMusical("EXTEEEEEENSION METHODSSSSSS")
```

Which gives us the rather pretty output of,

```
¸¸♬·¯·♩¸¸♪·¯·♫¸¸EXTEEEEEENSION METHODSSSSSS¸¸♬·¯·♩¸¸♪·¯·♫¸¸
```

In fact `@ExtensionMethod` can take any class with `static` methods and make them callable as instance methods such as `java.util.Arrays`.  

OK extension methods are just a nice bit of syntactic sugar but they can be really nice when creating a consistent approach to code style.

## Tooling Support

In short "yes there is".  Eclipse and IntelliJ both have Lombok plugins with powerful refactoring support (turn Lombok'd classes into their equivalent pure Java class and vice versa).  Some of the features, especially the experimental ones, have some little issues that can make editors complain, even though the project will compile and run fine, but it's no worse than some of the bizarre syntax issues you can get with Scala for example.

## Summing up

I've thrown a lot of code around and didn't cover all Lombok has to offer.  I focused on the features that I think are handy to use and I would certainly recommend you apply you're use of Lombok carefully.  As with all powerful things don't over do it just because you can.  A little goes a long way.

Finally I recommend you check out the [official Project Lombok site](https://projectlombok.org/), read the caveats on the various features that catch your eye and just give it a go yourself.

<hr/>
<sup><a name="1"></a>[1](#_1): There are some other things generated here such as the bizarre `java.beans.ConstructorProperties` annotation.  I pray I never need to know what that does.</sup>
