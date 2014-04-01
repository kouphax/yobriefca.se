---
date: 2012-03-22T00:00:00Z
title: rbenv and CodeRunner
published: true
categories: [Ruby]
type: article
external: false
---
You know [CodeRunner](http://krillapps.com/coderunner/) right?  If not maybe you should go check it out.  It's and aptly named little tool for running code snippets in various languages much like what [LinqPad](http://www.linqpad.net/) does for the .NET folks.  I've been using it extensivley recently to test out little Ruby and Scala snippets.  Thing is though - I hit a snag.  By default CodeRunner will run the system ruby executable (1.8 in OSX) and I generally use [rbenv](https://github.com/sstephenson/rbenv) along with version 1.9.whatever so some of my scripts started behaving a little... odd.

But hey it's an easy fix,

- Open your CodeRunner Preferences
- Select the Languages tab
- Select Ruby in the Language List
- Change the run command from `ruby $filename` to `~/.rbenv/shims/ruby $filename`

## Before

![](/images/blog/coderunnerruby/before.png)

## After

![](/images/blog/coderunnerruby/after.png)

Blam - global rbenv version is now the goto version for CodeRunner.  OK so it won't work with rbenv locals but I am sure you can tweak it to make it work (I haven't needed it so never looked).