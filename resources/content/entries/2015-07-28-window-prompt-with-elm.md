---
date: 2015-07-28T00:00:00Z
title: "window.prompt() in Elm"
published: true
categories: [Elm]
type: article
external: false
---

> I say "standard stuff" a few times in this article. When I say that I assume, like me, you've dipped your toes into Elm.  If that is not the case and you want to change that situation then I recommend go give [Pragmatic Studios](https://pragmaticstudio.com/elm) your money. I was in no way paid or influenced to say that BTW.

Having spent last night watching the Pragmatic Studios [Elm: Building Reactive Web Apps](https://pragmaticstudio.com/elm) I am now a fully certified expert in [Elm](http://elm-lang.org) and so it's time to start writing articles about it.  Of course this is nonsense but some of my adventures have inspired me to write an article or two that may be useful to others (and my future self).

I [recently documented](https://yobriefca.se/blog/2015/07/20/zombie-dice-score-card-with-reagent/) a little hobby project I worked on for keeping score on games of Zombie Dice.  The app was written in ClojureScript and after I had wrapped my head around Elm I decided to see what the same app would look like in Elm.  This article __isn't__ about that, but rather a small part of it that I found quite challenging.

Unlike ClojureScript (and Clojure) whose philosophy is to embrace the underlying host environment Elm abstracts it away.  It's not gone completely it's just not as prevalent or easy to access.  In my [Zombie Dice Score Card](https://yobriefca.se/zombie-dice/) app adding new players uses the standard JavaScript host function `window.prompt` to capture the name and I wanted to replicate this functionality in my Elm implementation.

## Basic Implementation

My first pass at this resulted in a working implementation (well not my __actual__ first pass it was many, __many__ passes before I even figured out what the hell I was doing).  While it wasn't going to be practical for my actual needs it did form the basis of something useful that I could build upon.

The key is to using `port`s.  A [port](http://elm-lang.org/guide/interop#ports) acts as a bridge between Elm and JavaScript.  They either go `in` to Elm or `out` of Elm.  You declare a `port` in Elm like this,

```haskell
port suppliedNames : Signal String
```  

This is `port` is an innie.  You can tell it is an innie because you supply no actual definition.  Instead we can `send` things to it from the JavaScript side.

```javascript
app.ports.suppliedNames.send("James");
```

Assuming, in our HTML file, our Elm app is created and assigned to a variable `app` we will have this `ports` object that lists all the ports we expose from our app.  This will have our `suppliedNames` port which will have a method of `send` that we can use to signal values through that port.

One other thing is that these innies must be given an initial value when the app is constructed or you'll get an exception. We can do it like this,

```javascript
var app = Elm.fullscreen(Elm.Confirm, { suppliedNames: "" });
```

So when we boot our app we pass in an initial value.

Lets look at another port,

```haskell
port totalCapacity : Signal Int
port totalCapacity = lift (sum . Dict.values) dock
```

This one, lifted from one of the original port samples, is an outtie.  This is used to signal things __out__ of our Elm app into JavaScript.  In this case it sends out the sum of some data strucutre as and when it changes.  We can listen to these signals in JavaScript like this,

```javascript
app.ports.totalCapacity.subscribe(function(x) { 
  console.log(x) 
});
```

Outtie ports have a `subscribe` method generated for them that takes a function accepting the payload.  In our case we simply log it.

Now onto out specific use case.  My needs are slightly different to the samples because while they broadcast data out of the Elm side I jsut want some sort of trigger to say "go ahead and open a prompt".  This is 100% impure in that it is __only__ used for side effects and I've found that with Elm being a rather pure language this sort of requirement feels kind of awkward.  Thats not a criticism, just an observation. you should expect bad things to feel awkward, it helps you minimise them.

The JavaScript/HTML side of our application is fairly unsurprising (assuming you've not skipped the last few paragraphs).

```html
<!DOCTYPE HTML>
<html>
  <head>
    <meta charset="UTF-8">
  	  <script type="text/javascript" src="confirm.js"></script>
  </head>
  <body>
  </body>
  <script type="text/javascript">
    var app = Elm.fullscreen(Elm.Confirm, { suppliedNames: "" });
    app.ports.prompt.subscribe(function(){
        var value = window.prompt("Players name?");
        app.ports.suppliedNames.send(value);
    });
  </script>
</html>
```

Here we, 

- boot our app passing in a blank value for `suppliedNames`
- `subscribe` to a `prompt` port which will display the `window.prompt`
- `send` the captured value from `window.prompt` back into our app

The Elm side of things is where the real stuff happens,

```haskell
module Confirm where

import Html        exposing (..)
import Html.Events exposing (..)
import Signal      exposing (..)

-- SIGNALS ------------------------------
type Action = NoOp | Prompt

actions : Signal.Mailbox Action
actions =
    Signal.mailbox NoOp

-- PORTS --------------------------------
port suppliedNames : Signal String

port confirm : Signal ()
port confirm =
  actions.signal
    |> filter (\s -> s == Prompt) NoOp
    |> map (always ())

-- VIEWS --------------------------------
view name =
  div []
    [ text name,
      button [ onClick actions.address Prompt ]
        [ text "Set Name"]]

-- APP ----------------------------------
main : Signal Html
main = Signal.map view suppliedNames
```

Given there is a fair few things happening here lets have a look at the interesting bits.

First of all we set up the various operations our application will perform and create a `Mailbox` that can be used to send actions to.  If you've ever tinkered with Elm this should be pretty common,

```haskell
type Action = NoOp | Prompt

actions : Signal.Mailbox Action
actions =
    Signal.mailbox NoOp
```

The `Prompt` action is the one we want to use to trigger our `window.prompt` call via a port.  Next we declare our actual ports,

```haskell
port suppliedNames : Signal String

port confirm : Signal ()
port confirm =
  actions.signal
    |> filter (\s -> s == Prompt) NoOp
    |> map (always ())
```

`suppliedNames` is our innie.  This will receive names we've created later on.  `confirm` is our outtie.  What we do is 

- take the signal from our `Mailbox`
- `filter` everything except the `Prompt` actions
- `map` these values an `always` return a `unit` value

Why `unit`?  Well we don't care about the actual value we just want to reach out to the JavaScript when we get a `Prompt` action through our mailbox.  Any value is meaningless so lets just go for the __most meaningless__ one we can find.

Then in our `view` we have a `button` that sends a `Prompt` action to our Mailbox,

```haskell
button [ onClick actions.address Prompt ]
```

Again this should be fairly unsurprising to the Elm-ites (or whatever the collective term is) reading this.

Finally we have our `main` that wires it all together,

```haskell
main = Signal.map view suppliedNames
```

Again - standard stuff.

## Notes

As the last title indicated this is just a basic implementation.  It requires a bit more work to integrate into an application that requires a more complicated model but its a good starting point.  I know this because I've already begun extending it to create the Elm version of my [Zombie Dice Score Card](https://yobriefca.se/zombie-dice/) which I'll dissect in another article soon.

Now I'm not saying this is idiomatic Elm, I'm not even saying this is a __recommended__ way to do things but it works and "feels" good enough to me.  If you want to comment/fix/critique/whatever my work then you can get me on [Twitter](https://twitter.com/kouphax) and let fly the dogs of conversation.