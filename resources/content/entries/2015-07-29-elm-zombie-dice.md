---
date: 2015-07-29T00:00:00Z
title: "Zombie Dice Score Card in Elm"
published: true
categories: [Elm]
type: article
external: false
---

[As promised](https://yobriefca.se/blog/2015/07/28/window-dot-prompt-in-elm/) yesterday evening I spent some time re-writing my [Zombie Dice Score Card](https://yobriefca.se/blog/2015/07/20/zombie-dice-score-card-with-reagent/), originally written in ClojureScript and [Reagent](http://reagent-project.github.io/), in [Elm](http://elm-lang.org).  

If you just want to poke around the code then the source for each implementation is available on Github,

1. [The Elm implementation](https://github.com/kouphax/zombie-dice-elm)
2. [The ClojureScript implementation](https://github.com/kouphax/zombie-dice)

If you want a bit of an experience report then read on.

> This work was done after spending an evening with some [Elm screencasts](https://pragmaticstudio.com/elm) and some internet research I cannot promise this is __good__ or __correct__ code and I'll welcome any [feedback via Twitter](https://twitter.com/kouphax)

Developing the Elm version of the app took the best part of 3 or 4 hours once I discovered how to [integrate with JavaScript via ports](https://yobriefca.se/blog/2015/07/28/window-dot-prompt-in-elm/).  All in all I'd say there is about 6 or 7 hours work to craft my first Elm app with only a couple of hours introduction to the language.  This should be seen as a positive reflection on __Elm__ and not me.  

## Types

My previous experience with type focused languages was a few years of general purpose Scala development and a brief foray into Haskell which ended in mild frustration.  I'm certainly more comfortable in dynamic languages and if I was ever bored enough to be drawn into a "dynamic vs static" debate I'd probably side on the dynamic side.  With that said picking apart my ClojureScript implementation and recreating it in Elm has really made me appreciate its typed nature.  The ability to look at our defined types e.g.

```haskell
type alias Player =
  { id       : Int,
    name     : String,
    gamesWon : Int,
    score    : Int }

type alias ScoreBoard =
  { players : List Player,
    uid     : Int }
```

This explicit declaration really calls out stuff that, in ClojureScript (or __my__ ClojureScript code at least), is implied and typically scattered over the place. For example, in the code below I'm just pulling out keys from a map `:id`, `:games-won` etc. because __I know__ these things should exist. 

```clojure
 (defn- finish-game []
   (let [players (vals @scoreboard)
         winner (reduce #(if (> (:score %1) (:score %2)) %1 %2) players)]
     (swap! scoreboard update-in [(:id winner) :games-won] inc)
     (reset-scores)))
```
 
Last night when I revisited this code I had forgotten what my model looked like and I had to figure it out by scanning all my code for when things are set and read from my data structure representing my state.  I'm sure I could structure my ClojureScript better, perhaps use something like [core.typed](https://github.com/clojure/core.typed) or [Prismatic Schema](https://github.com/Prismatic/schema), but the onus is on the developer.  Of course this is not a new and startling revelation this is just one of the many difference in dynamic/static languages.  So what does Elm do to make this different?  Well, for one, it uses gradual typing.  I can declare a function like this,

```haskell
newPlayer id name =
  { id       = id,
    name     = name,
    gamesWon = 0,
    score    = 0 }
```

This will work fine and the Elm compiler will infer the types and shout at me if I'm passing ambiguous types around.  However I can solidify my intentions by adding a type signature for the function,

```haskell
newPlayer: Int -> String -> Player
```

When I initially started dipping into Elm I thought these signatures would be an after thought for me.  I'd only add them in for completeness sake.  I was wrong. They are extremely useful and helped massively as I designed my implementation.  They also improve the error messages.  Scala has this sort of type inference as well but I almost always left it out unless required.  Perhaps I've got wiser in my old age or maybe Elm has made the type system more accessible.

## Error Messages

When something goes wrong in Clojure or ClojureScript the resulting stacktraces can be next to useless for figuring out what is actually wrong. No amount of formatting and colouring improves this, sometimes they are just confusing.  Error messages in Elm are the complete opposite.

```
-- TYPE MISMATCH ------------------------------------------------ ZombieDice.elm

The type annotation for `playerEntry` does not match its definition.

98| playerEntry: Address Action -> Player -> Html
                 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
Looks like a record is missing the field `uid`

As I infer the type of values flowing through your program, I see a conflict
between these two types:

    Player

    { a | score : Int, uid : Int, gamesWon : Int, name : String } Use --force to continue.
```

Just look at that message.  It's telling me exactly what is wrong and I knew how to fix it once I'd read this message.  Most Elm messages are like this although some of the type related ones did make me scratch my head until I wrapped my head around them a bit more.

## HTML

Hiccup style syntax in ClojureScript that we use for describing HTML structure is lovely.

```clojure
 [:div.row
  [:div.col.span_3.name
   [:h2 (:name player)]
   (for [win (range (:games-won player))]
     [:img { :src "images/gold-brain.png" :height 20}]) ]
  [:div.col.span_3
   [:image.brain { :src "images/brain.png"
                   :on-click #(update-score (:id player)) }]
   [:span.score [:small " x "] (:score player)]]]
```

With paredit enabled creating this sort of structure is an absolute breeze and rather easy to comprehend after it's been written.  The same, i'm afraid, can not be said for Elm,

```haskell
  div
    [ class "row" ]
    [ div
        [ class "col span_3 name" ]
        [ h2
            []
            [ text player.name ],
          span
            []
            (List.map (always wonGame) [1..(player.gamesWon)])],
      div
        [ class "col span_3" ]
        [ img
            [ class "brain",
              src   "images/brain.png",
              onClick address (Inc player.id) ]
            [],
          span
            [ class "score" ]
            [ small
                []
                [ text "x" ],
              span
                []
                [text (toString player.score)]  ] ] ]
```

This is formatted, I think, using the recommended formatting advice and while it is relatively easy to read and get to the place you want to make changes it caused me a lot pain creating it and refactoring it as I added new features. I almost always misjudged bracket placement and forgot commas resulting in errors.  Perhaps it's the fact paredit gives me so much power and ClojureScript doesn't use commas (they're treated as whitespace) but this was painful for me to create.

## It's just one big fold

The architecture of an Elm app is always the same and it's really rather clever.  However there is a bit of initial setup to get everything working.  To this end you can use the [start-app package](https://github.com/evancz/start-app) that removes a lot of the initial boilerplate (there's not really __that much__ boilerplate in reality).  I didn't use this package as I wanted to really understand how everything was wired together.

This led to my big revelation. The root of an Elm app is just a map our reactive model that returns the updated view as and when our state changes.  The reactive model part is just a fold over the application data which gets updated actions/signals happen that cause the state to transition to another state.  

The entire app updates when something changes the state.  

No management of discrete little parts and internal state - just rebuild the world.  This is the same principle behind React and Reagent (a ClojureScript React wrapper) but in React the mechanisms are somewhat hidden.  In Elm it's right there.  You can compose different signals, à la Reactive Extensions Observables, to make your app behave in different ways.  For example,

```haskell
model : Signal Model
model =
  let
    allActions = mergeMany
      [ actions.signal,
        externalActions ]
  in
    foldp update initialModel allActions
    
externalActions: Signal Action
externalActions =
  mergeMany
    [ (Add) <~ addPlayer ]
```

In this example from the app I merge the actions signal that comes from the apps main Mailbox (this is the one that streams all the internal Actions to update the model) and external signals (these are the ones that are generated by JavaScript outside of the Elm app).  `externalActions` does some manipulation of it's incoming data to create a signal of type `Action` that can be merged with our core Action signal.

While I have likely explained this in the worst possible way this approach makes everything wonderfully composable and straightforward.  As soon I saw how Elm approached reactive application development I was just blown away by its simplicity.

## Conclusion

I like Elm.  Go try it.  I can't really say one implementation of my little app is better than the other each has it's good and bad points.  Besides I haven't spent enough time with Elm to evaluate it properly yet.  All I know is that I've taken to it better than other languages of its ilk.

I'm not sure why it's type system feels more accessible to me compared to Scala or Haskell but I've found that the barrier of entry for Elm is much less than either of those languages.  There seems to be, compared to Scala at least, much less concepts to wrap your brain around while still remaining extremely powerful.  

Finally I recommend you check out [this talk](https://www.youtube.com/watch?v=oYk8CKH7OhE) by Evan Czaplicki (creator of Elm) around the design decisions that help make Elm more accessible.