---
date: 2011-11-16T00:00:00Z
title: 'Wednesday Tip: C# Type aliases'
published: true
categories: [.NET]
type: article
external: false
---
Ever written code that looks quite like this?

```csharp
Dictionary<string, KeyValuePair<string, string>> cache = new Dictionary<string, KeyValuePair<string,string>();
```

I have.  Ugly and noisy isn't it!  <!--more--> OK you could make it *slightly* less noisy by using `var`

```csharp
var cache = new Dictionary<string, KeyValuePair<string,string>();
```

Or I suppose you could ahead and create yet another random type to abstract the implementation out of the way but thats just too much work for something internal to a class.

Turns out you can make use of alises.  I've always known about using alises for namespaces to either resolve possible type conflicts or to make code a bit more understandable

```csharp
using CoreWeb = System.Web
...
var x = new CoreWeb.Request()
```

So this works with namespaces **AND** types.

```csharp
using Cache = Dictionary<string, KeyValuePair<string,string>();
```

This allows us to make the declaration above in a much nicer manner,

```csharp
Cache cache = new Cache();
```

OK so there are way to many uses of the word cache there but you get the gist.  Handy to know.  Never knew this until today #youlearnsomethingneweveryday