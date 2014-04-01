---
date: 2010-07-27T23:00:00Z
title: ASP.NET MVC 3 Preview 1 First Look
published: true
categories: [.NET]
type: article
external: false
---
<p style="margin-right: 0cm; margin-bottom: 16.5pt; margin-left: 0cm; line-height: 16.5pt; vertical-align: baseline;"><span style="font-size: 11.5pt; font-family: Georgia,serif; color: #333333;">[[posterous-content:6xLmqhke9CMHGNqiaUG7]]</span><span style="font-size: 11.5pt; font-family: Georgia,serif; color: #333333;"> </span></p><p>A much better read than the ramblings below:<br /><a href="http://weblogs.asp.net/scottgu/archive/2010/07/27/introducing-asp-net-mvc-3-preview-1.aspx">http://weblogs.asp.net/scottgu/archive/2010/07/27/introducing-asp-net-mvc-3-preview-1.aspx</a></p><p>So I had a chance to take ASP.NET MVC 3 Preview 1 out for a test drive today to see what was new and interesting.  For a Preview release it seems fairly solid - there is a lot of low level stuff introduced (new Interfaces etc) which is, I assume, going be used to add new features in Preview 2, 3, 4 whatever.  There is nothing MASSIVE per say in fact it really feels like the next logical step in the MVC roadmap rather than some crazy reimagining.  That's good by the way - backwards compatibility maintained and life goes on as normal.  The most obvious "issue" I have with this release is that it's NET4/VS2010 only which means if it came out tomorrow we'd probably not have the choice of making use of it due to the snails pace of the "enterprise".  Anyways enough preamble, down to the juicy stuff - what's this release giving us?</p><h2>Razor</h2><p>Preview 1 introduces Microsofts new ViewEngine, Razor, to the MVC world (WebMatrix aside).  Currently there is no syntax highlighting/intellisense for it but it'll still compile and generate views just fine.  I don't want to dive into Razor in this post but it's a weird syntax.  It's certainly feels much neater than the WebForms View Engine and for a weird reason it felt quite natural to use it - want to embed some serverside code?  Just type @ and you drop into serverside mode but with the advantage of actually still being able to embed HTML content without having to escape it.  The render just knows.  It's strange but good.</p><p></p><div class="highlight"><pre><code>@inherits System.Web.Mvc.WebViewPage<span class="nt">&lt;Models</span><span class="err">.</span><span class="na">Person</span><span class="nt">&gt;</span>
@{
    View.Title = "Home Page";
    LayoutPage = "~/Views/Shared/_Layout.cshtml";
}

<span class="nt">&lt;div&gt;</span>
    @Html.ValidationSummary()
    @{Html.EnableClientValidation();}
    @using(Html.BeginForm()){
        <span class="nt">&lt;p&gt;</span>Name:<span class="nt">&lt;/p&gt;</span>
        @Html.TextBoxFor(p =&gt; p.Name)
        <span class="nt">&lt;div&gt;</span>@Html.ValidationMessageFor(p =&gt; p.Name)<span class="nt">&lt;/div&gt;</span>
        <span class="nt">&lt;input</span> <span class="na">type=</span><span class="s">"submit"</span> <span class="na">value=</span><span class="s">"Submit"</span><span class="nt">/&gt;</span>
    }
<span class="nt">&lt;/div&gt;</span>
</code></pre></div>
<p>Read more here: <a href="http://weblogs.asp.net/scottgu/archive/2010/07/02/introducing-razor.aspx">http://weblogs.asp.net/scottgu/archive/2010/07/02/introducing-razor.aspx</a></p><p>On a side note when you create  a new view MVC 3 and VS2010 detects installed view engines so you can EASILY pick and choose which is very nice - though no ViewEngine currently supports this e.g.</p><p>[[posterous-content:Ryck0mqEkX92dSBGu1n4]]</p><p>Model Validation</p><p>There is some nice stuff in this area.  In MVC you where restricted to validating properties of a model (by default using data annotations) and if you wanted to validate entire models or perform validation in model scope (e.g. compare 2 fields) you had to write custom code.</p><p>MVC3 provides model-level validation in 2 ways,</p><p style="" class="MsoListParagraph"><span style="">1.<span style="font: 7.0pt Times New Roman;">       </span></span>Using Validation Attributes</p><p style="" class="MsoListParagraph"><span style="">2.<span style="font: 7.0pt Times New Roman;">       </span></span>Using the new data annotations interface IValidateObject</p><p>Validation attributes are the same attributes that the default validation strategy currently uses except you now have access to the model and ValidationResult has been tweaked to allow broken rules to be bound to multiple fields e.g.</p><p></p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">PasswordsMustMatchAttribute</span> <span class="p">:</span> <span class="n">ValidationAttribute</span>
<span class="p">{</span>
    <span class="k">protected</span> <span class="k">override</span> <span class="n">ValidationResult</span> <span class="nf">IsValid</span><span class="p">(</span>
        <span class="kt">object</span> <span class="k">value</span><span class="p">,</span> <span class="n">ValidationContext</span> <span class="n">validationContext</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="kt">var</span> <span class="n">model</span> <span class="p">=</span> <span class="n">validationContext</span><span class="p">.</span><span class="n">ObjectInstance</span> <span class="k">as</span> <span class="n">Person</span><span class="p">;</span>
        <span class="k">if</span> <span class="p">(</span><span class="n">model</span><span class="p">.</span><span class="n">Password</span> <span class="p">==</span> <span class="n">model</span><span class="p">.</span><span class="n">PasswordConfirm</span><span class="p">)</span>
        <span class="p">{</span>
            <span class="k">return</span> <span class="n">ValidationResult</span><span class="p">.</span><span class="n">Success</span><span class="p">;</span>
        <span class="p">}</span>

        <span class="k">return</span> <span class="k">new</span> <span class="nf">ValidationResult</span><span class="p">(</span>
            <span class="s">"Password and Password Confirmation must match"</span><span class="p">,</span>
            <span class="k">new</span> <span class="kt">string</span><span class="p">[]</span> <span class="p">{</span> <span class="s">"Password"</span><span class="p">,</span> <span class="s">"PasswordConfirm"</span> <span class="p">});</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<p>IValidateObject interface is the new way to provide validation of a Model object across all MS technologies (EntityFramework, DynamicData, Silverlight and ADO Data Services) and it behaves much better than the decoupled attribute stuff - at least in the preview.</p><p>If model implement the IValidateObject interface they are expected to implement the validate(ValidationContext context) method which returns an enumerable of ValidationResults.  The Person model in my sample app gives an example of this,</p><p></p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">Person</span> <span class="p">:</span> <span class="n">IValidatableObject</span>
<span class="p">{</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">Name</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">Nickname</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">Password</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">PasswordConfirm</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="k">public</span> <span class="kt">int</span> <span class="n">Age</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>

    <span class="k">public</span> <span class="n">IEnumerable</span><span class="p">&lt;</span><span class="n">ValidationResult</span><span class="p">&gt;</span> <span class="n">Validate</span><span class="p">(</span>
        <span class="n">ValidationContext</span> <span class="n">validationContext</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="k">if</span> <span class="p">(</span><span class="n">Name</span><span class="p">.</span><span class="n">Equals</span><span class="p">(</span><span class="n">Nickname</span><span class="p">))</span>
        <span class="p">{</span>
            <span class="k">yield</span> <span class="k">return</span> <span class="k">new</span> <span class="nf">ValidationResult</span><span class="p">(</span>
                <span class="s">"Name and Nickname cannot be the same"</span><span class="p">,</span>
                <span class="k">new</span> <span class="kt">string</span><span class="p">[]</span> <span class="p">{</span> <span class="s">"Name"</span><span class="p">,</span> <span class="s">"Nickname"</span> <span class="p">});</span>
        <span class="p">}</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<h2>Global Filters</h2><p>More of a convenience than anything, global filters allow you to apply the same Action Filters to EVERY controller action within an application.  Useful for all tha AOP style stuff like logging etc.  Taking the new KDF Action Logger we can log every action invocation by pushing the attribute type into the global filters via global.asax like so,</p><p><div class="highlight"><pre><code><span class="n">GlobalFilters</span><span class="p">.</span><span class="n">Filters</span><span class="p">.</span><span class="n">Add</span><span class="p">(</span><span class="k">new</span> <span class="n">LoggingFilter</span><span class="p">());</span>
</code></pre></div>
</p><p>Thats it really.  Nice and convenient.</p><h2>Json Model Binding</h2><p>Typically model bindng is done through creating an new instance of the model and attempting to bind up properties against the names of the values submitted by the client.  This doesn't really work in the Ajax world because the body of a POST request is generally a single unnamed parameter which represents a JSON encoded string of the client request data.  In MVC2 we had to write custom binders to parse the string and bind to a model but in MVC3 this is provided automatically.  Again another nice to have.</p><h2>Other stuff</h2><p style="" class="MsoListParagraph"><span style="font-family: Symbol;"><span style="">·<span style="font: 7.0pt Times New Roman;">         </span></span></span>Dynamic ViewModel  - Simply a dynamic bag allowing us to use ViewModel.SomeProperty vs ViewData["SomeProperty"]</p><p style="" class="MsoListParagraph"><span style="font-family: Symbol;"><span style="">·<span style="font: 7.0pt Times New Roman;">         </span></span></span>New hooks for IoC/Dependency Injection through the lifecycle</p><p style="" class="MsoListParagraph"><span style="font-family: Symbol;"><span style="">·<span style="font: 7.0pt Times New Roman;">         </span></span></span>New ways to expose Client Validation and Model Metadata which "should" allow an easier way to create a single point of validation that can be used across both client and server</p><p style="" class="MsoListParagraph"><span style="font-family: Symbol;"><span style="">·<span style="font: 7.0pt Times New Roman;">         </span></span></span>New view results - HttpStatusCodeResult and Permanent Redirects</p><h2>Summing up</h2><p>MVC3 Preview 1 is a solid enough release but ultimately it seems to be a foundation, albeit a solid one.  Given the previous release schedule I expect another release within the next few months.  What can we expect for future releases?  See <a href="http://aspnet.codeplex.com/wikipage?title=Road%20Map&amp;referringTitle=MVC">here</a>.</p>