---
date: 2011-01-07T00:00:00Z
title: "VS2010/MVC JavaScript Intellisense HTML \xe2\x80\x9cHack\xe2\x80\x9d"
published: true
categories: [JavaScript]
type: article
external: false
---
<p>There is a problem with Visual Studio 2010 JavaScript Intellisense (using MVC in this case) and it is  this - If you are referencing JavaScript resources correctly (or Stylesheets for  that matter) as you all should be you wont actually get intellisense within an  SCRIPT blocks in HTML.  So using this sort of script reference,</p><p><div class="highlight"><pre><code><span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"@Url.Content("</span><span class="err">~/</span><span class="na">Scripts</span><span class="err">/</span><span class="na">json2</span><span class="err">.</span><span class="na">js</span><span class="err">")"</span> <span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;&lt;/script&gt;</span>
</code></pre></div>
</p><p>Will not give you intellisense.  Sure you can use JavaScript files and make  use of the reference VSDOC tag (in fact maybe you should)</p><p></p><div class="highlight"><pre><code><span class="c1">/// &lt;reference path=?~/Scripts/json2.js?&gt;</span>
</code></pre></div>
<p>But this doesn't work (yet) within SCRIPT blocks.  So what about those rare  times you actually want intellisense within your page?  It's actually quite easy  though a bit hacky - stick it in a condition branch that never gets reached.   So,</p><p></p><div class="highlight"><pre><code>@if (false) {
<span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"~/Scripts/json2.js"</span> <span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;&lt;/script&gt;</span>
}
<span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"@Url.Content("</span><span class="err">~/</span><span class="na">Scripts</span><span class="err">/</span><span class="na">json2</span><span class="err">.</span><span class="na">js</span><span class="err">")"</span> <span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;&lt;/script&gt;</span>
</code></pre></div>
<p>The Intellisense parser doesn't really care about the fact that the code wont  ever get hit and you still get your typeahead and documentation etc. but it  won't ever get accidentally written to the generated output (as often happened  to me).  I guess this seems kind of obvious or whatever but perhaps so obvious  some people may not have even though about it and hopefully it'll help someone  out somewhere at some point sometime, eventually - maybe.</p>