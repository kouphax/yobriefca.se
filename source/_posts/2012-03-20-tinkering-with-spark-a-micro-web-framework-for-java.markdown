---
layout: post
title: "Tinkering with Spark: A Micro Web Framework for Java"
date: 2012-03-20 21:37
comments: true
categories: [Java,Spark,Web]
---

<div style="text-align:center;margin-bottom:20px;">
<img src="/images/blog/spark_logo_blue.png" style="border:none; box-shadow:none; -webkit-box-shadow:none;" />
</div>

You can't call yourself a language these days without having your own [Sinatra](http://sinatrarb.com) clone.  Not even, it seems, if you are Java.  I know - it sounds odd that Java, the language that turns XML into stacktraces, has a micro web framework of it's own - [Spark](http://sparkjava.com).  I'm well aware as of JRE 1.6 has a built in `HttpServer` class but there is such as thing as __too close__ to the metal :)  Also Sparks logo is fairly epic.

[Spark](http://sparkjava.com)'s site has a great little piece about the "why" and that is what stuck with me.  Especially this bit,

> Its intention is to be used by Java developers that want or are required to develop in pure Java. 

I know a few people who are "required" to develop in pure Java and anything that might make their lives easier would be welcomed.  Being completely out of loop with Java since before Maven made an appearance I had a lot of catching up to do just to get the project running.  In fact I went with SBT as it made things a lot easier for me and my mavenless brain.  I did have to tinker with the dependencies in the `ivy2` directory due to a [known issue](https://github.com/perwendel/spark/pull/15) but if I can do it anyone interested in this can to - if not give me a shout.

## App.java

A Spark app simply uses `main` as an entry point.  Within the method you can call certain methods to set up routes and filters and whatnots that your app will respond to.  For example

{% codeblock lang:java %}
import static spark.Spark.*;
import spark.*;
import java.io.*;

public class App {

  public static void main(String[] args)  {

{% endcodeblock %}

Standard Java stuff here, bringing in some of the necessary `spark` resources,

{% codeblock lang:java %}
    before(new Filter("/") {
      @Override
      public void handle(Request request, Response response) {
        boolean authenticated = request.queryParams("password").equals("secret");
        if(!authenticated){
          halt(403, "Incorrect password, hacker alert, hacker alert!!!!");
        }
      }
    });
{% endcodeblock %}

Spark allows us to define filters that run before and after requests.  It is also possbile to restrict them to certain routes or simply catch all routes - for example my filter only matches the "/" route.  A couple of other things are worth mentioning here,

1. The `Request` object gives you access to many things you would expect i.e. the query parameters.  It gives you so much more than this but I'll leave the [docs](http://www.sparkjava.com/readme.html#title2) to describe this.
2. Just like in Sinatra it is possible to simply `halt` the request chain at any time.  Here I am halting execution unless the request conforms to my very strict and highly secure authentication scheme.

{% codeblock lang:java %}
    get(new Route("/") {
      @Override
      public Object handle(Request request, Response response) {

        // extract the name from the request
        String name = request.queryParams("name");

        // set the response type
        response.type("text/html");

        // return some html
        return "" +
          "<DOCTYPE html>" +
          "<html>" +
          "  <head>" +
          "  </head>" +
          "  <body>" +
          "    <h1>Hello " + name + ", Spark here.  Howrya?</h1>" +
          "  </body>" +
          "</html>";
      }
    });

  }
}
{% endcodeblock %}

Finally I am defining a route ("/") that responds to the GET verb and introspects the query parameters returning a very complex view.  Routes can also be parameterized (`/user/:name` for example). Even given the slightly verbose nature of Java syntax this example seems clean enough (albeit a rather simple example).

## SBT Build File

For the curious among you (you got this far so you must be slightly curious... and rather odd) the SBT file required to get this bad boy running is nice and simple,

{% codeblock lang:scala %}
resolvers += "Spark Repository" at "http://www.sparkjava.com/nexus/content/repositories/spark/"

libraryDependencies ++= Seq(
  "spark" % "spark"  % "0.9.9.3-SNAPSHOT" exclude("org.apache.commons", "commons-io")
)
{% endcodeblock %}

A quick `sbt run` command and we are running a standalone Spark app (powered by an embedded Jetty server).  Aces.

## Mopping Up

OK so thats the basics of the framework but then again there really isn't much other stuff besides picking a port and redirecting.  This means there is some things still missing that would really make this framework really stand out,

- View rendering.  Even if it is just plain HTML it would be a big leap forward (turns out an [issue](https://github.com/perwendel/spark/issues/1) is already open too).  
- Static Content.  Would be nice to define a rule for serving static content without having to define a route or filter manually
- Session support.  Again an [issue and pull request](https://github.com/perwendel/spark/pull/12) is already available for this.

Good work [perwendel](https://github.com/perwendel/) keep it up :)

As always my code samples are available on [Github](https://github.com/kouphax/spark-tinkering).


