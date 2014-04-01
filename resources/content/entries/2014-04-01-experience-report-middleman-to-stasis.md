---
date: 2014-04-01T00:00:00Z
title: "Experience Report: Migrating from Middleman to Stasis"
published: false
categories: [Clojure]
type: article
external: false
---

I recently migrated my statically generated site (yep this site) from a Ruby/[Middleman](http://middlemanapp.com) solution to a Clojure/[Stasis](https://github.com/magnars/stasis) based solution. This post is a kind of experience report on the migration process.  It isn't a guide for using [Stasis](https://github.com/magnars/stasis) and if that is something you're looking for I can't recommend Christian Johansens "[Building static sites with Clojure](http://cjohansen.no/building-static-sites-in-clojure-with-stasis)" post enough.

## Background

My site has transitioned through various iterations,

- It started as an internal WordPress blog with my previous company
- Then I decided to go public and moved to Posterous
- After Posterous shut its doors I moved to Jekyll
- Finally after getting frustrated with Jekyll I migrated to a custom [Middleman](http://middlemanapp.com) solution

The next migration, the one I'm covering now, came about because the site had accrued a lot of content debt over the years and I needed to tidy it up.  There was a lot of duplicated and redundant YAML frontmatter settings and logic, there was a lot of different file types and some of the migrated Posterous stuff was locked up in HTML with embedded gists.  I could have done all this by simply reworking the Middleman stuff but I've always had an unsettled relationship with Ruby, I was knee deep in Clojure and having tried to tidy up some of the debt with Ruby scripts the results were messy.

- Stasis gives you less and thats OK
- Same generation time, more code but better code?
- Expressivness of Clojure allwoed me to better clean my content
  - ungisting using enlive
  - tidy up yaml
  - rename files
- Optimus was initially confusing
- Less of a black box.
- List of tools used
- Deployment pipeline
