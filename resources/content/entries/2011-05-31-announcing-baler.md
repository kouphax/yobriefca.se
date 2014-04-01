---
date: 2011-05-30T23:00:00Z
title: Announcing Baler
published: true
categories: [.NET]
type: article
external: false
---
<p>It's very early days (with regards to both code and documentation) but I though I amy as well put it out there.</p><blockquote><p><a href="http://yobriefca.se/projects/baler/">Baler</a> is a .<span class="caps">NET</span> Web Resource Bundler. Capable of concatenating and transforming <span class="caps">CSS</span> and JavaScript files to lower request count and bandwidth requirements. It works with any .<span class="caps">NET</span> Web Framework/View Engine capable of running C# in the view</p></blockquote><p>What differentiates <a href="http://yobriefca.se/projects/baler/">Baler</a> from other resource bundlers out there is that it aims to provided a bare bones, minimum feature set for building bales (or resource bundle).  The core <a href="http://nuget.org/List/Packages/CodeSlice.Web.Baler">Baler package</a> offers the ability to concatenate, render and cache JavaScript and <span class="caps">CSS</span> bales.  Thats it.  No minification nor fancy processing nothing.  However <a href="http://yobriefca.se/projects/baler/">Baler</a> does have 2 main extensibility hooks though (<code>Before</code> and <code>After</code>) which can be leveraged to control how bundles are manipulated.  There are currently 3 extensions for Baler but more are on their way,</p><ol>	<li><a href="http://nuget.org/List/Packages/CodeSlice.Web.Baler.Extensions.CoffeeScript">CoffeeScript</a> - Transforms <a href="http://coffeescript.org">CoffeeScript</a> files into JavaScript</li>	<li><a href="http://nuget.org/List/Packages/CodeSlice.Web.Baler.Extensions.Less">.<span class="caps">LESS</span></a> - Transforms <a href="http://www.dotlesscss.org/">.<span class="caps">LESS</span></a> files into <span class="caps">CSS</span></li>	<li><a href="http://nuget.org/List/Packages/CodeSlice.Web.Baler.Extensions.AjaxMinifier">Ajax Minifier</a> - Allows for minification of JavaScript and <span class="caps">CSS</span> using Microsofts Ajax Minifier</li></ol><h2>Quick Example</h2><div class="highlight"><pre><code><span class="err">&lt;</span>%@ Page Language="C#" AutoEventWireup="true" CodeBehind="Default.aspx.cs" Inherits="CodeSlice.Web.Test.Default" %&gt;
<span class="err">&lt;</span>%@ Import Namespace="CodeSlice.Web.Baler" %&gt;

<span class="cp">&lt;!DOCTYPE HTML /&gt;</span>
<span class="nt">&lt;html&gt;</span>
  <span class="nt">&lt;head&gt;</span>
    <span class="err">&lt;</span>%=
      Baler.Build(
        "~/scripts/script1.js",
        "~/scripts/script2.js",
        "~/scripts/script3.js",
      ).AsJs()
    %&gt;
  <span class="nt">&lt;/head&gt;</span>
  <span class="nt">&lt;body&gt;</span>
  <span class="nt">&lt;/body&gt;</span>
<span class="nt">&lt;/html&gt;</span>
</code></pre></div>
<p>This example will take 3 source files concatenate them into a random script file and output the necessary <code>&lt;script src='1jdk2ds.js' type='text/javascript'&gt;&lt;/script&gt;</code>.  Subsequent calls that match this bales signature (the order and contents of the bale) will use the cached script tag and not perform that concatenation again.</p><p>As I say it's early days.  I really need to get the docs up to snuff, write plenty of test and add a few more features before I start shouting from the rooftops but at least it's out now and there is no going back :-).</p><p>Baler v0.1 is available on <a href="http://nuget.org/List/Packages/CodeSlice.Web.Baler">Nuget</a> and <a href="https://github.com/kouphax/baler.git">Github</a> and the annotated source code is available <a href="http://kouphax.github.com/baler/CodeSlice.Web.Baler/CodeSlice.Web.Baler/Baler.html" title="Core">here</a>, <a href="http://kouphax.github.com/baler/CodeSlice.Web.Baler/CodeSlice.Web.Baler.Extensions.AjaxMinifier/MinificationExtensions.html" title="AjaxMin Extensions">here</a>, <a href="http://kouphax.github.com/baler/CodeSlice.Web.Baler/CodeSlice.Web.Baler.Extensions.CoffeeScript/CoffeeScriptExtensions.html" title="CoffeeScript Extensions">here</a> and <a href="http://kouphax.github.com/baler/CodeSlice.Web.Baler/CodeSlice.Web.Baler.Extensions.Less/LessExtensions.html" title=".LESS Extensions">here</a></p>