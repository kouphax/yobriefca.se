---
title:      "LocalOnly Actions in Play!"
published:  true
layout:     post
date:       2013-12-17
categories: [Scala]
slug:       "A quick and dirty way to create local only actions in Play!"
---

A `LocalOnly` action in Play! is essentially an action on a controller that can only be called from the machine this it resides on.  This is useful for restricting some endpoints (perhaps ops focused or database seeding _tasks_) from being called by all and sundry.

```scala
def LocalOnlyAction(f: Request[AnyContent] => Result): Action[AnyContent] = {
  Action { request =>
    request.remoteAddress match {
      case "127.0.0.1" | "::0" => f(request)
      case _ => NotFound
    }
  }
}
```

You can use this Action wrapper like so,

```scala
def index = LocalOnlyAction { request =>
  Ok("Local Only Action Executed")
} 
```

This can be useful in the early stages of a project when your design is constantly influx and you need to perform actions on running systems.  As your system grows this is perhaps a less favourable appraoch and you should consider moving these sort of tasks out of the application entriely.