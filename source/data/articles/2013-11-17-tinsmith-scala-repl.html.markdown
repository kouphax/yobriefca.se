---
title:      "Tinsmith: Online Scala REPL/Worksheet"
published:  true
layout:     post
date:       2013-11-18
categories: [Scala]
slug:       "Tinsmith is an online Scala REPL/Worksheet"
---

I recently released a basic self-hosted, we based Scala REPL called [Tinsmith](https://github.com/kouphax/tinsmith) (alternatively play with the [online demo](http://tinsmith.herokuapp.com)).
If you've been following Scala news recently you may have noticed a similar project [codebrew.io](http://codebrew.io) being talked about and
in fact [Tinsmith](https://github.com/kouphax/tinsmith) is heavily built off the same stack.

So why bother doing yet another REPL/Worksheet when one already exists?  Well the first spike came into existence because [codebrew.io](http://codebrew.io) was rather sluggish
one day I was using it and I wanted something I could run locally or deploy to a dedicated remote instance when I needed.  Then as I had my own
instance running there were things I realised I wanted to do differently or didn't need at all and so rather than cloning codebrew I decided
to use some common techs to produce a slightly different experience.

- Tinsmith __doesn't__ execute code automatically instead you need to explicitly execute code by `Alt-Enter` or `⌘-Enter`.
- Tinsmith __doesn't__ have the bells and whistles such as Doc lookup and autocomplete
- Tinsmith __doesn't__ have Github integration (though it did have gist integration at one point but I decided against it)
- Tinsmith __does__ save the code to the querystring to make pages bookmarkable (like [jsconsole](http://jsconsole.com))
- Tinsmith __does__ return the console output as well as the REPL output
- Tinsmith __does__ explicitly include some useful libs (Scala Utils, Scalaz, Guava etc.)
- Tinsmith __does__ run as a single application and can be quickly deployed to Heroku
- Tinsmith __does__ use WebSockets instead of Ajax

This is in no way meant to act as competition to the clearly better [codebrew.io](http://codebrew.io) - it's simply something I needed quickly
and decided other people might benefit from it as well.  Afterall it's just the [scala-codesheet](https://github.com/jedesah/scala-codesheet-api) and
[codemirror](http://codemirror.net/index.html) thrown together with a bit of webby glue.

## Demo <sup><a href="http://tinsmith.herokuapp.com" target="_blank">Fullscreen</a></sup>

<iframe src="http://tinsmith.herokuapp.com/" style="width:100%; height: 400px; border:none;"></iframe>
