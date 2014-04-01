---
date: 2011-02-24T00:00:00Z
title: Mobile Redirection for Web Applications in ASP.NET MVC
published: true
categories: [.NET]
type: article
external: false
---
<p><span style=""> </span>Mobile redirection is simple stuff but what happens when you need to deep link into the mobile application?</p><p>On a recent project we needed to produce mobile (iPad specifically)  equivalent.  The desktop app itself was developed using ExtJS (3.3.1) and had  three primary entry points,</p><ol><li>Logon Screen</li><li>Home Screen</li><li>Product Screen (New/View/Edit)</li></ol><p>Due to the way the project was implemented these became as 3 distinct  pages/controllers/actions.  Users could receive emails with links to a  particular product and they would go directly to that view (with a login  redirect if not previously authenticated).  The mobile solution, however,  written using Sencha Touch (consistent development experience, native-esque UI  with little effort) is a single page application.  This presents a problem when  the user is on a compatible mobile device and they receive a link to a  particular placement - how do we push that sort of deep linking into a single  page app.  Well on the client side frameworks such as Backbone.js, jQuery Mobile  and Sencha Touch [anyone got more please?] all offer history support using hash  navigation.  That's the client side sorted but how do we translate, say,  /Product/Show/12345 into /Mobile#placement/12345?</p><h2>MobileRedirectAttribute</h2><p>Firstly I created an extension of the AuthorizationAttribute that will act as  an interim redirection and request parser between the mobile and desktop  solutions.  Here's the code (usage follows),</p><p><div class="highlight"><pre><code><span class="c1">/// &lt;summary&gt;</span>
<span class="c1">/// Redirects to the mobile view if on a supported device</span>
<span class="c1">/// &lt;/summary&gt;</span>
<span class="na">[AttributeUsage(AttributeTargets.Class | AttributeTargets.Method, Inherited = true, AllowMultiple = false)]</span>
<span class="k">public</span> <span class="k">class</span> <span class="nc">MobileRedirectAttribute</span> <span class="p">:</span> <span class="n">AuthorizeAttribute</span>
<span class="p">{</span>
    <span class="k">private</span> <span class="kt">string</span> <span class="n">_clientFragment</span><span class="p">;</span>

    <span class="c1">/// &lt;summary&gt;</span>
    <span class="c1">/// Default Constructor</span>
    <span class="c1">/// &lt;/summary&gt;</span>
    <span class="k">public</span> <span class="nf">MobileRedirectAttribute</span><span class="p">()</span>
    <span class="p">{</span>
        <span class="n">_clientFragment</span> <span class="p">=</span> <span class="kt">string</span><span class="p">.</span><span class="n">Empty</span><span class="p">;</span>
    <span class="p">}</span>

    <span class="c1">/// &lt;summary&gt;</span>
    <span class="c1">/// Constructor that takes an argument</span>
    <span class="c1">/// &lt;/summary&gt;</span>
    <span class="c1">/// &lt;param name="clientUrl"&gt;The url fragment we should append to the url&lt;/param&gt;</span>
    <span class="k">public</span> <span class="nf">MobileRedirectAttribute</span><span class="p">(</span><span class="kt">string</span> <span class="n">clientFragment</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="n">_clientFragment</span> <span class="p">=</span> <span class="n">clientFragment</span><span class="p">;</span>
    <span class="p">}</span>

    <span class="c1">/// &lt;summary&gt;</span>
    <span class="c1">/// Tests if this request originates from a supported mobile device </span>
    <span class="c1">/// and redirects as appropriate</span>
    <span class="c1">/// &lt;/summary&gt;</span>
    <span class="c1">/// &lt;param name="ctx"&gt;The action execution context&lt;/param&gt;</span>
    <span class="k">public</span> <span class="k">override</span> <span class="k">void</span> <span class="nf">OnAuthorization</span><span class="p">(</span><span class="n">AuthorizationContext</span> <span class="n">ctx</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="k">if</span> <span class="p">(</span><span class="n">ctx</span><span class="p">.</span><span class="n">HttpContext</span><span class="p">.</span><span class="n">Request</span><span class="p">.</span><span class="n">Browser</span><span class="p">.</span><span class="n">IsMobileDevice</span><span class="p">)</span>
        <span class="p">{</span>
            <span class="c1">// parse the fragment with request parameters</span>
            <span class="kt">string</span> <span class="n">fragment</span> <span class="p">=</span> <span class="n">ParseClientFragment</span><span class="p">(</span><span class="n">ctx</span><span class="p">);</span>

            <span class="c1">// construct the redirect url</span>
            <span class="n">UrlHelper</span> <span class="n">urlHelper</span> <span class="p">=</span> <span class="k">new</span> <span class="n">UrlHelper</span><span class="p">(</span><span class="n">ctx</span><span class="p">.</span><span class="n">RequestContext</span><span class="p">);</span>
            <span class="kt">string</span> <span class="n">url</span> <span class="p">=</span> <span class="kt">string</span><span class="p">.</span><span class="n">Format</span><span class="p">(</span><span class="s">"{0}#{1}"</span><span class="p">,</span> <span class="n">urlHelper</span><span class="p">.</span><span class="n">Action</span><span class="p">(</span><span class="s">"Index"</span><span class="p">,</span> <span class="s">"Mobile"</span><span class="p">),</span> <span class="n">fragment</span><span class="p">);</span>

            <span class="c1">// return redirect result to prevent action execution</span>
            <span class="n">ctx</span><span class="p">.</span><span class="n">Result</span> <span class="p">=</span> <span class="k">new</span> <span class="n">RedirectResult</span><span class="p">(</span><span class="n">url</span><span class="p">);</span>
        <span class="p">}</span>            
    <span class="p">}</span>

    <span class="c1">/// &lt;summary&gt;</span>
    <span class="c1">/// Parses the client fragment and replaces :[token] with the request parameter</span>
    <span class="c1">/// &lt;/summary&gt;</span>
    <span class="c1">/// &lt;param name="ctx"&gt;The controller context&lt;/param&gt;</span>
    <span class="c1">/// &lt;returns&gt;The parsed fragment&lt;/returns&gt;</span>
    <span class="k">private</span> <span class="kt">string</span> <span class="nf">ParseClientFragment</span><span class="p">(</span><span class="n">ControllerContext</span> <span class="n">ctx</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="kt">string</span> <span class="n">parsedFragment</span> <span class="p">=</span> <span class="n">_clientFragment</span> <span class="p">??</span> <span class="kt">string</span><span class="p">.</span><span class="n">Empty</span><span class="p">;</span>

        <span class="k">if</span> <span class="p">(!</span><span class="kt">string</span><span class="p">.</span><span class="n">IsNullOrEmpty</span><span class="p">(</span><span class="n">parsedFragment</span><span class="p">))</span>
        <span class="p">{</span>
            <span class="n">NameValueCollection</span> <span class="n">@params</span> <span class="p">=</span> <span class="n">ctx</span><span class="p">.</span><span class="n">HttpContext</span><span class="p">.</span><span class="n">Request</span><span class="p">.</span><span class="n">Params</span><span class="p">;</span>
            <span class="n">MatchCollection</span> <span class="n">matches</span> <span class="p">=</span> <span class="n">Regex</span><span class="p">.</span><span class="n">Matches</span><span class="p">(</span><span class="n">_clientFragment</span><span class="p">,</span> <span class="s">":[a-zA-Z]+"</span><span class="p">);</span>
            <span class="n">RouteData</span> <span class="n">routeData</span> <span class="p">=</span> <span class="n">RouteTable</span><span class="p">.</span><span class="n">Routes</span><span class="p">.</span><span class="n">GetRouteData</span><span class="p">(</span><span class="n">ctx</span><span class="p">.</span><span class="n">HttpContext</span><span class="p">);</span>

            <span class="c1">// check each token and replace with param or route values</span>
            <span class="k">foreach</span> <span class="p">(</span><span class="n">Match</span> <span class="n">match</span> <span class="k">in</span> <span class="n">matches</span><span class="p">)</span>
            <span class="p">{</span>
                <span class="kt">string</span> <span class="n">token</span> <span class="p">=</span> <span class="n">match</span><span class="p">.</span><span class="n">Value</span><span class="p">.</span><span class="n">TrimStart</span><span class="p">(</span><span class="sc">':'</span><span class="p">);</span>
                <span class="kt">string</span> <span class="k">value</span> <span class="p">=</span> <span class="n">@params</span><span class="p">[</span><span class="n">token</span><span class="p">];</span>

                <span class="c1">// if we haven;t got a parameter here we must check the route values</span>
                <span class="k">if</span> <span class="p">(</span><span class="kt">string</span><span class="p">.</span><span class="n">IsNullOrEmpty</span><span class="p">(</span><span class="k">value</span><span class="p">)</span> <span class="p">&amp;&amp;</span> <span class="n">routeData</span><span class="p">.</span><span class="n">Values</span><span class="p">.</span><span class="n">ContainsKey</span><span class="p">(</span><span class="n">token</span><span class="p">))</span>
                <span class="p">{</span>
                    <span class="k">value</span> <span class="p">=</span> <span class="n">routeData</span><span class="p">.</span><span class="n">Values</span><span class="p">[</span><span class="n">token</span><span class="p">]</span> <span class="k">as</span> <span class="kt">string</span><span class="p">;</span>
                <span class="p">}</span>

                <span class="c1">// perform the replace</span>
                <span class="n">parsedFragment</span> <span class="p">=</span> <span class="n">parsedFragment</span><span class="p">.</span><span class="n">Replace</span><span class="p">(</span><span class="n">match</span><span class="p">.</span><span class="n">Value</span><span class="p">,</span> <span class="k">value</span><span class="p">);</span>
            <span class="p">}</span>
        <span class="p">}</span>

        <span class="k">return</span> <span class="n">parsedFragment</span><span class="p">;</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
</p><h2>Usage</h2><p>So for our 3 entry points into our application we attribute the controller  actions with the MobileRedirectAttribute and give it a client fragment.</p><p></p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">ProductController</span> <span class="p">:</span> <span class="n">Controller</span>
<span class="p">{</span>
<span class="na">    [MobileRedirect("[product/:id")]</span>
    <span class="k">public</span> <span class="n">ActionResult</span> <span class="nf">Index</span><span class="p">(</span><span class="n">Nullable</span><span class="p">&lt;</span><span class="kt">long</span><span class="p">&gt;</span> <span class="n">id</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="c1">// perform action</span>
    <span class="p">}</span>
<span class="p">}</span>

<span class="k">public</span> <span class="k">class</span> <span class="nc">HomeController</span> <span class="p">:</span> <span class="n">Controller</span>
<span class="p">{</span>
<span class="na">    [MobileRedirect("home")]</span>
    <span class="k">public</span> <span class="n">ActionResult</span> <span class="nf">Index</span><span class="p">()</span>
    <span class="p">{</span>
        <span class="k">return</span> <span class="nf">View</span><span class="p">();</span>
    <span class="p">}</span>
<span class="p">}</span>

<span class="k">public</span> <span class="k">class</span> <span class="nc">AuthenticationController</span> <span class="p">:</span> <span class="n">Controller</span>
<span class="p">{</span>
<span class="na">    [MobileRedirect("home")]</span>
    <span class="k">public</span> <span class="n">ActionResult</span> <span class="nf">Login</span><span class="p">()</span>
    <span class="p">{</span>
    <span class="p">}</span>
<span class="p">}</span>    
</code></pre></div>
<p>The client fragment is capable of translating tokens embedded within it ( as  :&lt;token_name&gt;) and replacing the token with a matching route value or  request parameter.  The ProductController Index action is a good example of  this.  A request to /Product/Index/12345 on a mobile device would translate  to /Mobile/#product/12345</p><h2>How It Works</h2><p>Pretty simple really.</p><ul><li>The attribute checks if the device is a compatible/mobile device.</li><li>If it is the retrieves the client fragment and extracts the tokens -  :&lt;token_name&gt;</li><li>It attempts to match the token names against request parameters first</li><li>If there is no parameter it then looks into the route values (eg. :id in the  above url isn;t a parameter but rather a route value)</li><li>It replaces the token with the real value</li><li>It performs a redirect to /Mobile#&lt;client_fragment&gt; which cancels the  execution of the action.</li></ul><h2>Other Points</h2><ul><li>It's probably not the most robust solution in that more complex scenarios  may not work as expected but it's a decent base that can be extended. </li><li>I have hardcoded the mobile route as it fitted my needs so I think that  should be externalised as well.</li><li>The determination of whether a device is a compatible device is facilitated  through Browser.IsMobileDevice.  This is for demonstration purposes only.  In  the project we use a different solution but it is a bit more long winded to  explain here.</li><li>The "redirect to logon" handling is performed within the app itself so that  is why the Logon view's fragment is simply "home".</li></ul><p></p><h2 style="font-size: 1.5em;">Demo</h2><p>I've pushed a very quick and dirty demo of this onto GitHub for anyone interested - <a href="https://github.com/kouphax/mobileredirect-mvc/">https://github.com/kouphax/mobileredirect-mvc/</a> .  It uses a really quick UserAgent.Contains("iPad") check for "mobile" detection so use and iPad or set your User Agent to try it out.</p><ul></ul><p>Any use to anyone out there?  Any problems with it?  Let me know.</p>