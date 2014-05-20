---
date: 2014-05-19T00:00:00Z
title: The Weird and Wonderful Characters of Clojure
published: true
categories:
  - Clojure
type: article
external: false
---

> A reference collection of characters used in Clojure that are difficult to "google". Descriptions sourced from various blogs, [StackOverflow](http://stackoverflow.com/questions/tagged/clojure), [Learning Clojure](http://en.wikibooks.org/wiki/Learning_Clojure) and the [official Clojure docs](http://clojure.org/documentation) - sources attributed where necessary.  Type the symbols into the box below to search (or use `CTRL-F`).  Sections not in any particular order but related items are grouped for ease. If I'm wrong or missing anything worthy of inclusion tweet me [@kouphax](http://twitter.com/kouphax) or mail me at <james@yobriefca.se>.

## `#` - Dispatch macro

You'll see this macro character beside another e.g. `#(` or `#"`. This topic will act as a bit preamble before looking at your specific case.

`#` is the dispatch macro, a reader macro that tells the Clojure reader (the thing that takes a file of Clojure text and parses it for consumption in the compiler) to go and look at another __read table__ for the definition of the next character - in essence this allows extending default reader behaviour.

Clojure doesn't provide support for creating reader macros but it is possible through [a bit of hackery](http://briancarper.net/blog/449/).

If you see `#` __at the end__ of a symbol then this is used to automatically generate a new symbol.  This is useful inside macros to keep macro specifics from leaking into the userspace.  A regular `let` will fail in a macro definition

```clojure
user=> (defmacro m [] `(let [x 1] x))
#'user/m
user=> (m)
CompilerException java.lang.RuntimeException: Can't let qualified name: user/x, compiling:(NO_SOURCE_PATH:1)
```

Instead you need to append `#` to the end of the variable name and let Clojure generate a unique symbol for it.

```clojure
user=> (defmacro m [] `(let [x# 1] x#))
#'user/m
user=> (m)
1
user=>
```

If we expand this macro we can see the `gensym`'d name

```clojure
user=> (macroexpand '(m))
(let* [x__681__auto__ 1] x__681__auto__)
```

Another place you'll see the `#` is in [tagged literals](http://clojure.org/reader#The%20Reader--Tagged%20Literals).  Most commonly you'll see this use in [EDN](https://github.com/edn-format/edn)(extensible data notation - a rich data format tahat can be used in Clojure) and in ClojureScript (`#js`). Search for `#inst`, `#uuid` or `#js` for some more info.

- [Clojure Documentation - Reader](http://clojure.org/reader)
- [Clojure Reader Macros](http://briancarper.net/blog/449/)
- [ClojureDocs - gensym](http://clojuredocs.org/clojure_core/clojure.core/gensym)

<hr/>

## `#{` - Set macro

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

## `#_` - Discard macro

See the dispatch (`#`) macro for additional details.

`#_` tells the reader to ignore the next form completely.

```clojure
user=> [1 2 3 #_ 4 5]
[1 2 3 5]
```

The docs suggest that "The form following `#_` is completely skipped by the reader. (This is a more complete removal than the comment macro which yields nil).".  This can prove useful for debugging situations or for multline comments. I've never used it.

- [Clojure Documentation - Reader](http://clojure.org/reader)

<hr/>

## `#"` - Regular Expression macro

See the dispatch (`#`) macro for additional details.

`#"` indicates the start of a regular expression pattern.

```clojure
user=> (re-matches #"^test$" "test")
"test"
```

This form is compiled at _read time_ into a `java.util.regex.Pattern`.

- [Clojure Documentation: Regex Support](http://clojure.org/other_functions#Other%20Useful%20Functions%20and%20Macros-Regex%20Support)

<hr/>

## `#(` - Function macro

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

## `#'` - Var macro

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

##  `#inst`, `#uuid` & `#js` etc. - tagged literals

Commonly found in EDN (extensible data notation - a rich data format) and ClojureScript this use of `#` is the tagged literal. Look at this example,

```clojure
user=> (java.util.Date.)
#inst "2014-05-19T19:12:37.925-00:00"
```

When we create a new date it is represented as a tagged literal, or in this case a tagged string.  We can use Clojures `read-string` to read this back (or use it directly)

```clojure
user=> (type #inst "2014-05-19T19:12:37.925-00:00")
java.util.Date
(read-string "#inst \"2014-05-19T19:12:37.925-00:00\"")
#inst "2014-05-19T19:12:37.925-00:00"
user=> (type (read-string "#inst \"2014-05-19T19:12:37.925-00:00\""))
java.util.Date
```

A tagged literal tells the reader how to parse the literal value.  Other common uses include `#uuid` for generating UUIDs and in the ClojureScript world an extremely common use of tagged literals is `#js` which can be used to convert ClojureScript data structures into JavaScript structures directly.

- [EDN Tagged Elements](https://github.com/edn-format/edn#tagged-elements)

<hr/>

## `%` - Argument placeholder

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
(fn* [arg1 arg2 arg3 arg4] (clojure.core/println arg4)) ; takes 4 args doesn't use 3
```

So you don't have to use the arguments but you do need to declare them in the order you'd expect an external caller to pass them in.

`%` and `%1` can be used interchangably,

```clojure
user=> (macroexpand `#(println % %1)) ; use both % and %1
(fn* [arg1] (clojure.core/println arg1 arg1)) ; still only takes 1 argument
```
<hr/>

## `@` - Deref macro

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

## `^` - Metadata

`^` is the metadata marker.  Metadata is a map of values (with shorthand option) that can be attached to various forms in Clojure.  This provides extra information for these forms and can be used for documentation, compilation warnings, typehints and other features.

It can be represented as a map,

```clojure
user=> (def ^{ :debug true } five 5) ; meta map with single boolean value
#'user/five
```

We can access the metadata by the `meta` method which should be executed against the declaration itself (rather than the returned value).

```clojure
user=> (def ^{ :debug true } five 5)
#'user/five
user=> (meta #'five)
{:ns #<Namespace user>, :name five, :column 1, :debug true, :line 1, :file "NO_SOURCE_PATH"}
```

As we have a single value here we can use a shorthand notation for declaring the metadata `^:name` which is useful for flags as the value will be set to true.

```clojure
user=> (def ^:debug five 5)
#'user/five
user=> (meta #'five)
{:ns #<Namespace user>, :name five, :column 1, :debug true, :line 1, :file "NO_SOURCE_PATH"}
```

Another use of `^` is for type hints.  These are used to tell the compiler what type the value will be and allow it to perform type specific optimisations thus potentially making resultant code a bit faster.

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

## `'` - Quote macro

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

## `;` - Comment

`;` is a comment.  In fact its a comment __macro__ that takes all input from its starting point to the end of the line and ensures the reader ignore it.

```clojure
user=> (def x "x") ; this is a comment
#'user/x
user=> ; this is a comment too
<returns nothing>
```

<hr/>

## `:` - Keyword

`:` is the indicator for a Keyword which is an interned string that provides fast comparison and lower memory overhead.

```clojure
user=> (type :test)
clojure.lang.Keyword
```

Alternativley you can use `keyword` to create a keyword from a string

```clojure
user=> (keyword "test")
:test
```

A neat thing about keywords is they also implement `IFn` and can act as functions for extracting values from maps which is very nice.

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

## `::` - Qualified keyword

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

## `/` - Namespace separator

Can be the division function `/` but can also act as a separator in a symbol name to break apart the symbol name and the namespace it resides in `my-namepace/utils`.  This allows symbols to be fully qualified to prevent collisions or spread.

- [Cloure Official Documentation](http://clojure.org/reader)

<hr/>

## `$` - Inner class reference

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

## `-> ->> some-> cond-> as->` etc. - Threading macros

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

Or if you prefer multiline and consistent brackettering

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

## `~` - Unquote macro

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
- [Clojure Official Documentation](http://clojure.org/macros)

<hr/>

## `~@` - Unquote splicing macro

See `` ` `` (syntax quote) and `~` (unquote) for additional information.

`~@` is unquote-splicing.  Where unquote (`~`) deals with single values (or treats its attached item as a single item) `~@` works on lists and expands them out into multiple statements.  Think JavaScripts `.apply` method that takes an array and expands it out as arguments to the applied function.

```clojure
user=> (def three-and-four (list 3 4))
#'user/three-and-four
user=> `(1 ~three-and-four) ; treates as a single statement produces a nested list
(1 (3 4))
user=> `(1 ~@three-and-four) ; expand out as seperate statements
(1 3 4)
```

Again this gives us a lot of power in macros.

- [Clojure for the Brave and True - Writing Macros](http://www.braveclojure.com/writing-macros/)
- [Clojure from the ground up: macros](http://aphyr.com/posts/305-clojure-from-the-ground-up-macros)
- [Clojure Official Documentation](http://clojure.org/macros)

<hr/>

## `` ` `` - Syntax quote

See `~@` (unquote splicing) and `~` (unquote) for additional information.

`` ` `` is the syntax quote.  When used on a symbol it resolves the symbol in the current context,

```clojure
user=> (def five 5)
#'user/five
user=> `five
user/five
```

When used with lists (remember every thing in Clojure is data) it forms a __template__ for the data structure and won't immediately resolve it.

```clojure
user=> (1 2 3)
ClassCastException java.lang.Long cannot be cast to clojure.lang.IFn  user/eval832 (NO_SOURCE_FILE:1)
user=> `(1 2 3)
(1 2 3)
```

You'll see this most often in the context of macros.  We can write one now,

```clojure
user=> (defmacro debug [body]
  #_=>   `(let [val# ~body]
  #_=>      (println "DEBUG: " val#)
  #_=>      val#))
#'user/debug
user=> (debug (+ 2 2))
DEBUG:  4
4
```

> Code updated based on recommendations from Leif Foged

The macro takes a single statement wraps it in a __quoted__ `do` block, evaluates and prints the result and then evaluates the body.  In effect this `defmacro` call returns a quoted data structure representing the program we are writing with it.  The `` ` `` allows this to happen.

- [Clojure for the Brave and True - Writing Macros](http://www.braveclojure.com/writing-macros/)
- [Clojure from the ground up: macros](http://aphyr.com/posts/305-clojure-from-the-ground-up-macros)
- [Clojure Official Documentation](http://clojure.org/macros)

<hr/>

## `*var-name*` - Earmuffs

Earmuffs (a pair of asterisk bookending var names) is a __naming convention__ in many LISPs used to denote __special vars__.  Most commonly in Clojure this seems to be used to denote __dynamic__ vars i.e. ones that can change depending on where you are in the program.  The earmuffs act as a warning that "here be dragons" and to never assume the state of the var. Remember this is a __convention__ not a __rule__.

Core Clojure examples are `*out*` and `*in*` which represent the standard in and out Writers for Clojure.

- [How is the *var-name* naming-convention used in clojure?](http://stackoverflow.com/questions/1986961/how-is-the-var-name-naming-convention-used-in-clojure)
- [Clojure API Docs](http://clojure.github.io/clojure/clojure.core-api.html#clojure.core/*out*)

<hr/>

## `>!!`, `<!!`, `>!` & `<!` - core.async channel macros

These symbols are channel operations in `core.async` - a Clojure/ClojureScript library for channel based asynchronous programming (specifically CSP - Concurrent Sequential Programming).

If you imagine, for the sake of argument, a channel is a bit like a queue that things can put stuff on and take stuff off then these symbols support that simple API.

- `>!!` & `<!!` are __blocking__ __put__ and __take__ respectively
- `>!` & `<!` are, simply, __put__ and __take__

The difference being the blocking versions operate outside `go` blocks and block the thread they operate on.

```clojure
user=> (def my-channel (chan 10)) ; create a channel
user=> (>!! my-channel "hello")   ; put stuff on the channel
user=> (println (<!! my-channel)) ; take stuff off the channel
hello
```

The non-blocking versions need to be executed within a `go` block otherwise they'll throw an exception

```clojure
user=> (def c (chan))
#'user/c
user=> (>! c "nope")
AssertionError Assert failed: >! used not in (go ...) block
nil  clojure.core.async/>! (async.clj:123)
```

While the difference between these is well outside the scope of this article fundamentally the `go` blocks operate and manage their own resources pausing __execution__ of code without blocking threads.  This makes asynchronously executed code appear to be synchronous and removing the pain of managing asynchronous code from the code base.

- [core.async Code Walkthrough](https://github.com/clojure/core.async/blob/master/examples/walkthrough.clj)
- [core.async Wiki](https://github.com/clojure/core.async/wiki)

<hr/>

<script>
(function(){
  var script = document.createElement('script');
  script.src = "http://code.jquery.com/jquery-latest.min.js"
  script.onload = function(){
    // wraps h3 in block
    jQuery("h2").each(function(){
      jQuery(this).nextUntil("h2")
           .andSelf()
           .wrapAll("<div class='block'/>")
    })

    jQuery('blockquote:first').after(jQuery("<input type='search' id='filter' placeholder='Search symbols..'/>").css({
      "font-size"      : "1.5em",
      "width"          : "100%",
      "border"         : "1px solid #e0e0e0",
      "text-indent"    : "0.5em",
      "color"          : "#999",
      "font-family"    : "Open Sans",
      "padding-top"    : "0.2em",
      "padding-bottom" : "0.2em"
    }))

    var all = jQuery('h2').parents(".block")

    var filter = function(e){
      if(this.value === "") {
        all.show();
      } else {
        all.hide()
        jQuery('h2 code:contains(' + this.value + ')').parents(".block").show();
      }
    };

    jQuery('#filter').on("keyup", filter).on("click", filter);
    
    //var initialFilter = window.location.hash.substring(1);
    //if(window.location.hash.substring(1) !== "") {
    //  jQuery('#filter').val(initialFilter);
    //  filter(null);
    //}
  }
  document.getElementsByTagName('head')[0].appendChild(script);
})();
</script>

> Many thanks to everyone who has contributed ideas and [the copious amounts of] spelling corrections (crikey I'm bad at speelingz - so thanks Michael R. Mayne).  I've tried to call out people who have specifically asked for things.  Sorry if I've missed you.
