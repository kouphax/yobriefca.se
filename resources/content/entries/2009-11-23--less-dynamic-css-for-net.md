---
date: 2009-11-23T00:00:00Z
title: .less - Dynamic CSS for .NET
published: true
categories: [CSS, .NET]
type: article
external: false
---
<p>Link: <a href="http://www.dotlesscss.com/">http://www.dotlesscss.com/</a></p><p>There is a <a href="http://lesscss.org/">Ruby library (LESS)</a> that gets a fair bit of buzz in Ruby circles.  It's purpose is to introduce the <a href="http://en.wikipedia.org/wiki/Don't_repeat_yourself">DRY principle</a> into CSS but also adds a lot of other useful features.  Well now there is a <a href="http://www.dotlesscss.com/">.NET port</a> of it - so what can it do (ripped from the original page as this pretty much sums it up)?</p><h2>Variables</h2><p>Variables allow you to specify widely used values in a single place, and then re-use them throughout the style sheet, making global changes as easy as changing one line of code.</p><p><div class="highlight"><pre><code><span class="k">@brand_color</span><span class="o">:</span> <span class="nf">#4D926F</span><span class="p">;</span>

<span class="nf">#header</span> <span class="p">{</span>
  <span class="k">color</span><span class="o">:</span> <span class="o">@</span><span class="n">brand_color</span><span class="p">;</span>
<span class="p">}</span>

<span class="nt">h2</span> <span class="p">{</span>
  <span class="k">color</span><span class="o">:</span> <span class="o">@</span><span class="n">brand_color</span><span class="p">;</span>
<span class="p">}</span>
</code></pre></div>
</p><p>And variables are a god-send in CSS world though other people disagree (<a href="http://www.w3.org/People/Bos/CSS-variables" rel="nofollow">http://www.w3.org/People/Bos/CSS-variables</a>) - but at least this is still generating plain old CSS for consumption</p><h2>Mixins</h2><p>Mixins allow you to embed all the properties of a class into another class by simply including the class name as one of its properties. It's just like variables, but for whole classes.</p><p></p><div class="highlight"><pre><code><span class="nc">.rounded_corners</span> <span class="p">{</span>
  <span class="o">-</span><span class="n">moz</span><span class="o">-</span><span class="k">border</span><span class="o">-</span><span class="n">radius</span><span class="o">:</span> <span class="m">8px</span><span class="p">;</span>
  <span class="o">-</span><span class="n">webkit</span><span class="o">-</span><span class="k">border</span><span class="o">-</span><span class="n">radius</span><span class="o">:</span> <span class="m">8px</span><span class="p">;</span>
  <span class="k">border</span><span class="o">-</span><span class="n">radius</span><span class="o">:</span> <span class="m">8px</span><span class="p">;</span>
<span class="p">}</span>

<span class="nf">#header</span> <span class="p">{</span>
  <span class="o">.</span><span class="n">rounded_corners</span><span class="p">;</span>
<span class="p">}</span>

<span class="nf">#footer</span> <span class="p">{</span>
  <span class="o">.</span><span class="n">rounded_corners</span><span class="p">;</span>
<span class="p">}</span>
</code></pre></div>
<h2>Operations</h2><p>Are some elements in your style sheet proportional to other elements? Operations let you add, subtract, divide and multiply property values and colors, giving you the power to do create complex relationships between properties.</p><p></p><div class="highlight"><pre><code><span class="k">@the-border</span><span class="o">:</span> <span class="nt">1px</span><span class="p">;</span>
<span class="k">@base-color</span><span class="o">:</span> <span class="nf">#111</span><span class="p">;</span>

<span class="nf">#header</span> <span class="p">{</span>
  <span class="k">color</span><span class="o">:</span> <span class="o">@</span><span class="n">base</span><span class="o">-</span><span class="k">color</span> <span class="o">*</span> <span class="m">3</span><span class="p">;</span>
  <span class="k">border-left</span><span class="o">:</span> <span class="o">@</span><span class="n">the</span><span class="o">-</span><span class="k">border</span><span class="p">;</span>
  <span class="k">border-right</span><span class="o">:</span> <span class="o">@</span><span class="n">the</span><span class="o">-</span><span class="k">border</span> <span class="o">*</span> <span class="m">2</span><span class="p">;</span>
<span class="p">}</span>

<span class="nf">#footer</span> <span class="p">{</span>
  <span class="k">color</span><span class="o">:</span> <span class="p">(</span><span class="o">@</span><span class="n">base</span><span class="o">-</span><span class="k">color</span> <span class="o">+</span> <span class="m">#111</span><span class="p">)</span> <span class="o">*</span> <span class="m">1</span><span class="o">.</span><span class="m">5</span><span class="p">;</span>
<span class="p">}</span>
</code></pre></div>
<p>Operations are "OK" i guess but I am not sold on them.</p><h2>Nesting</h2><p>Rather than constructing long selector names to specify inheritance, in Less you can simply nest selectors inside other selectors. This makes inheritance clear and style sheets shorter.</p><p></p><div class="highlight"><pre><code><span class="nf">#header</span> <span class="p">{</span>
  <span class="k">color</span><span class="o">:</span> <span class="nb">red</span><span class="p">;</span>
  <span class="n">a</span> <span class="err">{</span>
       <span class="k">font-weight</span><span class="o">:</span> <span class="k">bold</span><span class="p">;</span>
       <span class="k">text-decoration</span><span class="o">:</span> <span class="k">none</span><span class="p">;</span>
    <span class="p">}</span>
<span class="err">}</span>
</code></pre></div>
<p>I like the nesting as it moves the structure closer to the nexted DOM tree format that you are actually trying to map against</p><h2>Compression &amp; Caching</h2><p>Now you can configure if you enable Caching and minifying or the CSS output.</p><p></p><div class="highlight"><pre><code><span class="nt">&lt;dotless</span> 
    <span class="na">minifyCss=</span><span class="s">"false"</span> 
    <span class="na">cacheEnabled=</span><span class="s">"true"</span> <span class="nt">/&gt;</span>
</code></pre></div>
<p>It's still in beta and I haven't had a chance to properly tinker with it but it's a great idea as CSS bloat is something that is often overlooked and can make rebranding a nightmare if not carefully managed.  I might post more on this when I get a chance to have proper play with it.</p>