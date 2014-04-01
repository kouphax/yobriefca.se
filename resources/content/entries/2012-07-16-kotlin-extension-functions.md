---
date: 2012-07-15T23:00:00Z
title: 'Kotlin: Extension Functions'
published: true
categories: [Kotlin]
type: article
external: false
---
In any language (that supports the concept) extension functions are nothing more than syntactic sugar.  Instead of writing,

	StringUtils.encrypt(mystring, "salt")
	
Extension functions allow you to write,

	mystring.encrypt("salt")

Once you start moving into the world of chaining things get much cleaner.

	encrypted = StringUtils.encrypt(mystring, "salt")
	encoded = StringUtils.encode(encrypted, "UTF-8")
	
or worse,

	StringUtils.encode(StringUtils.encrypt(mystring, "salt"), "UTF-8")
	
I've seen this done a few times, it happens.  But with sugary sweet extension functions (Extension Methods in C#, Implicit Conversions in Scala) you could just do this,

	mystring.encrypt("salt").encode("UTF-8")

Much nicer.

Kotlin, unlike crusty old Java, supports extension functions.  Here is how you'd implement the methods above

```java
fun String.encrypt(salt: String): String {
   return StringBuffer(this).append(salt)?.reverse().toString()
}

fun String.encode(encoding: String) : ByteArray {
    return this.getBytes(encoding)
}
```

And we can use these, as you'd expect, like so,

```java
fun main(args: Array<String>) {
    val mystring = "james"
    val encoded = mystring.encrypt("salt").encode("UTF-8")
}
```

So what identified those functions as Extension Functions?  Simple - the prefixing of the method name with the type `String.encode`.  No need to create implicit wrappers (Scala), no need to create loads of static classes (C#).  And yes it's possible to use generics in these methods as well,

```java
fun <T> T?.getOrElse(t: T) : T {
    return this ?: t
}
```

This can be applied to any nullable type.

## Literal Extension Functions

An interesting little feature that Kotlin has that makes it a bit different is the use of function literal extension functions.  Basically where you can declare a function as a literal (a lambda method if you will) you can also make that literal an extension function,

So we could take our methods we wrote above and bring them right into our `main`

```java
fun main(args: Array<String>) {
    val encrypt = { String.(salt: String) -> StringBuffer(this).append(salt)?.reverse().toString()}
    val encode = { String.(encoding: String) -> this.getBytes(encoding) }
    
    val mystring = "james"
    val encoded = mystring.encrypt("salt").encode("UTF-8")
}
```
	
This may seem quite unnecessary at first glance but it become quite powerful when creating DSLs - as demonstrated in the docs around the [Groovy Style Builders](http://confluence.jetbrains.net/display/Kotlin/Type-safe+Groovy-style+builders)

So Kotlin has extension functions that don't require a load of boilerplate (C#) or rather odd implicit syntax (Scala) - cool, right?