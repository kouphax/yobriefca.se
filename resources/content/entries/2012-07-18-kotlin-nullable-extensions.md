---
date: 2012-07-17T23:00:00Z
title: 'Kotlin: Nullable Extensions'
published: true
categories: [Kotlin]
type: article
external: false
---
Haskell doesn't have if statements - that blew my mind the first time my old Scala mentor told me it.  `if`s just don't work with a strong function approach.  Plus you know what - `if`s are rather ugly especially when you need to wrap ALL THE THINGS in a null check.  Scala has a type called `Option`.  An `Option` is essentially a collection of none or one instances of a type.  If everything returned an `Option` nothing would be null and you don't need to do any null checks (they are much more powerful than I am making out but for now this is all that is necessary).

Kotlin doesn't have the `Option` type but it does have two things that allow you to act like it does,

1. Nullable types - e.g. `String` vs `String?` , and,
2. Nullable Extension Functions

Remember the brief definition of an `Option` - it's a list of 0 or 1 instances of a particualr type and as such we can treat it a bit like a list.  Now once we get functional with our badselves this makes null handling unnecessary.  We can use all those nice little functional methods that you get with collections - map, forEach, filter, fold etc.

This means you're less inclined to use `if`s and, more importantly, less inclined to use mutable variables which is great for building performant, scalable soltions.

Lets take a really contrived example to show you what I mean.  OImagine this bit of code....

```java 
   val auth = authenticate(username, password)
   var view : View = null
   
   if (auth == null){
   		view = views.html.login("Invalid Username or Password")
   }else{
   		view = views.html.index(auth)
   }

   return view
```

We want to authenticate a user and return a certain view.  Now yes we could refactor this down into something more bitsize but this is a very common state to find code - so bare with me for demonstration purposes.

`authenticate` returns a user if the username and password was correct otherwise it returns null.  With our nullable extensions (by importing `kotlin.nullable.*`) we can reduce this into something more Scala-like,

```java 
return authenticate(username, password).map {
    views.html.index(it)
} ?: views.html.login("Invalid Username or Password")
```

So what we have done here is basically take an object (the user), if it exists, and transform it to another object which we return.  Otherwise we return something different.  This is much simpler and avoids having to create a mutable var (yes we could have avoided that in the other example but in many cases its very difficult to achieve this).  In fact I am not even a fan of the elvis operator there (`?:`) and have a Scala-like syntax [pull request](https://github.com/JetBrains/kotlin/pull/100) open for getOrElse.  A small syntactic change that I think allows you create a much neater, chainable block of code.

Thats only the tip of the iceberg on Nullable Extensions - those of you with a functional mind will be able to put these collection-like extensions to great use I reckon.  Not quite Scalas `Option` type but good enough at providing the common features for me.