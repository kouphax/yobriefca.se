---
date: 2011-12-05T00:00:00Z
title: Rolling Your Own PhoneGap with MonoTouch
published: true
categories: [Mobile]
type: article
external: false
---
[PhoneGap](http://phonegap.com/) is a clever little idea.  Take a transparent native wrapper for a mobile platform, stretch a 100% x 100% web view and provide a JavaScript API that acts as a bridge to native methods that control the device features such as cameras, GPS and Contacts.  It brings all those lovely web people into the mobile space.  Truth be told PhoneGap isn't powered by flaked unicorn and rainbow essence - it's fairly simple to achieve yourself.

So I was thinking, as I do when I am bored, that it would be super awesome if MonoTouch could do the PhoneGap.  Turns out there have been some attempts in the past to create bindings but I barely understand the posts so I felt disappointed.  Then curious.  Then I started hacking.  Then happy.  Then bored again.  The happy once more.  Then slightly frustrated.  Finally I started writing this post and I think I feel a bit tired because it's late an I've had a hard day.

Anyway what follows is a very primitive start to what is essentially a PhoneGap, possibly NimbleKit, for MonoTouch.  Thats cool becuase not only would you be saying "Screw You Objective-C!!" by using MonoTouch but you'd be sort of saying "Hey MonoTouch - HTML5 just had your mum, twice" as well.  Hey I'd use a NimbleKit/PhoneGap-like framework for MonoTouch if it was available and offered a clever way of mixing Native with Web but thats just me.  Hence I am writing this post.

## The Recipe

There are various components that would make up a framework like this.

1. A fullscreen Web View (lets call it the webside)
2. A controller that handles requests from the webside (lets call it the nativeside)
3. A JavaScript API on the webside that talks to the nativeside

Simple enough so lets start proving things.

### The Webside

Using a simple Single View iPhone Project in MonoDevelop I added some local resources.

```
    |
    |_ www
    | |_ index.html
    |
    |_ AppDelegate.cs
    |_ MainViewController.cs
```

For now the index.html file has some basic content

```html 
<!DOCTYPE html>
<html>
    <head>
        <title></title>
    </head>
    <body>
        <h1>Hello UIWebView</h1>
    </body>
</html>
```

Next up I created a full screen `UIWebView` and asked it to point to the `index.html` I just created in `wwww` folder (which by the way should be set as "Content".

```csharp 
public override void ViewDidLoad ()
{
    base.ViewDidLoad ();

    using (UIWebView view = new UIWebView(new RectangleF(0f, 0f, 320f, 460f)))
    {
        // obtain path to actual content file
        string path = NSBundle.MainBundle.PathForResource("www/index", "html");

        // create an address and escape whitespace
        string address = string.Format("file:{0}", path).Replace(" ", "%20");

        // create url and request
        NSUrl url = new NSUrl(address);
        NSUrlRequest request = new NSUrlRequest(url);

        // load request and add to main ?view
        view.LoadRequest(request);
        this.View.AddSubview(view);
    }
}
```

Running this gives us what we would expect (I hope you expect this otherwise I'd be worried).

![Sample Output for Web View](/images/blog/monotouch-phonegap/capture1.png)

Webside more or less spiked for now.  Next up the nativeside.

### The Nativeside

So how is the native stuff supposed to respond to requests made via the mysterious JavaScript API from the webside.  The approach that makes the most sense here is to set up an internal HttpListener that reacts to web requests.  This means we can, thanks to the lack of a Same Origin Policy in UIWebViews via the `file://` protocol, make Ajax requests to said listener to do stuff and return appropriate responses.

In the AppDelegate class I created a basic HttpListener that simply returns a simple JSON response of true if a request is made.

```csharp 
public HttpListener listener;

public void HandleRequest (IAsyncResult result)
{
    //Get the listener context
    HttpListenerContext context = listener.EndGetContext(result);

    //Start listening for the next request
    listener.BeginGetContext(new AsyncCallback(HandleRequest), listener);

    string response = "true";
    byte[] responseBytes = System.Text.Encoding.UTF8.GetBytes(response);

    context.Response.ContentType = "text/json";
    context.Response.StatusCode = (int)HttpStatusCode.OK;
    context.Response.ContentLength64 = responseBytes.Length;
    context.Response.OutputStream.Write(responseBytes, 0, responseBytes.Length);
    context.Response.OutputStream.Close();
}

public override bool FinishedLaunching (UIApplication app, NSDictionary options)
{
    window = new UIWindow (UIScreen.MainScreen.Bounds);

    viewController = new JsBridgeViewController ();
    window.RootViewController = viewController;
    window.MakeKeyAndVisible ();

    listener = new HttpListener();
    listener.Prefixes.Add("http://*:30001/");
    listener.Start();

    listener.BeginGetContext(new AsyncCallback(HandleRequest), listener);

    return true;
}
```

### The Final Step - The Bridge

So we need to talk to this "server" from our JavaScript.  Easy stuff - for now we just make a simple ajax call to our endpoint in the `index.html` (synchronous for code simplicity).

```html 
<script type="text/javascript">
    var request = new XMLHttpRequest();
    request.open('GET','http://127.0.0.1:30001/', false);
    request.send();

    if(request.status == 200){
        alert(JSON.parse(request.responseText));
    }
    else{
        alert("Error");
    }

</script>
```

Running this once again reveals that all is sweet...

![Sample Output for Web View with Nativeside call](/images/blog/monotouch-phonegap/capture2.png)

## Now What?

Well thats about as far as I've actually gotten but this is far enough to satisfy myself that it's possible to talk between the web view and the native stuff.  Yeah I know it's not rocket science but I needed to prove this to myself.  So whats next?  Well I want see what I can do with regards to making real calls to real things, perhaps even venture into NimbleKit territory and generate native elements such as ViewControllers and Tabs - who knows!

_(MYSTERIOUS EXIT MUSIC)_

_(FADE TO BLACK)_

To Be Continued...

_(EVIL CACKLE HEARD IN THE DISTANCE)_

_(END SCENE)_