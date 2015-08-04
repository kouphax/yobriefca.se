---
date: 2015-08-04T00:00:00Z
title: "Refactoring Towards Transducers"
published: true
categories: [Clojure]
type: article
external: false
---

> This is a quick post but hey at least I'm not [talking][1] [about][2] [Elm][3]. COMBOBREAKER!

Clojure 1.7 introduced a new, fairly large concept - [transducers][4]. Transducers are "composable algorithmic transformations" which means they are little isolated transformer functions.  They aren't bound to any particular input or output and as such can be stacked or composed ad-hoc to make neat little pipelines for data processing.  

This required a fairly large internal change in the way many collection functions in Clojure behaved.  Now most of these functions now accept an arity of one less than they normally do.  For example,

```clojure
(def incrementalizer
  (map inc))
```

In this case `incrementalizer` is defined as a `map` function that will perform `inc` over whatever it gets passed.  We didn't pass `map` a data structure to map over and so it returns a transducer.  We can use `comp` to compose a bunch of these transducers together and run them across a collection using forms such as `into`, `transduce`, `eduction` and `sequence`.

As I was working on a new project recently I had just moved to Clojure 1.7 and so decided to take a stab at refactoring a few utility functions I had into a series of transducers.  This post covers how I approached this refactor.  

> It's worth noting that this is purely an exercise in refactoring towards transducers.  I am not suggesting that this increases performance, is easier to maintain or makes code any more readable. I'm not even suggesting this __should__ be done at all as that depends on the context of your work.

The original approach looked like this,

```clojure
(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))
            
(defn- list-files [folder]
  (file-seq (io/file folder)))

(defn- is-xml? [file]
  (-> file
      (.getName)
      (str/split #"\.")
      (last)
      (= "xml")))
                         
(defn- list-xml-files [files]
  (filter (fn [file]
            (and (.isFile file)
                 (is-xml? file)))
          files))

(defn- list-xml-paths [folder]
  (map (fn [file] (.getAbsolutePath file))
       (list-xml-files (list-files folder))))
       
(def xml-paths 
  (list-xml-paths "./workspace/xml"))
```

A bit of scrappy code that I wrote interactively while doing some ad-hoc file processing.  What we end up with is a list of absolute file paths to some XML files contained within a specified folder. In order to transform a folder path to a list of XML files it does a few things,

1. List the contents of the folder using the built-in `file-seq` function in Clojure
2. Remove everything in the list that isn't a file (such as pipes and directories)
3. Remove everything in the list without the `.xml` extension
4. Call `.getAbsolutePath` on the remainder of the list to get the full path as a string

## Towards Transducers

Any place that we apply some sort of filtering, mapping, reduction, take etc. over a collection is a candidate for being converted to a transducer.  In the previous examples we can identify a few points that can be rewritten as transducers,

- The `filter` which actually has 2 checks
	- is it a file?
	- is it an xml file?
- The `map` that converts a list of XML files into their absolute paths

If we rewrite these areas as transducers we come out with the following code,

```clojure
(def xml-extension-only
  (filter (fn [file] (-> file
                         (.getName)
                         (str/split #"\.")
                         (last)
                         (= "xml")))))

(def files-only
  (filter (fn [file] (.isFile file))))

(def as-path
  (map (fn [item] (.getAbsolutePath item))))
```

you should be able to see these transducer definitions are just restructured, single arity versions of our original `filter` and `map` calls.

Now we need to turn these into a pipeline.  We do this using the `comp` function,

```clojure
(def xform 
  (comp files-only xml-extension-only as-path))
```

The use of `xform` as a name is common in transducer examples (short for transform) so I have retained the use here for consistency.  

With our transducers composed into a transform pipeline we can apply it to our input,

```clojure
 (defn- list-xml-paths [folder]
   (sequence xform (list-files folder))
```

I redefined the `list-xml-paths` function to call `sequence` over the list of files and apply our transducer pipeline.

The complete code for this example looks like this,

```clojure
(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))
            
(defn- list-files [folder]
  (file-seq (io/file folder)))

(def xml-extension-only
  (filter (fn [file] (-> file
                         (.getName)
                         (str/split #"\.")
                         (last)
                         (= "xml")))))

(def files-only
  (filter (fn [file] (.isFile file))))

(def as-path
  (map (fn [item] (.getAbsolutePath item))))

(def xform 
  (comp files-only xml-extension-only as-path))

(defn- list-xml-paths [folder]
  (sequence xform (list-files folder)))
  
(def xml-paths 
  (list-xml-paths "./workspace/xml"))
```

[1]: https://yobriefca.se/blog/2015/07/28/window-dot-prompt-in-elm/
[2]: https://yobriefca.se/blog/2015/07/29/zombie-dice-score-card-in-elm/
[3]: https://yobriefca.se/blog/2015/08/02/deconstructing-your-first-elm-app/
[4]: http://clojure.org/transducers