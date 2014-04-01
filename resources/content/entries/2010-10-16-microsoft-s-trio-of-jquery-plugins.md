---
date: 2010-10-15T23:00:00Z
title: Microsoft's Trio of jQuery Plugins
published: true
categories: [JavaScript]
type: article
external: false
---
<p>Microsoft's 3 jQuery plugins have been accepted as official plugins (with templating being absorbed into the core in jQuery 1.5).  Lets have a quick look at what they provide.</p><p>There are a ton of blog posts and documentation out there describing what these plugins do,</p><ul><li><a href="http://blog.jquery.com/2010/10/04/new-official-jquery-plugins-provide-templating-data-linking-and-globalization/">Official Post</a> </li><li><a href="http://weblogs.asp.net/scottgu/archive/2010/10/04/jquery-templates-data-link-and-globalization-accepted-as-official-jquery-plugins.aspx">Scottgu's Annoucement</a> </li><li><a href="http://github.com/jquery/jquery-tmpl/">Template Git Repo</a> </li><li><a href="http://github.com/jquery/jquery-tmpl/">Data Link Git Repo</a> </li><li><a href="http://github.com/jquery/jquery-global">Globalisation Git Repo</a> </li></ul><p>I am going to ignore Globalisation for now as it takes too long to put a worthwhile demo together and it's alreaqdy heavily explained on the GitHub page linked above.  So lets take the other plugins and use them to create "the best todo app evar"</p><h2>ToDo App</h2><p>Lets take a simple to do app the you can add Tasks to (along with associated comments) then save them to the server.  Simple design like so,</p><p>[[posterous-content:iesdxxncdohkDnbunIad]]</p><h2>HTML</h2><p>So first things first lets get the HTML out of the way,</p><div class="highlight"><pre><code><span class="nt">&lt;h1&gt;</span>ToDo List<span class="nt">&lt;/h1&gt;</span>
<span class="nt">&lt;form</span> <span class="na">id=</span><span class="s">"new-task-form"</span> <span class="na">action=</span><span class="s">"\"</span><span class="nt">&gt;</span>
    <span class="nt">&lt;p&gt;</span>
        <span class="nt">&lt;label</span> <span class="na">for=</span><span class="s">"task"</span><span class="nt">&gt;</span>Task:<span class="nt">&lt;/label&gt;</span>
        <span class="nt">&lt;input</span> <span class="na">type=</span><span class="s">"text"</span> <span class="na">id=</span><span class="s">"task"</span> <span class="na">name=</span><span class="s">"task"</span><span class="nt">/&gt;</span>
    <span class="nt">&lt;/p&gt;</span>
    <span class="nt">&lt;p&gt;</span>
        <span class="nt">&lt;label</span> <span class="na">for=</span><span class="s">"note"</span><span class="nt">&gt;</span>Note:<span class="nt">&lt;/label&gt;</span>
        <span class="nt">&lt;textarea</span> <span class="na">id=</span><span class="s">"note"</span> <span class="na">name=</span><span class="s">"note"</span> <span class="na">rows=</span><span class="s">"5"</span> <span class="na">cols=</span><span class="s">"30"</span><span class="nt">&gt;&lt;/textarea&gt;</span>
    <span class="nt">&lt;/p&gt;</span>
    <span class="nt">&lt;input</span> <span class="na">type=</span><span class="s">"button"</span> <span class="na">value=</span><span class="s">"Add"</span> <span class="na">id=</span><span class="s">"add"</span><span class="nt">/&gt;</span>
    <span class="nt">&lt;input</span> <span class="na">type=</span><span class="s">"button"</span> <span class="na">value=</span><span class="s">"Save"</span> <span class="na">id=</span><span class="s">"save"</span><span class="nt">/&gt;</span>
<span class="nt">&lt;/form&gt;</span>
<span class="nt">&lt;h2&gt;</span>Current ToDos<span class="nt">&lt;/h2&gt;</span>
<span class="nt">&lt;ul</span> <span class="na">id=</span><span class="s">"todo-list"</span><span class="nt">&gt;&lt;/ul&gt;</span>
</code></pre></div>
<h2>JavaScript</h2><p>Next up is the interesting part - JavaScript,</p><div class="highlight"><pre><code>    <span class="kd">var</span> <span class="nx">model</span> <span class="o">=</span> <span class="p">{},</span> <span class="c1">// empty model object</span>
        <span class="nx">list</span> <span class="o">=</span> <span class="nx">$</span><span class="p">(</span><span class="s2">"#todo-list"</span><span class="p">),</span>
        <span class="nx">form</span> <span class="o">=</span> <span class="nx">$</span><span class="p">(</span><span class="s1">'#new-task-form'</span><span class="p">),</span>
        <span class="nx">addBtn</span> <span class="o">=</span> <span class="nx">$</span><span class="p">(</span><span class="s2">"#add"</span><span class="p">),</span>
        <span class="nx">saveBtn</span> <span class="o">=</span> <span class="nx">$</span><span class="p">(</span><span class="s2">"#save"</span><span class="p">);</span>

    <span class="c1">// link ui to model</span>
    <span class="nx">form</span><span class="p">.</span><span class="nx">link</span><span class="p">(</span><span class="nx">model</span><span class="p">);</span>

    <span class="c1">// handle onclick</span>
    <span class="nx">addBtn</span><span class="p">.</span><span class="nx">click</span><span class="p">(</span><span class="kd">function</span> <span class="p">()</span> <span class="p">{</span>
        <span class="c1">// clone the data model</span>
        <span class="kd">var</span> <span class="nx">item</span> <span class="o">=</span> <span class="nx">$</span><span class="p">.</span><span class="nx">extend</span><span class="p">({},</span> <span class="nx">model</span><span class="p">)</span>
        <span class="c1">// generate template</span>
        <span class="nx">$</span><span class="p">.</span><span class="nx">tmpl</span><span class="p">(</span><span class="s2">"&lt;li&gt;${task}&lt;/li&gt;"</span><span class="p">,</span> <span class="nx">item</span><span class="p">).</span><span class="nx">appendTo</span><span class="p">(</span><span class="nx">list</span><span class="p">);</span>
    <span class="p">});</span>

    <span class="c1">// handle save</span>
    <span class="nx">saveBtn</span><span class="p">.</span><span class="nx">click</span><span class="p">(</span><span class="kd">function</span> <span class="p">()</span> <span class="p">{</span>
        <span class="c1">// pull back the data</span>
        <span class="kd">var</span> <span class="nx">todos</span> <span class="o">=</span> <span class="nx">list</span><span class="p">.</span><span class="nx">find</span><span class="p">(</span><span class="s2">"li"</span><span class="p">).</span><span class="nx">map</span><span class="p">(</span><span class="kd">function</span> <span class="p">()</span> <span class="p">{</span>
            <span class="k">return</span> <span class="nx">$</span><span class="p">(</span><span class="k">this</span><span class="p">).</span><span class="nx">tmplItem</span><span class="p">().</span><span class="nx">data</span><span class="p">;</span>
        <span class="p">}).</span><span class="nx">get</span><span class="p">();</span>

        <span class="c1">// save the object</span>
        <span class="nx">$</span><span class="p">.</span><span class="nx">post</span><span class="p">(</span><span class="s2">"/Home/Save"</span><span class="p">,</span> <span class="nx">todos</span><span class="p">,</span> <span class="kd">function</span> <span class="p">()</span> <span class="p">{</span>
            <span class="nx">alert</span><span class="p">(</span><span class="s2">"Todos Saved"</span><span class="p">);</span>
        <span class="p">});</span>
    <span class="p">});</span>
</code></pre></div>
<p>That's it.  So lets break it down and see exactly what is happening.</p><h3>Variable Declarations</h3><div class="highlight"><pre><code><span class="kd">var</span> <span class="nx">model</span> <span class="o">=</span> <span class="p">{},</span> <span class="c1">// empty model object</span>
</code></pre></div>
<p>First thing we do is declare a model object.  Simply an empty object for our purposes but could be pre-populated with data that can be bound.</p><h3>Model Linking</h3><p>Model linking is effectively 2 way data binding between a data context and a form (1 way optional and configurable mappings possible see API). </p><div class="highlight"><pre><code>    <span class="c1">// link ui to model</span>
    <span class="nx">form</span><span class="p">.</span><span class="nx">link</span><span class="p">(</span><span class="nx">model</span><span class="p">);</span>
</code></pre></div>
<p>We just want simple 2 way binding so that is all we need.  This sets up the 2 form inputs so they are bound to our model object so any changes will be reflected accordingly.</p><h3>Add Functionality</h3><div class="highlight"><pre><code>    <span class="c1">// clone the data model</span>
    <span class="kd">var</span> <span class="nx">item</span> <span class="o">=</span> <span class="nx">$</span><span class="p">.</span><span class="nx">extend</span><span class="p">({},</span> <span class="nx">model</span><span class="p">)</span>
    <span class="nx">$</span><span class="p">.</span><span class="nx">tmpl</span><span class="p">(</span><span class="s2">"&lt;li&gt;${task}&lt;/li&gt;"</span><span class="p">,</span> <span class="nx">item</span><span class="p">).</span><span class="nx">appendTo</span><span class="p">(</span><span class="nx">list</span><span class="p">);</span>
</code></pre></div>
<p>First things first we need to take a clone of the current object as this is bound/linked to the form and if we don't clone it we will be referencing the same object over and over.  This wont affect the UI but templating has some nice data centric features to pull back our data later and we need this.</p><p>Next we create a very simple template passing the cloned model into the template call.  Finally we append this new LI to the UL list. </p><h3>Save Functionality</h3><p>The interesting part of the save functionality is how we actually pull back the list of data to be sent to the server.</p><div class="highlight"><pre><code>    <span class="c1">// pull back the data</span>
    <span class="kd">var</span> <span class="nx">todos</span> <span class="o">=</span> <span class="nx">list</span><span class="p">.</span><span class="nx">find</span><span class="p">(</span><span class="s2">"li"</span><span class="p">).</span><span class="nx">map</span><span class="p">(</span><span class="kd">function</span> <span class="p">()</span> <span class="p">{</span>
        <span class="k">return</span> <span class="nx">$</span><span class="p">(</span><span class="k">this</span><span class="p">).</span><span class="nx">tmplItem</span><span class="p">().</span><span class="nx">data</span><span class="p">;</span>
    <span class="p">}).</span><span class="nx">get</span><span class="p">();</span>
</code></pre></div>
<p>Templates are data centric.  That is, when they are created the original data that was used to generate them is stored in the jQuery data cache and can be retrieved using tmplItem.  So rather than having to scrape the data back from UI (<em>where are you going to get the comment value from?</em>) you can simply ask for the elements data context.</p><h2>So...</h2><p>That's it.  Some very nice data centric (3rd time in as many paragraphs) plugins.  Sure you can do this stuff without these plugins but it would certainly not be as elegant.  This is only skimming the surface of the data linking and templating plugins, there is a heap of stuff that I didn't cover,</p><ul><li>One way binding</li><li>Value converters for binding</li><li>Programatically change data values</li><li>Adding custom linking behaviour</li><li>Generate templats from embedded script templates</li><li>Complex and nested templating</li><li>Statically define a reusable template</li></ul>