---
date: 2014-02-20T00:00:00Z
title: SPAs and Enabling CORS in Spark
published: true
categories: [Java]
type: article
external: false
---
Having been tinkering with [Spark](http://sparkjava.com) again I needed to build a simple bit of infrastructure for building single page applications that could be deployed anywhere. I also needed to potentially support CORS (Cross Origin Resource Sharing) so that remote client-side JavaScript applications could actually access the JSON API aspect of the solution. Doing this with Spark is pretty simple but I thought I'd document the process to avoid some people (or future me) having to answer the same questions again. I'm going to assume you know a bit about Spark, if not have a skim of the [README](http://sparkjava.com/readme.html) on the site.

## Building a SPA

An SPA or Single Page Application is an application that serves a single HTML page and included JavaScript on that page is responsible for dealing with talking to the server, getting data, rendering different views and handling view navigation. This is different from the traditional approach of rendering views on the server through the typical request/response lifecycle. I'm not going to debate the merits and drawbacks of either strategy in this post let's just assume that you've decided to go with an SPA in a Java context and you want something simple. I'm also not going to suggest any sort of front-end framework here - that'll depend on what you need, right? So a sample anatomy of an SPA could look like this,

- The root path at `http://website/` returns a static `index.html` file
- The `index.html` file loads the necessary static assets such as JavaScript and CSS
- The JavaScript calls local API endpoints to load data<a name="_1"></a>[<sup>1</sup>](#1) and render content through client side templates or straightforward DOM manipulation

That's it. So in Spark to support these needs you need to do a few things,

## The API endpoints

JavaScript loves JSON, it makes sense. So we need to create an API endpoint that returns JSON responses.

```java
get(new JsonRoute("/api") {
    @Override
    public Object handle(Request request, Response response) {
        return new HashMap<String, Object>() {{
            put("message", "Hello World");
        }};
    }
});
```

This stubbed route simply returns a map of key value pairs. If you look at the route type (`JsonRoute`) you'll notice it's not a typical Spark route type. In fact it's a class implemented by me. `JsonRoute`, we'll look at the code shortly, makes use of a Spark class - `ResponseTransformerRoute` which is a specialized route type that supports the ability to "transform" the returned response model (the object that get returned by the routes handle method). In this implementation, below, I'm simply passing the model into a Jackson `ObjectMapper`,

```java
public abstract class JsonRoute extends ResponseTransformerRoute {

    private final ObjectMapper mapper = new ObjectMapper();

    protected JsonRoute(String path) {
       super(path, "application/json");
    }

    @Override
    public String render(Object model) {
        try {
            return mapper.writeValueAsString(model);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
 }
```

This allows us to abstract out the process of transforming API responses into JSON and serving the correct response type as well. Yep I could have done this directly in my route declaration but I like to keep the main entry point for my application clean and readable as that's where most people will start to reason about how the application behaves. So that gives us a nice API endpoint. We can access this by running the application and hitting `http://locahost:4567/api`.

## Serving Static Assets

Next up we want to be able to support serving the JavaScript, CSS and HTML that makes up our non-api aspects of our solution. Spark makes this easy

```java
staticFileLocation("/public");
```

This command (declared in the main method) will wire up a handler to serve resources that are stored in `src/main/resources/public` as static files. You can also specify external files (outside the built jar for example) but I've went with resources in this example. Then we create an index.html page in this folder along with some JavaScript and CSS,

```java
<!doctype html>
<html>
    <head>
        <title>Template</title>
        <link rel="stylesheet" href="/css/styles.css"/>
    </head>
    <body>
        <h1 id="css-broken-warning">The stylesheet didn't load or something, maybe</h1>
        <h1>Template</h1>
        <script type="text/javascript" src="/js/app.js"></script>
    </body>
</html>
```

The CSS file in this case hides the CSS Broken Banner to prove this works

```css
#css-broken-warning {
    display: none;
}
```
And the JavaScript simply appends a new H2 node to the document, again just to prove the whole thing works.

```javascript
(function(document, undefined) {
    var element = document.createElement("h2");
    element.innerHTML = "JavaScript Works";
    document.querySelector("body").appendChild(element);
}(document))
```

You'll rip these dummy files out when you actually being to write your app and it's better to write test for this sort of thing generally.

We can run the app again and browse to `http://localhost:4567/index.html` and make sure everything works.

## Serving the Root

If you go to the root URL right now (`http://localhost:4567/`) you see that we still haven't satisfied the first condition of our SPA structure, we need to navigate to the index.html page specifically to see the app in action. To support the route URL serving the `index.html` file we can use a Spark before filter.

```java
before(new Filter("/") {
    @Override
    public void handle(Request request, Response response) {
        try (InputStream stream = getClass().getResourceAsStream("/public/index.html")) {
            halt(200, IOUtils.toString(stream));
        } catch (IOException e) {
            // if the resource doesn't exist we just carry on.
        }
    }
});
```

The filter, locked down to the `/` route checks for the index.html resource and spits its contents out via a halt call (stop processing filters and handlers for this route). If it doesn't find it it just passed responsibility to the remaining filters and handlers. But why a filter? Well you could use a route and do something similar,

```java
get(new Route("/") {
    @Override
    public Object handle(Request request, Response response) {
        try (InputStream stream = getClass().getResourceAsStream("/public/index.html")) {
            return IOUtils.toString(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
});
```

But I think the filter is a bit neater. I the route case we have to return something. We can't just pass on to the next handler so instead we throw which may not make sense in all cases. Again it comes down to need here and YMMV so you now have 2 approaches to do the same thing. So running the app and hitting `http://localhost:4567/` gives use our SPA infrastructure in place.

## CORS

Supporting CORS in any web framework is a matter of Header manipulation - if you can do that you're sorted. Spark is no exception. CORS can be configured in a few different ways, you can set a few different types of headers, specify different header values etc. The example I provide here aims to give you a bit of configurability but uses it in the most permissive way possible (allowing any origins, header values and HTTP methods).

```java
private static void enableCORS(final String origin, final String methods, final String headers) {
    before(new Filter() {
        @Override
        public void handle(Request request, Response response) {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
        }
    });
}
```

This method, which should be called early and called once in the main method, attaches a Spark filter to every route (you should alter that to your needs) that sets the necessary CORS headers to the passed in values. We can call this from the main method like this,

```java
enableCORS("*", "*", "*");
```

Which is as open as possible (more than it needs to be in most cases) for remote clients. You can test this using [test-cors.org](http://client.cors-api.appspot.com/client) on your local instance.

Now we have a simple CORS enabled setup to build you SPAs using the wealth of technologies that Java offers. [Full sample is on Github](https://github.com/kouphax/spark-spa). Happy trails.

<hr/>
<sup><a name="1"></a>[1](#_1): You could, if you desired, return HTML snippets, or JS commands rather than data but for the purposes of this post lets assume an API as this a lot nicer when supporting 3rd party apps that want to integrate - DATA IS KING and all that.