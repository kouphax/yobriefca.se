---
date: 2010-11-18T00:00:00Z
title: The Razor View Engine
published: true
categories: [.NET]
type: article
external: false
---
<p><em>Sorry for the poor syntax highlighting on the source code... not much support for Razor ATM</em></p><p>I've been holding off trying to form an opinion on this until we got the full  package so to speak.  With MVC 3 RC1 being released last week I guess it's time  to share my thoughts on the <a href="http://blog.robertgreyling.com/2010/07/is-razor-just-wolf-in-sparks-clothing.html">mildly  contentious</a> view engine.  First things first though - lets be extremely  superficial and compare a a bit of code.  Specifically lets look a using a loop  to output a list of "really useful" list items.  In the older WebForms view  engine you'd do something like this.</p><p><div class="highlight"><pre><code><span class="nt">&lt;ul&gt;</span>
    <span class="err">&lt;</span>% foreach (int index in Enumerable.Range(1, 10))
        { %&gt;        
        <span class="nt">&lt;li&gt;</span>Entry <span class="err">&lt;</span>%=index%&gt;<span class="nt">&lt;/li&gt;</span>        
    <span class="err">&lt;</span>%}%&gt;
<span class="nt">&lt;/ul&gt;</span>
</code></pre></div>
</p><p>Even for a very trivial example it does look a bit messy - lots of redundant  start and end markers etc.  Lets look at the equivalent in Razor,</p><p></p><div class="highlight"><pre><code><span class="nt">&lt;ul&gt;</span>    
    @foreach (int index in Enumerable.Range(1, 10))    
    {        
        <span class="nt">&lt;li&gt;</span>Entry @index<span class="nt">&lt;/li&gt;</span>        
    }
<span class="nt">&lt;/ul&gt;</span>
</code></pre></div>
<p>Now excuse me while I go off on a tiny bit of a tangent.  There are two  things you probably notice about the equivalent Razor syntax,</p><ol><li>It's much cleaner because of some very clever parsing </li><li>It's still embedding code in a view.  THE HORROR!!!! </li></ol><p>Yeah I know in this day and age when everyone is trying to eradicate every  suggestion of server code in a view Microsoft have went right ahead and actually  enabled you to do it in neat/cleaner manner.  It works though - doesn't it?  I  mean why abstract C# when your view is going to get compiled into C#?  Why try  and mimic HTML/XML when technically speaking your custom markup is pretty much  meaningless?  What's wrong with being able to see when server side syntax  finishes and front end syntax takes over?  It makes a lot of sense to me  actually.</p><h2>The Truth Will Out</h2><p>I've been holding off judgement on Razor for a while until it was more  "complete".  Truth be told for a long time I could have easily summed it up  as</p><blockquote><p><span style="color: #000000;">It's the WebForms View Engine with the &lt;% %&gt;  replaced with @</span></p></blockquote><p>In some ways thats true but at an extremely basic level.  I misjudged it.  I  was wrong.  I actually kind of like it now.</p><h2>But what about Spark?</h2><p>I've been an advocate of the <a>Spark View  Engine</a> for sometime in my company and I've used it on a few projects very  successfully.  It's a great engine but has suffered from a lack of tooling -  there are plenty of developers that simply reject or get a negative impression  of something because of the lack of tooling for the tech.  Spark, especially in  comparison to Razor, has very limited tooling support in VS2010 - sure you have  SparkSense but it's currently not a patch on Razor support (which is still in  itself RC status) and I can't help but feel they'll always be playing catch-up  with in house developed/supported stuff.  Would I have like to see Spark being  adopted by MS rather than them rolling their own view engine?  Perhaps, but  variety is the spice of life and I'm happy to have Razor around.</p><p>So what will I use on future projects?  I'm tending towards Razor for now but  perhaps that's because it's new and I'm a technology magpie.  Also it easier to  justify the use of a technology to a customer when they are provided pre-bundled  and supported by such a large entity.</p><p>&lt;/opinion piece&gt;</p><h2>Back On Track</h2><p>Now I've had my little digression lets look at some of the features of  Razor.</p><h3>Syntax</h3><p>As I showed above the syntax is much more terse.  The parser is clever enough  to detect when you mean server code and when you mean client code.  This reduces  the need to use a lot of bulky delimiters like &lt;% and %&gt;.  Also the parser  is able to determine if the @ you just wrote is a server side delimiter or just  plain text - most of the time.  Alternatively you can fall back on the @@ syntax  to use a literal @. </p><p>The parser does choke sometimes when you don't wrap text in an appropriate  element or tag</p><p></p><div class="highlight"><pre><code>@if (true) 
{
    this is a test
}
</code></pre></div>
<p>fails whereas</p><p></p><div class="highlight"><pre><code>@if (true)  
{ 
    <span class="nt">&lt;span&gt;</span>this is a test<span class="nt">&lt;/span&gt;</span> 
}
</code></pre></div>
<p>is fine.  If you really insist on not using an HTML element you can fall back  on one of two things.  Using a &lt;text&gt; element that is a Razor tag for  marking an area for processing as plain text or a slightly shorter single line  equivalent of @:</p><p></p><div class="highlight"><pre><code>@if (true) 
{
    <span class="nt">&lt;text&gt;</span>this is a test<span class="nt">&lt;/text&gt;</span>
}

@if (true) 
{
    @:this is a test
}
</code></pre></div>
<h3>Layouts and Sections</h3><p>Razor (obviously) has support for master pages and rending content sections  including optional sections.  Syntax for it is pretty simple, lets start with  our master page _Layout.cshtml</p><p></p><div class="highlight"><pre><code><span class="cp">&lt;!DOCTYPE html&gt;</span>
<span class="nt">&lt;html&gt;</span>
    <span class="nt">&lt;head&gt;</span>
        <span class="nt">&lt;title&gt;</span>@View.Title<span class="nt">&lt;/title&gt;</span>
    <span class="nt">&lt;/head&gt;</span>
    <span class="nt">&lt;body&gt;</span>
        <span class="nt">&lt;div</span> <span class="na">id=</span><span class="s">"menu"</span><span class="nt">&gt;</span>
            @RenderSection("menu", required: true);
        <span class="nt">&lt;/div&gt;</span>
        <span class="nt">&lt;div</span> <span class="na">id=</span><span class="s">"content"</span><span class="nt">&gt;</span>
            @RenderBody()
        <span class="nt">&lt;/div&gt;</span>
        <span class="nt">&lt;div</span> <span class="na">id=</span><span class="s">"footer"</span><span class="nt">&gt;</span>
            @RenderSection("footer", required: false);
        <span class="nt">&lt;/div&gt;</span>
    <span class="nt">&lt;/body&gt;</span>
<span class="nt">&lt;/html&gt;</span>
</code></pre></div>
<p>Couple of features worth mentioning,</p><ol><li>Use of RenderSection() to render named sections from the view, also able to  specify if the section is a required or optional section throught the use of the required argument (i.e. if it needs to or  doesn't need to be included in the view during render - if required is true and the section is missing you'll get a runtime error when you attempt to access the page).  Another option for  optional sections is to use the IsSectionDefined method e.g. <p></p><div class="highlight"><pre><code><span class="nt">&lt;div</span> <span class="na">id=</span><span class="s">"footer"</span><span class="nt">&gt;</span>
    @if (IsSectionDefined("footer"))
    {
        RenderSection("footer");
    }
    else
    {
        @:Default Footer Text
     }
<span class="nt">&lt;/div&gt;</span>
</code></pre></div>
<p></p><p>This way we can, as demonstrated, provide some sort of default as well.</p></li><li>RenderBody convenience method.  This is used to render the body of the  view.  Unlike WebForms view engine Razor doesn't require you to wrap the main  body in a content area - similar to Spark. </li></ol><p>So an appropriate view that this master layout can "consume" could look like  this</p><p></p><div class="highlight"><pre><code>@{    
    Layout = "~/Views/Shared/_Layout.cshtml";
}

<span class="nt">&lt;h1&gt;</span>Super Useful Page<span class="nt">&lt;/h1&gt;</span>
<span class="nt">&lt;p&gt;</span>It really is super useful<span class="nt">&lt;/p&gt;</span> 

@section menu {
<span class="nt">&lt;ul&gt;</span>
    <span class="nt">&lt;li&gt;</span>Login<span class="nt">&lt;/li&gt;</span>
    <span class="nt">&lt;li&gt;</span>Register<span class="nt">&lt;/li&gt;</span>
<span class="nt">&lt;/ul&gt;</span>
}
</code></pre></div>
<p>Notice I have left out the footer but that's not an issue as it has been  marked as optional.  Also notice that the body is at the root level of the view  and sections are wrapped in a section marker.  This all adds a more minimal look  and feel to the whole view.</p><h3>_ViewStart.cshtml</h3><p>Another leaf from the Spark world (and probably many other - sorry to  everyone else that supports/implements this sort of feature) is the ability to  provide a global place to put code that applies to every view.  Using the  _ViewStart.cshtml file we could easily just put the Layout declaration from the  view code above into a single global location.  We can also add helpers and  functions there too which I'll talk about now.</p><h2>Helpers</h2><p>Helpers enable use to provide context aware reusable HTML "templates" in our  views.  Imagine a view that listed Top Selling, New and Top Rated products in 3  lists.  The most basic way to implement this would be 3 loops like so</p><p></p><div class="highlight"><pre><code><span class="nt">&lt;h1&gt;</span>Top Selling Product List<span class="nt">&lt;/h1&gt;</span>
<span class="nt">&lt;ul&gt;</span>    
    @foreach (string product in View.TopSellers)    
    {        
        <span class="nt">&lt;li&gt;</span>@product<span class="nt">&lt;/li&gt;</span>    
    }
<span class="nt">&lt;/ul&gt;</span> 
<span class="nt">&lt;h1&gt;</span>New Product List<span class="nt">&lt;/h1&gt;</span>
<span class="nt">&lt;ul&gt;</span>    
    @foreach (string product in View.NewProducts)    
    {        
        <span class="nt">&lt;li&gt;</span>@product<span class="nt">&lt;/li&gt;</span>    
    }
<span class="nt">&lt;/ul&gt;</span> 
<span class="nt">&lt;h1&gt;</span>Top Rated Product List<span class="nt">&lt;/h1&gt;</span>
<span class="nt">&lt;ul&gt;</span>    
    @foreach (string product in View.TopRated)    
    {        
        <span class="nt">&lt;li&gt;</span>@product<span class="nt">&lt;/li&gt;</span>    
    }
<span class="nt">&lt;/ul&gt;</span>
</code></pre></div>
<p>But that's not exactly DRY is it?  Helpers can help us out here (hence the  name I guess :-P).  Lets create a helper that does the repeatable stuff for  us,</p><p></p><div class="highlight"><pre><code>@helper ProductLister(List<span class="nt">&lt;string&gt;</span> products){    
    <span class="nt">&lt;ul&gt;</span>        
        @foreach (string product in products)        
        {            
            <span class="nt">&lt;li&gt;</span>@product<span class="nt">&lt;/li&gt;</span>        
        }    
    <span class="nt">&lt;/ul&gt;</span>
}
</code></pre></div>
<p>And the rest of the view...</p><p></p><div class="highlight"><pre><code><span class="nt">&lt;h1&gt;</span>Top Selling Product List<span class="nt">&lt;/h1&gt;</span>
@ProductLister(View.TopSellers) 
<span class="nt">&lt;h1&gt;</span>New Product List<span class="nt">&lt;/h1&gt;</span>
@ProductLister(View.NewProducts) 
<span class="nt">&lt;h1&gt;</span>Top Rated Product List<span class="nt">&lt;/h1&gt;</span>
@ProductLister(View.TopRated)
</code></pre></div>
<p>Much simpler, cleaner, whateverer.  Again with the Spark similarities - these  are similar to macros.</p><h2>Functions</h2><p>Razor also makes it easier to embed arbitrary methods on your page which I  guess can be useful for formatting and things like that.  This give us the power  to extend the Razor view engine and adapt it to suit our needs - powerful yes  but also open to abuse as you might guess.  Here's a simple function in  action,</p><p></p><div class="highlight"><pre><code>@functions {
    string Encrypt(string value){        
        char[] asArray = value.ToCharArray();        
        Array.Reverse(asArray);        
        return new string(asArray);    
    }
} 
<span class="nt">&lt;ul&gt;</span>    
    <span class="nt">&lt;li&gt;</span>Username: @View.Username<span class="nt">&lt;/li&gt;</span>    
    <span class="nt">&lt;li&gt;</span>Password: @Encrypt(View.Password)<span class="nt">&lt;/li&gt;</span>
<span class="nt">&lt;/ul&gt;</span>
</code></pre></div>
<p>The function applies a highly technical string encryption technique to a  string value returning the result.</p><h2>Inline Templates</h2><p>One more feature before I head off to watch telly or sleep or something.   Razor allows you to pass little html nuggets or templates as arguments to  helpers and functions.  There is a details blog post about this over at <a href="http://blog.andrewnurse.net/2010/08/02/InsideRazorPart3Templates.aspx">Vibrant  Code</a> and my example is heavily inspired/plagiarised from that.  Lets see  what we can do,</p><p></p><div class="highlight"><pre><code>@functions {     
    IHtmlString Times(int times, Func<span class="nt">&lt;int</span><span class="err">,</span> <span class="na">object</span><span class="nt">&gt;</span> template) {        
        StringBuilder b = new StringBuilder();        
        for(int i = 0; i <span class="nt">&lt; times</span><span class="err">;</span> <span class="na">i</span><span class="err">++)</span> <span class="err">{</span>              
            <span class="na">b</span><span class="err">.</span><span class="na">Append</span><span class="err">(</span><span class="na">template</span><span class="err">(</span><span class="na">i</span><span class="err">));</span>          
        <span class="err">}</span>         
        <span class="na">return</span> <span class="na">new</span> <span class="na">HtmlString</span><span class="err">(</span><span class="na">b</span><span class="err">.</span><span class="na">ToString</span><span class="err">());</span>    
    <span class="err">}</span> 
<span class="err">}</span>   
<span class="err">&lt;</span><span class="na">ul</span><span class="nt">&gt;</span>    
    @Times(10, @<span class="nt">&lt;li&gt;</span>List Item @item<span class="nt">&lt;/li&gt;</span>)
<span class="nt">&lt;/ul&gt;</span>
</code></pre></div>
<p>Look at the second to last line see that little block of html preceded by the  Razor marker (@)?  See how in our Times function the HTML block it is getting translated transparently as a  Func&lt;int, object&gt; the we can call and manipulate in the helper function anyway we want?  Can  you see how powerful that could be in the right place?  Me too.  Me too.</p><h2>Done For Now</h2><p>There is more to Razor than all this stuff including the ability to use it  outside of the view context so it could simply be a templating language if needs  be.  There is even more than that too but thats for another time.  Imagine  though.... I started out this blog post more or less telling you I used to think  Razor was simply the WebForms view engine but with @'s instead of &lt;% %&gt;'s  and hopefully you can see why I was wrong and why I was happy to admit it.  My  only gripe is that the Razor documentation could be a bit more available.  There  isn't really a great deal out there ATM.  However this has been promised for the  official release so I am looking forward to that.</p><p>Oh Misfits is about to start......</p>