---
date: 2015-09-19T00:00:00Z
title: "The 12 Factor App"
published: true
categories: [DevOps]
type: article
external: false
---

<style>
  body {
    background-color: #135a67;
    color: #c6d7d9;
  }

  a {
    color: #93bdc2;
  }

  .row h1 {
    display:none;
  }

  .homer a {
    color: #cfd8dc;
  }

  blockquote, p code, li code {
    background-color: #93bdc2;
    color: #135a67;
    border: none;
  }
</style>

> This article is adapted from a presentation I gave on the 12 factor app at ABB Dev Day 2015

![12 factor app](/images/twelvefactorapp/twelvefactorapp.001.png)

> The Twelve Factor App: A methodology for building software as a service apps.  

What a very grand statement.  In fact I'd even argue that the twelve factor app is less of a methodology and more just a collection of good advice based on years of real world experience that will let you build web apps in an ops-friendly way so that they can be deployed quickly, scaled easily and do all this in such a way that is not reliant on any particular technology or technologies.  In fact the 12 factor app work can be applied to any technology stack.

The 12 factor app guidelines are rather old.  The offical site was last updated in 2012 but this doesn't mean it is abandoned, just complete.  In an industry that views change, regardless of if it is necessary or not, as a sign of progress this may make some people uncomfortable but thats the way it is.  The 12 factor app advice has been distilled to the point that it doesn't need to change very much.

Finally an indication that the 12 factor app guidlines are sound is that many of the points to follow have been codified in modern technologies and are viewed as normal patterns of sfotware development.

![12 factor app](/images/twelvefactorapp/twelvefactorapp.002.png)

But first - a story about software.

![12 factor app](/images/twelvefactorapp/twelvefactorapp.003.png)

This is a bit of software.  A product or service that some team has created.  The software is at the MVP (Minimum Viable Product) stage and so it is in a sufficiently useablt enough state that people can start using it.  In order for people to use it we need to put the software somewhere.

![12 factor app](/images/twelvefactorapp/twelvefactorapp.004.png)

And so we deploy it.  This could be anything from FTPing PHP files up to a live production box to all the way at the other end of the crazy scale and building a massive 2GB Java Enterprise EAR file that gets handed over to an ops team responsible for propogating the file across a massive global websphere cluster.

One way or another the software gets deployed.

![12 factor app](/images/twelvefactorapp/twelvefactorapp.005.png)

As the software is now available - people use it.  I'm making the assumption that we have produced something people want to use.  I'm assuming there is a genuine need for the software and there has been some degree of user research undertaken to identify needs.

![12 factor app](/images/twelvefactorapp/twelvefactorapp.006.png)

Because people use your software they ultimatley pay you for the priviledge.  It could be a subscription model, a one off fee or simply just ad revenue - but one way or another you get money for the software you've built.

![12 factor app](/images/twelvefactorapp/twelvefactorapp.007.png)

The money that you've generated and then be used to improve the service, add new features or even build new services.  People then use these new features which generates more money and the glorious cycle continues.

But looking at this cycle is it suprising how little control we have over people using our software and therefore paying us for it.  You could do extensive user research to build the best possible service but  humans are fickle and a competitor with a slightly flashier banner may take all your users away.  What we do have control over is the software we produce and the deployment of it and of we can deploy new features and changes quickly we can adapt or pivot quicker which will allow us to respond to changing user needs rapidly and make people more likely to user our service.

![12 factor app](/images/twelvefactorapp/twelvefactorapp.008.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.009.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.010.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.011.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.012.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.013.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.014.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.015.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.016.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.017.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.018.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.019.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.020.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.021.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.022.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.023.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.024.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.025.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.026.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.027.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.028.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.029.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.030.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.031.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.032.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.033.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.034.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.035.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.036.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.037.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.038.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.039.png)
![12 factor app](/images/twelvefactorapp/twelvefactorapp.040.png)
