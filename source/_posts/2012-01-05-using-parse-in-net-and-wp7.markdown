---
layout: post
title: "Using Parse in .NET and WP7"
date: 2012-01-05 13:59
comments: true
categories: [".NET", "Parse", "WP7", "MonoTouch"]
---

> UPDATE: Being completely wrong in assuming WP7 supports `dynamic` which apparently it doesn't the current build wont run in WP7.  So another item on the list is a version that doesn't rely on `dynamic` I guess.  Sorry for misleading anyone on that front - I did say it was rough and mostly untested right?

[Parse](https://www.parse.com/) is a site aimed at providing a simple service for pushing and pulling data on mobile devices.  You can create applications and store your mobile data there, create users, provide logon/off mechanisms and even achieve push notifications.  There are 2 ways to access [Parse](https://www.parse.com/)

<!-- more -->

1. Via its natively compiled binaries (on iOS and Android)
2. Via a its Rest API (for all non iOS/Android devices and desktops)

Now I wanted to try and create bindings for the iOS library so I could use it for a Monotouch application I've been playing with but truth be told the whole process confused the hell out of me.  Thing was the REST API gave me everything I needed so I started recently to write a dynamic wrapper around the API.  Currently built on .NET in Visual Studio (Mono on my list) with a few sanity tests it works well enough to put it out there incase anyone was wanting to do something similar and could extract value from it.

It's pretty early at the minute - in fact all you can do with this first spike is basic object CRUD.  No querying yet, no special user management features etc.  But they will come.  So what could you use it for?  Well a number of things,

- WP7 Cloud Storage
- Monotouch (and Monodroid) storage until someone smarter than me creates proper bindings to the actual iOS frameworks
- It's an object store so why not use it to store data on any web app?
- Just for tinkering.

It makes use of `dynamic` from .NET 4 so you can kind of pass it arbitrary stuff and get arbitrary stuff back (Parse is schemaless so this makes sense for me).  It also means you wont have to create concrete classes for every single thing you want to store.

If you want to run the tests you'll need to sign up for Parse, create an app and update the `consts` in `describe_Parsely` to point to your new app (and authenticate).

## Sample Uses

{% codeblock lang:csharp %}
// create your parsley API
Parsley parsley = new Parsley(applicationId, masterKey);

// create new class
dynamic response = parsley.Create("MyClass", new { CoolProperty = 1  });

// do other cool stuff
parsley.Update("MyClass", response.objectId, new { CoolProperty = 4});

dyanmic myObject = parsley.Retrieve("MyClass", response.objectId);

parsley.Delete(myObject.objectId);
{% endcodeblock %}
## Outstanding Tasks

Lots.  But here is the main ones I see right now.

- Compile on Mono and show MonoTouch some Parse love.
- Samples (for WP7, Monotouch, possibly Monodroid)
- Add Queries
- Add User Management

It's probably fairly rough right now and could do with some TLC if anyone wants to pitch in?  I've given it a name (Parsley) but that in no way implies this is going to be a long running project or anything.  I just like naming things.  Get it while it's hot, the code is in my [usual place](https://bitbucket.org/kouphax/parsley).

## Pre-Post Update

Rather than update this blog post I decided to tack this on here.  With BitBucket experiencing some issues at present I spent about 10 minutes hacking in the user management features.  No tests written yet (oh no I have broken all the rules and must be beaten with oversized crayons) so lets just say that it's very edge, very experimental at the minute.  Also bonus - returning a collection of objects is implemented as `Query` but again totally untested (fast and loose - it's how I roll son).  There be dragons there therebe!

Happy hacking.

Can I have my WP7 MVP now please?  **Kidding**