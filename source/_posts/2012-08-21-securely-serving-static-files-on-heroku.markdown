---
layout: post
title: "Securely Serving Static Files on Heroku"
date: 2012-08-21 21:00
comments: true
categories: [Heroku, PaaS, Apache]
---

More than once I've had a need to host what is essentially static content on Heroku.  For example when building a simple internal web app for my company that used an existing API or Parse.  Invariably in this situation I have also needed to secure this app - nothing to drastic, no need for multiple user accounts or roles or any such nonsense (we generally run our non-essential systems on the honour system).  Basic auth is more than enough.

So, static content plus basic auth.  Heroku itself doesn't offer basic auth as an option (why would it?) and so you'd need to roll it into your app.  Once I hosted my static stuff inside a Scala/Play! app, once a Sinatra app and the other a Rails app - variety is the spice of life.  But each time I did it I felt a little dirty - it was noticeably slower serving this content from within an app and it seemed rather wasteful.  So, in a bid to stamp out technical debt, I've ripped out all those web frameworks an replaced them with good old Apache.

Now anyone with any knowledge of Apache will already be able to deduce the steps required here - but I want to note it for posterity, for myself, when I forget, in about a week.

## BuildPack

First things first you'll want to get your hands on a custom [Heroku BuildPack](https://devcenter.heroku.com/articles/buildpacks) - these are the things that prepare your app instance with all the software and support it needs to run.

I recommend you fork and clone the [heroku-buildpack-static](https://github.com/pearkes/heroku-buildpack-static) buildpack available from pearkes on GitHub.

## .htpassword

Next you need to generate your `.htpasswd` file that will serve as your store for your Basic Auth credentials.  The two simplest ways to generate one of these is to either use [htaccesstools.com's htpasswd generator](http://www.htaccesstools.com/htpasswd-generator/) or generate on via the command line.

The simplest command to generate one is this,

	htpasswd -cb <password_file> <username> <password>
	
For example,

	htpasswd -cb .htpasswd james password

Will generate a file in the current directory called .htpasswd (the standard name for these files) with a single entry of username = james and password = password.

## Updating the BuildPack

Either run the above command in the `heroku-buildpack-static/conf` folder or move your `.htpasswd` file into the that folder.

Next up you want to configure apache to authenticate against that generate password file.  Adding the following lines to the `httpd.conf` file in the `heroku-buildpack-static/conf` will lock all assets under your apps site down,

{% codeblock lang:apache %}
    AuthType      Basic
    AuthName      "Authentication Required"
    AuthUserFile  "/app/apache/conf/.htpasswd"
    Require       valid-user
{% endcodeblock %}

This block should be added to the configuration section starting `<Directory />` i.e.

{% codeblock lang:apache %}
  <Directory />
      Options FollowSymLinks
      AllowOverride None

      AuthType      Basic
      AuthName      "Authentication Required"
      AuthUserFile  "/app/apache/conf/.htpasswd"
      Require       valid-user

      Order deny,allow
      Deny from all
  </Directory>
{% endcodeblock %}

You'll notice the `AuthUserFile` points to the correct absolute path for our custom .htpasswd file.  If you called it anything else please update this line accordingly.

Commit and push that to your forked repo.
	
## Using the BuildPack
	
Finally we need to tell Heroku to use the custom buildpack.  If you have yet to create an app then you can specify the buildpack during creation,

	heroku create <app name> --buildpack <url to your repo>
	
If you have created you app already you can add the buildpack through an additional config setting

	heroku config:add BUILDPACK_URL=<url to your repo>
	
Push your new or updated site to Heroku and watch in awe as your site is that bit more secure.


