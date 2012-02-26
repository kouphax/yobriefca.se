---
layout:     post
title:      "Wednesday Tip: C# Type aliases"
published:  true
categories: [".NET", "C#"]
---

Ever written code that looks quite like this?

{% codeblock lang:csharp %}
Dictionary<string, KeyValuePair<string, string>> cache = new Dictionary<string, KeyValuePair<string,string>();
{% endcodeblock %}

I have.  Ugly and noisy isn't it!  <!--more--> OK you could make it *slightly* less noisy by using `var`

{% codeblock lang:csharp %}
var cache = new Dictionary<string, KeyValuePair<string,string>();
{% endcodeblock %}

Or I suppose you could ahead and create yet another random type to abstract the implementation out of the way but thats just too much work for something internal to a class.

Turns out you can make use of alises.  I've always known about using alises for namespaces to either resolve possible type conflicts or to make code a bit more understandable

{% codeblock lang:csharp %}
using CoreWeb = System.Web
...
var x = new CoreWeb.Request()
{% endcodeblock %}

So this works with namespaces **AND** types.

{% codeblock lang:csharp %}
using Cache = Dictionary<string, KeyValuePair<string,string>();
{% endcodeblock %}

This allows us to make the declaration above in a much nicer manner,

{% codeblock lang:csharp %}
Cache cache = new Cache();
{% endcodeblock %}

OK so there are way to many uses of the word cache there but you get the gist.  Handy to know.  Never knew this until today #youlearnsomethingneweveryday