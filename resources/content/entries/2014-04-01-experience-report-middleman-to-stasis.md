---
date: 2014-04-01T00:00:00Z
title: "Experience Report: Migrating from Middleman to Stasis"
published: true
categories: [Clojure]
type: article
external: false
---

I recently migrated my statically generated site (yep this site) from a Ruby/[Middleman](http://middlemanapp.com) solution to a Clojure/[Stasis](https://github.com/magnars/stasis) based solution. This post is a kind of experience report on the migration process.  It isn't a guide for using [Stasis](https://github.com/magnars/stasis) and if that is something you're looking for I can't recommend Christian Johansens "[Building static sites with Clojure](http://cjohansen.no/building-static-sites-in-clojure-with-stasis)" post enough.

## Background

My site has transitioned through various platforms - from WordPress to Posterous, Jekyll and Middleman.  Its also went through a few types - Internal corporate blog, public blog and finally a site that represents my role as a contractor.

I never really enjoyed WordPress or Posterous because there was a distinct lack of control and way too many features and points of failure and while Jekyll gave me flexibility its pattern for extension was rather undocumented and ambiguious.  Middleman offered me the flexibility with a very simple programming model.

The next migration, the one I'm covering now, came about because the site had accrued a lot of content debt over the years and I needed to tidy it up.  Problems such as

- duplicated and redundant YAML frontmatter settings and logic,
- a lot of different file types
- the migrated Posterous stuff was locked up in prerendered HTML
- many of the older posts use embedded gists and I wasn't happy with that

## Decision to Migrate

So why move from Ruby to Clojure, Middleman to Stasis?  Truth be told it was initially a learning experience.  I was knee deep in using Clojure and decided the project was substantial enough to help further my education.  I had started with refactoring the existing Ruby codebase but the content tidy up effort was proving awkward in Ruby.  Once I started the work in Clojure it was clear I was able to better express my intent.

## Observations

The next few sections are observations I've made during the migration.

### Stasis gives you less (and that's OK)

> Statis just offers a few functions that are useful when creating static web sites.
>
> No more. There are no batteries included.

Thats directly from the Stasis [README](https://github.com/magnars/stasis/blob/master/README.md) and as you can guess it's spot on. Fundamentally speaking Stasis gives you two entry points

1. A ring handler for serving content
2. An `export-pages` function for saving content to disk

Both of these expect a map of `path` and `content` and  thats it. You could serve an simple hello world style site like this.

```clojure
(stasis.core/export-pages { "/index.html" "<html><body>Hello World</body></html>" }
                          "build")
```

Running that would result in and `index.html` file in the `/build` folder of your project with the contents above.

To complement this stasis also gives you a method to `slurp` a directory, read its contents and generate one of these content maps.  This is an excellent starting point for transforming file based content.

In my site I slurp a directory of metadata enriched markdown files (YAML frontmatter) and thread them through a bunch of transformation functions. The heart of my content processing is a simple little function

```clojure
(defn entries
  []
  (let [entries (slurp-content "resources/content/entries")]
    (->> entries
         (filter :published)
         (map #(assoc % :uri
                      (case (keyword (:type %))
                        :article    (article-uri %)
                        :screencast (screencast-uri %)
                        :talk       (:url %)
                        :project    (:url %))))
         (sort-by :date)
         (reverse))))
```

`slurp-content` in this function extracts the file contents and converts it to a map based on the YAML frontmatter and the content body

At the end each bit of content (article, screencast, talk entry etc.) is a map that can be passed around to generate category pages, RSS feeds, HTML pages.  Content as data so to speak. So in future if I want to provide a JSONified version of all talk metadata I can just add another function that merges my new paths into the site map that Stasis uses.

This sort of work was mostly handled by Middleman internally and transformations configured by providing different file extensions to the content (which could be stacked like `.html.erb.md`). This meant that behaviour was controlled by manipulating content rather than it being treated as data. In Stasis this means there is a bit more code involved but the end result is perfectly acceptable.

There is a simplicity in Stasis I could never achieve with Middleman.

### Expressiveness of Clojure

When it came to tidying up the content I found the use of an interactive REPL and a few simple libraries meant I was able to achieve things quickly and easily.  When working with files combining the REPL with Git gave me a transactional approach to messing with files without fear.  I've saved most of my efforts in the fom of a [gist](https://gist.github.com/kouphax/9854290) for this work.

One of the biggest challenges I came up against was taking the old articles that contained embedded gists and replacing these with syntax highlighted code blocks.  This involved

- Scraping the `script` tags that linked to embedded gists from the articles
- Using the Github API to load the content and file type of each Gist (some had multiple files)
- Run the content through the pygments based syntax highlighter to produce a code block
- Swap the `script` and potential `noscript` block from the offending files
- Write the new content out.

It sounds easy on paper (and maybe for you it is) but previous attempts in Ruby had driven me to give up.  With Clojure and [Enlive](https://github.com/cgrand/enlive) this work was easier to express and achieve.  Of course it wasn't without problems.  I found [Enlive](https://github.com/cgrand/enlive) confusing at times but the interactive nature of the REPL meant I could break the problem down and isolate pain points.

Clojures REPL is more powerful than `irb` and this allowed me to babystep my problems.  Clojures syntax, simplicity and powerful standard lib allowed me to express my intent more clearly, with less code and fewer problems. The two together gave me an environment that allowed me to get to where I wanted with fewer frustrations.

### Generation Time

I build my site with an alias I set up `lein build-site`.  Previously I used `bundle exec middleman build`.  At first I noticed that the time to generate the static site in Clojure felt a lot longer.  In fact the generation process itself is probably a bit slower.  But, there is always a _but_, overall a fresh build and deploy (thanks to [Travis](https://travis-ci.org/kouphax/yobriefca.se)) actually takes a tiny bit less (a few seconds difference, nothing to write home about).

It seems a lot of the build time in Ruby comes from the resolution of dependencies of which Middleman has a lot more than Stasis. Note: This analysis is by no means in-depth of course and ultimatley it still only takes about 3 minutes from commiting a chance to seeing it live (depending on the speed of Travis picking it up).

## Credits

I've previously mentioned Christian Johansens "[Building static sites with Clojure](http://cjohansen.no/building-static-sites-in-clojure-with-stasis)" post and I'd like to acknowledge that it was the post that finally made me decide to migrate.  His post was invaluable during the inital migration (especially around the syntax highlighting).

While the post kicked of the decision to migrate the following list of technolgies made that transition possible.

- [Stasis](https://github.com/magnars/stasis) - provides the core of the site generation
- [Optimus](https://github.com/magnars/optimus) - Static asset optimisation (bundling, minification etc.)
- [Enlive](https://github.com/cgrand/enlive) - DOM manipulation library that was an essential part of unembedding the gists and replaceing code blocks with syntax highlighted blocks.
- [cegdown](https://github.com/Raynes/cegdown) - Markdown processing library. Essentially a Clojure wrapper for [Pegdown](https://github.com/sirthias/pegdown)
- [clj-yaml](https://github.com/lancepantz/clj-yaml) - Encoding and decoding of YAML frontmatter
- [fs](https://github.com/Raynes/fs) - A bunch of file system utilities.  Used a lot when renaming and replating content files.
- [slugger](https://github.com/pelle/slugger) - Genreates HTML friendly slugs for articles. This is a port of the stringex Ruby library used in Middleman which was essential to avoid breaking original urls
- [clygments](https://github.com/bfontaine/clygments) - A Clojure wrapper around then Pygments syntax highlighting library.
