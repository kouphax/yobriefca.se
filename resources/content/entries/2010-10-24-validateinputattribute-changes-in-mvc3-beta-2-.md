---
date: 2010-10-23T23:00:00Z
title: ValidateInputAttribute Changes in MVC3 (Beta 2)
published: true
categories: [.NET]
type: article
external: false
---
<p><strong>This post is now redundant with the release of ASP.NET MVC 3 RC 1.  SkipRequestValidation is the new ValidateInputAttribute(Exclude="").</strong><span style="color: #ff6600;"><strong>  <a href="http://yobriefcase.posterous.com/all-change-validateinputattribute-and-skipreq">Read More.</a></strong></span></p><p>The ValidateInputAttribute has received a nice little tweak in MVC 3 offering  more fine grained control over parameters of a request. </p><p>In MVC 2 using ValidateInputAttribute was limited to the request level, that  is all parameters in the request where either validated or not.  Lets  demonstrate this with a simple example - a simple forum posting page,</p><p></p><div class="highlight"><pre><code><span class="err">&lt;</span>%@ Page Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<span class="nt">&lt;Common</span><span class="err">.</span><span class="na">Model</span><span class="err">.</span><span class="na">ForumPost</span><span class="nt">&gt;</span>" %&gt;

<span class="nt">&lt;asp:Content</span> <span class="na">ContentPlaceHolderID=</span><span class="s">"TitleContent"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
	Login
<span class="nt">&lt;/asp:Content&gt;</span>

<span class="nt">&lt;asp:Content</span> <span class="na">ContentPlaceHolderID=</span><span class="s">"MainContent"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
    <span class="nt">&lt;h2&gt;</span>Post Question<span class="nt">&lt;/h2&gt;</span>
    <span class="err">&lt;</span>% using (Html.BeginForm()) { %&gt;
        <span class="nt">&lt;p&gt;</span>
            <span class="err">&lt;</span>%=Html.LabelFor(c =&gt; c.Subject)%&gt;
            <span class="err">&lt;</span>%=Html.TextBoxFor(c =&gt; c.Subject)%&gt;
        <span class="nt">&lt;/p&gt;</span>
        <span class="nt">&lt;p&gt;</span>
            <span class="err">&lt;</span>%=Html.LabelFor(c =&gt; c.Body)%&gt;
            <span class="err">&lt;</span>%=Html.TextAreaFor(c =&gt; c.Body)%&gt;
        <span class="nt">&lt;/p&gt;</span>
        <span class="nt">&lt;input</span> <span class="na">type=</span><span class="s">"submit"</span> <span class="na">value=</span><span class="s">"Post"</span> <span class="nt">/&gt;</span>
    <span class="err">&lt;</span>%} %&gt;
<span class="nt">&lt;/asp:Content&gt;</span>
</code></pre></div>
<p>We don;t care what the controller actions actually do but lets describe them  here anyway</p><p></p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">HomeController</span> <span class="p">:</span> <span class="n">Controller</span>
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
<p>Now attempting to post any sort of markup back to the action will result in  an exception being thrown,</p><p>[[posterous-content:DasrjrabrrcEcDeiHdeG]]</p><p>If we wanted to allow markup to go through we can add the  [ValidateInput(false)] attribute to the action.  The only problem with that is  if we only wanted to allow markup in the Body and not the Subject we would have  to write our own tests in the controller to prevent this.  Not the most ideal or  clean solution.</p><p>MVC 3 solves this quite simply by extending the ValidateInputAttribute and  allowing use to specify exclusions.  This means we can have validation turned on  but specifically state the we don't want to validate a specific request  parameter(s) (e.g. Body).</p><p><div class="highlight"><pre><code><span class="na">[ValidateInput(true, Exclude = "Body")]</span>
</code></pre></div>
</p><p>A very minor tweak that makes a big leap to being able to produce cleaner more readable code.</p>