---
date: 2010-10-22T23:00:00Z
title: Unobtrusive JavaScript in MVC3
published: true
categories: [JavaScript, .NET]
type: article
external: false
---
<p></p><p>One of the "it's not new but it's cool" features that appeared in the ASP.NET  MVC3 Beta was Unobtrusive JavaScript (well actually Unobtrusive Ajax and an  unobtrusive validation adapter for the jQuery Validation plugin).  Before we  dive into how this differs from MVC2 lets talk about JavaScript in MVC.</p><p>First things first - jQuery is now the defacto standard for any ASP.NET MVC  solution.  In the MVC2 Project Template you got both the Microsoft Ajax Library  and jQuery.  This hasn't changed in MVC3 but the Microsoft stuff is only there  for any potential backward compatibility issues a solution may have - feel free  to just delete these files and embrace jQuery.  All the new client side stuff is  all facilitated through jQuery which means you wont have to have some other  framework on your page just because generated code mandates it.</p><h2>What Is It?</h2><p>Unobtrusive JavaScript (in the MVC3 sense) is a strategy that ensures that no  JavaScript is embedded within the markup (unless you do it yourself).  100% no  generated code muddying your markup.  No code islands, no inline event handlers,  better handling of failure cases and no dependence on any specific framework.   To me, being a web focused developer, this is HUGE and it should be to you too -  it is after all considered best practise.</p><h2>Turn It On</h2><p>There are two ways to turn Unobtrusive Ajax/Validation on,</p><h3>1. Web.Config</h3><p>Within the &lt;appSettings&gt; config node in Web.Config you can specify  whether unobtrusive JavaScript is on or off</p><p></p><div class="highlight"><pre><code>  <span class="nt">&lt;appSettings&gt;</span>
    <span class="nt">&lt;add</span> <span class="na">key=</span><span class="s">"enableSimpleMembership"</span> <span class="na">value=</span><span class="s">"false"</span> <span class="nt">/&gt;</span>
    <span class="nt">&lt;add</span> <span class="na">key=</span><span class="s">"ClientValidationEnabled"</span> <span class="na">value=</span><span class="s">"true"</span><span class="nt">/&gt;</span> 
    <span class="nt">&lt;add</span> <span class="na">key=</span><span class="s">"UnobtrusiveJavaScriptEnabled"</span> <span class="na">value=</span><span class="s">"true"</span><span class="nt">/&gt;</span> 
  <span class="nt">&lt;/appSettings&gt;</span>
</code></pre></div>
<h3>2 On a Per-Page Basis</h3><p>Just like EnableClientValidation it is possible to activate unobtrusive  JavaScript  at a page level.</p><p></p><div class="highlight"><pre><code><span class="err">&lt;</span>% Html.EnableUnobtrusiveJavaScript(); %&gt;
</code></pre></div>
<h2>What Does It Do</h2><p>Best way to show what it does is by comparison to, um, Obtrusive JavaScript.   So lets create a scenario in both MVC2 and MVC3 and seeing how they compare.   The scenario will be a simple ajaxified login form (Username and Password) with  some client validation and no server side magic.  Most of this, bar the client  side scripts, is the same across both solutions. </p><h2>The Model</h2><p></p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">Credentials</span>
<span class="p">{</span>
<span class="na">    [Required]</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">Username</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
<span class="na">    [Required]</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">Password</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<p>Two properties with basic [Required] validation. </p><h2>The Controller</h2><p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">HomeController</span> <span class="p">:</span> <span class="n">Controller</span>
<span class="p">{</span>
    <span class="k">public</span> <span class="n">ActionResult</span> <span class="nf">Login</span><span class="p">()</span>
    <span class="p">{</span>
        <span class="k">return</span> <span class="nf">View</span><span class="p">();</span>
    <span class="p">}</span>

<span class="na">    [HttpPost]</span>
    <span class="k">public</span> <span class="n">ActionResult</span> <span class="nf">Login</span><span class="p">(</span><span class="n">Credentials</span> <span class="n">credentials</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="k">return</span> <span class="k">new</span> <span class="nf">EmptyResult</span><span class="p">();</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
</p><p>Again there isn't much going on here, this all about the client side!</p><h2>The View</h2><h3>Common Parts</h3><p></p><div class="highlight"><pre><code><span class="err">&lt;</span>%@ Page Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<span class="nt">&lt;Common</span><span class="err">.</span><span class="na">Model</span><span class="err">.</span><span class="na">Credentials</span><span class="nt">&gt;</span>" %&gt;

<span class="nt">&lt;asp:Content</span> <span class="na">ContentPlaceHolderID=</span><span class="s">"TitleContent"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
	Login
<span class="nt">&lt;/asp:Content&gt;</span>

<span class="nt">&lt;asp:Content</span> <span class="na">ContentPlaceHolderID=</span><span class="s">"MainContent"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
    <span class="nt">&lt;h2&gt;</span>Login<span class="nt">&lt;/h2&gt;</span>
    <span class="err">&lt;</span>% using (Ajax.BeginForm(new AjaxOptions { OnSuccess = "onSuccess" })) { %&gt;
        <span class="err">&lt;</span>%=Html.ValidationSummary(true)%&gt;
        <span class="nt">&lt;p&gt;</span>
            <span class="err">&lt;</span>%=Html.LabelFor(c =&gt; c.Username)%&gt;
            <span class="err">&lt;</span>%=Html.TextBoxFor(c =&gt; c.Username)%&gt;
            <span class="err">&lt;</span>%=Html.ValidationMessageFor(c =&gt; c.Username)%&gt;
        <span class="nt">&lt;/p&gt;</span>
        <span class="nt">&lt;p&gt;</span>
            <span class="err">&lt;</span>%=Html.LabelFor(c =&gt; c.Password)%&gt;
            <span class="err">&lt;</span>%=Html.PasswordFor(c =&gt; c.Password)%&gt;
            <span class="err">&lt;</span>%=Html.ValidationMessageFor(c =&gt; c.Password)%&gt;
        <span class="nt">&lt;/p&gt;</span>
        <span class="nt">&lt;input</span> <span class="na">type=</span><span class="s">"submit"</span> <span class="na">value=</span><span class="s">"Login"</span> <span class="nt">/&gt;</span>
    <span class="err">&lt;</span>%} %&gt;

    <span class="c">&lt;!-- JAVASCRIPT LIBRARIES GO HERE! --&gt;</span>

    <span class="nt">&lt;script </span><span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;</span>
        <span class="kd">function</span> <span class="nx">onSuccess</span><span class="p">()</span> <span class="p">{</span>
            <span class="c1">// logged in, carry on</span>
        <span class="p">}</span>
    <span class="nt">&lt;/script&gt;</span>
<span class="nt">&lt;/asp:Content&gt;</span>
</code></pre></div>
<p>A simple view that is the same across both projects the only thing that is  going to differ is the actual libraries/scripts that do all the wiring up. </p><h3>MVC2</h3><p></p><div class="highlight"><pre><code>    <span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"&lt;%=Url.Content("</span><span class="err">~/</span><span class="na">Scripts</span><span class="err">/</span><span class="na">MicrosoftAjax</span><span class="err">.</span><span class="na">js</span><span class="err">")</span> <span class="err">%</span><span class="nt">&gt;</span><span class="s2">" type="</span><span class="nx">text</span><span class="o">/</span><span class="nx">javascript</span><span class="err">"</span><span class="o">&gt;</span><span class="nt">&lt;/script&gt;</span>
    <span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"&lt;%=Url.Content("</span><span class="err">~/</span><span class="na">Scripts</span><span class="err">/</span><span class="na">MicrosoftMvcAjax</span><span class="err">.</span><span class="na">js</span><span class="err">")</span> <span class="err">%</span><span class="nt">&gt;</span><span class="s2">" type="</span><span class="nx">text</span><span class="o">/</span><span class="nx">javascript</span><span class="err">"</span><span class="o">&gt;</span><span class="nt">&lt;/script&gt;</span>
    <span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"&lt;%=Url.Content("</span><span class="err">~/</span><span class="na">Scripts</span><span class="err">/</span><span class="na">MicrosoftMvcValidation</span><span class="err">.</span><span class="na">js</span><span class="err">")%</span><span class="nt">&gt;</span><span class="s2">" type="</span><span class="nx">text</span><span class="o">/</span><span class="nx">javascript</span><span class="err">"</span><span class="o">&gt;</span><span class="nt">&lt;/script&gt;</span>
</code></pre></div>
<p>Using the out of the box Microsoft Ajax Library (which is now deprecated)</p><h3>MVC3</h3><p></p><div class="highlight"><pre><code>    <span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"&lt;%=Url.Content("</span><span class="err">~/</span><span class="na">Scripts</span><span class="err">/</span><span class="na">jquery-1</span><span class="err">.</span><span class="na">4</span><span class="err">.</span><span class="na">1</span><span class="err">.</span><span class="na">min</span><span class="err">.</span><span class="na">js</span><span class="err">")%</span><span class="nt">&gt;</span><span class="s2">" type="</span><span class="nx">text</span><span class="o">/</span><span class="nx">javascript</span><span class="err">"</span><span class="o">&gt;</span><span class="nt">&lt;/script&gt;</span>
    <span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"&lt;%=Url.Content("</span><span class="err">~/</span><span class="na">Scripts</span><span class="err">/</span><span class="na">jquery</span><span class="err">.</span><span class="na">validate</span><span class="err">.</span><span class="na">min</span><span class="err">.</span><span class="na">js</span><span class="err">")%</span><span class="nt">&gt;</span><span class="s2">" type="</span><span class="nx">text</span><span class="o">/</span><span class="nx">javascript</span><span class="err">"</span><span class="o">&gt;</span><span class="nt">&lt;/script&gt;</span>
    <span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"&lt;%=Url.Content("</span><span class="err">~/</span><span class="na">Scripts</span><span class="err">/</span><span class="na">jquery</span><span class="err">.</span><span class="na">unobtrusive-ajax</span><span class="err">.</span><span class="na">min</span><span class="err">.</span><span class="na">js</span><span class="err">")%</span><span class="nt">&gt;</span><span class="s2">" type="</span><span class="nx">text</span><span class="o">/</span><span class="nx">javascript</span><span class="err">"</span><span class="o">&gt;</span><span class="nt">&lt;/script&gt;</span>
    <span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"&lt;%=Url.Content("</span><span class="err">~/</span><span class="na">Scripts</span><span class="err">/</span><span class="na">jquery</span><span class="err">.</span><span class="na">validate</span><span class="err">.</span><span class="na">unobtrusive</span><span class="err">.</span><span class="na">min</span><span class="err">.</span><span class="na">js</span><span class="err">")%</span><span class="nt">&gt;</span><span class="s2">" type="</span><span class="nx">text</span><span class="o">/</span><span class="nx">javascript</span><span class="err">"</span><span class="o">&gt;</span><span class="nt">&lt;/script&gt;</span>
</code></pre></div>
<p>Using the out of the box jQuery plugins.</p><p>Done.  That's all we need for our solutions to function.  The form will be  validated on the client side and submission will be performed through ajax.   Both will behave exactly the same but the markup and code they generate will be  quite different.  I've tidied both up in terms of formatting just so it's  slightly easier to read but I've tried to keep the layout style consistent  across both.</p><h2>MVC2 Output</h2><p></p><div class="highlight"><pre><code><span class="cp">&lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"&gt;</span>
<span class="nt">&lt;html</span> <span class="na">xmlns=</span><span class="s">"http://www.w3.org/1999/xhtml"</span><span class="nt">&gt;</span>
<span class="nt">&lt;head&gt;</span>
    <span class="nt">&lt;title&gt;</span>Login <span class="nt">&lt;/title&gt;</span>
    <span class="nt">&lt;link</span> <span class="na">href=</span><span class="s">"/Mvc2/Content/Site.css"</span> <span class="na">rel=</span><span class="s">"stylesheet"</span> <span class="na">type=</span><span class="s">"text/css"</span> <span class="nt">/&gt;</span>
<span class="nt">&lt;/head&gt;</span>
<span class="nt">&lt;body&gt;</span>
    <span class="nt">&lt;a</span> <span class="na">href=</span><span class="s">"/Mvc2/"</span><span class="nt">&gt;</span>Home<span class="nt">&lt;/a&gt;</span>
    <span class="nt">&lt;h2&gt;</span>
        Login<span class="nt">&lt;/h2&gt;</span>
    <span class="nt">&lt;form</span> <span class="na">action=</span><span class="s">"/Mvc2/Home/Login"</span> 
          <span class="na">id=</span><span class="s">"form0"</span> 
          <span class="na">method=</span><span class="s">"post"</span> 
          <span class="na">onclick=</span><span class="s">"Sys.Mvc.AsyncForm.handleClick(this, new Sys.UI.DomEvent(event));"</span>
          <span class="na">onsubmit=</span><span class="s">"Sys.Mvc.AsyncForm.handleSubmit(this, new Sys.UI.DomEvent(event), { insertionMode: Sys.Mvc.InsertionMode.replace, onSuccess: Function.createDelegate(this, onSuccess) });"</span><span class="nt">&gt;</span>
    <span class="nt">&lt;div</span> <span class="na">class=</span><span class="s">"validation-summary-valid"</span> <span class="na">id=</span><span class="s">"validationSummary"</span><span class="nt">&gt;</span>
        <span class="nt">&lt;ul&gt;</span>
            <span class="nt">&lt;li</span> <span class="na">style=</span><span class="s">"display: none"</span><span class="nt">&gt;&lt;/li&gt;</span>
        <span class="nt">&lt;/ul&gt;</span>
    <span class="nt">&lt;/div&gt;</span>
    <span class="nt">&lt;p&gt;</span>
        <span class="nt">&lt;label</span> <span class="na">for=</span><span class="s">"Username"</span><span class="nt">&gt;</span>Username<span class="nt">&lt;/label&gt;</span>
        <span class="nt">&lt;input</span> <span class="na">id=</span><span class="s">"Username"</span> <span class="na">name=</span><span class="s">"Username"</span> <span class="na">type=</span><span class="s">"text"</span> <span class="na">value=</span><span class="s">""</span> <span class="nt">/&gt;</span>
        <span class="nt">&lt;span</span> <span class="na">class=</span><span class="s">"field-validation-valid"</span> <span class="na">id=</span><span class="s">"Username_validationMessage"</span><span class="nt">&gt;&lt;/span&gt;</span>
    <span class="nt">&lt;/p&gt;</span>
    <span class="nt">&lt;p&gt;</span>
        <span class="nt">&lt;label</span> <span class="na">for=</span><span class="s">"Password"</span><span class="nt">&gt;</span>Password<span class="nt">&lt;/label&gt;</span>
        <span class="nt">&lt;input</span> <span class="na">id=</span><span class="s">"Password"</span> <span class="na">name=</span><span class="s">"Password"</span> <span class="na">type=</span><span class="s">"password"</span> <span class="nt">/&gt;</span>
        <span class="nt">&lt;span</span> <span class="na">class=</span><span class="s">"field-validation-valid"</span> <span class="na">id=</span><span class="s">"Password_validationMessage"</span><span class="nt">&gt;&lt;/span&gt;</span>
    <span class="nt">&lt;/p&gt;</span>
    <span class="nt">&lt;input</span> <span class="na">type=</span><span class="s">"submit"</span> <span class="na">value=</span><span class="s">"Login"</span> <span class="nt">/&gt;</span>
    <span class="nt">&lt;/form&gt;</span>
    <span class="nt">&lt;script </span><span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;</span> 
    <span class="c1">//&lt;![CDATA[</span>
        <span class="k">if</span> <span class="p">(</span><span class="o">!</span><span class="nb">window</span><span class="p">.</span><span class="nx">mvcClientValidationMetadata</span><span class="p">)</span> <span class="p">{</span> <span class="nb">window</span><span class="p">.</span><span class="nx">mvcClientValidationMetadata</span> <span class="o">=</span> <span class="p">[];</span> <span class="p">}</span>
        <span class="nb">window</span><span class="p">.</span><span class="nx">mvcClientValidationMetadata</span><span class="p">.</span><span class="nx">push</span><span class="p">({</span>
            <span class="s2">"Fields"</span><span class="o">:</span> <span class="p">[{</span>
                <span class="s2">"FieldName"</span><span class="o">:</span> <span class="s2">"Username"</span><span class="p">,</span>
                <span class="s2">"ReplaceValidationMessageContents"</span><span class="o">:</span> <span class="kc">true</span><span class="p">,</span>
                <span class="s2">"ValidationMessageId"</span><span class="o">:</span> <span class="s2">"Username_validationMessage"</span><span class="p">,</span>
                <span class="s2">"ValidationRules"</span><span class="o">:</span> <span class="p">[{</span>
                    <span class="s2">"ErrorMessage"</span><span class="o">:</span> <span class="s2">"The Username field is required."</span><span class="p">,</span>
                    <span class="s2">"ValidationParameters"</span><span class="o">:</span> <span class="p">{},</span>
                    <span class="s2">"ValidationType"</span><span class="o">:</span> <span class="s2">"required"</span>
                <span class="p">}]</span>
            <span class="p">},</span> <span class="p">{</span>
                <span class="s2">"FieldName"</span><span class="o">:</span> <span class="s2">"Password"</span><span class="p">,</span>
                <span class="s2">"ReplaceValidationMessageContents"</span><span class="o">:</span> <span class="kc">true</span><span class="p">,</span>
                <span class="s2">"ValidationMessageId"</span><span class="o">:</span> <span class="s2">"Password_validationMessage"</span><span class="p">,</span>
                <span class="s2">"ValidationRules"</span><span class="o">:</span> <span class="p">[{</span>
                    <span class="s2">"ErrorMessage"</span><span class="o">:</span> <span class="s2">"The Password field is required."</span><span class="p">,</span>
                    <span class="s2">"ValidationParameters"</span><span class="o">:</span> <span class="p">{},</span>
                    <span class="s2">"ValidationType"</span><span class="o">:</span> <span class="s2">"required"</span>
                <span class="p">}]</span>
            <span class="p">}],</span>
            <span class="s2">"FormId"</span><span class="o">:</span> <span class="s2">"form0"</span><span class="p">,</span>
            <span class="s2">"ReplaceValidationSummary"</span><span class="o">:</span> <span class="kc">false</span><span class="p">,</span>
            <span class="s2">"ValidationSummaryId"</span><span class="o">:</span> <span class="s2">"validationSummary"</span> 
        <span class="p">});</span>
    <span class="c1">//]]&gt;</span>
    <span class="nt">&lt;/script&gt;</span>
    <span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"/Mvc2/Scripts/MicrosoftAjax.js"</span> <span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;&lt;/script&gt;</span>
    <span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"/Mvc2/Scripts/MicrosoftMvcAjax.js"</span> <span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;&lt;/script&gt;</span>
    <span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"/Mvc2/Scripts/MicrosoftMvcValidation.js"</span> <span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;&lt;/script&gt;</span>
    <span class="nt">&lt;script </span><span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;</span>
        <span class="kd">function</span> <span class="nx">onSuccess</span><span class="p">()</span> <span class="p">{</span>
            <span class="c1">// logged in, carry on</span>
        <span class="p">}</span>
    <span class="nt">&lt;/script&gt;</span>
<span class="nt">&lt;/body&gt;</span>
<span class="nt">&lt;/html&gt;</span>
</code></pre></div>
<h2>MVC 3 Output</h2><p></p><div class="highlight"><pre><code><span class="cp">&lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"&gt;</span>
<span class="nt">&lt;html</span> <span class="na">xmlns=</span><span class="s">"http://www.w3.org/1999/xhtml"</span><span class="nt">&gt;</span>
<span class="nt">&lt;head&gt;</span>
    <span class="nt">&lt;title&gt;</span>Login <span class="nt">&lt;/title&gt;</span>
    <span class="nt">&lt;link</span> <span class="na">href=</span><span class="s">"/Mvc3/Content/Site.css"</span> <span class="na">rel=</span><span class="s">"stylesheet"</span> <span class="na">type=</span><span class="s">"text/css"</span> <span class="nt">/&gt;</span>
<span class="nt">&lt;/head&gt;</span>
<span class="nt">&lt;body&gt;</span>
    <span class="nt">&lt;a</span> <span class="na">href=</span><span class="s">"/Mvc3/"</span><span class="nt">&gt;</span>Home<span class="nt">&lt;/a&gt;</span>
    <span class="nt">&lt;h2&gt;</span>
        Login<span class="nt">&lt;/h2&gt;</span>
    <span class="nt">&lt;form</span> <span class="na">action=</span><span class="s">"/Mvc3/Home/Login"</span> 
          <span class="na">data-ajax=</span><span class="s">"true"</span> 
          <span class="na">data-ajax-success=</span><span class="s">"onSuccess"</span>
          <span class="na">id=</span><span class="s">"form0"</span> <span class="na">method=</span><span class="s">"post"</span><span class="nt">&gt;</span>
    <span class="nt">&lt;p&gt;</span>
        <span class="nt">&lt;label</span> <span class="na">for=</span><span class="s">"Username"</span><span class="nt">&gt;</span>Username<span class="nt">&lt;/label&gt;</span>
        <span class="nt">&lt;input</span> <span class="na">data-val=</span><span class="s">"true"</span> <span class="na">data-val-required=</span><span class="s">"The Username field is required."</span> <span class="na">id=</span><span class="s">"Username"</span> <span class="na">name=</span><span class="s">"Username"</span> <span class="na">type=</span><span class="s">"text"</span> <span class="na">value=</span><span class="s">""</span> <span class="nt">/&gt;</span>
        <span class="nt">&lt;span</span> <span class="na">class=</span><span class="s">"field-validation-valid"</span> <span class="na">data-valmsg-for=</span><span class="s">"Username"</span> <span class="na">data-valmsg-replace=</span><span class="s">"true"</span><span class="nt">&gt;</span>
        <span class="nt">&lt;/span&gt;</span>
    <span class="nt">&lt;/p&gt;</span>
    <span class="nt">&lt;p&gt;</span>
        <span class="nt">&lt;label</span> <span class="na">for=</span><span class="s">"Password"</span><span class="nt">&gt;</span>Password<span class="nt">&lt;/label&gt;</span>
        <span class="nt">&lt;input</span> <span class="na">data-val=</span><span class="s">"true"</span> <span class="na">data-val-required=</span><span class="s">"The Password field is required."</span> <span class="na">id=</span><span class="s">"Password"</span> <span class="na">name=</span><span class="s">"Password"</span> <span class="na">type=</span><span class="s">"password"</span> <span class="nt">/&gt;</span>
        <span class="nt">&lt;span</span> <span class="na">class=</span><span class="s">"field-validation-valid"</span> <span class="na">data-valmsg-for=</span><span class="s">"Password"</span> <span class="na">data-valmsg-replace=</span><span class="s">"true"</span><span class="nt">&gt;</span>
        <span class="nt">&lt;/span&gt;</span>
    <span class="nt">&lt;/p&gt;</span>
    <span class="nt">&lt;input</span> <span class="na">type=</span><span class="s">"submit"</span> <span class="na">value=</span><span class="s">"Login"</span> <span class="nt">/&gt;</span>
    <span class="nt">&lt;/form&gt;</span>
    <span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"/Mvc3/Scripts/jquery-1.4.1.min.js"</span> <span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;&lt;/script&gt;</span>
    <span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"/Mvc3/Scripts/jquery.validate.min.js"</span> <span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;&lt;/script&gt;</span>
    <span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"/Mvc3/Scripts/jquery.unobtrusive-ajax.min.js"</span> <span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;&lt;/script&gt;</span>
    <span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"/Mvc3/Scripts/jquery.validate.unobtrusive.min.js"</span> <span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;&lt;/script&gt;</span>
    <span class="nt">&lt;script </span><span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;</span>
        <span class="kd">function</span> <span class="nx">onSuccess</span><span class="p">()</span> <span class="p">{</span>
            <span class="c1">// logged in, carry on</span>
        <span class="p">}</span>
    <span class="nt">&lt;/script&gt;</span>
<span class="nt">&lt;/body&gt;</span>
<span class="nt">&lt;/html&gt;</span>
</code></pre></div>
<h2>Analysis</h2><p>The first obvious thing we can see is that the UnobJS code is slightly  lighter, even with strict formatting turned it generally produces less LOC's.   But that's not really that important in the grand scheme of things, but is  important is the whole unobtrusiveness of the UnobJS source.</p><p>In the old MVC2 code look at those inline event handlers at lines 14 and 15,  look at that huge code island at line 33, smack bang in the middle of our HTML.   It's not wrong per say but it's certainly not best practise.  No it's always  best to keep your View (HTML) and your Code (JavaScript) separated as much as  possible.  Imagine if some wayward script was added between line 61 and 62 did  something like this</p><p></p><div class="highlight"><pre><code><span class="nb">window</span><span class="p">.</span><span class="nx">mvcClientValidationMetadata</span> <span class="o">=</span> <span class="kc">null</span><span class="p">;</span>
<span class="nb">document</span><span class="p">.</span><span class="nx">forms</span><span class="p">[</span><span class="mi">0</span><span class="p">].</span><span class="nx">onclick</span> <span class="o">=</span> <span class="kd">function</span><span class="p">()</span> <span class="p">{</span> <span class="p">};</span>
<span class="nb">document</span><span class="p">.</span><span class="nx">forms</span><span class="p">[</span><span class="mi">0</span><span class="p">].</span><span class="nx">onsubmit</span> <span class="o">=</span> <span class="kd">function</span><span class="p">()</span> <span class="p">{</span> <span class="p">};</span>
</code></pre></div>
<p>OHT3HNOES END OF THE WORLD!</p><p>Or what if your CDN that served your scripts was down?  You are actually  going to get JavaScript errors during form submission which can, in various  browsers, prevent the form being submitted.</p><p>OHT3HNOES END OF THE WORLD AGAIN!</p><p>MVC 3/UnobJS on the other hand isn't going to error out and will fall back to  a normal form submission - progressive enhancement or just expected  behaviour?</p><p>On other thing... those inline event handlers pretty much need to have the MS  Ajax Library available to work (and not cause errors).  What if I was already  using jQuery for my project?  I'd still need to include MS Ajax on the page even  though only generated code required it (well I could write my own API mimicking  the required MS Ajax API but why should I?).</p><p>Now Unobtrusive JavaScript isn't without it's issues.  Using HTML5's data-  attributes can invalidate your HTML which can be a showstopper for some  projects.  One other issue that we may start to see is that EVERYONE is starting  to use data- attributes (e.g. jQuery Mobile) I wonder if we are going to start  seeing collisions between different libraries?</p><p>All in all I don't think there should be an option to turn  Unobtrusive JavaScript off, it should just be the only way of doing things :-)  but thats just me.</p>