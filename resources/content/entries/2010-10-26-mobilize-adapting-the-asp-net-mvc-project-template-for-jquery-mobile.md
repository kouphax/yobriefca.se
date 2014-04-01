---
date: 2010-10-25T23:00:00Z
title: Mobilize! Adapting the ASP.NET MVC Project Template for jQuery Mobile
published: true
categories: [.NET]
type: article
external: false
---
<p class="img-holder"><img alt="" src="http://farm3.static.flickr.com/2413/5721975436_fb47349a04_o.png" /></p><p style="text-align: right;"><em><span style="font-size: xx-small;"><span style="font-size: xx-small;">Image</span> courtesy of the  awesome </span></em><em><span style="font-size: xx-small;"><a href="http://www.threadless.com/">Threadless</a></span></em></p><p>Having tinkered with <a href="http://yobriefcase.posterous.com/practical-jquery-mobile-with-aspnet-mvc">jQuery  Mobile and MVC</a> a bit recently but I wanted to do a bit more.  The first app  I wrote was read only which is actually pretty easy to accomplish regardless of  technology/framework.  So I wanted to try a little something different,  something a kin to a real application.  However time and coders block left me  with little inspiration for creating the worlds next amazing mobile app.   Sitting there with a new ASP.NET MVC project open in Visual Studio I couldn't  think of what to write.  Then it struck me... don't write anything.  The MVC  starter skeleton project was sitting right there why not just mobilise it?  So I  did.  Without modifying any server side code and only tweaking the views I found  I was able to create a fully jquery-mobilified version of the template.  Here's  what I did.</p><h2>Clean House</h2><p>We need to strip some of the dead weight out of our project including some of  the mark up and resources,</p><h3>Resources</h3><p>First things first I dropped the current bundle of scripts (MS Ajax, jQuery  1.4.1, Validate etc.) and the Site.css.  Next I added the jQuery Mobile  assets,</p><ul><li>jQuery 1.4.3 (jquery-1.4.3.js)</li><li>jQuery Mobile 1.0a1 (jquery.mobile-1.0a1.js)</li><li>jQuery Mobile CSS (jquery.mobile-1.0a1.css)</li><li>jQuery Mobile images</li></ul><p>In the following project structure</p><p class="img-holder"><img src="http://farm3.static.flickr.com/2696/5721416507_18861d0ae1_o.png" /></p><h3>Site.Master</h3><p>Next I cut down the Site.Master to it's bare minimum and add the HTML5  DocType and the references to jQuery Mobile ,</p><p><div class="highlight"><pre><code><span class="err">&lt;</span>%@ Master Language="C#" Inherits="System.Web.Mvc.ViewMasterPage" %&gt;
<span class="cp">&lt;!DOCTYPE html&gt;</span>
<span class="nt">&lt;html&gt;</span>
    <span class="nt">&lt;head</span> <span class="na">id=</span><span class="s">"Head1"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
        <span class="nt">&lt;title&gt;&lt;asp:ContentPlaceHolder</span> <span class="na">ID=</span><span class="s">"TitleContent"</span> <span class="na">runat=</span><span class="s">"server"</span> <span class="nt">/&gt;&lt;/title&gt;</span>
        <span class="nt">&lt;link</span> <span class="na">href=</span><span class="s">"../../Content/jquery.mobile-1.0a1.css"</span> <span class="na">rel=</span><span class="s">"stylesheet"</span> <span class="na">type=</span><span class="s">"text/css"</span> <span class="nt">/&gt;</span>
        <span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"../../Scripts/jquery-1.4.3.min.js"</span> <span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;&lt;/script&gt;</span>
        <span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"../../Scripts/jquery.mobile-1.0a1.min.js"</span> <span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;&lt;/script&gt;</span>
    <span class="nt">&lt;/head&gt;</span>
    <span class="nt">&lt;body&gt;</span>
        <span class="nt">&lt;asp:ContentPlaceHolder</span> <span class="na">ID=</span><span class="s">"MainContent"</span> <span class="na">runat=</span><span class="s">"server"</span> <span class="nt">/&gt;</span>
    <span class="nt">&lt;/body&gt;</span>
<span class="nt">&lt;/html&gt;</span>
</code></pre></div>
</p><p>I could have included the basic markup for a full page but I wanted to keep  it as flexible for now.  I also removed the LogOnUserControl for now as I want  to just push it into the main page.</p><h2>Marking Up</h2><p>The next step was to tweak the markup of the pages so they comply with jQuery  Mobile and can be mobilised correctly.  Most pages are marked up in the same way  so rather than list them all I'll just list the highlights.</p><h3>Home.aspx</h3><p></p><div class="highlight"><pre><code><span class="err">&lt;</span>%@ Page Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage" %&gt;

<span class="nt">&lt;asp:Content</span> <span class="na">ID=</span><span class="s">"Content1"</span> <span class="na">ContentPlaceHolderID=</span><span class="s">"TitleContent"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
    Home Page
<span class="nt">&lt;/asp:Content&gt;</span>

<span class="nt">&lt;asp:Content</span> <span class="na">ID=</span><span class="s">"Content2"</span> <span class="na">ContentPlaceHolderID=</span><span class="s">"MainContent"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
    <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"page"</span><span class="nt">&gt;</span>
	    <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"header"</span><span class="nt">&gt;</span>
		    <span class="nt">&lt;h1&gt;</span>Home Page<span class="nt">&lt;/h1&gt;</span>
            <span class="err">&lt;</span>% if (!Request.IsAuthenticated) { %&gt;
                <span class="nt">&lt;a</span> <span class="na">href=</span><span class="s">"&lt;%=Url.Action("</span><span class="na">LogOn</span><span class="err">",</span> <span class="err">"</span><span class="na">Account</span><span class="err">")%</span><span class="nt">&gt;</span>" data-icon="forward" class="ui-btn-right"&gt;Logon<span class="nt">&lt;/a&gt;</span>
            <span class="err">&lt;</span>% } else { %&gt;
                <span class="nt">&lt;a</span> <span class="na">href=</span><span class="s">"&lt;%=Url.Action("</span><span class="na">LogOff</span><span class="err">",</span> <span class="err">"</span><span class="na">Account</span><span class="err">")%</span><span class="nt">&gt;</span>" data-icon="back" class="ui-btn-right"&gt;Log Off<span class="nt">&lt;/a&gt;</span>
            <span class="err">&lt;</span>% } %&gt;
	    <span class="nt">&lt;/div&gt;</span>
	    <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"content"</span><span class="nt">&gt;</span>	
		    <span class="nt">&lt;h2&gt;</span><span class="err">&lt;</span>%: View.Message %&gt;<span class="nt">&lt;/h2&gt;</span>	
	    <span class="nt">&lt;/div&gt;</span>
    <span class="nt">&lt;/div&gt;</span>
<span class="nt">&lt;/asp:Content&gt;</span>
</code></pre></div>
<p>I have a top toolbar button for Login/Log Off (depending on context) and the  View.Message from the old page is included as the content of the page.</p><h3>Logon.aspx</h3><p></p><div class="highlight"><pre><code><span class="err">&lt;</span>%@ Page Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<span class="nt">&lt;Mvc</span><span class="err">.</span><span class="na">Mobile</span><span class="err">.</span><span class="na">Template</span><span class="err">.</span><span class="na">Models</span><span class="err">.</span><span class="na">LogOnModel</span><span class="nt">&gt;</span>" %&gt;

<span class="nt">&lt;asp:Content</span> <span class="na">ID=</span><span class="s">"loginTitle"</span> <span class="na">ContentPlaceHolderID=</span><span class="s">"TitleContent"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
    Log On
<span class="nt">&lt;/asp:Content&gt;</span>

<span class="nt">&lt;asp:Content</span> <span class="na">ID=</span><span class="s">"loginContent"</span> <span class="na">ContentPlaceHolderID=</span><span class="s">"MainContent"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
    <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"page"</span><span class="nt">&gt;</span>
	    <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"header"</span><span class="nt">&gt;</span>
		    <span class="nt">&lt;h1&gt;</span>Log On<span class="nt">&lt;/h1&gt;</span>
            <span class="nt">&lt;a</span> <span class="na">href=</span><span class="s">"&lt;%=Url.Action("</span><span class="na">Register</span><span class="err">",</span> <span class="err">"</span><span class="na">Account</span><span class="err">")%</span><span class="nt">&gt;</span>" data-icon="gear" class="ui-btn-right"  data-rel="dialog" data-transition="pop"&gt;Register<span class="nt">&lt;/a&gt;</span>
	    <span class="nt">&lt;/div&gt;</span>
	    <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"content"</span><span class="nt">&gt;</span>	            
            <span class="nt">&lt;p&gt;</span>Please enter your username and password<span class="nt">&lt;/p&gt;</span>
            <span class="err">&lt;</span>% using (Html.BeginForm()) { %&gt;
                <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"fieldcontain"</span><span class="nt">&gt;</span>
                    <span class="err">&lt;</span>%: Html.LabelFor(m =&gt; m.UserName) %&gt;
                    <span class="err">&lt;</span>%: Html.TextBoxFor(m =&gt; m.UserName) %&gt;
                    <span class="nt">&lt;div&gt;</span>
                        <span class="err">&lt;</span>%: Html.ValidationMessageFor(m =&gt; m.UserName) %&gt;
                    <span class="nt">&lt;/div&gt;</span>
                <span class="nt">&lt;/div&gt;</span>
                <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"fieldcontain"</span><span class="nt">&gt;</span>
                    <span class="err">&lt;</span>%: Html.LabelFor(m =&gt; m.Password) %&gt;
                    <span class="err">&lt;</span>%: Html.PasswordFor(m =&gt; m.Password) %&gt;
                    <span class="nt">&lt;div&gt;</span>
                        <span class="err">&lt;</span>%: Html.ValidationMessageFor(m =&gt; m.Password) %&gt;   
                    <span class="nt">&lt;/div&gt;</span>
                <span class="nt">&lt;/div&gt;</span>
                <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"fieldcontain"</span><span class="nt">&gt;</span>
                    <span class="err">&lt;</span>%: Html.CheckBoxFor(m =&gt; m.RememberMe) %&gt;
                    <span class="err">&lt;</span>%: Html.LabelFor(m =&gt; m.RememberMe) %&gt;
                <span class="nt">&lt;/div&gt;</span>
                <span class="nt">&lt;input</span> <span class="na">type=</span><span class="s">"submit"</span> <span class="na">value=</span><span class="s">"Log On"</span> <span class="na">data-theme=</span><span class="s">"b"</span><span class="nt">/&gt;</span>
            <span class="err">&lt;</span>% } %&gt;
	    <span class="nt">&lt;/div&gt;</span>
    <span class="nt">&lt;/div&gt;</span>
<span class="nt">&lt;/asp:Content&gt;</span>
</code></pre></div>
<p>Gives us the login form and a link to the Register screen.  I marked the  Register screen as a Dialog just to be a wee bit different.  The Register screen  itself is marked up as a normal page (the framework handles the displaying of a  dialog automagically).  I also added a different theme to the login button and a  "pop" transition to the Register dialog through the data- attributes.</p><p>One other thing I had to do was add a "fieldcontain" wrapper to the field  blocks as there was some layout issues.  This is documented in the jQuery Mobile  docs.</p><h3>Register.aspx</h3><p></p><div class="highlight"><pre><code><span class="err">&lt;</span>%@ Page Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<span class="nt">&lt;Mvc</span><span class="err">.</span><span class="na">Mobile</span><span class="err">.</span><span class="na">Template</span><span class="err">.</span><span class="na">Models</span><span class="err">.</span><span class="na">RegisterModel</span><span class="nt">&gt;</span>" %&gt;

<span class="nt">&lt;asp:Content</span> <span class="na">ID=</span><span class="s">"registerTitle"</span> <span class="na">ContentPlaceHolderID=</span><span class="s">"TitleContent"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
    Register
<span class="nt">&lt;/asp:Content&gt;</span>

<span class="nt">&lt;asp:Content</span> <span class="na">ID=</span><span class="s">"registerContent"</span> <span class="na">ContentPlaceHolderID=</span><span class="s">"MainContent"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
    <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"page"</span><span class="nt">&gt;</span>
	    <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"header"</span><span class="nt">&gt;</span>
		    <span class="nt">&lt;h1&gt;</span>Register<span class="nt">&lt;/h1&gt;</span>
            <span class="nt">&lt;a</span> <span class="na">href=</span><span class="s">"&lt;%=Url.Action("</span><span class="na">Index</span><span class="err">",</span> <span class="err">"</span><span class="na">Home</span><span class="err">")%</span><span class="nt">&gt;</span>" data-icon="grid" class="ui-btn-right"&gt;Home<span class="nt">&lt;/a&gt;</span>
	    <span class="nt">&lt;/div&gt;</span>
	    <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"content"</span><span class="nt">&gt;</span>	
            <span class="nt">&lt;p&gt;</span>Passwords are required to be a minimum of <span class="err">&lt;</span>%: View.PasswordLength %&gt; characters in length.<span class="nt">&lt;/p&gt;</span>		    
            <span class="err">&lt;</span>% using (Html.BeginForm()) { %&gt;
                <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"fieldcontain"</span><span class="nt">&gt;</span>
                    <span class="err">&lt;</span>%: Html.LabelFor(m =&gt; m.UserName) %&gt;
                    <span class="err">&lt;</span>%: Html.TextBoxFor(m =&gt; m.UserName) %&gt;
                    <span class="nt">&lt;div&gt;</span>
                        <span class="err">&lt;</span>%: Html.ValidationMessageFor(m =&gt; m.UserName) %&gt;
                    <span class="nt">&lt;/div&gt;</span>
                <span class="nt">&lt;/div&gt;</span>
                
                <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"fieldcontain"</span><span class="nt">&gt;</span>
                    <span class="err">&lt;</span>%: Html.LabelFor(m =&gt; m.Email) %&gt;
                    <span class="err">&lt;</span>%: Html.TextBoxFor(m =&gt; m.Email) %&gt;
                    <span class="nt">&lt;div&gt;</span>
                        <span class="err">&lt;</span>%: Html.ValidationMessageFor(m =&gt; m.Email) %&gt;
                    <span class="nt">&lt;/div&gt;</span>
                <span class="nt">&lt;/div&gt;</span>
                
                <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"fieldcontain"</span><span class="nt">&gt;</span>
                    <span class="err">&lt;</span>%: Html.LabelFor(m =&gt; m.Password) %&gt;
                    <span class="err">&lt;</span>%: Html.PasswordFor(m =&gt; m.Password) %&gt;
                    <span class="nt">&lt;div&gt;</span>
                        <span class="err">&lt;</span>%: Html.ValidationMessageFor(m =&gt; m.Password) %&gt;
                    <span class="nt">&lt;/div&gt;</span>
                <span class="nt">&lt;/div&gt;</span>
                
                <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"fieldcontain"</span><span class="nt">&gt;</span>
                    <span class="err">&lt;</span>%: Html.LabelFor(m =&gt; m.ConfirmPassword) %&gt;
                    <span class="err">&lt;</span>%: Html.PasswordFor(m =&gt; m.ConfirmPassword) %&gt;
                    <span class="nt">&lt;div&gt;</span>
                        <span class="err">&lt;</span>%: Html.ValidationMessageFor(m =&gt; m.ConfirmPassword) %&gt;
                    <span class="nt">&lt;/div&gt;</span>
                <span class="nt">&lt;/div&gt;</span>

                <span class="nt">&lt;input</span> <span class="na">type=</span><span class="s">"submit"</span> <span class="na">value=</span><span class="s">"Register"</span> <span class="na">data-theme=</span><span class="s">"b"</span> <span class="nt">/&gt;</span>
            <span class="err">&lt;</span>%} %&gt;
	    <span class="nt">&lt;/div&gt;</span>
    <span class="nt">&lt;/div&gt;</span>
<span class="nt">&lt;/asp:Content&gt;</span>
</code></pre></div>
<p>The most complex screen in the app.  Nothing special added only some  different markup.</p><h2>Running It</h2><p>That was it.  After adapting the rest of the pages (via manipulating markup)   I was left with a mobilized version of the MVC project template.  Again I didn't  have to make any code changes - validation still worked, forms still worked,  login, register, change password - all worked as expected (screen shots from Chrome which has a few issues with rounded corners on fields in jQuery Mobile).</p><p class="img-holder">  <img style="border:1px solid #000;" width="200" src="http://farm4.static.flickr.com/3502/5721975554_272b823481_o.png" />  <img style="border:1px solid #000;" width="200" src="http://farm3.static.flickr.com/2756/5721416629_4b4e7ebaed_o.png" />  <img style="border:1px solid #000;" width="200" src="http://farm3.static.flickr.com/2510/5721416689_20e25e2983_o.png" /></p>