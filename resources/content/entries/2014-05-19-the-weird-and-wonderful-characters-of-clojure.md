---
date: 2014-05-19T00:00:00Z
title: The Weird and Wonderful Characters of Clojure
published: true
categories: 
  - Clojure
type: article
external: false
---

> A reference collection of characters used in Clojure that are difficult to "google". Descriptions sourced from various blogs, [StackOverflow](http://stackoverflow.com/questions/tagged/clojure), [Learning Clojure](http://en.wikibooks.org/wiki/Learning_Clojure) and the [official Clojure docs](http://clojure.org/documentation) - sources attributed where necessary.  Use `CTRL-F` "Character: ..." to search.  Sections not in any particular order but related items are grouped for ease. If I'm wrong or missing anything worthy of inclusion tweet me [@kouphax](http://twitter.com/kouphax) or mail me at <james@yobriefca.se>.

<hr/>

### Character: `#`

You'll see this macro character beside another e.g. `#(` or `#"`. This topic will act as a bit preamble before looking at your specific case.  

`#` is the dispatch macro, a reader macro that tells the Clojure reader (the thing that take a file of Clojure text and parses it for consumption in the compiler) to go and look at another __read table__ for the definition of the next character - in essence this allows extending default reader behaviour.  

Clojure doesn't provide support for creating reader macros but it is possible through [a bit of hackery](http://briancarper.net/blog/449/).

- [Clojure Documentation - Reader](http://clojure.org/reader)
- [Clojure Reader Macros](http://briancarper.net/blog/449/)

<hr/>

### Character: `#{`

See the dispatch (`#`) macro for additional details.

`#{` defines a set (a collection of unique values) specifically a `hash-set`.  The following are equivalent,

```clojure
user=> #{1 2 3 4}
#{1 2 3 4}
user=> (hash-set 1 2 3 4)
#{1 2 3 4}
```

Attempting to create a `set` using this literal form will throw if there are duplicates.  Instead the `set` function should be used on a vector

```clojure
user=> #{1 2 3 4 1}

IllegalArgumentException Duplicate key: 1  clojure.lang.PersistentHashSet.createWithCheck (PersistentHashSet.java:68)
user=> (set [1 2 3 4 1]) ; convert vector to set, removing duplicates
#{1 2 3 4}
```

- [Clojure Documentation: Sets](http://clojure.org/data_structures#Data%20Structures-Sets)

<hr/>

### Character: `#"`

See the dispatch (`#`) macro for additional details.

`#"` indicates the start of a regular expression pattern.

```clojure
user=> (re-matches #"^test$" "test")
"test"
```

This form is compiled at _read time_ into a `java.util.regex.Pattern`.

- [Clojure Documentation: Regex Support](http://clojure.org/other_functions#Other%20Useful%20Functions%20and%20Macros-Regex Support)

<hr/>

### Character: `#(`

See the dispatch (`#`) macro for additional details.

`#(` begins the short hand syntax for an inline function definition.  The following 2 bits of code are similar,

```clojure
; anonymous function takin a single argument and printing it
(fn [line] (println line)) ; 

; anonymous function takin a single argument and printing it - shorthand
#(println %)
```

The macro expands the shorthand syntax into a function definition whose arity (the number of arguments it takes) is defined by how the `%` placeholders are declared. See the `%` character for discussion around arity.

```clojure
user=> (macroexpand `#(println %))
(fn* [arg] (clojure.core/println arg)) ; argument names shortened for clarity
```

<hr/>

### Character: `#'`

`#'` is the var quote. It is the same a the `var` method,

```clojure
user=> (def nine 9)
#'user/nine
user=> nine
9
user=> (var nine)
#'user/nine
user=> #'nine
#'user/nine
```

When used it will attempt to return the referenced var.  This is useful when you want to talk about the reference/declaration instead of the value it represents.  See the use of `meta` in the  metadata (`^`) discussion.

- [Clojure Official Documentation: Special Forms](http://clojure.org/special_forms#var)

<hr/>

### Character: `%`

`%` is not a macro but a placeholder for use in the `#(` macro.  It represents an argument that will be passed into the function when it is expanded. 

```clojure
user=> (macroexpand `#(println %))
(fn* [arg] (clojure.core/println arg)) ; takes a single arg, uses it once

user=> (macroexpand `#(println % %))
(fn* [arg] (clojure.core/println arg arg)) ; takes a single arg, uses it twice
```

Numbers can be placed directly after the `%` to indicate the arguments position. Numbers are also used by the `#(` macro to determine the number of arguments to pass in.

```clojure
user=> (macroexpand `#(println %1 %2))
(fn* [arg1 arg2] (clojure.core/println arg1 arg2)) ; takes 2 args

user=> (macroexpand `#(println %4))
(fn* [arg1 arg2 arg3 arg4] (clojure.core/println 4)) ; takes 4 args doesn't use 3
```

So you don't have to use the arguments but you do need to declare them in the order you'd expect an external caller to pass them in.  

`%` and `%1` can be used interchangably,

```clojure
user=> (macroexpand `#(println % %1)) ; use both % and %1
(fn* [arg1] (clojure.core/println arg1 arg1)) ; still only takes 1 argument 
```
<hr/>

### Character: `@`

`@` is the deref macro, it is the shorthand equivalent of the `deref` function so these 2 forms are the same,

```clojure
user=> (def x (atom 1))
#'user/x
user=> @x
1
user=> (deref x)
1
user=> 
```

`@` is used to get the current value of a reference.  The above example uses `@` to get the current value of an [atom](http://clojure.org/atoms) but `@` can be applied to other things such as `future`s, `delay`s, `promise`s etc. to force computation and potentially block.

<hr/>

### Character: `^`

`^` is the metadata marker.  Metadata is a map of values (with shorthand option) that can be attached to various forms in Clojure.  This provides extra information for these forms and can be used for documentation, compilation warnings, typehints and other features.

It can be represented as a map,

```clojure
user=> (def ^{ :debug true } five 5) ; meta map with singel boolena value
#'user/five
```

We can access the metadata by the `meta` method which should be executed against the declaration itself (rather than the returned value).

```clojure
user=> (def ^{ :debug true } five 5)
#'user/five
user=> (meta #'five)
{:ns #<Namespace user>, :name five, :column 1, :debug true, :line 1, :file "NO_SOURCE_PATH"}
```

As we have a single value here we can use a shorthand notation for decalring the metadata `^:name` which is useful for flags as the value will be set to true.

```clojure 
user=> (def ^:debug five 5)
#'user/five
user=> (meta #'five)
{:ns #<Namespace user>, :name five, :column 1, :debug true, :line 1, :file "NO_SOURCE_PATH"}
```

Anotehr use of `^` is for type hints.  These are used to tell the compiler what type the value will be and allow it to perform type specific optimisations thus potentially making resultant code a bit faster.

```clojure
user=> (def ^Integer five 5)
#'user/five
user=> (meta #'five)
{:ns #<Namespace user>, :name five, :column 1, :line 1, :file "NO_SOURCE_PATH", :tag java.lang.Integer}
```

We can see in that example the `:tag` property is set.

You can also stack the shorthand notations,

```clojure
user=> (def ^Integer ^:debug ^:private five 5)
#'user/five
user=> (meta #'five)
{:ns #<Namespace user>, :name five, :column 1, :private true, :debug true, :line 1, :file "NO_SOURCE_PATH", :tag java.lang.Integer}
```

- [Clojure Official Documentation: Metadata](http://clojure.org/metadata)
- [Learning Clojure: Meta Data](http://en.wikibooks.org/wiki/Learning_Clojure/Meta_Data)

<hr/>

### Character: `'`

Can be used against symbols as part of a dispatch macro (see `#'`).  Also used to quote forms and prevent their evaluation as with the `quote` function.

```clojure
user=> (1 3 4) ; fails as it tries to evaluate 1 as a function

ClassCastException java.lang.Long cannot be cast to clojure.lang.IFn  user/eval925 (NO_SOURCE_FILE:1)
user=> '(1 3 4) ; quote
(1 3 4)
user=> (quote (1 2 3)) ; using the longer quote method
(1 2 3)
user=> 
```

- [Clojure Official Documentation](http://clojure.org/special_forms#quote)

<hr/>

### Character: `;`

`;` is a comment.  In fact its a comment __macro__ that takes all input from its starting point to the end of the line and ensures the reader ignore it.

```clojure
user=> (def x "x") ; this is a comment
#'user/x
user=> ; this is a comment too
<returns nothing>
```

<hr/>

### Character: `:` 

`:` is the indicator for a Keyword which is an interned string that provides fast comaprison and lower memory overhead.

```clojure
user=> (type :test)
clojure.lang.Keyword
```

Alternativley you can use `keyword` to create a keyowrd from a string

```clojure
user=> (keyword "test")
:test
```

A neat thing about keywords is they also implement `IFn` and can act as functions for extracting values fom maps which is hella nice.

```clojure
user=> (def my-map { :one 1 :two 2 })
#'user/my-map
user=> (:one my-map) ; get the value for :one by invoking it as function
1
user=> (:three my-map) ; it can safely access non-keys
nil
user=> (:three my-map 3) ; it can return a default if specified
3
```

- [Clojure Official Documentation](http://clojure.org/data_structures#Data%20Structures-Keywords)

<hr/>

### Character: `::` 

`::` is used to fully qualify a keyword with the current namespace.

```clojure
user=> :my-keyword
:my-keyword
user=> ::my-keyword
:user/my-keyword
user=> (= ::my-keyword :my-keyword)
false
```

I have found this useful when creating macros.  If I want to ensure a macro, that calls another method in the macro namespace, correctly expands to call the method I have used ::my-method to refer to the fully qualified name.

- [What is the :: used for in clojure?](http://stackoverflow.com/questions/5771168/what-is-the-used-for-in-clojure)

<hr/>

### Character: `/`

Can be the division function `/` but can also act as a seperator in a symbol name to break apart the symbol name and the namespace is resides in `my-namepace/utils`.  This allows symbols to be fully qualified to prevent collisions or spread.

- [Cloure Official Documentation](http://clojure.org/reader)

<hr/>

### Character: `$`

Used to reference inner classes and interfaces in Java.  Seperates the container class name and the inner class name,

```clojure
(:import (basex.core BaseXClient$EventNotifier)

(defn- build-notifier [notifier-action]
  (reify BaseXClient$EventNotifier
    (notify [this value]
      (notifier-action value))))
```

`EventNotifier` is an inner interface of the `BaseXClient` class which is an imported Java class.

- [Clojure: Using Java Inner Classes](http://blog.jayfields.com/2011/01/clojure-using-java-inner-classes.html)

<hr/>

### Character: `->`, `->>` etc.

These are threading macros.  Almost all of them take an initial value and __thread__ this value through a number of forms.  Lets imagine (for reasons unknown) we wanted to take a number, find the square root, cast it to an int, then a string then back to an integer again. We could write it like this,

```clojure
user=> (Integer. (str (int (Math/sqrt 25))))
5
```

The threading macro allows us to unravel this deep nesting,

```clojure
user=> (-> 25 (Math/sqrt) int str Integer.)
5
```

Or if you like multi lines and consistent brackettering

```clojure
(-> 25
    (Math/sqrt)
    (int)
    (str)
    (Integer.))
```    

What the macro does is take the value returned from each expression and push it in as the first argument to the next one.  

`->>` is the same but different.  Rather than push the last value in as the __first__ argument it passes it in as the __last__ argument.

The "etc." in the title refers to the fact there are a whole host of threading macros that perform variations on the same theme (`cond->`, `some->`, `as->` and their `->>` equivalents).  There is also an entire library, [swiss-arrows](https://github.com/rplevy/swiss-arrows), dedicated to the threading macros.

- [Understanding the Clojure -> macro](http://blog.fogus.me/2009/09/04/understanding-the-clojure-macro/)
- [Clojure Changelog](https://github.com/clojure/clojure/blob/67571d1844e7b9a0cab6089245d7e5cde208c67e/changes.md)

<hr/>

### Character: `~`

See `` ` `` (syntax quote) for additional information. 

`~` is unquote.  That is within as syntax quoted (`` ` ``) block `~` will __unquote__ the associated symbol i.e. resolve it in the current context.

```clojure
user=> (def five 5) ; create a named ref representing the number 5
#'user/five
user=> five ; five will yeild its internal value
5
user=> `five ; syntax quoting five will fully resolve the SYMBOL
user/five
user=> `~five ; within a syntax quoted block ~ wil resolve the value in the current context
5
```

This forms the meat and potatoes of creating macros which are, to be highly reductionist, functions that return blocks of syntax with parts evaluated in varying contexts.

- [Clojure for the Brave and True - Writing Macros](http://www.braveclojure.com/writing-macros/)
- [Clojure from the ground up: macros](http://aphyr.com/posts/305-clojure-from-the-ground-up-macros)
- [Clojure Offial Documentation](http://clojure.org/macros)

<hr/>

### Character: `~@`

See `` ` `` (syntax quote) and `~` (unquote) for additional information. 

`~@` is unquote-slicing.  Where unquote (`~`) deals with single values (or treats its attached item as a single item) `~@` works on lists and expands them out into multiple statements.  Think JavaScripts `.apply` method that takes an array and expands it out as arguments the applied function.

```clojure
user=> (def three-and-four (list 3 4))
#'user/three-and-four
user=> `(1 ~three-and-four) ; treates as a single statement produces a nested list
(1 (3 4))
user=> `(1 ~@three-and-four) ; expand out as seperate statements
(1 3 4)
```

Again this gives us a lot power in macros.

- [Clojure for the Brave and True - Writing Macros](http://www.braveclojure.com/writing-macros/)
- [Clojure from the ground up: macros](http://aphyr.com/posts/305-clojure-from-the-ground-up-macros)
- [Clojure Offial Documentation](http://clojure.org/macros)

<hr/>

### Character: `` ` ``

See `~@` (unquote splicing) and `~` (unquote) for additional information. 

`` ` `` is the syntax quote.  When used on a symbol it resolves the symbol in the current context,

```clojure
user=> (def five 5)
#'user/five
user=> `five
user/five
```

When used with lists (remember every thing in Clojure is data) it forms a __template__ for the data structure and wont immediately resolve it.

```clojure
user=> (1 2 3)
ClassCastException java.lang.Long cannot be cast to clojure.lang.IFn  user/eval832 (NO_SOURCE_FILE:1)
user=> `(1 2 3)
(1 2 3)
```

You'll see this most often in the context of macros.  We can write one now,

```clojure
user=> (defmacro debug [body] 
  #_=>   `(do 
  #_=>      (println "DEBUG: " ~body) 
  #_=>      ~body))
#'user/debug
user=> (debug (+ 2 2))
DEBUG:  4
4
```

The macro takes a single statement wraps it in a __quoted__ `do` block, evaluates and prints the result and then evaluates the body (yes thats double execution but that irrelevant to the point).  In effect this `defmacro` call returns a quoted data structure representing the program we are writing with it.  The `` ` `` allows this to happen.

- [Clojure for the Brave and True - Writing Macros](http://www.braveclojure.com/writing-macros/)
- [Clojure from the ground up: macros](http://aphyr.com/posts/305-clojure-from-the-ground-up-macros)
- [Clojure Offial Documentation](http://clojure.org/macros)
