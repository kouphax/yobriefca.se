---
date: 2010-10-13T23:00:00Z
title: Modernizr & RequireJS
published: true
categories: [JavaScript]
type: article
external: false
---
<p>I've been pulling a lot of JavaScript libraries and utilities together to build up a NuPack server as it's by far one of the best ways to distribute client side resources during development.  Some of these libraries I have used myself and thought it might be worth mentioning some of them.</p><h2>Modernizr</h2><p>Modernizr is a feature detection library that allows us to test the existence of certain HTML5 features within the browser.  This is not only useful when constructing a page and applying styling of JS widgets but also for detecting things such as WebWorkers, Web DB, application cache etc.  It supports many things,</p><blockquote class="posterous_medium_quote"><p>@font-face, Canvas, Canvas Text, HTML5 Audio, HTML5 Video, rgba(), hsla(), border-image:, border-radius:, box-shadow:, opacity:, Multiple backgrounds, CSS Animations, CSS Columns, CSS Gradients, CSS Reflections, CSS 2D Transforms, CSS 3D Transforms, CSS Transitions, Geolocation API, localStorage, sessionStorage, SVG, SMIL, SVG Clipping, Drag and Drop, hashchange, X-window Messaging, History Management, applicationCache, Web Sockets, Web Workers, Web SQL Database, IndexedDB, Input Types, Input Attributes</p></blockquote><p>In most cases native support for a feature is always better than an alternative CSS hack or JS script.  Rounded Corners are a good example of this.  A lot of, but not all, browsers already support rounded corners natively but in a world where people think a site should be <a href="http://dowebsitesneedtolookexactlythesameineverybrowser.com/">identical</a> in all browsers it is often necessary to provide your own solution.  This means adding extra work for all browsers.  This is where Modernizr comes in.  Rather than applying non-native rounded corners to browsers that support them nativley we can do this,</p><div class="highlight"><pre><code><span class="nt">&lt;script </span><span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;</span>
    <span class="k">if</span><span class="p">(</span><span class="o">!</span><span class="nx">Modernizr</span><span class="p">.</span><span class="nx">borderradius</span><span class="p">){</span>
        <span class="nx">$</span><span class="p">(</span><span class="s2">".borderradius"</span><span class="p">).</span><span class="nx">roundedcorners</span><span class="p">();</span>
    <span class="p">}</span>
<span class="nt">&lt;/script&gt;</span>
</code></pre></div>
<p>That way we can always defer to the browser and remove unnecessary processing.  But lets take it step further.  Obviously for the majority of browsers pulling down a rounded corner plugin is completely pointless as it will never be used.  What can we do about that?</p><h2>Require.JS</h2><p>Require.JS is one of many module/script loaders available but the good thing about Require.JS is that is extremely simple.  Essentially it injects a script file into the head of the document and fires a callback when ready (there is a bit more configurability held within but that's the gist of it).  So lets take the above example and see what we can do about it.  We don't want to load scripts unnecessarily (lets leave that up to ASP.NET WebForms, ugh.... ) so lets only load the rounded corner script when needed,</p><div class="highlight"><pre><code><span class="nt">&lt;script </span><span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;</span>
    <span class="k">if</span><span class="p">(</span><span class="o">!</span><span class="nx">Modernizr</span><span class="p">.</span><span class="nx">borderradius</span><span class="p">){</span>
        <span class="nx">require</span><span class="p">([</span><span class="s2">"scripts/jquery-corners.js"</span><span class="p">],</span> <span class="kd">function</span><span class="p">(){</span>
            <span class="nx">$</span><span class="p">(</span><span class="s2">".borderradius"</span><span class="p">).</span><span class="nx">roundedcorners</span><span class="p">();</span>
        <span class="p">});</span>        
    <span class="p">}</span>
<span class="nt">&lt;/script&gt;</span>
</code></pre></div>
<p>So basically we only get the extra script pulled down in the few browsers that don't support rounded corners, less processing, less traffic (as long as require.js file size &lt; total size of optional scripts). </p><p>Nice.</p><p>Obviously you need to be a bit more strategic about where you place this code as you can get FOUC if placed after document has fully loaded.  Another issue is that Require.JS intentionally doesn't support CSS as there is no guaranteed way to ensure it has loaded across all browser.  Thats an easy fix though,</p><div class="highlight"><pre><code><span class="kd">function</span> <span class="nx">loadCss</span><span class="p">(</span><span class="nx">url</span><span class="p">)</span> <span class="p">{</span>
    <span class="kd">var</span> <span class="nx">link</span> <span class="o">=</span> <span class="nb">document</span><span class="p">.</span><span class="nx">createElement</span><span class="p">(</span><span class="s2">"link"</span><span class="p">);</span>
    <span class="nx">link</span><span class="p">.</span><span class="nx">type</span> <span class="o">=</span> <span class="s2">"text/css"</span><span class="p">;</span>
    <span class="nx">link</span><span class="p">.</span><span class="nx">rel</span> <span class="o">=</span> <span class="s2">"stylesheet"</span><span class="p">;</span>
    <span class="nx">link</span><span class="p">.</span><span class="nx">href</span> <span class="o">=</span> <span class="nx">url</span><span class="p">;</span>
    <span class="nb">document</span><span class="p">.</span><span class="nx">getElementsByTagName</span><span class="p">(</span><span class="s2">"head"</span><span class="p">)[</span><span class="mi">0</span><span class="p">].</span><span class="nx">appendChild</span><span class="p">(</span><span class="nx">link</span><span class="p">);</span>
<span class="p">}</span>
</code></pre></div>
<p>As always I've put together a nice wee demo app that makes use of these technologies.... coming to a NuPack server near you too.</p><p><a href="svn://subversion/Playground/trunk/jameshu/Src/Kainos.Internal.RequireAndModernizr" title="svn://subversion/Playground/trunk/jameshu/Src/Kainos.Internal.RequireAndModernizr">svn://subversion/Playground/trunk/jameshu/Src/Kainos.Internal.RequireAndModernizr</a></p><p>Let me know if that works as it uses NuPack (but I don't think that is a requirement to open the project).</p>