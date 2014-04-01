---
date: 2010-10-18T23:00:00Z
title: JavaScript Proxy Pattern
published: true
categories: [JavaScript]
type: article
external: false
---
<p></p><p>More often than not when using 3rd party Javascript widgets/plugins/frameworks is that they don't behave in exactly the way that you want forcing you to change tactics, write some horribly messy boilerplate code, hack the original code or look for alternatives. The proxy pattern is a useful pattern that provides a sort of AOP-lite technique that can be used to transparently manage 3rd party code without having to make breaking changes or repeating horrible code from a single point in the code.</p><p>Functions are 1st class objects in JavaScript and this is the feature that powers this pattern. Imagine the setup below,</p><p></p><div class="highlight"><pre><code><span class="c1">// AJAX Object</span>
<span class="kd">var</span> <span class="nx">XHR</span> <span class="o">=</span> <span class="p">{</span>
    <span class="nx">get</span> <span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">url</span><span class="p">,</span> <span class="nx">callback</span><span class="p">){</span>
        <span class="c1">// perform ajax call to url</span>
        <span class="c1">// call the callbakc with the returned data</span>
    <span class="p">}</span>
<span class="p">}</span>
 
<span class="c1">// specific internal ajax functions.  </span>
<span class="c1">// These are 3rd party code that shouldn't be touched</span>
<span class="kd">function</span> <span class="nx">GetData</span><span class="p">(</span><span class="nx">url</span><span class="p">,</span> <span class="nx">callback</span><span class="p">){</span>
    <span class="nx">XHR</span><span class="p">.</span><span class="nx">get</span><span class="p">(</span><span class="nx">url</span><span class="p">,</span> <span class="kd">function</span><span class="p">(</span><span class="nx">data</span><span class="p">){</span>
        <span class="kd">var</span> <span class="nx">parsedData</span> <span class="o">=</span> <span class="nx">parse</span><span class="p">(</span><span class="nx">data</span><span class="p">);</span>
        <span class="nx">callback</span><span class="p">(</span><span class="nx">parsedData</span><span class="p">);</span>
    <span class="p">});</span>
<span class="p">}</span>
 
<span class="kd">function</span> <span class="nx">GetData2</span><span class="p">(</span><span class="nx">url</span><span class="p">,</span> <span class="nx">callback</span><span class="p">){</span> <span class="cm">/* similar thing to GetData*/</span> <span class="p">}</span>
<span class="p">...</span>
<span class="kd">function</span> <span class="nx">GetDataN</span><span class="p">(</span><span class="nx">url</span><span class="p">,</span> <span class="nx">callback</span><span class="p">){</span> <span class="cm">/* similar thing to GetData*/</span> <span class="p">}</span>
</code></pre></div>
<p>Now imagine there are a set of URLs that require some sort of authentication token passed along with the request. How do we deal with that? Well some people would just hack the internal functions but that's a bad idea as it probably breaks other parts of the system. Another way to do it would be to write your own Ajax framework for the new calls you want to make but that's overkill. The proxy pattern can help here.</p><p><div class="highlight"><pre><code><span class="c1">// THE PROXY BLOCK</span>
<span class="p">(</span><span class="kd">function</span><span class="p">(){</span>
 
    <span class="c1">// create empty list of secured URLs</span>
    <span class="nx">XHR</span><span class="p">.</span><span class="nx">securedURLs</span> <span class="o">=</span> <span class="p">[];</span>
 
    <span class="c1">// point to original function</span>
    <span class="kd">var</span> <span class="nx">_get</span> <span class="o">=</span> <span class="nx">XHR</span><span class="p">.</span><span class="nx">get</span><span class="p">;</span>
 
    <span class="c1">// create proxy function</span>
    <span class="nx">XHR</span><span class="p">.</span><span class="nx">get</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">url</span><span class="p">,</span> <span class="nx">callback</span><span class="p">){</span>
        <span class="c1">// is url a secured url?</span>
        <span class="k">if</span><span class="p">(</span><span class="nx">XHR</span><span class="p">.</span><span class="nx">securedURLs</span><span class="p">.</span><span class="nx">indexOf</span><span class="p">(</span><span class="nx">url</span><span class="p">)</span> <span class="o">&amp;</span><span class="nx">gt</span><span class="p">;</span> <span class="o">-</span><span class="mi">1</span><span class="p">){</span>
            <span class="c1">// append authentication token</span>
            <span class="nx">url</span> <span class="o">+=</span> <span class="s2">"?authToken="</span> <span class="o">+</span> <span class="nx">XHR</span><span class="p">.</span><span class="nx">token</span><span class="p">;</span>
        <span class="p">}</span>
 
        <span class="c1">// call original get function with potentially updated url</span>
        <span class="nx">_get</span><span class="p">(</span><span class="nx">url</span><span class="p">,</span> <span class="nx">callback</span><span class="p">);</span>
    <span class="p">}</span>
<span class="p">})();</span>
 
<span class="nx">XHR</span><span class="p">.</span><span class="nx">token</span> <span class="o">=</span> <span class="s2">"AUTH_TOKEN"</span><span class="p">;</span>
<span class="nx">XHR</span><span class="p">.</span><span class="nx">securedURLs</span><span class="p">.</span><span class="nx">push</span><span class="p">(</span><span class="s2">"https://secured.kainos.com"</span><span class="p">);</span>
 
<span class="nx">GetData</span><span class="p">(</span><span class="s2">"https://secured.kainos.com"</span><span class="p">,</span> <span class="kd">function</span><span class="p">(</span><span class="nx">d</span><span class="p">){</span> <span class="cm">/* code */</span> <span class="p">}</span> <span class="p">);</span>  <span class="c1">// via proxy</span>
<span class="nx">GetData</span><span class="p">(</span><span class="s2">"http://notsecured.kainos.com"</span><span class="p">,</span> <span class="kd">function</span><span class="p">(</span><span class="nx">d</span><span class="p">){</span> <span class="cm">/* code */</span> <span class="p">}</span> <span class="p">);</span>  <span class="c1">// via original</span>
</code></pre></div>
</p><p>So basically if the URL is a secured URL (present in the XHR.securedURLs array) then there will be a bit of pre-processing done to the URL before the function is called. Otherwise the normal function is called. That is the primary aim of the proxy pattern - to enhance existing functionality - at the very least the proxied function should fall back to the original function otherwise other parts of the system could be come affected (unless you know what you are doing of course - cancelling illegal calls etc).</p>