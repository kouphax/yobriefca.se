---
date: 2009-10-18T23:00:00Z
title: Lazy Function Definition Pattern
published: true
categories: [JavaScript]
type: article
external: false
---
<p>Have a look at this..</p><p></p><div class="highlight"><pre><code><span class="kd">function</span> <span class="nx">getTemplate</span><span class="p">(</span><span class="nx">id</span><span class="p">){</span>
  <span class="kd">var</span> <span class="nx">scripts</span> <span class="o">=</span> <span class="nb">document</span><span class="p">.</span><span class="nx">getElementsByTagName</span><span class="p">(</span><span class="s2">"script"</span><span class="p">);</span>
  <span class="k">for</span><span class="p">(</span><span class="nx">script</span> <span class="k">in</span> <span class="nx">scripts</span><span class="p">){</span>
    <span class="kd">var</span> <span class="nx">sid</span> <span class="o">=</span> <span class="nx">script</span><span class="p">.</span><span class="nx">getAttribute</span><span class="p">(</span><span class="nx">id</span><span class="p">);</span>
    <span class="k">if</span><span class="p">(</span><span class="nx">id</span><span class="o">==</span><span class="nx">sid</span><span class="p">){</span>
      <span class="k">return</span> <span class="nx">script</span><span class="p">.</span><span class="nx">innerHtml</span><span class="p">;</span>
    <span class="p">}</span>
  <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<p>Fairly simple function that loads the contents of a specific script tag (based on id) so that it can be used as a template (for something). <span> </span>The reasons why you would do this is not important but the implementation of this function is. Every time you call this function you are re-selecting all the tag elements on the page which is just plain wrong. A quick fix for this would be to cache the results after the initial call and use the cache.</p><p></p><div class="highlight"><pre><code><span class="kd">var</span> <span class="nx">cache</span> <span class="o">=</span> <span class="nb">document</span><span class="p">.</span><span class="nx">getElementsByTagName</span><span class="p">(</span><span class="s2">"script"</span><span class="p">);</span>
<span class="kd">function</span> <span class="nx">getTemplate</span><span class="p">(</span><span class="nx">id</span><span class="p">){</span>
  <span class="k">for</span><span class="p">(</span><span class="nx">script</span> <span class="k">in</span> <span class="nx">cache</span><span class="p">){</span>
    <span class="kd">var</span> <span class="nx">sid</span> <span class="o">=</span> <span class="nx">script</span><span class="p">.</span><span class="nx">getAttribute</span><span class="p">(</span><span class="nx">id</span><span class="p">);</span>
    <span class="k">if</span><span class="p">(</span><span class="nx">id</span><span class="o">==</span><span class="nx">sid</span><span class="p">){</span>
      <span class="k">return</span> <span class="nx">script</span><span class="p">.</span><span class="nx">innerHtml</span><span class="p">;</span>
    <span class="p">}</span>
  <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<p>This would allow the the costly getElementsByTagName to be called once and the results reused. This however starts polluting the global namespace with unnecessary variables plus even if the getTemplate function is never used you still have the cost of one call to getElementsByTagName which is pretty poor. You can get around these issues fairly easily by extending the actual function object itself</p><p></p><div class="highlight"><pre><code><span class="kd">function</span> <span class="nx">getTemplate</span><span class="p">(</span><span class="nx">id</span><span class="p">){</span>
  <span class="k">if</span><span class="p">(</span><span class="o">!</span><span class="nx">getTemplate</span><span class="p">.</span><span class="nx">cache</span><span class="p">){</span>
    <span class="nx">getTemplate</span><span class="p">.</span><span class="nx">cache</span> <span class="o">=</span> <span class="nb">document</span><span class="p">.</span><span class="nx">getElementsByTagName</span><span class="p">(</span><span class="s2">"script"</span><span class="p">);</span>
  <span class="p">}</span> 
  <span class="k">for</span><span class="p">(</span><span class="nx">script</span> <span class="k">in</span> <span class="nx">getTemplate</span><span class="p">.</span><span class="nx">cache</span><span class="p">){</span>
    <span class="kd">var</span> <span class="nx">sid</span> <span class="o">=</span> <span class="nx">script</span><span class="p">.</span><span class="nx">getAttribute</span><span class="p">(</span><span class="nx">id</span><span class="p">);</span>
    <span class="k">if</span><span class="p">(</span><span class="nx">id</span><span class="o">==</span><span class="nx">sid</span><span class="p">){</span>
      <span class="k">return</span> <span class="nx">script</span><span class="p">.</span><span class="nx">innerHtml</span><span class="p">;</span>
    <span class="p">}</span>
  <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<p>The benefits of this approach are that you aren't polluting the global namespace and you are only making the call to getElementsByTagName ONLY when it is needed but it does still have an issue. The cache should only be available to the getTemplate functions internal logic but it is currently exposed allowing other functions to remove or update it.</p><p>Introducing the Lazy Function Definition Pattern. Thanks to the dynamic nature of JavaScript functions can actually redefine themselves at runtime and this allows us to provide private, lazy, one time processing without polluting the global namespace. Lets see it in action...</p><p><div class="highlight"><pre><code><span class="kd">function</span> <span class="nx">getTemplate</span><span class="p">(</span><span class="nx">id</span><span class="p">){</span>
  <span class="kd">var</span> <span class="nx">cache</span> <span class="o">=</span> <span class="nb">document</span><span class="p">.</span><span class="nx">getElementsByTagName</span><span class="p">(</span><span class="s2">"script"</span><span class="p">);</span>
 
  <span class="nx">getTemplate</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">id</span><span class="p">){</span>
    <span class="k">for</span><span class="p">(</span><span class="nx">script</span> <span class="k">in</span> <span class="nx">cache</span><span class="p">){</span>
      <span class="kd">var</span> <span class="nx">sid</span> <span class="o">=</span> <span class="nx">script</span><span class="p">.</span><span class="nx">getAttribute</span><span class="p">(</span><span class="nx">id</span><span class="p">);</span>
      <span class="k">if</span><span class="p">(</span><span class="nx">id</span><span class="o">==</span><span class="nx">sid</span><span class="p">){</span>
        <span class="k">return</span> <span class="nx">script</span><span class="p">.</span><span class="nx">innerHtml</span><span class="p">;</span>
      <span class="p">}</span>
    <span class="p">}</span>    
  <span class="p">}</span>
 
  <span class="k">return</span> <span class="nx">getTemplate</span><span class="p">(</span><span class="nx">id</span><span class="p">);</span>
<span class="p">}</span>
</code></pre></div>
</p><p>The big change here is the middle statement. getTemplate is actually redefining itself. So what happens here?</p><ol><li>On the first call getElementsByTagName is called and stored in the cache variable</li><li>The function then redefines itself as simple function that simply loops through the already defined cache</li><li>The function then calls itself (the new self!)</li></ol><p>Thanks to the creation of a <a href="http://www.jibbering.com/faq/faq_notes/closures.html">closure</a> the redefined function actually has access to the variables of the old definition - cache. This means that cache is private to the new function, only called once and not part of the global namespace. Exactly what I wanted. Programatically as well it's a better solution than the ones above because there are no checks to see if cache is defined saving a few cycles each call.</p>