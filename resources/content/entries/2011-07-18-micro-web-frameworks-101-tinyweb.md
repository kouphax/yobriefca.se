---
date: 2011-07-17T23:00:00Z
title: 'Micro Web Frameworks in .NET 101: Tinyweb'
published: true
categories: [.NET]
type: article
external: false
---
<p>Tinyweb takes a slightly different view than the other web frameworks I have talked about.  In fact it takes a fairly opinionated approach to the way your structure your applications code and forces you to think in terms of resource endpoints rather then big monolithic modules or controllers.  It's certainly true that projects based on the other frameworks such as Nancy and Jessica can be architected in such a way but Tinyweb ensures that you don't start cutting corners and making allowances for lazy code by simply not providing the ability to do it!</p><h2>Getting Started - Hello World</h2><p>Lets get Tinyweb first from good old reliable Nuget,</p><div class="highlight"><pre><code><span class="n">Install-Package</span> <span class="n">Tinyweb</span>
</code></pre></div>
 <p>With Tinyweb installed we can go ahead and create a <code>RootHandler</code> this name is the one exception to the Tinyweb handler naming convention which I'll touch on in a minute.</p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">RootHandler</span>
<span class="p">{</span>
    <span class="k">public</span> <span class="n">IResult</span> <span class="nf">Get</span><span class="p">()</span>
    <span class="p">{</span>
        <span class="k">return</span> <span class="n">Result</span><span class="p">.</span><span class="n">String</span><span class="p">(</span><span class="s">"Hello World"</span><span class="p">);</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<p>In our handler I also defined the a <code>Get</code> method which returns an IResult this will act as our endpoint.  One last thing before we magic up a web page is to initialise Tinyweb via our <code>Global.asax.cs</code> so Tinyweb can do its bootstrapping discovery voodoo stuffs.</p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">Global</span> <span class="p">:</span> <span class="n">HttpApplication</span>
<span class="p">{</span>
    <span class="k">protected</span> <span class="k">void</span> <span class="nf">Application_Start</span><span class="p">(</span><span class="kt">object</span> <span class="n">sender</span><span class="p">,</span> <span class="n">EventArgs</span> <span class="n">e</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="n">Tinyweb</span><span class="p">.</span><span class="n">Init</span><span class="p">();</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
 <p>Et voila! Run the app and once again we have a hello world.</p><h2>Tinyweb Features</h2><p>Even in that small Hello World example there are plenty of Tinyweb features but you'll also notice that Tinyweb really doesn't get in the way at all.</p><h3>Handlers</h3><p>Each Tinyweb Handler represents a single resource endpoint.  With the exception of <code>RootHandler</code> the <span class="caps">URL</span> endpoint for each handler is inferred from the name of the handler.  For example a handler named <code>HelloHandler</code> will react to <code>/hello</code>, <code>HelloWorldHandler</code> will react to <code>/hello/world</code>.  You can see from these examples that casing of the handler name is important in terms of the url endpoint generation.</p><p>The next thing about handlers is that they respond to 4 methods each one corresponding to the 4 main Http Verbs - <code>Get()</code>, <code>Post()</code>, <code>Put()</code> and <code>Delete</code> - the only requirement of these is that they return an <code>IResult</code></p><h3>Model Binding and Arguments</h3><p>Model binding is also provided by handlers actions - just pass in an object and Tinyweb will do it's best to bind request values to this object (accepts primitives, collections and plain C# objects).</p><p>Alternatively if you want to do some work under the hood you can make sure the <code>RequestContext</code> is passed in by simply passing it instead.  This gives you access to the bowels of the request to do with what you please.</p><h3>Advanced Routes</h3><p>It's also possible to override the default routing convention by declaring a handler level variable called <code>route</code> of type <code>Route</code>.  This example shows how we can override handlers default route <code>/hello/world</code> and go with the more understandable "/helloworld".</p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">HelloWorldHandler</span>
<span class="p">{</span>
    <span class="n">Route</span> <span class="n">route</span> <span class="p">=</span> <span class="k">new</span> <span class="n">Route</span><span class="p">(</span><span class="s">"helloworld"</span><span class="p">);</span>

    <span class="k">public</span> <span class="n">IResult</span> <span class="nf">Get</span><span class="p">()</span>
    <span class="p">{</span>
        <span class="k">return</span> <span class="n">Result</span><span class="p">.</span><span class="n">String</span><span class="p">(</span><span class="s">"Hello World"</span><span class="p">);</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<h3>Route Parameters</h3><p>In the spirit of RESTFulness you may also want to accept parameters as part of your <span class="caps">URL</span> and we can do that as well with this <code>route</code> class as well as optionally suppling default values for parameters,</p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">HelloWorldHandler</span>
<span class="p">{</span>
    <span class="n">Route</span> <span class="n">route</span> <span class="p">=</span> <span class="k">new</span> <span class="n">Route</span><span class="p">(</span><span class="s">"hello/{name}"</span><span class="p">);</span>
    <span class="c1">// OR WITH A DEFAULT VALUE FOR PARAMS</span>
    <span class="n">Route</span> <span class="n">route</span> <span class="p">=</span> <span class="k">new</span> <span class="n">Route</span><span class="p">(</span><span class="s">"hello/{name}"</span><span class="p">,</span> <span class="k">new</span> <span class="p">{</span> <span class="n">name</span> <span class="p">=</span> <span class="s">"World"</span> <span class="p">});</span>

    <span class="k">public</span> <span class="n">IResult</span> <span class="nf">Get</span><span class="p">(</span><span class="kt">string</span> <span class="n">name</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="k">return</span> <span class="n">Result</span><span class="p">.</span><span class="n">String</span><span class="p">(</span><span class="s">"Hello "</span> <span class="p">+</span> <span class="n">name</span><span class="p">);</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<p>A powerful little approach.</p><h3>Results</h3><p>All Tinyweb results (e.g. what the response returns) implement the <code>IResult</code> interface so it's simple enough to implement your own custom result type though it is probably unnecessary for the most part as Tinyweb offers a range of result types straight away via the <code>Result</code> classes static methods.</p><ul>	<li>String</li>	<li>File</li>	<li>Json</li>	<li><span class="caps">XML</span></li>	<li>JsonOrXml (returns either Json or <span class="caps">XML</span> depending on the request headers)</li>	<li>Html</li>	<li>Redirect (to a specific handler or <span class="caps">URL</span>)</li></ul><p>Tinyweb also offers a <code>View</code> class that can render views written with Spark and Razor so rendering a view can be as simple as</p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">HelloWorldHandler</span>
<span class="p">{</span>
    <span class="n">Route</span> <span class="n">route</span> <span class="p">=</span> <span class="k">new</span> <span class="n">Route</span><span class="p">(</span><span class="s">"hello/{name}"</span><span class="p">,</span> <span class="k">new</span> <span class="p">{</span> <span class="n">name</span> <span class="p">=</span> <span class="s">"World"</span> <span class="p">});</span>

    <span class="k">public</span> <span class="n">IResult</span> <span class="nf">Get</span><span class="p">(</span><span class="kt">string</span> <span class="n">name</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="k">return</span> <span class="n">View</span><span class="p">.</span><span class="n">Razor</span><span class="p">&lt;</span><span class="kt">string</span><span class="p">&gt;(</span><span class="n">name</span><span class="p">,</span> <span class="s">"hello.cshtml"</span><span class="p">);</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<h3>Filters</h3><p>Filters allow us to intercept requests both before and after they are processed on both a per handler and global level.</p><p>Each handler can optionally contain an <code>After</code> and/or a <code>Before</code> method that will, not surprisingly, be called after and before each handler request (I'll let you guess which one does which) e.g.</p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">RootHandler</span>
<span class="p">{</span>
    <span class="k">public</span> <span class="k">void</span> <span class="nf">Before</span><span class="p">()</span>
    <span class="p">{</span>
        <span class="n">Logger</span><span class="p">.</span><span class="n">Log</span><span class="p">(</span><span class="s">"Before Executed"</span><span class="p">);</span>
    <span class="p">}</span>

    <span class="k">public</span> <span class="n">IResult</span> <span class="nf">Get</span><span class="p">()</span>
    <span class="p">{</span>
        <span class="k">return</span> <span class="n">Result</span><span class="p">.</span><span class="n">String</span><span class="p">(</span><span class="s">"Hello World"</span><span class="p">);</span>
    <span class="p">}</span>

    <span class="k">public</span> <span class="k">void</span> <span class="nf">After</span><span class="p">()</span>
    <span class="p">{</span>
        <span class="n">Logger</span><span class="p">.</span><span class="n">Log</span><span class="p">(</span><span class="s">"After Executed"</span><span class="p">);</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<p>Alternatively if you want the same before or after filter applied across all handlers you can create a Filter class by creating a class appended with the word <code>Filter</code>.  We can recreate the same handler above with a global filter for logging,</p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">LoggingFilter</span>
<span class="p">{</span>
    <span class="k">public</span> <span class="k">void</span> <span class="nf">Before</span><span class="p">()</span>
    <span class="p">{</span>
        <span class="n">Logger</span><span class="p">.</span><span class="n">Log</span><span class="p">(</span><span class="s">"Before Executed"</span><span class="p">);</span>
    <span class="p">}</span>

    <span class="k">public</span> <span class="k">void</span> <span class="nf">After</span><span class="p">()</span>
    <span class="p">{</span>
        <span class="n">Logger</span><span class="p">.</span><span class="n">Log</span><span class="p">(</span><span class="s">"After Executed"</span><span class="p">);</span>
    <span class="p">}</span>
<span class="p">}</span>

<span class="k">public</span> <span class="k">class</span> <span class="nc">RootHandler</span>
<span class="p">{</span>
    <span class="k">public</span> <span class="n">IResult</span> <span class="nf">Get</span><span class="p">()</span>
    <span class="p">{</span>
        <span class="k">return</span> <span class="n">Result</span><span class="p">.</span><span class="n">String</span><span class="p">(</span><span class="s">"Hello World"</span><span class="p">);</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<p><code>Before</code> and <code>After</code> methods can also return <code>IResult</code> objects if you want to work with the actual response.</p><h3>Error Handling and Debugging</h3><p>Not all of us write flawless code and so sometimes things go south and exceptions start throwing their weight around.  Tinyweb has a global hook that is useful for capturing such errors as they bubble up to the surface.  The TinyWeb class has a static property <code>OnError</code> that accepts an Action that can be used to handle the exception e.g.</p><div class="highlight"><pre><code><span class="k">protected</span> <span class="k">void</span> <span class="nf">Application_Start</span><span class="p">(</span><span class="kt">object</span> <span class="n">sender</span><span class="p">,</span> <span class="n">EventArgs</span> <span class="n">e</span><span class="p">)</span>
<span class="p">{</span>
    <span class="n">Tinyweb</span><span class="p">.</span><span class="n">Init</span><span class="p">();</span>
    <span class="n">Tinyweb</span><span class="p">.</span><span class="n">OnError</span> <span class="p">=</span> <span class="p">(</span><span class="n">exception</span><span class="p">,</span> <span class="n">context</span><span class="p">,</span> <span class="n">data</span><span class="p">)</span> <span class="p">=&gt;</span>
    <span class="p">{</span>
        <span class="n">Logger</span><span class="p">.</span><span class="n">Log</span><span class="p">(</span><span class="n">exception</span><span class="p">);</span>
    <span class="p">};</span>
<span class="p">}</span>
</code></pre></div>
<p>Another useful tool when debugging your app is the <code>Tinyweb.WhatHaveIGot()</code>.  It's a convenience method that can be used to print out all the matched routes and filters.</p><h2>Conclusion</h2><p>On the surface the focus of Tinyweb may seem only moderately different from other frameworks but after playing with it for a while it become apparent that the opinionated approach really makes you think about your projects structure and <span class="caps">API</span>.  I'm certainly of the opinion that this is a damn good thing<sup class="footnote" id="fnr1"><a href="#fn1">1</a></sup>.  Tinyweb gives me just enough framework to do pretty much everything I need - which I would expect from a real micro-framework.  On top of that it's flexible and intuitive and another handy utility in my tool belt.</p><p class="footnote" id="fn1"><a href="#fnr1"><sup>1</sup></a> I've already said that it is possible to make the likes of Nancy and Jessica behave in this, either by ensuring that all module adhere to this pattern, or creating an abstract base class that enforces this sort of structure so I am in no way bashing the alternatives.  I like choice.</p>