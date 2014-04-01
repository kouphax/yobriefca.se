---
date: 2009-05-18T23:00:00Z
title: JavaScript Method Overloading
published: true
categories: [JavaScript]
type: article
external: false
---
<p>I was recently asked a question if method overloading in JavaScript was good practise, or even possible, so I thought I'd spread share my thoughts on this to anyone who bothers to read my ramblings. JavaScript doesn't offer method overloading directly but it does offer features that can be used to facilitate overloading.<span> </span> Imagine these C# method signatures,</p><p></p><div class="highlight"><pre><code><span class="c1">// Joins 2 strings</span>
<span class="k">public</span> <span class="kt">string</span> <span class="nf">Join</span><span class="p">(</span><span class="kt">string</span> <span class="n">a</span><span class="p">,</span> <span class="kt">string</span> <span class="n">b</span><span class="p">)</span>
<span class="p">{</span>
    <span class="k">return</span> <span class="n">a</span><span class="p">+</span><span class="n">b</span><span class="p">;</span>
<span class="p">}</span>
 
<span class="c1">// Joins an array of strings</span>
<span class="k">public</span> <span class="kt">string</span> <span class="nf">Join</span><span class="p">(</span><span class="kt">string</span><span class="p">[]</span> <span class="n">a</span><span class="p">)</span>
<span class="p">{</span>
    <span class="k">return</span> <span class="n">a</span><span class="p">.</span><span class="k">join</span><span class="p">(</span><span class="err">''</span><span class="p">);</span>
<span class="p">}</span>
 
<span class="n">Join</span><span class="p">(</span><span class="k">new</span> <span class="kt">string</span><span class="p">[</span><span class="m">2</span><span class="p">]</span> <span class="p">{</span><span class="sc">'a'</span><span class="p">,</span> <span class="sc">'b'</span><span class="p">});</span> <span class="c1">// &lt;-- 'ab'</span>
<span class="n">Join</span><span class="p">(</span><span class="sc">'a'</span><span class="p">,</span><span class="sc">'b'</span><span class="p">);</span> <span class="c1">// &lt;-- 'ab'</span>
</code></pre></div>
<p>So depending on the arguments passed to the function the correct method will be called. Now this style of overloading isn't possible in JavaScript.</p><p></p><div class="highlight"><pre><code><span class="c1">// Joins 2 strings</span>
<span class="kd">function</span> <span class="nx">join</span><span class="p">(</span><span class="nx">a</span><span class="p">,</span> <span class="nx">b</span><span class="p">){</span>
    <span class="k">return</span> <span class="nx">a</span><span class="o">+</span><span class="nx">b</span><span class="p">;</span>
<span class="p">}</span>
 
<span class="c1">// Joins an array of strings</span>
<span class="kr">public</span> <span class="nx">join</span><span class="p">(</span><span class="nx">a</span><span class="p">){</span>
    <span class="k">return</span> <span class="nx">a</span><span class="p">.</span><span class="nx">join</span><span class="p">(</span><span class="s1">''</span><span class="p">);</span>
<span class="p">}</span>
 
<span class="nx">join</span><span class="p">([</span><span class="s1">'a'</span><span class="p">,</span><span class="s1">'b'</span><span class="p">])</span> <span class="c1">// &lt;-- 'ab'</span>
<span class="nx">join</span><span class="p">(</span><span class="s1">'a'</span><span class="p">,</span><span class="s1">'b'</span><span class="p">)</span> <span class="c1">// &lt;-- ERROR! (join is not a function of string)</span>
</code></pre></div>
<p>As I have said JavaScript has no notion of method overloading so all that happens here is that the join function get redefined with the last occurrence of the function definition. However thanks to JavaScript's typeless nature and optional arguments overloading can be achieved (or at least simulated depending on your viewpoint).</p><p></p><div class="highlight"><pre><code><span class="kd">function</span> <span class="nx">join</span><span class="p">(</span><span class="nx">a</span><span class="p">,</span> <span class="nx">b</span><span class="p">){</span>
    <span class="k">if</span><span class="p">(</span><span class="k">typeof</span><span class="p">(</span><span class="nx">a</span><span class="p">)</span> <span class="o">==</span> <span class="s1">'string'</span><span class="p">){</span>
        <span class="k">return</span> <span class="nx">a</span><span class="o">+</span><span class="nx">b</span>
    <span class="p">}</span><span class="k">else</span><span class="p">{</span>
        <span class="k">return</span> <span class="nx">a</span><span class="p">.</span><span class="nx">join</span><span class="p">(</span><span class="s1">''</span><span class="p">);</span>
    <span class="p">}</span>
<span class="p">}</span>
 
<span class="nx">join</span><span class="p">([</span><span class="s1">'a'</span><span class="p">,</span><span class="s1">'b'</span><span class="p">])</span> <span class="c1">// &lt;-- 'ab'</span>
<span class="nx">join</span><span class="p">(</span><span class="s1">'a'</span><span class="p">,</span><span class="s1">'b'</span><span class="p">)</span> <span class="c1">// &lt;-- 'ab'</span>
</code></pre></div>
<p>Fixed! OK so that may seem simple enough and in this case it is but there are 2 things you need to consider before actually putting this into practise.</p><h3>Signature Detection</h3><p>Obviously the first thing you need to do when overloading is detect the intention of the user - what method signature are they using. This is the biggest issue you'll probably come across, especially when dealing with arguments of type<code>function</code> or <code>array</code>. <a href="http://thinkweb2.com/projects/prototype/category/isarray/">These</a> <a href="http://bytes.com/groups/javascript/746441-isfunction-code-worth-recommending-project">posts</a> explain the various issues with type detection and functions/arrays but to summarise</p><ul><li>typeof returns "object" for Arrays</li><li>An array created in a frame/iframe does not share the same prototype as another Array in the parent document or another frame/iframe which make instanceof, typeof and [].constructor fail</li><li>Attempting to detect features such as methods (splice, join) and properties (length) can fail if the a non-array has matching methods/properties</li><li>Certain browsers report objects as functions making typeof == function fail</li><li>Host methods (browsers provided methods) sometimes do not report themselves as functions</li></ul><p>The list goes on! So you need to be aware of the target browsers quirks and work with them. Another way to detect signature is to use the <code>arguments</code> psuedo-array but this is only useful in situations where your overloaded methods accepts a varying number of arguments e.g.</p><p><div class="highlight"><pre><code><span class="kd">function</span> <span class="nx">join</span><span class="p">(</span><span class="nx">a</span><span class="p">,</span> <span class="nx">b</span><span class="p">){</span>
    <span class="k">if</span><span class="p">(</span><span class="nx">arguments</span><span class="p">.</span><span class="nx">length</span> <span class="o">==</span> <span class="mi">2</span><span class="p">){</span>
        <span class="k">return</span> <span class="nx">a</span><span class="o">+</span><span class="nx">b</span>
    <span class="p">}</span><span class="k">else</span><span class="p">{</span>
        <span class="k">return</span> <span class="nx">a</span><span class="p">.</span><span class="nx">join</span><span class="p">(</span><span class="s1">''</span><span class="p">);</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
</p><p>But then this is open to abuse by ignorant/stupid/malicious coders as it doesn't really imply how the function should behave unless commented well (yeah right!!).</p><h3>Code Bloat</h3><p>You need to be sure you aren't abusing this overloading strategy as it can lead to spaghetti code. Functions that attempt to do many things with similar signatures result in loads of argument testing and lot's of indented code. jQuery code is riddled with this due to the way almost every method acts as either a setter, bulk setter or getter method and it's run into bugs in the past due to this. Also the jQuery (or $) function is overloaded to do 5 different things so looking at the init function you'll see that it is a big if..elseif..elseif..elseif..else block which is pretty gruesome and breaks extensability and modularity of the code base.</p><h2>Conclusion</h2><p>Overloading is possible in JavaScript but needs to be used with caution. Not only can it lead to very hard to find bugs due to type detection in different browsers but it can also make code harder to understand and extend, not to mention that amount of boilerplate code you need to write to detect the correct method signature etc.</p>