---
date: 2010-10-17T23:00:00Z
title: 'T4MVC: Strong Typing vs Magic Strings'
published: true
categories: [.NET]
type: article
external: false
---
<p>Magic strings are everywhere in ASP.NET MVC 2.  Less so than in version 1 and some of the Beta and RC releases but there are still some kicking around.  The problem with literals is that they don't give you any compile time error checking or refactoring ability.  Say for example you have an action link that points you to the Edit action of the Person controller - you write it like this.</p><p></p><div class="highlight"><pre><code><span class="err">&lt;</span>%=Html.ActionLink("Edit", "Person", new { id = 12 }) %&gt;
</code></pre></div>
<p>Now image you've written something like this over numerous pages and all of sudden you change the name of the Action or the Controller.  These are literal strings so you can't use the refactor tools in VS or get any compile time error when you build.  The only real way to ensure everything has been changed would be to do a quick regression test.  Nightmare.</p><h2><a href="http://mvccontrib.codeplex.com/wikipage?title=T4MVC&amp;referringTitle=Documentation">T4MVC</a></h2><p>T4MVC makes use of the Visual Studio bundled T4 Text Template Transform Toolkit (Template based code generation tool) to generate invisible classes that give you strongly typed links to Controllers, Actions, Views and even JavaScript, CSS, Images and other static links.</p><p>T4MVC is part of the MVCContrib project (though completely standalone).  You simply download the zip with the 2 T4 files and add them to the root of your MVC project and you are done.  Here are some examples of it in action.</p><h3>VIEW NAMES</h3><p></p><div class="highlight"><pre><code><span class="err">&lt;</span>% Html.RenderPartial("DinnerForm"); %&gt;
<span class="err">&lt;</span>% Html.RenderPartial(MVC.Dinners.Views.DinnerForm); %&gt;
</code></pre></div>
<p>And within a controller</p><p></p><div class="highlight"><pre><code><span class="k">return</span> <span class="nf">View</span><span class="p">(</span><span class="s">"InvalidOwner"</span><span class="p">);</span>
<span class="k">return</span> <span class="nf">View</span><span class="p">(</span><span class="n">Views</span><span class="p">.</span><span class="n">InvalidOwner</span><span class="p">);</span>
</code></pre></div>
<h3>CONTROLLER ACTIONS</h3><p><span style="font-weight: normal;"></span></p><div class="highlight"><pre><code><span class="err">&lt;</span>%= Html.ActionLink("Delete Dinner", "Delete", "Dinners", new { id = Model.DinnerID }, null)%&gt;
<span class="err">&lt;</span>%= Html.ActionLink("Delete Dinner", MVC.Dinners.Delete(Model.DinnerID))%&gt;
</code></pre></div>
 <h3>STATIC FILES</h3><p>You also get the ability to reference static files so that if they are moved or renamed you'll not be missing images etc. - something that is a pain to test most times.</p><p><div class="highlight"><pre><code><span class="nt">&lt;img</span> <span class="na">src=</span><span class="s">"/Content/nerd.jpg"</span> <span class="nt">/&gt;</span>
<span class="nt">&lt;img</span> <span class="na">src=</span><span class="s">"&lt;%= Links.Content.nerd_jpg %&gt;"</span> <span class="nt">/&gt;</span>
</code></pre></div>
</p><p>Another nicety is that you also get this for JavaScript files but without having to do anything it will swap out debug (uncompressed) versions of you files for minified versions when you move into production.  As long as you have &lt;filename&gt;.js and &lt;filename&gt;-min.js side by side it will determine which one to use based on the build environment.</p><p></p><div class="highlight"><pre><code><span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"/Scripts/Map.js"</span> <span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;&lt;/script&gt;</span>
<span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"&lt;%= Links.Scripts.Map_js %&gt;"</span> <span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;&lt;/script&gt;</span>
</code></pre></div>
 <h3>Summing Up</h3><p>T4MVC is a nice tool to get around magic strings.  There are other things out there that give "fluent" syntax to methods that use literals which use delegates instead of generating any sort of proxy classes.  Which is better is a question I still can't answer confidently.  I have had some minor issues using T4MVC (mostly my fault) and I am a bit dubious about the syntax for static files but it's a decent starter for 10 and doesn't need any thought to implement and use.  Of course you could also roll own specific T4 template (or extend T4MVC) but that obviously requires a bit more work.</p>