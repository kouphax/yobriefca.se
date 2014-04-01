---
date: 2012-04-02T23:00:00Z
title: Alfred Hack for Web Developers
published: true
categories: [Random]
type: article
external: false
---
I've been a fan of [Alfred](http://www.alfredapp.com/) for some time and use it to make pretty much everything a keystroke away.  I've thrown a very small little extension that reduces the time to jump between console and newly started web app.  Generally during development we need to run our app jump to `localhost:PORT` and make sure it's working.  If, like me, you have a lot of different apps and platforms (Scala, Jekyll, Rails, Sinatra, Express to name a few) then `PORT` becomes the one thing you always need to type and the browsers auto predict is generally not that helpful in this situation.

Alfred to the rescue here, just create a script as shown below and then running alfred and typing `l 9000` you can launch your default browser at `http://localhost:9000`.  Very simple but saves me heaps of time at the minute.

![](/images/blog/alfredscript.png)