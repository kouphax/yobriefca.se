---
date: 2010-11-09T00:00:00Z
title: All Change! ValidateInputAttribute and SkipRequestValidation
published: true
categories: [.NET]
type: article
external: false
---
<p>I recently <a href="http://yobriefcase.posterous.com/validateinputattribute-changes-in-mvc3">blogged</a> about the new Exclude feature of ValidateInputAttribute in ASP.NET MVC 3 (Beta  2).  Well as with most early adopter types I've been shafted!  Well not really  but things have changed slightly and it makes my last <a href="http://yobriefcase.posterous.com/validateinputattribute-changes-in-mvc3">post</a> slightly redundant.</p><p>Let me clarify a bit.  The Exclude property no longer exists.  It has instead  been replaced with a new attribute SkipRequestValidation.  This is a  per-property attribute that lets you specify what properties should be  excluded.  This has the effect of pushing the validation flag down to the model  rather than on the controller.  This make a lot more sense especially in a  controller/solution that has many actions accepting the model.  So what  changes?  Lets take a look at our old code.</p><p>First things first the controller no longer need the ValidateInput attribute  and goes back to the old "thinner" version of itself,</p><p></p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">HomeController</span> <span class="p">:</span> <span class="n">Controller</span>
<span class="p">{</span>
    <span class="k">public</span> <span class="n">ActionResult</span> <span class="nf">Post</span><span class="p">()</span>
    <span class="p">{</span>
        <span class="k">return</span> <span class="nf">View</span><span class="p">();</span>
    <span class="p">}</span>

<span class="na">    [HttpPost]</span>
    <span class="k">public</span> <span class="n">ActionResult</span> <span class="nf">Post</span><span class="p">(</span><span class="n">ForumPost</span> <span class="n">post</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="k">return</span> <span class="nf">View</span><span class="p">();</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<p>We then need to mark the Bodyt property of our ForumPost model to skip  validation,</p><p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">ForumPost</span>
<span class="p">{</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">Subject</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
<span class="na">    </span>
<span class="na">    [SkipRequestValidation]</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">Body</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
</p><p>Thats it.  To be honest this feels like a much neater solution.  It feels  more a solution rather than a tacked on property that was added to cater for a  specific case.</p><p>ASP.NET MVC 3 RC1 is out now - read more <a href="http://weblogs.asp.net/scottgu/archive/2010/11/09/announcing-the-asp-net-mvc-3-release-candidate.aspx">here</a>,  and get it <a href="http://go.microsoft.com/fwlink/?LinkID=191797">here</a>.</p>