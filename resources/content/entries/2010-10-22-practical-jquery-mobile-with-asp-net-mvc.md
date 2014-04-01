---
date: 2010-10-21T23:00:00Z
title: Practical jQuery Mobile with ASP.NET MVC
published: true
categories: [JavaScript, .NET]
type: article
external: false
---
<p>That's a bit of a mouthful.  I wanted to write a post about creating a basic  jQuery Mobile app but as I started putting the code together MVC became more and  more involved so I combined the 2.</p><p>The end solution?  Lets create a phone directory with 2 main views,</p><ol><li>A filterable list of all people with quick info (telephone extension and  name) that is grouped and sorted alphabetically, and,</li><li>A disclosure view of a selected person showing more details including a  photo</li></ol><p>[[posterous-content:FddxCHuIgArIuezHEbcn]]</p><h2>MVC (The Server Side)</h2><p>jQuery Mobile works by progressive enhancement and uses Ajax to load and  parse external links so it has more control over page transitions and Ajax  history.  This means that we create a plain old website that will work without  jQuery Mobile, Ajax or any JavaScript.  So I started with the ASP.NET MVC 2 Web  Application Visual Studio Template and ripped out everything bar the Home  Controller and the 2 views.  I also stripped the Site.Master down to the bare  bones.</p><p>Next I created my model with data access methods,</p><p></p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">Entry</span>
<span class="p">{</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">Id</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">Title</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">FirstName</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">Surname</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">Email</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">InternalNo</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">Room</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">ExternalNo</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">PhotoLocation</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>

    <span class="k">public</span> <span class="k">static</span> <span class="n">IEnumerable</span><span class="p">&lt;</span><span class="n">Entry</span><span class="p">&gt;</span> <span class="n">GetAll</span><span class="p">()</span>
    <span class="p">{</span>
        <span class="c1">// get all entries</span>
    <span class="p">}</span>

    <span class="k">public</span> <span class="k">static</span> <span class="n">Entry</span> <span class="nf">GetById</span><span class="p">(</span><span class="kt">string</span> <span class="n">id</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="c1">// get a specific entry</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<p>You can implement your own data access there or just hard code some values.   Next I updated the HomeController to return the right models  to the views</p><p><div class="highlight"><pre><code><span class="k">public</span> <span class="n">ActionResult</span> <span class="nf">Index</span><span class="p">()</span>
<span class="p">{</span>
    <span class="k">return</span> <span class="nf">View</span><span class="p">(</span><span class="k">from</span> <span class="n">e</span> <span class="k">in</span> <span class="n">Entry</span><span class="p">.</span><span class="n">GetAll</span><span class="p">()</span>
                <span class="k">orderby</span> <span class="n">e</span><span class="p">.</span><span class="n">Surname</span>
                <span class="k">group</span> <span class="n">e</span> <span class="n">by</span> <span class="n">e</span><span class="p">.</span><span class="n">Surname</span><span class="p">.</span><span class="n">Substring</span><span class="p">(</span><span class="m">0</span><span class="p">,</span><span class="m">1</span><span class="p">)</span> <span class="k">into</span> <span class="n">g</span>
                <span class="k">select</span> <span class="n">g</span><span class="p">);</span>
<span class="p">}</span>

<span class="k">public</span> <span class="n">ActionResult</span> <span class="nf">About</span><span class="p">(</span><span class="kt">string</span> <span class="n">id</span><span class="p">)</span>
<span class="p">{</span>
    <span class="k">return</span> <span class="nf">View</span><span class="p">(</span><span class="n">Entry</span><span class="p">.</span><span class="n">GetById</span><span class="p">(</span><span class="n">id</span><span class="p">));</span>
<span class="p">}</span>
</code></pre></div>
</p><p>I updated the views to display the information in a straightforward way. </p><p>Index.aspx</p><p></p><div class="highlight"><pre><code><span class="err">&lt;</span>%@ Page Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<span class="nt">&lt;IEnumerable</span><span class="err">&lt;</span><span class="na">IGrouping</span><span class="err">&lt;</span><span class="na">string</span><span class="err">,</span><span class="na">Kas</span><span class="err">.</span><span class="na">JQueryMobile</span><span class="err">.</span><span class="na">WhosWho</span><span class="err">.</span><span class="na">Models</span><span class="err">.</span><span class="na">Entry</span><span class="nt">&gt;</span>&gt;&gt;" %&gt;
<span class="err">&lt;</span>%@ Import Namespace="Kainos.JQueryMobile.WhosWho.Models" %&gt;
<span class="nt">&lt;asp:Content</span> <span class="na">ID=</span><span class="s">"Content1"</span> <span class="na">ContentPlaceHolderID=</span><span class="s">"TitleContent"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>Whos Who<span class="nt">&lt;/asp:Content&gt;</span>

<span class="nt">&lt;asp:Content</span> <span class="na">ID=</span><span class="s">"Content2"</span> <span class="na">ContentPlaceHolderID=</span><span class="s">"MainContent"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
    <span class="nt">&lt;div&gt;</span>
        <span class="nt">&lt;div&gt;</span>   
            <span class="nt">&lt;h1&gt;</span>Whos Who<span class="nt">&lt;/h1&gt;</span>
        <span class="nt">&lt;/div&gt;</span>
        <span class="nt">&lt;div&gt;</span>
            <span class="nt">&lt;ul&gt;</span>
                <span class="err">&lt;</span>% foreach (IGrouping<span class="nt">&lt;string</span><span class="err">,</span><span class="na">Entry</span><span class="nt">&gt;</span> group in Model){%&gt;
                    <span class="nt">&lt;li&gt;</span><span class="err">&lt;</span>%=group.Key%&gt;<span class="nt">&lt;/li&gt;</span>
                    <span class="err">&lt;</span>% foreach (Entry item in group){%&gt;
                        <span class="nt">&lt;li&gt;</span>
                            <span class="nt">&lt;a</span> <span class="na">href=</span><span class="s">"&lt;%=Url.Action("</span><span class="na">About</span><span class="err">",</span> <span class="err">"</span><span class="na">Home</span><span class="err">",</span> <span class="na">new</span> <span class="err">{</span> <span class="na">id =</span><span class="err"> </span><span class="s">item.Id</span> <span class="err">})</span> <span class="err">%</span><span class="nt">&gt;</span>"&gt;
                                <span class="err">&lt;</span>%=item.Title %&gt;
                            <span class="nt">&lt;/a&gt;</span>
                            <span class="nt">&lt;p&gt;</span>
                                <span class="nt">&lt;strong&gt;</span><span class="err">&lt;</span>%=item.InternalNo %&gt;<span class="nt">&lt;/strong&gt;</span>
                            <span class="nt">&lt;/p&gt;</span>
                        <span class="nt">&lt;/li&gt;</span>       
                    <span class="err">&lt;</span>%} %&gt;
                <span class="err">&lt;</span>%} %&gt;                               
            <span class="nt">&lt;/ul&gt;</span>        
        <span class="nt">&lt;/div&gt;</span>
    <span class="nt">&lt;/div&gt;</span>
<span class="nt">&lt;/asp:Content&gt;</span>
</code></pre></div>
<p>About.aspx</p><p></p><div class="highlight"><pre><code><span class="err">&lt;</span>%@ Page Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<span class="nt">&lt;Kas</span><span class="err">.</span><span class="na">JQueryMobile</span><span class="err">.</span><span class="na">WhosWho</span><span class="err">.</span><span class="na">Models</span><span class="err">.</span><span class="na">Entry</span><span class="nt">&gt;</span>" %&gt;

<span class="nt">&lt;asp:Content</span> <span class="na">ID=</span><span class="s">"Content1"</span> <span class="na">ContentPlaceHolderID=</span><span class="s">"TitleContent"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
    <span class="err">&lt;</span>%=Model.Title%&gt;
<span class="nt">&lt;/asp:Content&gt;</span>

<span class="nt">&lt;asp:Content</span> <span class="na">ID=</span><span class="s">"Content2"</span> <span class="na">ContentPlaceHolderID=</span><span class="s">"MainContent"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
    <span class="nt">&lt;div&gt;</span>
        <span class="nt">&lt;div&gt;</span>   
            <span class="nt">&lt;h1&gt;</span><span class="err">&lt;</span>%=Model.Title%&gt;<span class="nt">&lt;/h1&gt;</span>
        <span class="nt">&lt;/div&gt;</span>
        <span class="nt">&lt;div</span> <span class="na">style=</span><span class="s">"background: url(&lt;%=Url.Content("</span><span class="err">~/</span><span class="na">Images</span><span class="err">/"</span> <span class="err">+</span> <span class="na">Model</span><span class="err">.</span><span class="na">PhotoLocation</span><span class="err">)</span> <span class="err">%</span><span class="nt">&gt;</span>) no-repeat top right"&gt;
            <span class="nt">&lt;p&gt;</span><span class="err">&lt;</span>%=Model.Title%&gt;<span class="nt">&lt;/p&gt;</span>
            <span class="nt">&lt;p&gt;&lt;a</span> <span class="na">href=</span><span class="s">"mailto:&lt;%=Model.Email%&gt;"</span><span class="nt">&gt;</span><span class="err">&lt;</span>%=Model.Email%&gt;<span class="nt">&lt;/a&gt;&lt;/p&gt;</span>
            <span class="nt">&lt;p&gt;</span><span class="err">&lt;</span>%=Model.InternalNo%&gt;<span class="nt">&lt;/p&gt;</span>
            <span class="nt">&lt;p&gt;</span><span class="err">&lt;</span>%=Model.ExternalNo%&gt;<span class="nt">&lt;/p&gt;</span>
            <span class="nt">&lt;p&gt;</span><span class="err">&lt;</span>%=Model.Room%&gt;<span class="nt">&lt;/p&gt;</span>                       
        <span class="nt">&lt;/div&gt;</span>
    <span class="nt">&lt;/div&gt;</span>
<span class="nt">&lt;/asp:Content&gt;</span>
</code></pre></div>
<p>This leaves us with a normal, not so pretty but fully functioning site. </p><p>[[posterous-content:DblJukiumuFlvvqlHokq]]</p><p>Now lets inject some mobile niceness into it,</p><h2>jQuery Mobile</h2><p>First things first lets put reference to jQuery and jQuery Mobile  (script  and css) into our site master and update our DOCTYPE to the HTML5 DOCTYPE  leaving us with,</p><p></p><div class="highlight"><pre><code><span class="err">&lt;</span>%@ Master Language="C#" Inherits="System.Web.Mvc.ViewMasterPage" %&gt;
<span class="cp">&lt;!DOCTYPE html&gt;</span>
<span class="nt">&lt;html&gt;</span>
    <span class="nt">&lt;head</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
        <span class="nt">&lt;title&gt;&lt;asp:ContentPlaceHolder</span> <span class="na">ID=</span><span class="s">"TitleContent"</span> <span class="na">runat=</span><span class="s">"server"</span> <span class="nt">/&gt;&lt;/title&gt;</span>    
        <span class="nt">&lt;link</span> <span class="na">href=</span><span class="s">"&lt;%=Url.Content("</span><span class="err">~/</span><span class="na">ClientBin</span><span class="err">/</span><span class="na">jquery</span><span class="err">.</span><span class="na">mobile-1</span><span class="err">.</span><span class="na">0a1</span><span class="err">.</span><span class="na">min</span><span class="err">.</span><span class="na">css</span><span class="err">")</span> <span class="err">%</span><span class="nt">&gt;</span>" rel="stylesheet" type="text/css" /&gt;
        <span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"&lt;%=Url.Content("</span><span class="err">~/</span><span class="na">ClientBin</span><span class="err">/</span><span class="na">jquery-1</span><span class="err">.</span><span class="na">4</span><span class="err">.</span><span class="na">3</span><span class="err">.</span><span class="na">min</span><span class="err">.</span><span class="na">js</span><span class="err">")%</span><span class="nt">&gt;</span><span class="s2">" type="</span><span class="nx">text</span><span class="o">/</span><span class="nx">javascript</span><span class="err">"</span><span class="o">&gt;</span><span class="nt">&lt;/script&gt;</span>
        <span class="nt">&lt;script </span><span class="na">src=</span><span class="s">"&lt;%=Url.Content("</span><span class="err">~/</span><span class="na">ClientBin</span><span class="err">/</span><span class="na">jquery</span><span class="err">.</span><span class="na">mobile-1</span><span class="err">.</span><span class="na">0a1</span><span class="err">.</span><span class="na">min</span><span class="err">.</span><span class="na">js</span><span class="err">")%</span><span class="nt">&gt;</span><span class="s2">" type="</span><span class="nx">text</span><span class="o">/</span><span class="nx">javascript</span><span class="err">"</span><span class="o">&gt;</span><span class="nt">&lt;/script&gt;</span>
    <span class="nt">&lt;/head&gt;</span>
    <span class="nt">&lt;body&gt;</span>
        <span class="nt">&lt;asp:ContentPlaceHolder</span> <span class="na">ID=</span><span class="s">"MainContent"</span> <span class="na">runat=</span><span class="s">"server"</span> <span class="nt">/&gt;</span>
    <span class="nt">&lt;/body&gt;</span>
<span class="nt">&lt;/html&gt;</span>
</code></pre></div>
<p>Now we need to tell jQuery Mobile how to layout the pages and mobilise.  This  isn't necessarily done in script as you might think.  jQuery Mobile makes use of  HTML5's data- attributes to identify how the page should be laid out and mark  areas to specific roles.</p><p>Index.aspx</p><p></p><div class="highlight"><pre><code><span class="err">&lt;</span>%@ Page Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<span class="nt">&lt;IEnumerable</span><span class="err">&lt;</span><span class="na">IGrouping</span><span class="err">&lt;</span><span class="na">string</span><span class="err">,</span><span class="na">Kas</span><span class="err">.</span><span class="na">JQueryMobile</span><span class="err">.</span><span class="na">WhosWho</span><span class="err">.</span><span class="na">Models</span><span class="err">.</span><span class="na">Entry</span><span class="nt">&gt;</span>&gt;&gt;" %&gt;
<span class="err">&lt;</span>%@ Import Namespace="Kainos.JQueryMobile.WhosWho.Models" %&gt;
<span class="nt">&lt;asp:Content</span> <span class="na">ID=</span><span class="s">"Content1"</span> <span class="na">ContentPlaceHolderID=</span><span class="s">"TitleContent"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
    Whos Who
<span class="nt">&lt;/asp:Content&gt;</span>
<span class="nt">&lt;asp:Content</span> <span class="na">ID=</span><span class="s">"Content2"</span> <span class="na">ContentPlaceHolderID=</span><span class="s">"MainContent"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
    <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"page"</span><span class="nt">&gt;</span>
        <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"header"</span><span class="nt">&gt;</span>   
            <span class="nt">&lt;h1&gt;</span>Whos Who<span class="nt">&lt;/h1&gt;</span>
        <span class="nt">&lt;/div&gt;</span>
        <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"content"</span><span class="nt">&gt;</span>
            <span class="nt">&lt;ul</span> <span class="na">data-role=</span><span class="s">"listview"</span> <span class="na">data-filter=</span><span class="s">"true"</span><span class="nt">&gt;</span>
                <span class="err">&lt;</span>% foreach (IGrouping<span class="nt">&lt;string</span><span class="err">,</span><span class="na">Entry</span><span class="nt">&gt;</span> group in Model){%&gt;
                    <span class="nt">&lt;li</span> <span class="na">data-role=</span><span class="s">"list-divider"</span><span class="nt">&gt;</span><span class="err">&lt;</span>%=group.Key%&gt;<span class="nt">&lt;/li&gt;</span>
                    <span class="err">&lt;</span>% foreach (Entry item in group){%&gt;
                        <span class="nt">&lt;li&gt;</span>
                            <span class="nt">&lt;a</span> <span class="na">href=</span><span class="s">"&lt;%=Url.Action("</span><span class="na">About</span><span class="err">",</span> <span class="err">"</span><span class="na">Home</span><span class="err">",</span> <span class="na">new</span> <span class="err">{</span> <span class="na">id =</span><span class="err"> </span><span class="s">item.Id</span> <span class="err">})</span> <span class="err">%</span><span class="nt">&gt;</span>"&gt;
                                <span class="err">&lt;</span>%=item.Title %&gt;
                            <span class="nt">&lt;/a&gt;</span>
                            <span class="nt">&lt;p</span> <span class="na">class=</span><span class="s">"ui-li-aside"</span><span class="nt">&gt;</span>
                                <span class="nt">&lt;strong&gt;</span><span class="err">&lt;</span>%=item.InternalNo %&gt;<span class="nt">&lt;/strong&gt;</span>
                            <span class="nt">&lt;/p&gt;</span>
                        <span class="nt">&lt;/li&gt;</span>       
                    <span class="err">&lt;</span>%} %&gt;
                <span class="err">&lt;</span>%} %&gt;                               
            <span class="nt">&lt;/ul&gt;</span>        
        <span class="nt">&lt;/div&gt;</span>
    <span class="nt">&lt;/div&gt;</span>
<span class="nt">&lt;/asp:Content&gt;</span>
</code></pre></div>
<p>About.aspx</p><p></p><div class="highlight"><pre><code><span class="err">&lt;</span>%@ Page Language="C#" MasterPageFile="~/Views/Shared/Site.Master" Inherits="System.Web.Mvc.ViewPage<span class="nt">&lt;Kas</span><span class="err">.</span><span class="na">JQueryMobile</span><span class="err">.</span><span class="na">WhosWho</span><span class="err">.</span><span class="na">Models</span><span class="err">.</span><span class="na">Entry</span><span class="nt">&gt;</span>" %&gt;

<span class="nt">&lt;asp:Content</span> <span class="na">ID=</span><span class="s">"Content1"</span> <span class="na">ContentPlaceHolderID=</span><span class="s">"TitleContent"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
    <span class="err">&lt;</span>%=Model.Title%&gt;
<span class="nt">&lt;/asp:Content&gt;</span>

<span class="nt">&lt;asp:Content</span> <span class="na">ID=</span><span class="s">"Content2"</span> <span class="na">ContentPlaceHolderID=</span><span class="s">"MainContent"</span> <span class="na">runat=</span><span class="s">"server"</span><span class="nt">&gt;</span>
    <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"page"</span><span class="nt">&gt;</span>
        <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"header"</span><span class="nt">&gt;</span>   
            <span class="nt">&lt;h1&gt;</span><span class="err">&lt;</span>%=Model.Title%&gt;<span class="nt">&lt;/h1&gt;</span>
        <span class="nt">&lt;/div&gt;</span>
        <span class="nt">&lt;div</span> <span class="na">data-role=</span><span class="s">"content"</span> <span class="na">style=</span><span class="s">"background: url(&lt;%=Url.Content("</span><span class="err">~/</span><span class="na">Images</span><span class="err">/"</span> <span class="err">+</span> <span class="na">Model</span><span class="err">.</span><span class="na">PhotoLocation</span><span class="err">)</span> <span class="err">%</span><span class="nt">&gt;</span>) no-repeat top right"&gt;
            <span class="nt">&lt;p&gt;</span><span class="err">&lt;</span>%=Model.Title%&gt;<span class="nt">&lt;/p&gt;</span>
            <span class="nt">&lt;p&gt;&lt;a</span> <span class="na">href=</span><span class="s">"mailto:&lt;%=Model.Email%&gt;"</span><span class="nt">&gt;</span><span class="err">&lt;</span>%=Model.Email%&gt;<span class="nt">&lt;/a&gt;&lt;/p&gt;</span>
            <span class="nt">&lt;p&gt;</span><span class="err">&lt;</span>%=Model.InternalNo%&gt;<span class="nt">&lt;/p&gt;</span>
            <span class="nt">&lt;p&gt;</span><span class="err">&lt;</span>%=Model.ExternalNo%&gt;<span class="nt">&lt;/p&gt;</span>
            <span class="nt">&lt;p&gt;</span><span class="err">&lt;</span>%=Model.Room%&gt;<span class="nt">&lt;/p&gt;</span>
                       
        <span class="nt">&lt;/div&gt;</span>
    <span class="nt">&lt;/div&gt;</span>
<span class="nt">&lt;/asp:Content&gt;</span>
</code></pre></div>
<p>So what have we added here? </p><ul><li>data-roles - telling jQuery Mobile what each div actually represents e.g.     <ul><li>page - a single view (a single html page can have multiple views)</li><li>header - the header of a page</li><li>content - the pages content</li><li>footer - the footer of a page</li><li>list-view - a special role to specifying  that the content is a list</li><li>list-divider - a divide for a list that doesn't do anything but look  different</li></ul></li><li>data-filter - telling jQuery Mobile that it should provide filtering on this  view</li><li>ui-li-aside class identifies that this is some aside information for this  list item</li></ul><p>And that is it.  We have taken a static site and without writing any code  (bar markup) created a mobile app.  Probably might be a good idea to include an  application cache manifest file as well so there is some semblance of offline  capability provided.  But that's for another day.</p><p>[[posterous-content:pJHwwsHHmrfAkJIJFkrp]]</p>