---
date: 2011-12-03T00:00:00Z
title: So this mobile stuff then... (Hybrid)
published: true
categories: [Mobile]
type: article
external: false
---
It's fair to say over the past year or so I've dabbled in a bit of mobile development.  I've been involved in a number of  projects (from a technical perspective),

- Web Based, Tablet Focused Analytics Dashboard (Custom HTML and [HighCharts](http://www.highcharts.com/))
- Web Based, Tablet Focused Data Capture application ([Sencha Touch](http://www.sencha.com/products/touch))
- Hybrid application for a teleco ([jQuery Mobile](http://jquerymobile.com) and [PhoneGap](http://phonegap.com))
- Mobile Hackathon ([jQuery Mobile](http://jquerymobile.com) and [PhoneGap](http://phonegap.com) and [Parse](http://parse.com))
- Hybrid Application for a Bank ([Sencha Touch](http://www.sencha.com/products/touch) and [PhoneGap](http://phonegap.com))
- Various iPhone and iPad POCs for a health based project ([MonoTouch](http://xamarin.com/monotouch), XCode)
- Technology evaluations (Native Apple Development, [Parse](http://parse.com), [Sencha Touch](http://www.sencha.com/products/touch)], [PhoneGap](http://phonegap.com), [NimbleKit](http://nimblekit.com/index.php), [jQuery Mobile](http://jquerymobile.com), [Backbone](http://backbonejs.org/), [Spine.js](http://spinejs.com), [Appcelerator](http://www.appcelerator.com/), [MonoTouch](http://xamarin.com/monotouch), [Monodroid](http://android.xamarin.com/) and plenty more...)
- Consulted on various projects around the whole "native vs hybrid vs web" thing

## That Hybrid Word

One thing I've found is that many clients are using the old "a web app will never be as polished as a native app" argument but TBH that really isn't the case.  This is down to a lack of real understanding and abuse of the word in the industry.  A bit of hybridism can go along way in creating a rich experience.

### A Rose By Any Other Name...

Many people break mobile applications into 3 VERY distinct areas,

1. __Native__ - app installed on the device, written using the default toolkit for that platform.
2. __Hybrid__ - consists of a transparent "native" layer and a Web View into which HTML, JavaScript and CSS is loaded (generally stored within the native package).  The nativelayer provides and API for the JavaScript model in the Web View to call.  Think PhoneGap.
3. __Web__ - HTML, CSS, JavaScript assets hosted on the web and accessed from a mobile device.  Possibly some native-like features such as icons and removing the browser chrome.

The problem with the hybrid definition there is that it is, well, wrong.  There isn't a clear distinction between the native and hybrid and there shouldn't be.  If I spun up a Silverlight app that put a Web View in the middle to display some HTML I wouldn't call it hybrid.  If I wrote an Android app that used a library written in Scala it wouldn't be hybrid.  Or even if I wrote a Monotouch app using some bindings to the [Flurry](http://www.flurry.com/) framework - nope not hybrid.

### The Charting Example

A good example of "hybrid" is charting.  There aren't a great deal of nice flexible charting libraries for iOS, but there is for JavaScript/HTML5.  So some of our projects have made use of HighCharts and Sencha Charting inside a transparent UIWebView to render charts that are flexible and rich.  I wouldn't bother pinning the hybrid tag to that app - it's simply making use of whatever technologies helped us get the job done in the best way possible.

I understand the use of hybrid in sales pitches or to simply convey meaning to people who are less technical but the problem is people are starting to develop opinions based on these words alone which is disatorous.  I've had customers tell me that they want native because that hybrid stuff is ugly.  True some hybrid apps are ugly as sin (Aer Lingus iPhone app for one) but then so are some native ones.  It's unfair to collectively throw away and entire type of app because of poorly misunderstood words.

## But, But, But... Reusability

People use the hybrid term to show that they have thought about portability.  Write once, run anywhere.  In that sense I guess using hybrid makes sense (but that sort of talk is really part of the sales talk right?).  But then it would be just as easy to say that "elements of the solution will be written in a platform agnostic language to minimise code rewrites on different devices" - or something like that.  In fact that would be better.  That way you cover off more than "hybrid".  You've included the things like [MonoTouch](http://xamarin.com/monotouch) or [Appcelerator](http://www.appcelerator.com/) as well.  You've moved away from implementation specifics at the point where they shouldn't matter anyway!  Thats got to be a good thing.

## Ah ha but what about Skills?

People go "hybrid" for another good reason - because they have no experience in the native platform.  I used to be one of those people but I'm not convinced that this is a good reason on its own.  If you want to make a compelling experience your limitation should never be your lack of skill.  Get skilled.  Obj-C isn't as scary as it seems - in fact I'd rather write Obj-C than Java.  Dig deep - thats what we've got the internet for - understanding and learning (well that and porn).

## Awareness and Education

So what am I saying?  Well for a start I'm not saying we totally abandon the word hybrid and mock anyone who uses it.  Hells no.  It's still useful as I've already mentioned above. All I ask is that you make sure you know what you are talking about and that the people you are talking to know what you are talking about.  That and don't just take the easy route because it's easier for you.
