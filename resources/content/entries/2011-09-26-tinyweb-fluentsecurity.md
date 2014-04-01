---
date: 2011-09-25T23:00:00Z
title: Tinyweb Fluent Security
published: true
categories: [.NET]
type: article
external: false
---
I've been working on a little project recently that is using Tinyweb.  It's a typical little CRUDdy app and makes use of typical security features in a web app.  Tinyweb itself doesn't provide any sort of custom security features out of the box but as it sits on top of ASP.NET you can avail of all the normal membership stuff.  Problem is though - the default membership stuff, when working directly with it, is a bit cumbersome.  I recently tinkered with a project for ASP.NET MVC called [FluentSecurity](http://www.fluentsecurity.net/) that added a fluent syntax to configuring security for ASP.NET MVC applications.  Taking a healthy dose of inspiration from this project I rolled a lightweight Tinyweb version of my own.  I figure this might be useful to someone else so I have now created a new project specifically for this.  It's probably totally broken in areas but the areas I've written tests around appear to work.  

The source is available in a [BitBucket repository](https://bitbucket.org/kouphax/tinyweb-fluentsecurity) so feel free to critique.  Once I get it tested better, documents written up and generally applied a bit of sipt and polish I'll push it to Nuget and the fun can really begin.

Tinyweb.FluentSecurity
======================

FluentSecurity comes as a 2 part solution

1. The `Security` class is the main entry point for configuring FluentSecurity and authenticating users
2. The `SecurityFilter` class is a Tinyweb filter that performs auth tests on the current request

Security
--

The `Security` class provides a central location to configure FluentSecurity.  `Security` is also responsible for executing the auth tests you pass to it.

### Security.Configure(Action<Configurator> configurator)

`Configure` lets you define the configuration for you module.  It accepts an Action that can be used to define rules for each handler.  For example

    Security.Configure(c =>
    {
        c.For<RootHandler>().DenyAnonymousAccess();
        c.For<AdminHandler>().RequireRoles("Admin");
        c.For<UserHandler>().DenyRoles("Admin");
        c.For<SecretHandler>().AllowVerbs(Security.AllowedVerbs.GET | Security.AllowedVerbs.POST);
    });

    Tinyweb.Init();
    
We call `Security.Configure` just before we Init Tinyweb (though it can be done at anytime realistically speaking).  The configuration block shows off most of the ways you can configure handlers e.g.

1. `DenyAnonymousAccess` will prevent, as the name suggests, all anonymous users from accessing this handler
2. `RequireRoles` specifes 1..N roles that are required to access this handler
3. `DenyRoles` specifies 1..N roles that aren't permitted to access this handler
4. `AllowVerbs` restricts the HTTP Verbs that can be used to access this handler (PUT, POST, GET etc.)
5. `WithCustomRule` (not listed) allows you to specify a function that can be used to apply a custom rule to each request

Each method returns the configuration object so it is possible to chain the calls to create more complex rules.

    Security.Configure(c =>
        c.For<EditHandler>()
            .DenyAnonymousAccess()
            .RequireRoles("Author")
            .DenyRoles("Reader")
            .AllowVerbs(Security.AllowedVerbs.POST);

Obviously some combinations will not make sense, in fact AllowedVerbs needs to be tweaked as it may only apply to certain roles (so the combinations don't make sense right now).  Currently FluentSecurity doesn't care and it probably never will if you try and create illogical combinations.

### bool Security.Test(RequestContext req, HandlerData d)

This will run all pre-configured rules against the current request to determine if the request is authorised to continue.  It will return a true/false result depengin on whether the rules pass or not.

    bool granted = Security.Test(context, data);

### IResult Security.Validate(RequestContext req, HandlerData d)

Implements the typical SecurityFilter use case.  This function will call the `Test` method and returns `Result.None()` if the test passes (may be configurable in the future) otherwise it executes the `Security.OnAccessDenied` function and returns the `IResult`.  If no `OnAccessDenied` is defined `Result.None()` will be returned.

    IResult result = Security.Validate(context, data);
    
This is simply shorthand for now but may be expanded with some custom logic in the near future.

### Security.OnAccessDenied

This property is used to define a function that can be used to return a result should access been denied.  This is done lazily as it allows you access to the `RequestContext` and `HandlerData` so you could implement per-request handling of the Result (redirects or return URLs etc.)

    Security.OnAccessDenied = (c, d) => Result.Redirect<AccessDeniedHandler>();

SecurityFilter
--

The `SecurityFilter` class is simply a Tinyweb filter that performs the request validation for authentication.  Nothing special here move along now.

For Now and Up Next
===================

So thats all there is for now.  Code is, as I've said, available at the [BitBucket repository](https://bitbucket.org/kouphax/tinyweb-fluentsecurity).  I'm starting to build up some docs and stuff now and maybe tweak and add useful features.  If there is anything you would like to see raise it as an [issue](https://bitbucket.org/kouphax/tinyweb-fluentsecurity/issues?status=new&status=open) and I'll get around to it.