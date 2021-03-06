---
date: 2012-06-24T23:00:00Z
title: Super Basic Trello API Wrapper for Scala and Play
published: true
categories: [Scala]
type: article
external: false
---
So I am currently doing a bit of work with using Trello as a kind of data store and decided to write a litle app in [Play!](http://playframework.org) (the Scala variety of course).  There didn't appear to be a library out there to handle some of the basic Trello stuff so over the past 20 minutes I put one together.

```scala 
import play.api.libs.ws.WS

case class Trello(key: String, token: String) {

  val host = "https://api.trello.com"

  def get(uri: String, query: (String,String)*) = request(uri, query:_*).get()
  def post(uri: String, query: (String,String)*) = request(uri, query:_*).post("")
  def put(uri: String, query: (String,String)*) = request(uri, query:_*).put("")
  def delete(uri: String, query: (String,String)*) = request(uri, query:_*).delete()

  private def request(uri: String, query: (String,String)*) = {
    WS.url(host + (uri.startsWith("/") match {
      case true => uri
      case false => "/" + uri
    })).withQueryString(query ++ Seq("key" -> key, "token" -> token): _*)
  }
}
```

Yep it's very basic and all but the `get` is untested (it's all I've needed so far).  But in the spirit of shipping early this is a good start.  The class makes use of Play!s uberflexible `WS` class and returns `Promise[Request]` so you can use it like so (just an example)

A simple API object to wrap the Trello class instance to load cards and map them into my expected objects

```scala 
object Api {

  val api = Trello(KEY, TOKEN)

  implicit object CardFormat extends Format[Card] {
    def reads(json: JsValue): Card = Card(
      (json \ "name").as[String]
      (json \ "due").as[String]
    )
    def writes(card: Card): JsValue = throw new NotImplementedException()
  }

  def getPastTalks = {
    api.get("/1/lists/_____", "cards" -> "open", "card_fields" -> "desc,name,due").map { res =>
      (res.json \ "cards") match {
        case JsArray(cards) => cards.map(_.as[Card])
      }
    }
  }
}
```

And calling this bad boy from a controller.

```scala 
object Application extends Controller {
  def index = Action {
    Async {
      Api.getPastTalks.map { talks =>
        Ok(views.html.index(talks))
      }
    }
  }
}
```

So - lots of work to do around this - I want to move away from a thin wrapper to something a bit more robust with strong classes for the different entites.  

Also if you want to find out where to get the API keys etc. go read the decent [API docs from Trello](https://trello.com/docs/).