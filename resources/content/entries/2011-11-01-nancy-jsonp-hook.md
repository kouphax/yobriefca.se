---
date: 2011-11-01T00:00:00Z
title: Nancy JSONP Hook
published: true
categories: [.NET]
type: article
external: false
---
> 3 Nov 2011: As of Nancy v0.9 this is part of the core (same logic applies)

I needed to write a simple JSONP capable service recently and decided to run with [Nancy](http://nancyfx.org).  Turns out Nancy doesn't give us a way to do JSONP so I set about creating something that fitted my use case.  The fruits of my very very short labour (thanks to the super-hyper-flexible-powerfulness of the Nancy Pipeline) can be seen here.  The full source is available in the [Github repo](https://github.com/kouphax/nancy-jsonp) and hopefully we can get this pushed into the core of Nancy shortly (or at least part of the official repo one way or another) - just for the geek cred of course :)

<!--more-->

## Implementation Rundown

I took the same approach as the existing hooks (using the SassAndCoffee one as my implementation pattern) so created a static `Hooks` class with an `Enable` method that simply adds a new item to the end of the pipeline

```csharp
/// <summary>
/// Enable JSONP support in the application
/// </summary>
/// <param name="pipeline">Application Pipeline to Hook into</param>
public static void Enable(IApplicationPipelines pipeline)
{
    pipeline.AfterRequest.AddItemToEndOfPipeline(PrepareJsonp);
}
```

The meat of the work lies in the `PrepareJsonp` method

```csharp 
/// <summary>
/// Transmogrify original response and apply JSONP Padding
/// </summary>
/// <param name="context">Current Nancy Context</param>
private static void PrepareJsonp(NancyContext context)
{
    bool isJson = context.Response.ContentType == "application/json";
    bool hasCallback = context.Request.Query["callback"].HasValue;

    if (isJson && hasCallback)
    {
        // grab original contents for running later
        Action<Stream> original = context.Response.Contents;
        string callback = context.Request.Query["callback"].Value;

        // set content type to application/javascript so browsers can handle it by default
        // http://stackoverflow.com/questions/111302/best-content-type-to-serve-jsonp
        context.Response.ContentType = "application/javascript";
        context.Response.Contents = stream =>
        {
            // disposing of stream is handled elsewhere
            StreamWriter writer = new StreamWriter(stream)
            {
                AutoFlush = true
            };

            writer.Write("{0}(", callback);
            original(stream);
            writer.Write(");");
        };
    }
}
```

Let me highlight a few of the key areas here.

### Determine if Response need JSONP Padded

I decided to take a basic approach to determining if the request should return a JSONP response,

- If the content type of the current response is `application/json`
- If there is a query string parameter called `callback`

Here is what we need to satisfy that,

```csharp 
bool isJson = context.Response.ContentType == "application/json";
bool hasCallback = context.Request.Query["callback"].HasValue;

if (isJson && hasCallback)
{
```

I am sure this could be refined and open to suggestion (perhaps more applicable content types or extra possible configuration for the callback parameter).  I wonder if it is a bit safer to restrict requests that are GET requests seeing as that is the only way to do JSONP? Hmmmm.

Next thing I set the content type to `application/javascript` as that makes perfect sense

```csharp 
context.Response.ContentType = "application/javascript";
```

Finally I wrap the original responses content in the JSONP "padding" (the callback function) and write everything out.

```csharp 
context.Response.Contents = stream =>
{
    // disposing of stream is handled elsewhere
    StreamWriter writer = new StreamWriter(stream)
    {
        AutoFlush = true
    };

    writer.Write("{0}(", callback);
    original(stream);
    writer.Write(");");
};
```

So all in all very simple but damn I'm proud that I got off my arse and contributed to such a stellar community. hopefully someone finds this useful.