---
date: 2010-11-20T00:00:00Z
title: Better JSON Serialisation for ASP.NET MVC
published: true
categories: [.NET]
type: article
external: false
---
<p></p><p>The Json() method of controllers is a nice little convenience method for  serialising server side data into JSON for handy consumption on the client side  but it's not without it's issues.  Firstly it just cant serialise dates in any  nice way due to no hard and fast standard in JSON for dates.</p><p></p><div class="highlight"><pre><code><span class="k">return</span> <span class="nf">Json</span><span class="p">(</span><span class="k">new</span>
<span class="p">{</span>
    <span class="n">SomeDate</span> <span class="p">=</span> <span class="n">DateTime</span><span class="p">.</span><span class="n">Now</span>
<span class="p">});</span>
</code></pre></div>
<p>Simply gets spat back to the front end as a string like so,</p><p></p><div class="highlight"><pre><code><span class="s2">"/Date(1290181373164)/"</span>
</code></pre></div>
<p>Not exactly the most useful thing in the world.  You are going to need to make use of JSON's reviver callback to parse this accordingly.</p><p>Another issue is lack of configurability.  C# coding standards recommend the  use of PascalCased property names whereas in JavaScript land the standard is  camelCase (and many frameworks assume this casing which can cause issues behind  the scenes *cough*ExtJS*cough*).  You can fix this by adding DataContract and  DataMember attributes to your model object but it gets a bit messy and open to  error if an attribute is left out accidentally.  Or what if you don't have  access to the model code?</p><h2>JSON.NET</h2><p><a href="http://json.codeplex.com/">Json.NET</a> is my JSON de/serialiser of  choice.  It's fast and widely configurable plus it fixes the problems I've  mentioned above (among many many other things).  Not to mention some BSON  (Binary JSON) support which could come in handy in the future.</p><h2>JsonNetResult</h2><p>So lets fix things.  Lets wrap Json.NET up in an ActionResult type and offer  a configurable entry point.</p><p></p><div class="highlight"><pre><code><span class="c1">/// &lt;summary&gt; </span>
<span class="c1">/// Simple Json Result that implements the Json.NET serialiser offering more versatile serialisation </span>
<span class="c1">/// &lt;/summary&gt; </span>
<span class="k">public</span> <span class="k">class</span> <span class="nc">JsonNetResult</span> <span class="p">:</span> <span class="n">ActionResult</span> 
<span class="p">{</span> 
    <span class="k">public</span> <span class="nf">JsonNetResult</span><span class="p">()</span> 
    <span class="p">{</span> 
    <span class="p">}</span>

    <span class="k">public</span> <span class="nf">JsonNetResult</span> <span class="p">(</span><span class="kt">object</span> <span class="n">responseBody</span><span class="p">)</span> 
    <span class="p">{</span> 
        <span class="n">ResponseBody</span> <span class="p">=</span> <span class="n">responseBody</span><span class="p">;</span> 
    <span class="p">}</span>

    <span class="k">public</span> <span class="nf">JsonNetResult</span><span class="p">(</span><span class="kt">object</span> <span class="n">responseBody</span><span class="p">,</span> <span class="n">JsonSerializerSettings</span> <span class="n">settings</span><span class="p">)</span> 
    <span class="p">{</span> 
        <span class="n">Settings</span> <span class="p">=</span> <span class="n">settings</span><span class="p">;</span> 
    <span class="p">}</span>

    <span class="c1">/// &lt;summary&gt;Gets or sets the serialiser settings&lt;/summary&gt; </span>
    <span class="k">public</span> <span class="n">JsonSerializerSettings</span> <span class="n">Settings</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>

    <span class="c1">/// &lt;summary&gt;Gets or sets the encoding of the response&lt;/summary&gt; </span>
    <span class="k">public</span> <span class="n">Encoding</span> <span class="n">ContentEncoding</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>

    <span class="c1">/// &lt;summary&gt;Gets or sets the content type for the response&lt;/summary&gt; </span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">ContentType</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>

    <span class="c1">/// &lt;summary&gt;Gets or sets the body of the response&lt;/summary&gt; </span>
    <span class="k">public</span> <span class="kt">object</span> <span class="n">ResponseBody</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>

    <span class="c1">/// &lt;summary&gt;Gets the formatting types depending on whether we are in debug mode&lt;/summary&gt; </span>
    <span class="k">private</span> <span class="n">Formatting</span> <span class="n">Formatting</span> 
    <span class="p">{</span> 
        <span class="k">get</span> 
        <span class="p">{</span> 
            <span class="k">return</span> <span class="n">Debugger</span><span class="p">.</span><span class="n">IsAttached</span> <span class="p">?</span> <span class="n">Formatting</span><span class="p">.</span><span class="n">Indented</span> <span class="p">:</span> <span class="n">Formatting</span><span class="p">.</span><span class="n">None</span><span class="p">;</span> 
        <span class="p">}</span> 
    <span class="p">}</span>

    <span class="c1">/// &lt;summary&gt; </span>
    <span class="c1">/// Serialises the response and writes it out to the response object </span>
    <span class="c1">/// &lt;/summary&gt; </span>
    <span class="c1">/// &lt;param name="context"&gt;The execution context&lt;/param&gt; </span>
    <span class="k">public</span> <span class="k">override</span> <span class="k">void</span> <span class="nf">ExecuteResult</span><span class="p">(</span><span class="n">ControllerContext</span> <span class="n">context</span><span class="p">)</span> 
    <span class="p">{</span> 
        <span class="k">if</span> <span class="p">(</span><span class="n">context</span> <span class="p">==</span> <span class="k">null</span><span class="p">)</span> 
        <span class="p">{</span> 
            <span class="k">throw</span> <span class="k">new</span> <span class="nf">ArgumentNullException</span><span class="p">(</span><span class="s">"context"</span><span class="p">);</span> 
        <span class="p">}</span>

        <span class="n">HttpResponseBase</span> <span class="n">response</span> <span class="p">=</span> <span class="n">context</span><span class="p">.</span><span class="n">HttpContext</span><span class="p">.</span><span class="n">Response</span><span class="p">;</span>

        <span class="c1">// set content type </span>
        <span class="k">if</span> <span class="p">(!</span><span class="kt">string</span><span class="p">.</span><span class="n">IsNullOrEmpty</span><span class="p">(</span><span class="n">ContentType</span><span class="p">))</span> 
        <span class="p">{</span> 
            <span class="n">response</span><span class="p">.</span><span class="n">ContentType</span> <span class="p">=</span> <span class="n">ContentType</span><span class="p">;</span> 
        <span class="p">}</span> 
        <span class="k">else</span> 
        <span class="p">{</span> 
            <span class="n">response</span><span class="p">.</span><span class="n">ContentType</span> <span class="p">=</span> <span class="s">"application/json"</span><span class="p">;</span> 
        <span class="p">}</span>

        <span class="c1">// set content encoding </span>
        <span class="k">if</span> <span class="p">(</span><span class="n">ContentEncoding</span> <span class="p">!=</span> <span class="k">null</span><span class="p">)</span> 
        <span class="p">{</span> 
            <span class="n">response</span><span class="p">.</span><span class="n">ContentEncoding</span> <span class="p">=</span> <span class="n">ContentEncoding</span><span class="p">;</span> 
        <span class="p">}</span>

        <span class="k">if</span> <span class="p">(</span><span class="n">ResponseBody</span> <span class="p">!=</span> <span class="k">null</span><span class="p">)</span> 
        <span class="p">{</span> 
            <span class="n">response</span><span class="p">.</span><span class="n">Write</span><span class="p">(</span><span class="n">JsonConvert</span><span class="p">.</span><span class="n">SerializeObject</span><span class="p">(</span><span class="n">ResponseBody</span><span class="p">,</span> <span class="n">Formatting</span><span class="p">,</span> <span class="n">Settings</span><span class="p">));</span>             
        <span class="p">}</span> 
    <span class="p">}</span> 
<span class="p">}</span>
</code></pre></div>
<p>As you can see I have exposed the JsonSerializerSettings object allow  developers to tune the serialisation all they want.  We can also make things  even simpler by providing a Controller extension method that we can call without  having to create the object directly,</p><p></p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">static</span> <span class="k">class</span> <span class="nc">ControllerExtensions</span> 
<span class="p">{</span> 
    <span class="k">public</span> <span class="k">static</span> <span class="n">JsonNetResult</span> <span class="nf">JsonEx</span><span class="p">(</span><span class="k">this</span> <span class="n">Controller</span> <span class="n">controller</span><span class="p">,</span> <span class="kt">object</span> <span class="n">responseBody</span><span class="p">)</span> 
    <span class="p">{</span> 
        <span class="k">return</span> <span class="k">new</span> <span class="nf">JsonNetResult</span><span class="p">(</span><span class="n">responseBody</span><span class="p">);</span> 
    <span class="p">}</span>

    <span class="k">public</span> <span class="k">static</span> <span class="n">JsonNetResult</span> <span class="nf">JsonEx</span><span class="p">(</span><span class="k">this</span> <span class="n">Controller</span> <span class="n">controller</span><span class="p">,</span> <span class="kt">object</span> <span class="n">responseBody</span><span class="p">,</span> <span class="n">JsonSerializerSettings</span> <span class="n">settings</span><span class="p">)</span> 
    <span class="p">{</span> 
        <span class="k">return</span> <span class="k">new</span> <span class="nf">JsonNetResult</span><span class="p">(</span><span class="n">responseBody</span><span class="p">,</span> <span class="n">settings</span><span class="p">);</span> 
    <span class="p">}</span> 
<span class="p">}</span>
</code></pre></div>
<p>Simple stuff but still pretty powerful, lets see it in action.</p><h2>The Date Problem</h2><p>We can fix the date problem in a number of ways via converters.  Converters  give us a custom way to convert values of objects and Json.NET provides a number  of converters out of the box - IsoDateTimeConverter, JavaScriptDateTimeConverter  and an abstract base DateTimeConverter to roll your own.  Lets use the  JavaScriptDateTimeConverter for this example,</p><p></p><div class="highlight"><pre><code><span class="kt">var</span> <span class="n">result</span> <span class="p">=</span> <span class="k">new</span>
<span class="p">{</span>
    <span class="n">SomeDate</span> <span class="p">=</span> <span class="n">DateTime</span><span class="p">.</span><span class="n">Now</span>
<span class="p">};</span>

<span class="k">return</span> <span class="k">this</span><span class="p">.</span><span class="n">JsonEx</span><span class="p">(</span><span class="n">result</span><span class="p">,</span> <span class="k">new</span> <span class="n">JsonSerializerSettings</span><span class="p">()</span>
<span class="p">{</span>
    <span class="n">Converters</span> <span class="p">=</span> <span class="k">new</span> <span class="n">List</span><span class="p">&lt;</span><span class="n">JsonConverter</span><span class="p">&gt;</span>
    <span class="p">{</span>
        <span class="k">new</span> <span class="nf">JavaScriptDateTimeConverter</span><span class="p">()</span>
    <span class="p">}</span>
<span class="p">});</span>
</code></pre></div>
<p>The resultant JSON object will generate a JSON object with a JavaScript Date  constructor</p><p></p><div class="highlight"><pre><code><span class="p">{</span> 
    <span class="k">new</span> <span class="nb">Date</span><span class="p">(</span><span class="o">?</span><span class="p">)</span> 
<span class="p">}</span>
</code></pre></div>
<p>While not strictly valid JSON native JSON parsers and JSON2 handle it  fine.</p><h2>The Casing Issue</h2><p>We can use Json.NET's contract resolver to automagically convert PascalCased  property names of C# to camelCased property names of JavaScript.</p><p><div class="highlight"><pre><code><span class="kt">var</span> <span class="n">result</span> <span class="p">=</span> <span class="k">new</span> 
<span class="p">{</span> 
    <span class="n">SomeProperty</span> <span class="p">=</span> <span class="p">?</span><span class="n">my</span> <span class="k">value</span><span class="p">?</span> 
<span class="p">};</span> 

<span class="k">return</span> <span class="k">this</span><span class="p">.</span><span class="n">JsonEx</span><span class="p">(</span><span class="n">result</span><span class="p">,</span> <span class="k">new</span> <span class="n">JsonSerializerSettings</span><span class="p">()</span> 
<span class="p">{</span> 
    <span class="n">ContractResolver</span> <span class="p">=</span> <span class="k">new</span> <span class="n">CamelCasePropertyNamesContractResolver</span><span class="p">()</span> 
<span class="p">});</span>
</code></pre></div>
</p><p>Which gives us a response like so,</p><p></p><div class="highlight"><pre><code><span class="p">{</span> 
    <span class="nx">someProperty</span><span class="o">:</span> <span class="o">?</span><span class="nx">my</span> <span class="nx">value</span><span class="o">?</span> 
<span class="p">}</span>
</code></pre></div>
<p>Fixed.</p><h2>Wrap Up</h2><p>So we have managed to create a much more powerful JSON serialisation  technique without having to sacrifice too much of the convenience of the Json()  method in the controller.  Anyone got any recommendations/enhancements they are  willing to share?  </p>