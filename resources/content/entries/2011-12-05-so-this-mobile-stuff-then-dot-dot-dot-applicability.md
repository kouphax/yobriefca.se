---
date: 2011-12-05T00:00:00Z
title: So This Mobile Stuff Then... (Techologies)
published: true
categories: [Mobile]
type: article
external: false
---
Do you have your mobile strategy yet?  Do you have your "one true way" to cater for every mobile application need?  If not, the world will suddenly end and you'll never get anywhere in the mobile market.  Seriously.  Well not seriously.  Obviously that's a pile of sheep.

To be blunt the best "mobile strategy" is the obvious one.  Do whatever suits the project best.  Yeah, yeah your company or your presales guys may not like that, no prepackaged waffle to fill document with or spew out in front of customers.  The easy route is seldom the best route.  Seriously though if your playing it safe with web just in case that iOS project you've gotten involved in goes Android you'll be compromising all the way.

## Just In Case

Ahhhh good old "just in case".  Nothing produces more mediocrity through compromise than "just in case".  It's up to you to use your head when it comes to deciding what is the best fit for the project but as long as all the stakeholders are aware of the constraints those choices put on the project then those "what if" scenarios can be dealt with later.  We need to stop treating "mobile" as if it was a technology decision.  Web vs Native is not the same as saying Entity Framework vs NHibernate.  It's so much bigger than that.

## Weighing up the options

So you got a killer mobile project and you want it to be awesome - just like all your projects.  Before we pull out the "Native, Hybrid or Web" question we need to know what is out there.  What tools, technologies or languages can we use to make the best solution we can?

## Going Native

The problem with the native moniker is that people assume it means a different code base per mobile target and therefore crazy expensive, difficult to handle change, and very time consuming but that isn't always the case.  It is entirely possible to produce truly native apps without having to have an expansive code base in every language possible.

### Appcelerator Titanium

[Appcelerator's Titanium](http://www.appcelerator.com/) is a good example of a platform that provides an abstract API via a common meta language (in it's case JavaScript) that allows you to build directly to native apps for each target platform.  You write your code and send it off to the cloud for compilation (AFAIK there currently isn't an in-house option for building but happy to be corrected).  So you've got a middleman that you have no control over. What happens if your app gets rejected due to some low level issue in the compiled code?  What if the cloud service goes down when you really really gotta build?  What if [Appcelerator](http://www.appcelerator.com/) goes bust?  I dunno maybe that's just mostly FUD but these are the questions that customers ask and they are difficult ones to answer.  I realise I've lambasted the use of too much "what if"s in decision making but I've come up against these ones and they directly affect the current solution rather than only the future scope.  They need addressing and that's difficult.

Having said the a brilliant example of [Appcelerators](http://www.appcelerator.com/) usefulness is [Wunderlist](http://www.wunderlist.com/) - available on a ton of mobile and desktop platforms and built with Titanium the whole project shares, AFAIK, a common code base.

### MonoTouch, Monodroid etc.

[MonoTouch](http://xamarin.com/monotouch) and its ilk take a slightly different approach to the meta-language.  [MonoTouch](http://xamarin.com/monotouch), for example, isn't, as many believe, a cross-platform tool for building native apps.  No, [MonoTouch](http://xamarin.com/monotouch) simply compiles to native code through direct bindings to Objective-C code.  You are simply using the C# and the powerful features of the Mono/.NET Base Class Library.  In fact it's even possible to bind to third party Objective-C code like Flurry, Three20 etc. (and many are made available by other developers).

The ability to share a common codebase comes from the fact that the same platform is available in MonoDroid (C# and Android) and Silverlight (Windows Phone 7).  So sharing comes down to architecture.  You can share code provided your code doesn't touch and platform specific code.  You can create your own abstraction layer and work it into your build process or you can use MonoMobile.Extensions which gives you a common API already.

So in one way the [MonoTouch](http://xamarin.com/monotouch) stuff is simply the same as writing Objective-C but simply doing it using C#.  This is nice for a number of reasons.

- Mono has some very nice features like LINQ and Parallel programming.
- Plenty of nice compatible frameworks - [sqlite-net](http://code.google.com/p/sqlite-net/), [catnap](https://github.com/timscott/catnap), [restsharp](http://restsharp.org/) and plenty more.
- Syntax that is less noisy and easier to read

There are of course issues.  Assuming most people who use [MonoTouch](http://xamarin.com/monotouch) will come from a .NET background and want to use those skills - when you start you'll realise just how nice Visual Studio is.  MonoDevelop isn't terrible, it's just fine, but it's simply not on par with Visual Studio (would you expect it to be?).  With the way [MonoTouch](http://xamarin.com/monotouch) works your are kind of stuck with MonoDevelop too.  Your average VS2010 developer might easily get frustrated with MonoDevelop.  An aesthetic concern but one that can put people off easily.  That said XCode is equally as infuriating at times (bugs and quirks abound!).

It's not all roses though.  What if Xamarin, the commercial sponsor of [MonoTouch](http://xamarin.com/monotouch), pull the plug?  What if Apple decide, as they tried to before, to ban the use of any intermediary language?  These are questions that people will ask but again the immediate and real advantages always trump theoretical risks.  One other thing worth thinking about - If you came from a Ruby background for example I'm not sure why you would use [MonoTouch](http://xamarin.com/monotouch) over Obj-C.  Will this niche have an affect on Mono* in the long term?  Who knows.

## Mixing It Up aka _(cough)_Hybrid_(cough)_

So maybe you're a web guru/ninja/hero/wizard type person and you want to make use of those powers/skills in your mobile development but you want to get market visibility by creating a presence in the app stores.  Perhaps you want to have some sort of reuse across platforms but aren't a .NET dev and Titanium isn't appealing.  There a number of options in this area already.

### PhoneGap/Callback

[PhoneGap](http://phonegap.com/) is easily the most technology in the hybrid space.  It is exactly what you expect.  A fullscreen UIWebView in which resources embedded in the application package are executed.  These resources are given access to native device features such as device access etc. via a JavaScript API that hooks into native function calls.  There are wrappers for numerous platforms (iOS, Android, Bada, Blackberry, WP7 etc.) but your code calls the same adaptive API.  Things start to veer off course when you need to introduce custom plugins as these are native and so you need to target all your platforms.  Not much [PhoneGap](http://phonegap.com/)  can do in that case of course but worth pointing out.

With [PhoneGap](http://phonegap.com/)  it is up to you to create the experience.  Even if you want a simple straight forward native look and feel you'll need to simulate it.  Results may vary depending on how good you are at that sort of thing.  Of course something like Sencha Touch could be used to fake native but again what is native on one platform is weirdly alien on another and if you are targeting one platform only why not just man-up and learn that platform in the long term?  At least give yourself that option.

### NimbleKit

[NimbleKit](http://nimblekit.com/index.php) is in some ways a bit like MonoTouch meets [PhoneGap](http://phonegap.com/) .  You can use it as you would [PhoneGap](http://phonegap.com/)  as a transparent API the bridges native and Web Technologies but you can also create real life native components such a Navigation Controllers and Tabs.  Currently iOS is the only supported platform (more support for iPhone than iPad) though an Android version does exist in beta.

The interesting thing about [NimbleKit](http://nimblekit.com/index.php) is that it allows you to make use of a range of technologies to create a really native application while at the same time making use of HTML5/CSS3 features to create a more compelling experience that becomes very difficult in native platforms (see my previous charting example in my last post).

Plugins are extremely simple as well.  Just register the Obj-C class and call it from JavaScript.  Very nice, very simple.  This allows third party extensions to be bridged very easily too (Flurry for example).

The slightly annoying thing about [NimbleKit](http://nimblekit.com/index.php) is that it is currently very quiet.  There is still activity but it's certainly not on par with the likes of PhoneGap.  There are also some issues around stability.  It's not falling over all the time but there are somethings that need to be done in a certain order that aren't properly documented.  There is some minor speculation that [Sencha](http://www.sencha.com/) have acquired [NimbleKit](http://nimblekit.com/index.php) and I'd welcome that with open arms because I like the philosophy around [NimbleKit](http://nimblekit.com/index.php) and it could do with some money behind it.

## Options for Using Web Technologies

Be it hybrid or simply a mobile targeted web site/app there are a ton of options out there.  An absolute TON!

- Native look and feel, ultra rich frameworks
- Truly cross-platform progressively enhancing frameworks
- Frameworks for creating structure in your apps like MVC, MVVM etc.
- Frameworks for offering responsive designs

Mind boggling choices that change and evolve on a near hourly basis.  Here are just a few

### Native Look and Feel - Sencha Touch

[Sencha Touch](http://www.sencha.com/products/touch/) targets high end iOS and Android devices and creates native-esque UIs (WebKit only).  It's can be patchy on Android at times but the results you can get on iOS are very appealing.  Version 2.0 is seeing massive improvements in performance across the compatible platforms.  People who have experience in ExtJS will really feel at home here - other people will struggle with the short but steep learning curve (as with all [Sencha](http://www.sencha.com/) frameworks).

Bear in mind that [Sencha Touch](http://www.sencha.com/products/touch/) is a framework not a product so there will be work involved in getting it to behave exactly how you want but the available architecture options such as the MVC approach and the Data Stores make it very powerful.

### Cross Platform - jQuery Mobile

[jQuery Mobile](http://jquerymobile.com/) lives at the opposite end of the mobile framework spectrum.  It takes the philosophy of jQuery UI (Progressive Enhancement, accessibility, themeability etc.) and put it into a mobile framework.  So you construct a purely functional purely HTML application or site and with data annotations you let [jQuery Mobile](http://jquerymobile.com/) make the site as rich and functional as it can.  This means provided your mobile device can render HTML the site or app will at least be functional.  Butt ugly but functional.

This carries a penalty though.  Even when used on the top of the line device there are some trade offs that need to be made.  The experience is not as polished as it could be.  It is possible to tweak it yourself and cater to the high end devices only but there is extra work involved.  Again the result is still very nice and generally smooth but it is easy to nitpick little quirks in the navigation or styling.

### MVC Frameworks - Backbone, Spine(Mobile), Sammy etc.

If you want to roll your own UI entirely but still need to create a scalable and proven foundation [Backbone](http://backbonejs.org/) and [Spine](http://spinejs.com/mobile/index) are two very interesting projects.  It's worth noting that Sencha Touch has a powerful MVC architecture built into to and it is somewhat similar to these frameworks.

These sort of frameworks offer base classes for your typical project structure - Controllers, Models and Views.  They provide the wiring between these abstract classes allowing the developers to focus on the important stuff.  The controllers allow you to specify custom routing in your single page apps, the models give you structured persistence and sometimes relationships and the views give you a nicer lifecycle around page components and custom event handling.

It's often said these frameworks take some time to "get" - some people can use them with ease while others just think they introduce unnecessary complexity.  I think they come into their own in the mobile app space but I do still struggle with the concepts at times.

### Responsive Design - Skeleton

There are grid and UI toolkit frameworks out there that are aimed at creating a responsive design.  Got a large screen?  Make use of the better horizontal space and stack elements horizontally.  On a small mobile device with tiny viewport?  Stack the elements vertically.  [Skeleton](http://getskeleton.com) is the perfect example of this using media queries to layout a grid system depending on available space.  It also has a few nice widgets like Tabs and Buttons.  Geared more around the web site rather than the app these frameworks are a handy addition.

## Run out of things to say

I've covered a lot there but I wanted to get my thoughts out there in the world and hopefully educate and be educated.  My tools of choice right now?  Well RIGHT NOW I am tinkering with Obj-C and iOS as well as MonoTouch and did some NimbleKit hacking at the weekend.  To me this sort of understanding is important and that's why I do it.  You may argue that it makes you a jack-of-all-trades but I don't think so - this I merely trying to better understand the mobile ecosystem in the same way I try and grok the entire .NET ecosystem.

To me if you want to embrace mobile you need to understand it properly.  If you're just sticking with a particular stack because it's familiar to you you are doing yourself and the project a dis-service.  As many articles before me have said - you are not the programming language you code in - you need to embrace the polyglot nature of a good developer and do what is right for that project.

Hope you enjoyed this.  That's me for December I think :).  I have other things to do.
