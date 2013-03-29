---
layout:     post
title:      "Tinkering with Agility: Honorable Mentions"
published:  true
categories: ["Agility.js", Web, Tinyweb, WebActivator]
---

In the process of [researching Agility](/blog/2011/09/01/tinkering-with-agility/) I needed to throw together a quick serverside stack to accept the RESTful calls from Agility.  I went with my current framework de jour [Tinyweb](https://github.com/martinrue/Tinyweb) and I must admit it was a great call.  For more basic info on Tinyweb check out my [blog post](/blog/2011/07/18/micro-web-frameworks-101-tinyweb/)

Agility by default routes the RESTful requests to `api/model/{id}` and Tinyweb let me create a handler associated with this sort of url by way of areas.

Areas
-----

Tinyweb has a feature I wasn;t aware of before.  It is possible to register a handlers namespace as an "area" (if you have experience with ASP.NET MVC you'll know what that means).

    Tinyweb.Areas.Add("Agility.Tinkerings.Web.Handlers.Api", "api");

This means that even though my handler is call `TodoHandler` the url would map to the one above without having to sacrifice naming style of the handler or force us to create a custom route variable in each handler that had to map to this area.  Flippin clever stuff

WebActivator
------------

Another excellent Nuget package is the [WebActivator](http://nuget.org/List/Packages/WebActivator) and I have been charmed by it's slickness recently.  WebActivator allows you to attribute a class/namespace and specify what code should be run when the application starts.  So rather than having to put in a load of lines for configuring different aspects of your stack into `Global.asax.cs` you can break them into distinct classes and avoid breaking [SRP](http://en.wikipedia.org/wiki/Single_responsibility_principle) more than you have too.  Here is the example I used in the Agility.js source

    [assembly: WebActivator.PostApplicationStartMethod(typeof(TinywebActivator), "Activate")]
    namespace Agility.Tinkerings.Web.Activators
    {
        public class TinywebActivator
        {
            public static void Activate()
            {
                Tinyweb.Areas.Add("Agility.Tinkerings.Web.Handlers.Api", "api");
                Tinyweb.Init();
            }
        }
    } 
 
 I don't even have a `Global.asax` defined at all.
 
 Feel free to poke around the source to get a better picture of how I threw the backend together.  Thanks go to [Tinyweb](https://github.com/martinrue/Tinyweb) and [@martinrue](http://twitter.com/#!/martinrue)
 