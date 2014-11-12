---
date: 2014-10-09T00:00:00Z
title: "Preflight Checks"
published: true
categories: [Craftsmanship]
type: article
external: false
---

What I'm about to describe is in no way a new thing, you'll find evidence of this sort of pattern everywhere.  I've noticed some people do it instinctively and others simply fall into the trap that this sort of technique/pattern tries to avoid.  Therefore I think its worth a bit of a mention.

## An Example in a Different Context

Lets explore the problem in a slightly different context - dynamic vs static languages.  Imagine the given scenario in JavaScript

```javascript
function longest(a, b){
  return a.length < b.length ? b : a;
}

longest("aa", "b");  // "aa"
longest("a", "bc");  // "bc"
longest(1, "bc");    // 1
```

We've defined a function called `longest` that returns the longest string argument between the two passed to it.  We also defined a couple of sample uses of the function,

1. `"aa"` is longer than `"b"` so `"aa"` gets returned
2. `"bc"` is longer than `"a"` so `"bc"` gets returned
3. __Here be dragons!__

In the last example our return value isn't what we expected.  At best we'd expect `"bc"`, with a length of 2, to be longer than `1`, whose string value would be `"1"` and therefore have a length of 1.  But that isn't the case.  Numbers don't have a `length` property and therefore the result of `1.length` is `undefined`.  Comparing `undefined` to 2 is going to yield false, hence our unexpected return value.

Lets look at this problem in another language, one that is statically typed - TypeScript,

```javascript
function longest(a: string, b: string) {
  return a.length < b.length ? b : a;
}

longest("aa", "b");
longest("a", "bb");
longest(1, "bb");
```

Trying to compile this sample will fail on the last line because a number isn't a string.  This happens long before you get to run your code.

## The Problem, Solution and Result

So what's the problem?  Well in the dynamic example we won't know about the issue until such times as the application gets into a certain state and the manifest problem may happen elsewhere far far down the call chain causing great confusion and frustration.

Solutions to this problem are available.

- In static languages we actually have to bend the type system to encounter such problems because we have type checking and compilation up front providing a certain level of application correctness.
- In dynamic languages we avoid these issues by writing tests that (should) ensure correctness of our application before we run them.

Ultimately being able to know something is wrong __as early as possible__ is essential to making releases pain free and predictable.  In turn, being able to release with confidence allows you to release more often and tighten those vital product feedback loops.

## The Problem at a Higher Level

Now lets move away from code and compiler level concerns and think about your application as a deployable thing.  Your app will no doubt have dependencies on external services, datastores, configuration or other applications within the same host.  All of these things will cause your app to go into some form of failure state should they be unavailable (lets leave aside the fact that services can go down during the course of a running application, thats a different matter entirely).  The issue may not be immediately obvious either - a misconfigured application that you call may only present errors for a given set of requirements and finding out whats wrong in a production or other similarly closed off environment can be a a test of any ones patience.

Being able to detect, as early as possible; like we do with a compiler or test suite, if our applications environment is in a valid state can save a lot of pain further down the line.

Some of you may be ready to tell me that your favourite provisioning tool (Puppet, Chef, Ansible, Salt etc.) gives you certain guarantees already and that is true to a certain extent but they are often not a sure fire guarantee.

1. Nothing is perfect and sometimes even the most tested tools behave differently
2. You may not have it set up correctly to deal with certain unique states of deployment boxes
3. You may have ultra-secure environments that you can't even run these tools on without a lot of pain and so manual provisioning is necessary.

Additionally if you are following any of the points in the [Twelve Factor App Methodology](http://12factor.net/) you'll notice you are relying more on the hosting environment and this can amplify the problem.

## The Preflight Check

The pre-flight check pattern aims to address these problems by ensuring invalid states in application dependencies fail deployment in a way that lights a flare above the problem so it can be fixed.

A few things led me to develop this habit/pattern

1. I was deploying smaller services to Heroku that promotes [pushing config down to the OS](http://12factor.net/config)
2. I was a standard Play! stack which often has less-than-helpful error messages when things go wrong during startup
3. I was provisioning things using vagrant and puppet/chef/ansible which, during the early stages, I often forgot to update as the applications needs evolved.

Ultimately I got bored of opaque stacktraces and bizarre behaviour in odd edge cases and so attempted to ensure my hosting environment was in good shape before we "took off" so to speak.

In short a pre-flight check is code that runs when your application starts, asserts the state of the environment or external services/applications.  If something fails the code will halt startup with a meaningful message that can be used to quickly diagnose the problem.

In Play! this took the form of a hook in `Global.scala`

```scala
object Global extends GlobalSettings {
  override def onStart(app: Application) {
    performPreflightChecks()
  }

  def performPreflightChecks() {
    Option(System.getenv("DATABASE_URL")).getOrElse {
        Logger.error("[PREFLIGHT_CHECK] DATABASE_URL is not set")
        throw new Exception("[PREFLIGHT_CHECK] DATABASE_URL is not set")
    }
  }
}
```

This is an exceptionally primitive example (that I've written without the aid of a compiler so it may be incorrect) - in fact as the pattern developed I eventually extracted the preflight check pattern into it's own plugin that performed a number of things,

1. Ran a number of checks (classes that extended `Runnable`) within a configurable namespace.
2. Logged a report of checks that passed and failed (regardless of if everything passed or not)
3. Exposed a method that can be called in tests - where deployment environments also supported running test directly.

Some of the checks I've used have been,

- Ensuring ImageMagick is correctly installed with the correct codecs (difficult to get right even if you use a provisioning tool I've found)
- Ensuring System environment variables are set correctly
- Database connectivity
- OpenCV JNI bindings available

Of course, this is in no way limited to Play! or Heroku, I've used this across a number of technologies (Clojure, Ruby, Dropwizard etc.) and environments (locally, vagrant, AWS, Rackspace, Heroku etc.) and the time tackling silly oversights has been greatly reduced.

Many technologies already have the capacity to perform preflight checks in a way that integrates with your ops story (Dropwizards healthchecks are a perfect example) but preflight checks would mandate running these checks as the application starts as well.

In short, regardless of technology or platform, it is essential that deployments validate application and hosting environment state before going live.  The preflight check pattern aims to address this issue by ensuring you map out all you dependencies of your intended host irrespective of where that may be.
