---
date: 2010-10-17T23:00:00Z
title: Strong Typing vs. Magic Strings (Part II)
published: true
categories: [.NET]
type: article
external: false
---
<p>One of the problems with ASP.NET MVC is it's use of magic strings in almost every facet of the architecture.  I've mentioned this <a href="http://blogs.kainos.com/jameshu/2010/03/19/avoiding-magic-strings-in-mvc2/">before</a> but I have a few other things I'd like to mention.  In my last post I used T4MVC to generate static classes that layer on top of the magic strings but if these don't generate when you want them to they become out of sync and break compile time checking.  One solution is to generate the "proxy" files one every save however if you have a large project this could be a bit of a pain.</p><p>Other solutions exist already.  MVC2 comes with strongly typed HtmlHelpers so which turns</p><p></p><div class="highlight"><pre><code><span class="err">&lt;</span>%=Html.TextBox("FullName")%&gt;
</code></pre></div>
<p>Into,</p><p></p><div class="highlight"><pre><code><span class="err">&lt;</span>%=Html.TextBoxFor(m =&gt; m.FullName)%&gt;
</code></pre></div>
<p>Great - compile time checking and the ability to reflect things such as attribute for generating validation etc.  Problem is they didn't really do much else.  Controller methods still suffered from Magicstringitis.  RedirectToAction still needed magic strings for controller and action names and ModelState.AddModelError still needed string for property names.</p><h2>MvcContrib</h2><p><a href="http://mvccontrib.codeplex.com/Wikipage">MvcContrib</a> offers a load of non-official but excellent extensions to MVC and one of the things that comes bundled with the build is a strongly typed RedirectToAction extension method.  This lets change this,</p><p></p><div class="highlight"><pre><code><span class="k">this</span><span class="p">.</span><span class="n">RedirectToAction</span><span class="p">(</span><span class="s">"Index"</span><span class="p">,</span> <span class="s">"Home"</span><span class="p">);</span>
</code></pre></div>
<p>Into</p><p><div class="highlight"><pre><code><span class="k">this</span><span class="p">.</span><span class="n">RedirectToAction</span><span class="p">&lt;</span><span class="n">HomeController</span><span class="p">&gt;(</span><span class="n">c</span> <span class="p">=&gt;</span> <span class="n">c</span><span class="p">.</span><span class="n">Index</span><span class="p">());</span>
</code></pre></div>
</p><p>Yet again  - compile time checking, refactoring support and no magic strings - Win.  But that leaves one last thing that still bugs me - ModelState.AddModelError.  To add an error for a particualr property of your model you still need to do something like this...</p><p></p><div class="highlight"><pre><code><span class="n">ModelState</span><span class="p">.</span><span class="n">AddModelError</span><span class="p">(</span><span class="s">"Username"</span><span class="p">,</span> <span class="s">"Username is already in use"</span><span class="p">)</span>
</code></pre></div>
<p>YAMS! Yet another magic string!</p><h2>DIY</h2><p>Seeing as I couldn't find a solution from my good friend Google I decided to see if I could do it myself.  Turns out it's bloody easy.  This extension method will allow us to get rid of magic strings when expecting to map to a property of a simple class (such as a model or DTO)</p><p></p><div class="highlight"><pre><code><span class="c1">/// &lt;summary&gt;</span>
<span class="c1">/// Adds a model error using strongly typed lambda expressions</span>
<span class="c1">/// &lt;/summary&gt;</span>
<span class="k">public</span> <span class="k">static</span> <span class="k">void</span> <span class="n">AddModelError</span><span class="p">&lt;</span><span class="n">TModel</span><span class="p">&gt;(</span>
    <span class="k">this</span> <span class="n">ModelStateDictionary</span> <span class="n">modelState</span><span class="p">,</span> 
    <span class="n">Expression</span><span class="p">&lt;</span><span class="n">Func</span><span class="p">&lt;</span><span class="n">TModel</span><span class="p">,</span> <span class="kt">object</span><span class="p">&gt;&gt;</span> <span class="n">method</span><span class="p">,</span> 
    <span class="kt">string</span> <span class="n">message</span><span class="p">)</span>
<span class="p">{</span>
    <span class="k">if</span> <span class="p">(</span><span class="n">method</span> <span class="p">==</span> <span class="k">null</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="k">throw</span> <span class="k">new</span> <span class="nf">ArgumentNullException</span><span class="p">(</span><span class="s">"method"</span><span class="p">);</span>
    <span class="p">}</span>
 
    <span class="n">MemberExpression</span> <span class="n">mce</span> <span class="p">=</span> <span class="n">method</span><span class="p">.</span><span class="n">Body</span> <span class="k">as</span> <span class="n">MemberExpression</span><span class="p">;</span>
    <span class="kt">string</span> <span class="n">property</span> <span class="p">=</span> <span class="n">mce</span><span class="p">.</span><span class="n">Member</span><span class="p">.</span><span class="n">Name</span><span class="p">;</span>
    <span class="n">modelState</span><span class="p">.</span><span class="n">AddModelError</span><span class="p">(</span><span class="n">property</span><span class="p">,</span> <span class="n">message</span><span class="p">);</span>
<span class="p">}</span>
</code></pre></div>
<p>And you can now call it like this...</p><p></p><div class="highlight"><pre><code><span class="n">ModelState</span><span class="p">.</span><span class="n">AddModelError</span><span class="p">&lt;</span><span class="n">Model</span><span class="p">.</span><span class="n">User</span><span class="p">&gt;(</span><span class="n">u</span> <span class="p">=&gt;</span> <span class="n">u</span><span class="p">.</span><span class="n">Username</span><span class="p">,</span> <span class="s">"Username already in use"</span><span class="p">);</span>
</code></pre></div>
<p>Neat!</p><p>One more bit of proof that lambda expressions are awesome - true there is reflection involved so it's going to be slower than magic strings but in my daily use I haven't really noticed any performance hits so (thanks to Jeff Atwood @ <a href="http://www.codinghorror.com">codinghorror.com</a>) I can give it a "Works on my Machine" badge.</p><p><img title="6a0120a85dcdae970b0128776ff992970c-pi[1]" src="http://codinghorror.typepad.com/.a/6a0120a85dcdae970b0128776ff992970c-pi" border="0" height="193" alt="6a0120a85dcdae970b0128776ff992970c-pi[1]" width="200" /></p>