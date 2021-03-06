---
date: 2012-03-08T00:00:00Z
title: Tinkering with minion.js
published: true
categories: [JavaScript]
type: article
external: false
---
Yay a technical post...  

[minion.js](https://github.com/gigafied/minion) is a micro framework providing classical inheritance strategy for JavaScript plus many other goodies.  It can be used in both browsers and [node.js](http://nodejs.org).  Now I don't want to get caught up in any of this classical inheritance for JavaScript is an anti-pattern stuff so just take this framework for what it is - a decent way to define both client and server side models for you JavaScripts!

I've already said it has a host of goodies that can be useful for creating a nice flexible set of models and associated helpers.  This includes,

- Class and inheritance support
- Modularity
- A cross model pub/sub mechanism
- Statics, Singletons, static methods and properties
- Dependency management
- Some other stuff I've yet to touch upon...

The code will more or less speak for itself here so lets dive in (the full suite of tests and sample node.js/express implementation can be found on my [GitHub thingy](https://github.com/kouphax/minion-tinkering)).

## Classes

One of the first things I did was try out the whole inheritence thing.  I create a Fruit base class and provided a Strawberry implementation on top of it.

```javascript 
minion.define("fruit", {
  Fruit: minion.extend("minion.Class", {

    isInSeason: function(){
      return this.inSeason;
    }
  })
});
```

```javascript 
minion.define("fruit", {
  Strawberry: minion.extend("fruit.Fruit", {
    inSeason: true
  })
});
```

Using the Strawberry class is pretty damn simple.  First thing you need to do is a one off simple configuration of minion,

```javascript 
minion.configure({
  classPath : "js"
});
```

This just tells minions dependency manager where to start looking for your class implementations.  We can then start using the Strawberry class easily using minions `require` method

```javascript 
minion.require("fruit.Strawberry", function(Strawberry){
  expect(Strawberry).to.not.be(null);
});
```

## Static Objects

Static objects are pre-initialised Singletons in minion.js and that makes a lot of sense.  I created a Grocer static by simply extending another minion type `Static`.

```javascript 
minion.define("fruit", {
  Grocer: minion.extend("minion.Static", {

    init: function(){
      this.stock = 0;
      this.subscribe("purchase", this.buy);
    },

    sell: function(){
      this.stock--
    },

    buy: function(){
      this.stock++
    },

    checkLevels: function(){
      return this.stock;
    }
  })
});
```

Pretty unsuprising but minion will new up a shared instance of this class the first time it is called.  You'll see a little hint of the pub/sub mechanism in the `init` method as well so lets jump on that.

## Pub/Sub

So minion has a nice little pub/sub mechanism built right into the classes as well.  So lets say a customer buys stuff from the grocer by publishing his/her request - sure beats just taking it without asking!!!

```javascript 
minion.define("fruit", {
  Customer: minion.extend("minion.Class", {
    buy: function(){
      this.publish("purchase")
    }
  })
});
```

Minion once again handles all the internal wiring up of this stuff and it just works.  Nice.

## The Rest

There is more to minion.js, I've just given you a very high level view but you should dive a bit deeper by looking at the [official docs](https://github.com/gigafied/minion/blob/master/docs/getting_started.md).  There is a few bits of handy sugar sprinkled on the minion types (proxy, scoped setTimeout and setInterval).  The pub/sub also goes a bit further provinding `Notifications` that allow a sort of async callback/response mechanism which can be handy.

Now I've never been a fan of these sorts of Class frameworks - I've never really seen the need but I must admit minion appeared at about the right time.  I've been playing with a nice way of structuring [mongoose](http://mongoosejs.com) Schemas/Classes and I think minion here might be a nice fit.

Anyway check it out.  Yay a technical blog with more to come!

[minion.js](https://github.com/gigafied/minion)

[Sample Code](https://github.com/kouphax/minion-tinkering)