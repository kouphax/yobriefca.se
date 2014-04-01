---
date: 2012-03-12T00:00:00Z
title: 'sentimentalizer: A Ruby port of Sentan'
published: true
categories: [Ruby]
type: article
external: false
---
In a bid to be at least 1% less stupid I decided to jump into Ruby.  Properly this time, no tinkering with Rails or watching a few videos.  So the first thing I did was take [@martinrue](https://twitter.com/#!/martinrue)'s [Sentan](https://github.com/martinrue/Sentan) project, fork it, made it run on mono (just because), ported it over to Ruby and renamed it [sentimentalizer](https://github.com/kouphax/sentimentalizer/) (but keeping all necessary attribution to Martins work in the README of course).<!-- more -->  I did this for two reasons,

1. I wanted something fairly meaty to actually port over to Ruby in an effort to get a more-than-superficial feel for the language
2. I might have a need for such a service in an upcoming hackathon (though the original .NET version would have been fine too)

This is in no way some sort of new project or intended for some production environment.

So what is it?  [sentamentalizer](https://github.com/kouphax/sentimentalizer/) is a quick and dirty [sentiment analysis](http://en.wikipedia.org/wiki/Sentiment_analysis) tool.  With it you can loosley class statements as either positive or negative (and get a breakdown of how that classification was derived).  You "train" it with datasets for both negative and positive control statements and then feed it sentences you want analysed.  At the minute the engine sits behind a super super super, did I say super already?, simple sinatra app that you submit statements to and it returns a simple JSON response with the sentiment result (negative, positive, neutral) and probability.  If you want to train the bitch you've got to change the `api.rb` file or update the files in the `data\positive` and `data\negative` folders.

Plans? Yeah I might poke around some more, make into more of a standalone learning service or something (again another feature I might need soon anyway).  But for now - feel free to review my shitey code Rubyists (I assume there are better ways to do things, standard appraoches that I'm not using etc.) and offer suggestions.

Once again thanks to [@martinrue](https://twitter.com/#!/martinrue) for doing all the hard-work on [Sentan](https://github.com/martinrue/Sentan)