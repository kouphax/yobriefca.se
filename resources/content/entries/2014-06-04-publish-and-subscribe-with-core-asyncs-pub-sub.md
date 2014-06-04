---
date: 2014-06-04T00:00:00Z
title: "Publish and Subscribe with core.async's pub and sub"
published: true
categories: [Clojure, ClojureScript]
type: article
external: false
---

Following on from my [previous article](/blog/2014/06/01/combining-and-controlling-channels-with-core-dot-asyncs-merge-and-mix/) that covered `core.async`'s  `merge` and `mix` this article will delve into another suite of complementary __high level__ methods  - namely the channel driven publish/subscribe model that `core.async` can support.

While the pub/sub mechanism provided by core.async is higher level than working directly with `chan`s and `mult`s to achieve the same thing (in fact that is how the pub/sub operations are implemented internally) it should still be considered a set of lower level primitives compared to say a specific event bus notification with topic namespacing, wildcard broadcasting and other such specialised features.

## Setup

Once again we'll use ClojureScript to demonstrate the use of pub/sub.  I'll refer you the __Setting Up__ section of my [previous article](/blog/2014/06/01/combining-and-controlling-channels-with-core-dot-asyncs-merge-and-mix/) if you want to get a basic ClojureScript setup running with `core.async`

## Publishing

Channels are `core.async`s foundation and this is no different when you move up a level of abstraction to a pub/sub model.  Looking at the publishing side first there are 2 main types of actors or components involved,

1. The publisher channel - Putting stuff on this channel will cause the publishing mechanism to kick in
2. The publication - calling `pub` on the publisher (we'll see this in a minute) produces a `publication` that subscribers can `sub`/subscribe to.

This may appear a bit more jarring than a typical approach where you just have a monolithic pub/sub object (e.g. a bus) that you publish and subscribe to directly.  However I found the idea a bit easier to understand when I thought of the publication as a realised __topic__ or set of __topics__.  

If you think of a simple message bus implementation (in whatever language floats your boat - I've went with Swift, nah just kidding its JavaScript) you would traditionally do something like this,

```javascript
// grab/create a bus - typically global.
var bus = new Bus();

bus.subscribe("account:created", function(data){
  // do something with the knowledge that 
  // an account has been created
});

// at some point we publish to the same topic
bus.publish("acoount:created", { 
  success:true, 
  username: "petedaleet17" 
})
```

The topic in this example is `account:created` however in reality its just a string, there is no real confidence that you are achieving  what you expect. A spelling error will result in rather unpredictable results and as we have introduced a high degree of decoupling it may well be difficult to track down.  The observant will notice there is in fact a spelling error in the code.

In `core.async` the notion of a `publication` is a realised entity you perform actions on and pass around.  It's the transport mechanism, that is the "concept".  This inversion is interesting.  99.9% of the time I'm ultimately interested in publishing or subscribing to a topic, I really don't care about the mechanics of that operation and this __topic-first__ view better supports that thought model.  

Now just to confuse things a bit more, a publication also defines a way to derive the topic of a published value so its entirely possible for a publication to handle subscriptions for any number of topics.  Another analogy that may help, or just make things worse, is that a `publication` could be considered a `router` that defines a set of rules for where to publish data.

To expand on this lets look at some code.

First we need to import a few things into out ClojureScript file (probably `core.cljs`)

```clojure
(ns chat.core
  (:require [cljs.core.async :refer [chan <! >! timeout pub sub unsub unsub-all]])
  (:require-macros [cljs.core.async.macros :refer [go]]))
```

Now lets introduce our publisher and our publication,

```clojure
; publisher is just a normal channel
(def publisher (chan))

; publication is a thing we subscribe to
(def publication 
  (pub publisher #(:topic %)))
```

You can see the `publisher` is simply a channel, no fancy annotations or extra functions being applied.  It's just a vanilla channel that you can put stuff and take stuff off if you so desired.

The `publication` ont he other hand is composed by calling `pub` on `publisher` passing a function.  When you put stuff onto `publisher` this function is used to derive the topic of the data put onto the channel.  It will get passed the put data and is expected to return __something__, __anything__ that represents the topic.  

In this case I am making an assumption that our data will be a map with a `:topic` entry.  Of course if `(:topic data)` returns `nil` that will be our topic.  Any topics that have no subscribers will be disregarded so `nil` will typically be an acceptable __dead letter office__ so to speak.

## Subscribing

So now we have something we can push data onto - the `publisher` channel, and something that will accept subscriptions - the `publication`.  Next we need to subscribe and start putting stuff on that our subscribers can consume.  We could start putting stuff on the channel now but no one will be around to receive it just yet.

The yin to `pub`s yang is `sub`.  `sub` always takes a __publication__, a __topic__ and a __channel__ (you can also specify how and when the subscribing channel will be closed).  As I've already mentioned you'll notice that the subscriber channels don't subscribe directly to the publisher channel.  This would allow you to create many publications from a single source channel.  

Taking our previous example we could subscribe to a number of topics like this,

```clojure
; define a bunch of subscribers
(def subscriber-one (chan))
(def subscriber-two (chan))
(def subscriber-three (chan))

; subscribe
(sub publication :account-created subscriber-one)
(sub publication :account-created subscriber-two)
(sub publication :user-logged-in  subscriber-two)
(sub publication :change-page     subscriber-three)
```

We now have 3 channels

- `subscriber-one` is subscribed to the `:account-created` topic
- `subscriber-two` is subscribed to both the `:account-created` topic and the `:user-logged-in` topic
- `subscriber-three` is subscribed to the `:change-page` topic

Now when messages are put on to the `publisher` the `publication` will inspect the data and determine where to route the message.  To make this more visual we can listen to and print the values from these channels by trying to take from them in a `go-loop`

```clojure
(defn take-and-print [channel prefix]
  (go-loop []
    (println prefix ": " (<! channel))
    (recur)))

(take-and-print subscriber-one "subscriber-one")    
(take-and-print subscriber-two "subscriber-two")
(take-and-print subscriber-three "subscriber-three")
```

If we start putting messages on the publisher we should see some console output.

```clojure
(go (>! publisher { :topic :change-page :dest "/#home" }))
; subscriber-three: { :topic :change-page :dest "/#home" }

(go (>! publisher { :topic :account-created :username "billy" }))
; subscriber-one: { :topic :account-created :username "billy" }
; subscriber-two: { :topic :account-created :username "billy" }

(go (>! publisher { :topic :user-logged-in :username "billy" }))
; subscriber-two: { :topic :user-logged-in :username "billy" }

(go (>! publisher { :topic :user-logged-out :username "billy" }))
; No subscribers so nothing to see
```

Of course subscribing to something is entirely additive.  You need some way to take subscriptions away.  At this point `unsub` and `unsub-all` are what you need.

```clojure
; unsubscribe subscriber-two from account-created
(unsub publication :account-created subscriber-two)

;test
(go (>! publisher { :topic :account-created :username "billy" }))
; subscriber-one: { :topic :account-created :username "billy" }

; unsubscribe every subscriber from the :account-created topic
(unsub-all publication :account-created)

;test
(go (>! publisher { :topic :account-created :username "billy" }))
; nada

; finally unsubscribe every channel from every topic
(unsub-all publication)

;test
(go (>! publisher { :topic :change-page :dest "/#home" }))
(go (>! publisher { :topic :account-created :username "billy" }))
(go (>! publisher { :topic :user-logged-in :username "billy" }))
(go (>! publisher { :topic :user-logged-out :username "billy" }))
; wonderful silence
```

## Summing Up

So that covers the `pub`/`sub` model you get with `core.async` which gives a slightly higher abstraction from having to deal with the internal logic of managing a `mult` to perform this operation while still remaining suitably low level to allow you to build upon it for your own needs.

Remember that because everything still boils down to channels, which you have access to, there is huge potential to combine various abstractions to construct simple pipelines for all your needs.  

One day I'll finish my channel based [Rube Goldberg machine](https://en.wikipedia.org/wiki/Rube_Goldberg_machine) put a message at one end and watch as it gets piped through mults, publications, merged channels, alts and anything else I can think of finally ending up in a `console.log`...

> "Hi James".
