---
date: 2009-04-14T23:00:00Z
title: UpdatePanels & Alternatives
published: true
categories: [.NET]
type: article
external: false
---
<p></p><p>I'm not sold on ASP.NET UpdatePanels. Conceptually they are quite nice but their implementation is nasty. So much so I'd be happy to say the should be considered an anti-pattern. My reasons? Well...</p><ul><li>They generate too much JavaScript</li><li>It's too "development focused" - it puts saving time ahead of a clean performant solution</li><li>Ajax calls should be data focused (returning XML, JSON etc), UpdatePanels are content focused</li><li>Generates a massive amount of network traffic in comparison to alternative solutions (including ViewState and markup)</li><li>UpdatePanels use innerHTML to replace contents which orphans bound events (leakage) that need to be rebound each time (extra work).</li></ul><p>But what are the alternatives? Page Methods and Scripted Services are a good starting point. The good things about these alternatives is that they are lightweight - they send JSON data to and from the server so your network traffic is greatly reduced. Here is a simple example of using a page method - I'll not cover Scripted Services as they are very similar and you can find info about them on the net.</p><p>In your code behind you can add a [WebMethod] attributed method</p><div><span style="font-size: small;"><span><div class="highlight"><pre><code><span class="k">using</span> <span class="nn">System</span><span class="p">;</span>
<span class="k">using</span> <span class="nn">System.Web.Services</span><span class="p">;</span>
 
<span class="k">namespace</span> <span class="nn">WebformControls</span><span class="p">{</span>
    <span class="k">public</span> <span class="k">partial</span> <span class="k">class</span> <span class="nc">PageMethods</span> <span class="p">:</span> <span class="n">System</span><span class="p">.</span><span class="n">Web</span><span class="p">.</span><span class="n">UI</span><span class="p">.</span><span class="n">Page</span><span class="p">{</span>        
<span class="na">        [WebMethod]</span>
        <span class="k">public</span> <span class="k">static</span> <span class="kt">string</span> <span class="nf">GetCurrentDateTime</span><span class="p">(){</span>
            <span class="k">return</span> <span class="n">DateTime</span><span class="p">.</span><span class="n">Now</span><span class="p">.</span><span class="n">ToString</span><span class="p">();</span>
        <span class="p">}</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
</span></span></div><p>And turn on EnablePageMethods attribute of the pages ScriptManager</p><div><span style="font-size: small;"><span></span></span><div class="highlight"><pre><code><span class="nt">&lt;html</span> <span class="na">xmlns=</span><span class="s">"http://www.w3.org/1999/xhtml"</span> <span class="nt">&gt;</span>
    <span class="nt">&lt;head</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
        <span class="nt">&lt;title&gt;&lt;/title&gt;</span>
    <span class="nt">&lt;/head&gt;</span>
    <span class="nt">&lt;body&gt;</span>
        <span class="nt">&lt;form</span> <span class="na">id=</span><span class="s">"form1"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
            <span class="nt">&lt;asp:ScriptManager</span> <span class="na">runat=</span><span class="s">"server"</span> <span class="na">EnablePageMethods=</span><span class="s">"true"</span><span class="nt">&gt;&lt;/asp:ScriptManager&gt;</span>
        <span class="nt">&lt;/form&gt;</span>
    <span class="nt">&lt;/body&gt;</span>
<span class="nt">&lt;/html&gt;</span>
</code></pre></div>
</div><p>What happens next is that when the page is rendered a JavaScript Proxy Class will be generated called PageMethods that contains a function call GetCurrentDateAndTime. It'll look something like this.</p><div><span style="font-size: small;"><span></span></span><div class="highlight"><pre><code><span class="kd">var</span> <span class="nx">PageMethods</span> <span class="o">=</span> <span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
    <span class="nx">PageMethods</span><span class="p">.</span><span class="nx">initializeBase</span><span class="p">(</span><span class="k">this</span><span class="p">);</span>
    <span class="k">this</span><span class="p">.</span><span class="nx">_timeout</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span>
    <span class="k">this</span><span class="p">.</span><span class="nx">_userContext</span> <span class="o">=</span> <span class="kc">null</span><span class="p">;</span>
    <span class="k">this</span><span class="p">.</span><span class="nx">_succeeded</span> <span class="o">=</span> <span class="kc">null</span><span class="p">;</span>
    <span class="k">this</span><span class="p">.</span><span class="nx">_failed</span> <span class="o">=</span> <span class="kc">null</span><span class="p">;</span>
<span class="p">}</span>
<span class="nx">PageMethods</span><span class="p">.</span><span class="nx">prototype</span> <span class="o">=</span> <span class="p">{</span>
    <span class="nx">_get_path</span><span class="o">:</span> <span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
        <span class="kd">var</span> <span class="nx">p</span> <span class="o">=</span> <span class="k">this</span><span class="p">.</span><span class="nx">get_path</span><span class="p">();</span>
        <span class="k">if</span> <span class="p">(</span><span class="nx">p</span><span class="p">)</span> <span class="k">return</span> <span class="nx">p</span><span class="p">;</span>
        <span class="k">else</span> <span class="k">return</span> <span class="nx">PageMethods</span><span class="p">.</span><span class="nx">_staticInstance</span><span class="p">.</span><span class="nx">get_path</span><span class="p">();</span>
    <span class="p">},</span>
    <span class="nx">GetCurrentDateTime</span><span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">succeededCallback</span><span class="p">,</span> <span class="nx">failedCallback</span><span class="p">,</span> <span class="nx">userContext</span><span class="p">)</span> <span class="p">{</span>
        <span class="c1">/// </span>
        <span class="c1">/// </span>
        <span class="c1">/// </span>
        <span class="k">return</span> <span class="k">this</span><span class="p">.</span><span class="nx">_invoke</span><span class="p">(</span><span class="k">this</span><span class="p">.</span><span class="nx">_get_path</span><span class="p">(),</span> <span class="s1">'GetCurrentDateTime'</span><span class="p">,</span> <span class="kc">false</span><span class="p">,</span> <span class="p">{},</span> <span class="nx">succeededCallback</span><span class="p">,</span> <span class="nx">failedCallback</span><span class="p">,</span> <span class="nx">userContext</span><span class="p">);</span>
    <span class="p">}</span> 
<span class="p">}</span>
<span class="nx">PageMethods</span><span class="p">.</span><span class="nx">set_path</span><span class="p">(</span><span class="s2">"/PageMethods.aspx"</span><span class="p">);</span>
<span class="nx">PageMethods</span><span class="p">.</span><span class="nx">GetCurrentDateTime</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">onSuccess</span><span class="p">,</span> <span class="nx">onFailed</span><span class="p">,</span> <span class="nx">userContext</span><span class="p">)</span> <span class="p">{</span>
    <span class="c1">/// </span>
    <span class="c1">/// </span>
    <span class="c1">/// </span>
    <span class="nx">PageMethods</span><span class="p">.</span><span class="nx">_staticInstance</span><span class="p">.</span><span class="nx">GetCurrentDateTime</span><span class="p">(</span><span class="nx">onSuccess</span><span class="p">,</span> <span class="nx">onFailed</span><span class="p">,</span> <span class="nx">userContext</span><span class="p">);</span>
<span class="p">}</span>
</code></pre></div>
</div><p>Now all we have to do is use it. This is easy...</p><div><span style="font-size: small;"><span></span></span><div class="highlight"><pre><code><span class="nt">&lt;html</span> <span class="na">xmlns=</span><span class="s">"http://www.w3.org/1999/xhtml"</span> <span class="nt">&gt;</span>
    <span class="nt">&lt;head</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
        <span class="nt">&lt;title&gt;&lt;/title&gt;</span>
        <span class="nt">&lt;script </span><span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;</span>
            <span class="kd">function</span> <span class="nx">getDate</span><span class="p">()</span> <span class="p">{</span>
                <span class="nx">PageMethods</span><span class="p">.</span><span class="nx">GetCurrentDateTime</span><span class="p">(</span><span class="kd">function</span><span class="p">(</span><span class="nx">data</span><span class="p">)</span> <span class="p">{</span>
                    <span class="nb">document</span><span class="p">.</span><span class="nx">getElementById</span><span class="p">(</span><span class="s2">"results"</span><span class="p">).</span><span class="nx">innerHTML</span> <span class="o">=</span> <span class="nx">data</span><span class="p">;</span>
                <span class="p">})</span>
            <span class="p">}</span>
        <span class="nt">&lt;/script&gt;</span>
    <span class="nt">&lt;/head&gt;</span>
    <span class="nt">&lt;body&gt;</span>
        <span class="nt">&lt;form</span> <span class="na">id=</span><span class="s">"form1"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
            <span class="nt">&lt;asp:ScriptManager</span> <span class="na">runat=</span><span class="s">"server"</span> <span class="na">EnablePageMethods=</span><span class="s">"true"</span><span class="nt">&gt;&lt;/asp:ScriptManager&gt;</span>
            <span class="nt">&lt;a</span> <span class="na">href=</span><span class="s">"#"</span> <span class="na">onclick=</span><span class="s">"getDate()"</span><span class="nt">&gt;</span>Get Date!<span class="nt">&lt;/a&gt;</span>
            <span class="nt">&lt;div</span> <span class="na">id=</span><span class="s">"results"</span><span class="nt">&gt;&lt;/div&gt;</span>
        <span class="nt">&lt;/form&gt;</span>
    <span class="nt">&lt;/body&gt;</span>
<span class="nt">&lt;/html&gt;</span>
</code></pre></div>
</div><p>That wasn't too difficult now was it? But we aren't done yet. Sure this solution works but there is still one problem - We are including the ScriptManager in our page which means event though all we are doing is getting a date back from the server we are still required to pull down at least 160k (in it's most compressed &amp; gzipped form) of framework 90% of which we aren't even going to use.</p><h3>Calling PageMethods and ScriptedServices using jQuery</h3><p>Obviously a request from client to server is always done through HTTP it doesn't actually matter how it is handled on the client. So it's easy to swap out the ASP.NET Ajax framework for jQuery. You'll probably be asking why? Well the long and short of it is that regardless of what the requirements say you (or someone else on the project) WILL end up writing Javascript and jQuery is the better choice...</p><ul><li>It's a better, smaller, compact and faster framework</li><li>It's much better documented and easier to learn</li><li>It's extensible via plugins so you don't need the whole "kitchen sink" to do simple things</li><li>It can do everything ASP.NET Ajax can do</li><li>It's technology agnostic so you can jump to a Java project and use the same solutions</li></ul><p>I could go on, honestly I could, but that's not what this post is about. There are a few nuances in calling PageMethods and ScriptedServices, namely</p><ul><li>The request content-type must be <code>application/json; charset=utf-8</code></li><li>The request must be a POST request</li><li>Data sent to the server must be encoded as a JSON string or "{}" if empty</li><li>Standard ASP.NET JSON serialization doesn't convert DateTime to a proper JSON date</li><li>The JSON data in the request must map directly to arguments of the function</li></ul><p>Failure to comply with any of the above will result in either a security error or, usually, the entire page being rendered back to the response. So calling the above PageMethod using jQuery is as simple as this....</p><div><span style="font-size: small;"><span></span></span><div class="highlight"><pre><code><span class="nx">$</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
  <span class="nx">type</span><span class="o">:</span> <span class="s2">"POST"</span><span class="p">,</span>
  <span class="nx">url</span><span class="o">:</span> <span class="s2">"PageMethods.aspx/GetCurrentDateTime"</span><span class="p">,</span>
  <span class="nx">data</span><span class="o">:</span> <span class="s2">"{}"</span><span class="p">,</span>
  <span class="nx">contentType</span><span class="o">:</span> <span class="s2">"application/json; charset=utf-8"</span><span class="p">,</span>
  <span class="nx">dataType</span><span class="o">:</span> <span class="s2">"json"</span><span class="p">,</span>
  <span class="nx">success</span><span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">msg</span><span class="p">)</span> <span class="p">{</span>
    <span class="nx">$</span><span class="p">(</span><span class="s1">'#result'</span><span class="p">).</span><span class="nx">html</span><span class="p">(</span><span class="nx">msg</span><span class="p">)</span>
  <span class="p">}</span>
<span class="p">});</span>
</code></pre></div>
</div><p>Not bad, but there is a lot of boiler plate code there. Thankfully it's easy to write a jQuery plugin to abstract out most of this and make the calls simple. So all in all even with the 2 plugins (JSON and .Net Services) and the jQuery framework we are only forced to pull down 60k of scripts (minified but NOT gzipped) so you could see this reduced to 25-30k all in with gzipping turned on.</p>