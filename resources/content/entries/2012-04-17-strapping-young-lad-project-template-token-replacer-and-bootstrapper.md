---
date: 2012-04-16T23:00:00Z
title: Strapping Young Lad - Project Template Token Replacer and Bootstrapper
published: true
categories: [Ruby]
type: article
external: false
---
Today I have released the super simple, rough-as-hell, barely [MVP](http://en.wikipedia.org/wiki/Minimum_viable_product) [Strapping Young Lad](https://rubygems.org/gems/lad)([Source](https://github.com/kouphax/strapping-young-lad)).  It's fulfils a need I keep having but never really fixing.  

## Evelvator Pitch

SYL, or lad for short, will take a git repo, clone it and replace a bunch of tokens in it with the values you've supplied.  

## Say Wha?

This means you can create a bunch of base solution/project types for any language and SYL will simply do some basic file/folder renaming and file content replacing for a given token.  For example doing the following,

    lad /path/to/git/repo MyNewProject

Will do the following things,

1. Clone the repo at `path/to/git/repo` into a temporary folder
2. Remove the `.git` folder from the repo
3. Check for the existence of a `.ladconfig` in the freshly cloned repo
  - If one exists it loads the config setting
  - Otherwise it falls back to the defaults
4. Replace all instances of the configured token (eg. `__NAME__`) in all files, directories and file contents with the project name (in our case `MyNewProject`)
5. Copy the new folder over to your current working directory (`./MyNewProject`)

What this allows you to do is have a standard project template for pretty much anything (.NET, Ruby, Scala - listen I mean anything - if it a collection of folders and files in a git repo then you set) and create a new instance with a few keystrokes - saving you time and potential RSI.

SYL was inspired by [WarmuP](https://github.com/chucknorris/warmup) which is itself a gem but it requires you to be running .NET which for a lot of my requirement isn't going to cut it.

If you want to see what a typical template project looks like try [Amir's Loam repo](https://github.com/amirrajan/Loam).  This is a WarmuP project but works just fine.  With SYL you can create a `.ladconfig` file to override some of the conventions - get the source README linked above.

## Whats Next

Yep it's pretty damn basic right now but it satifies most of my needs right now.  There are a few things I want to add for myself but I didn't release this for ME, I am pretty sure this will be of use to someone in some community in some shape or form.  With that in mind here are some features I think would take this to the next level,

1. Specifiying multiple tokens e.g. `__NAME__` and `__NAMESPACE__` and `__YEAR__` etc. etc.
2. Post processing actions (e.g. `bundle install` or `npm install` or `sbt compile` or something like that)
3. A catalog - I have a [github organisation](https://github.com/strapping-young-lad) with no repos setup right now but this would be a brilliant place to hold a load of templates compatible with SYL, don't ya think?  Then I could roll a site like rubygems or npmjs.org and make the projects searchable etc.  If you build it, they will come.  Maybe, who knows.

So if you like it - HIGHFIVE - if not - it's all good.  Now go break it and criticise me.   

> So we'll criticise him, because he can take it.... [The Dark Knight, Approx.]