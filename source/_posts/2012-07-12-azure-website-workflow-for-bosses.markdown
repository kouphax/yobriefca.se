---
layout: post
title: "Azure Website Workflow for Bosses"
date: 2012-07-12 12:23
comments: true
categories: [Azure, ".NET", "Microsoft"]
---

> So consider my indefinite hiatus suspended definitely.  I suck at quitting!  But we wont dive into the whys and wherefores  around that now.

I've a confession to make, the "new" Azure, I like it.  Yeah I do, s'nice.  I like the way it blends PaaS with IaaS (though I do feel that adding 1st class node.js and PHP support is a bit of a tacked on marketing ploy).  So you dont have the "Add-On" model you get from Appharbor and Heroku but thats fine because this blending of "as a Services" gives you an alternate approach.  For a start you can just use the external services without them having to be "Add-Ons".  Sure you lose out on the nice automatic integration points for managing your services but it's not the end of the world and what happens when you want something that isn't available as an Add-On or worse not available as a cloudy SaaS thing?  Well I guess you need to go off to EC2 or similar and host your own (assuming you want to stick with the cloudy infrastructure).  But with Azure you also get another option - you want to use Mongo or some huge enterprisey platform that isn't SaaSified?  Stick it on an Azure VM.  Everything managed under one cloud provider.  Thats such a nice thing to have at your disposal.

But thats not the point of this post.  Most posts out there cover a workflow for Azure WebSites that make use of the portal to provision new sites (and then usually `git` to deploy) but that's weak sauce (the portal part at least - gits still aces).  No, no no - You want hyper-productivity, you want automated provisioning,  you want to be hip; node.js hip; and you want to feel like a boss hanging out on your command line while other "lesser people" click stuff with a silly little rodent.  I just read your mind, didn't I?  Well then - you got that with Azure.

## Setting Up

First things first you need to install a few things.

### Installing node.js + NPM

[Download](http://nodejs.org/#download) 

The tools are built and deployed in node via NPM, the node package manager so just run the one click installer and you'll be all setup.

### Installing Azure CLI Tools

Install these via NPM so fire up your terminal/command prompt/whatever and type

    npm install azure -g

Then let NPM install all the dependencies and what not (the `-g` option just tells NPM to install the tools globally so they are accessible from the command line)

### Adding your account

Next up you need to tell azure what account to use so fire this command into you command line

    azure account download
    
You'll then be taken to the Azure website and it should (assuming you're logged in already) download a `publishsettings` file.  Next up you need to import this file into your azure toolchain.

	azure account import YOUR_DOWNLOADED_FILE.publishsettings

After some scrolling text you'll be all set up and ready to deploy.  You'll be asked to delete the file you downloaded - probably best to do this :)

## Creating the app

OK then lets create an app - what we create is irrelevant here so lets just create the default ASP.NET MVC website and use that as our sample app.  Remeber though, if you're a cutting edge awesome-addict to switch from .NET 4.5 to 4 or else you'll get some issues when deploying your site to Azure.

> __UPDATE__ : The next few steps can be condensed, thanks to the feedback from Glenn Block (@gblock),
> <blockquote class="twitter-tweet" data-in-reply-to="224800579388575744"><p>@<a href="https://twitter.com/kouphax">kouphax</a> oh dude, you don't need all those steps! Do "azure site create my site --git" It will create the repo for you and add the remote!</p>&mdash; Glenn Block (@gblock) <a href="https://twitter.com/gblock/status/224855192800215040" data-datetime="2012-07-16T13:17:10+00:00">July 16, 2012</a></blockquote>
<script src="//platform.twitter.com/widgets.js" charset="utf-8"></script>
> This sets up your git repo during creation of the site, adds the remote endpoint and even generates a .gitignore file (which you need to edit yourelf for .NET projects.  WIN!!!!
>
> Anyway.... on with the old post.

Once you've created you app and saved it then you'll want to intialise an empty repo at the root of the solution,

	git init
	
Then we need to create a site on Azure we can push to.

## Creating a new site

Back to the command line now,

	azure site create SITE_NAME

If we execute this, again at the root of our solution, it will do a number of things,

1. Validate the request (site name availability and what not)
2. Create a new site on Azure
3. Add a new git `remote` to your `.git\config` file

## Pushing site

As I've said the last command actually added a remote repo to our git configuration so if we do the usual gitty stuff (you'll want to, for neatness sake, set up your `.gitignore` but we can skip this for brevity),

	git add -A
	git commit -m "Boring commit message"

Now we push,

	git push azure master

Done!

## Viewing your site

You could just browse to your new site but thats just not hip enough, hell no.  Instead lets launch it from the command line...

	azure site browse
	
That will launch our new site without having to touch a silly old, out of date rodent.

## Wrapping up

Ok so thats the basic workflow for using CLI tools to manage Azure sites.  You can do this with most of the Azure services (VMs, Certs, Cloud Services etc.) aswell.

So apart from making you look all hackery working in the terminal it does have other uses, specifically around the fact terminal command can be easily executed in a headless automated environment.  Think of creating new sites as part of your CI's deployment pipeline, or as part of some disaster recovery process.  The possibilities are endless... heck even write your own UI for managing sites or deploying new sites/VM/whatever!

So lets see - 

- CLI hackery coolness... ✓
- Node.js hipster bragging rights... ✓
- Azure website created and deployed with about 6 commands... ✓

Not bad for a few minutes work!
