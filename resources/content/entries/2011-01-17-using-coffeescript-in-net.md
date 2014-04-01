---
date: 2011-01-17T00:00:00Z
title: Using CoffeeScript in .NET
published: true
categories: [JavaScript, .NET]
type: article
external: false
---
<p style="text-align: center;"><img src="http://posterous.com/getfile/files.posterous.com/temp-2011-01-17/GzcAkdBuuBhqnFqExtbBrpthjHwbqpFyslpzEnJEiIiwqmulsAjFgFAuciHJ/coffeescriptdotnet.png.scaled500.png" alt="" /></p><p>I've been playing with <a href="http://nodejs.org/">node.js</a> for a while  now and some of the things coming out of that community are simply amazing.  One  of the interesting techs is <a href="http://coffeescript.org/">CoffeeScript</a> which is essentially a  JavaScript dialect that compiles into JavaScript.  It's goal is to simplify the  JavaScript language by removing all those bad parts that can cause a lot of  grief and taking all those common tasks (array manipulation, scoping etc and  making them very very easy to achieve.  I have an old post I have yet to publish  externally on the subject but I will get it out there ASAP.</p><p>CoffeeScript comes in 2 variations - a node.js specific version and a  "standalone" version that can be run in browser (pure JavaScript without any of  the node.js assumptions basically).  Considering it's parsing one language into  another it's not recommended to be running the script client side every time so  if you want to use it you want to be doing the parsing server side and possibly  using some sort of caching to prevent redoing the parsing on every request.   This is the bad(ish) news for .NET folks because there really isn't any stable  JavaScript implementations that can run a script as complex a CoffeeScript (this  is essentially a compiler of sorts).  Well at least not until now....  When i  first investigated this I tried using a number of JavaScript engines,</p><ul><li><a href="https://github.com/fholm/IronJS">IronJS</a>: Currently only a  partial implementation and doesn't appear to run the CoffeeScript  compiler fully.</li><li><a href="http://javascriptdotnet.codeplex.com/">JavaScript.NET</a>: Worked  for version 0.9 or so of CoffeeScript but there is a bug that causes version 1.0  to fall over and development appears to have ceased.</li><li>Command line JScript: Epic fail.  Just wouldn't do what I needed it  to.</li></ul><p>Then along came yet another JavaScript engine for .NET so I gave it a shot.   On the surface <a href="http://jurassic.codeplex.com/">Jurassic</a> appeared to  offered everything I needed and upon diving a bit deeper I discovered that, yes,  it does indeed offer everything I needed - it runs the CoffeeScript compiler.   Sweet.</p><p>So lets write the compiler wrapper.  Pretty basic really,</p><p><div class="highlight"><pre><code><span class="c1">/// &lt;summary&gt;</span>
<span class="c1">/// Processes CoffeeScript files into javascript</span>
<span class="c1">/// &lt;/summary&gt;</span>
<span class="k">public</span> <span class="k">class</span> <span class="nc">CoffeeScriptProcessor</span>
<span class="p">{</span>
    <span class="k">private</span> <span class="k">static</span> <span class="k">readonly</span> <span class="kt">string</span> <span class="n">COMPILE_TASK</span> <span class="p">=</span> <span class="s">"CoffeeScript.compile(Source, {bare: true})"</span><span class="p">;</span>

<span class="na">    [ThreadStatic]</span>
    <span class="k">private</span> <span class="k">static</span> <span class="n">ScriptEngine</span> <span class="n">_engine</span><span class="p">;</span>
    <span class="k">private</span> <span class="k">static</span> <span class="kt">object</span> <span class="n">_o</span> <span class="p">=</span> <span class="k">new</span> <span class="kt">object</span><span class="p">();</span>

    <span class="k">private</span> <span class="k">static</span> <span class="n">ScriptEngine</span> <span class="n">Engine</span>
    <span class="p">{</span>
        <span class="k">get</span>
        <span class="p">{</span>
            <span class="k">if</span> <span class="p">(</span><span class="n">_engine</span> <span class="p">==</span> <span class="k">null</span><span class="p">)</span>
            <span class="p">{</span>
                <span class="n">_engine</span> <span class="p">=</span> <span class="k">new</span> <span class="n">ScriptEngine</span><span class="p">();</span>
                <span class="n">_engine</span><span class="p">.</span><span class="n">Execute</span><span class="p">(</span><span class="n">Resources</span><span class="p">.</span><span class="n">CoffeeScriptSource</span><span class="p">);</span>
            <span class="p">}</span>

            <span class="k">return</span> <span class="n">_engine</span><span class="p">;</span>
        <span class="p">}</span>
    <span class="p">}</span>

    <span class="c1">/// &lt;summary&gt;</span>
    <span class="c1">/// Precesses contents as a coffeescript file</span>
    <span class="c1">/// &lt;/summary&gt;</span>
    <span class="c1">/// &lt;param name="contents"&gt;The javascript contents&lt;/param&gt;</span>
    <span class="c1">/// &lt;returns&gt;&lt;/returns&gt;</span>
    <span class="k">public</span> <span class="k">static</span> <span class="kt">string</span> <span class="nf">Process</span><span class="p">(</span><span class="kt">string</span> <span class="n">contents</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="k">lock</span> <span class="p">(</span><span class="n">_o</span><span class="p">)</span>
        <span class="p">{</span>
            <span class="k">try</span>
            <span class="p">{</span>
                <span class="n">Engine</span><span class="p">.</span><span class="n">SetGlobalValue</span><span class="p">(</span><span class="s">"Source"</span><span class="p">,</span> <span class="n">contents</span><span class="p">);</span>
                <span class="k">return</span> <span class="n">Engine</span><span class="p">.</span><span class="n">Evaluate</span><span class="p">&lt;</span><span class="kt">string</span><span class="p">&gt;(</span><span class="n">COMPILE_TASK</span><span class="p">);</span>
            <span class="p">}</span>
            <span class="k">catch</span> <span class="p">(</span><span class="n">Exception</span> <span class="n">e</span><span class="p">)</span>
            <span class="p">{</span>
                <span class="k">return</span> <span class="k">null</span><span class="p">;</span>
            <span class="p">}</span>
        <span class="p">}</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
</p><p>Few points worth noting here,</p><ul><li>I create a ThreadStatic instance of the Jurassic Engine and feed the  CoffeeScript compiler source into it once.  The executions/evaluation of that  script is quite slow so I only wanted to take the hit once.  Jurassic isn't  thread safe so I made the reference ThreadStatic.</li><li>Just to be extra safe I am locking during the execution of the compilation  task.</li><li>Any errors just return null.  This is a bare bones implementation I have  stripped out custom logging and profiling code and an interface that means  little in this context.</li></ul><p>I have created a VS2010 MVC3 solution that makes use of this processor to generate JavaScript from a simple CoffeeScript example.  Have fun.</p><p><a href="https://github.com/kouphax/coffeescript-dotnet">https://github.com/kouphax/coffeescript-dotnet</a></p><h2>What's Next?</h2><p>This is obviously only the start - if I wanted to use CoffeeScript in a .NET  environment I am going to need to consider how it is used.  When should I be  compiling it?  How should I be caching it etc?  The usual things.  Hopefully  I'll touch on these in a later post.</p><p>So right now this is kind of a request for comments, this was knocked up quite quickly and likely I have forgotten something or made a massive mistake.  Anything you want to offer fire away.</p>