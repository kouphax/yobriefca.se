---
date: 2010-11-10T00:00:00Z
title: Remote Validation in ASP.NET MVC 3 RC1
published: true
categories: [.NET]
type: article
external: false
---
<p><em><strong>UPDATE</strong>:  Added some words around the extra configuration options available</em></p><p>Remote validation has finally landed in RC1 of <a href="http://weblogs.asp.net/scottgu/archive/2010/11/09/announcing-the-asp-net-mvc-3-release-candidate.aspx">ASP.NET  MVC 3</a>.  It's a weird area as more often than not people tend to over  complicate something that is really pretty simple.  Thankfully the MVC  implementation is fairly straightforward by simply providing wiring allowing the <a href="http://bassistance.de/jquery-plugins/jquery-plugin-validation/">jQuery  Validation</a> plugin to work it's magic.  Basically there is a new Remote attribute that can be used  like so.</p><p></p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">Credentials</span>
<span class="p">{</span>    
<span class="na">    [Remote("Username", "Validation")]</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">Username</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    
    <span class="k">public</span> <span class="kt">string</span> <span class="n">Password</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<p>As you can see we have attributed the Username field with a Remote  attribute.  The 2 parameters tell us what Action and Controller we should call  to perform the validation.  This does make me feel slightly uneasy as it kind of feels like you are coupling the controller to the model which doesn't sit right by me.  currently sitting on the fence I'll see how it works in real life.  Anyway I implemented it like so,</p><p></p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">ValidationController</span> <span class="p">:</span> <span class="n">Controller</span>
<span class="p">{</span>
    <span class="k">public</span> <span class="n">ActionResult</span> <span class="nf">Username</span><span class="p">(</span><span class="kt">string</span> <span class="n">UserName</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="k">return</span> <span class="nf">Json</span><span class="p">(</span><span class="n">Repository</span><span class="p">.</span><span class="n">UserExists</span><span class="p">(</span><span class="n">Username</span><span class="p">),</span> <span class="n">JsonRequestBehavior</span><span class="p">.</span><span class="n">AllowGet</span><span class="p">);</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<p>And thats you - provided you have the necessary client side libraries included  of course (jQuery, jQuery Validate etc). and have Client Side Validation turned on (now by default in MVC3).</p><h2>Configuration</h2><p>The Remote attribute offers a few nice little configuration options to make  things easier.  The typical ones are there such as ErrorMessage, ErrorResource  etc. but there are a few specific ones as well.</p><h3>Fields</h3><p>There may be a case where ding the name and the value of a single form field  isn't enough to perform validation.  Perhaps validation is affected by some  other field/value in the form.  The Remote attribute accepts a comma separated  list of other fields that need to be sent up with the request using the Fields  parameter</p><p>This basic example will send up the value of the EmailService input field  along with the value of Username.  Clean and simple.</p><p></p><div class="highlight"><pre><code><span class="na">[Remote("Username", "Validation", Fields = "EmailService")]</span>
</code></pre></div>
<h3>HttpMethod</h3><p>HttpMethod simply allows us to change how the ajax request is sent e.g. via  POST or GET or anything else that makes sense.  So to send a remote request via  POST</p><p></p><div class="highlight"><pre><code><span class="na">[Remote("Username", "Validation", HttpMethod = "POST")]</span>
</code></pre></div>
<h2>A Minor Difference</h2><p>You might notice if you read the release notes for RC1 that my implementation  of the controller is slightly different.  The reason being that the example in  the release notes is broken :-).  The example looks like this</p><p></p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">UsersController</span> <span class="p">{</span>
    <span class="k">public</span> <span class="kt">bool</span> <span class="nf">UserNameAvailable</span><span class="p">(</span><span class="kt">string</span> <span class="n">username</span><span class="p">)</span> <span class="p">{</span>
        <span class="k">return</span> <span class="p">!</span><span class="n">MyRepository</span><span class="p">.</span><span class="n">UserNameExists</span><span class="p">(</span><span class="n">username</span><span class="p">);</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<p>However the Validate plugin expects a JSON response which is fine on the  surface but returning a boolean response to the client side results in a  response body of False (notice the captial F) which in turn causes a parse error  when the plugin performs JSON.parse.  My suggested solution is actually more  inline with how most people would typically write an Ajax capable controller  action anyway (though I am not happy with the JsonRequestBehaviour usage) but  there are other ways but they aren't pretty....</p><p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">ValidationController</span> <span class="p">:</span> <span class="n">Controller</span>
<span class="p">{</span>        
    <span class="k">public</span> <span class="kt">string</span> <span class="nf">Username</span><span class="p">(</span><span class="kt">string</span> <span class="n">username</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="k">return</span> <span class="p">(!</span><span class="n">Repository</span><span class="p">.</span><span class="n">UserExists</span><span class="p">(</span><span class="n">Username</span><span class="p">)).</span><span class="n">ToString</span><span class="p">().</span><span class="n">ToLower</span><span class="p">();</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
</p><p>See?  Ugly and plain <strong>WRONG</strong> (but it will work).</p><p>Nice to see this feature finally landing as it can be useful in certain  situations.</p>