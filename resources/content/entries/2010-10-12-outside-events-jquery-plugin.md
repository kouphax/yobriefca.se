---
date: 2010-10-11T23:00:00Z
title: Outside Events jQuery Plugin
published: true
categories: [JavaScript]
type: article
external: false
---
<p><a href="http://benalman.com/projects/jquery-outside-events-plugin/">Outside Event Plugin</a> doesn't have a great deal of uses but it does solve one very important issue in client side code - Diminishing Responsibility.  Before I explain how it does this I need to explain what it does.</p><p>Outside events are a new set of events provided by the plugin taht are fired on the registered element when the event happens OUTSIDE that element.  Imagine a click event</p><div class="highlight"><pre><code><span class="nx">$</span><span class="p">(</span><span class="s2">"#somelement"</span><span class="p">).</span><span class="nx">bind</span><span class="p">(</span><span class="s2">"click"</span><span class="p">,</span> <span class="kd">function</span><span class="p">(){</span>
    <span class="c1">// this element was clicked</span>
<span class="p">});</span>
</code></pre></div>
<p>Everytime that #somelement gets clicked the event fires.  An outside event is the opposite of that so,</p><div class="highlight"><pre><code><span class="nx">$</span><span class="p">(</span><span class="s2">"#somelement"</span><span class="p">).</span><span class="nx">bind</span><span class="p">(</span><span class="s2">"outsideclick"</span><span class="p">,</span> <span class="kd">function</span><span class="p">(){</span>
    <span class="c1">// some other element was clicked</span>
<span class="p">});</span>
</code></pre></div>
<p>This event fires when any other element is clicked that WASN'T #somelement (or whatever query you use - we are not stuck to a single element).  Outside events that are supported include (some more useful than others),</p><blockquote class="posterous_medium_quote"><p><code>clickoutside</code>, <code>dblclickoutside</code>, <code>focusoutside</code>, <code>bluroutside</code>, <code>mousemoveoutside</code>, <code>mousedownoutside</code>, <code>mouseupoutside</code>, <code>mouseoveroutside</code>,<code>mouseoutoutside</code>, <code>keydownoutside</code>, <code>keypressoutside</code>, <code>keyupoutside</code>, <code>changeoutside</code>, <code>selectoutside</code>, <code>submitoutside</code>.</p></blockquote><p>Great, cool but what use is this?  I've already said it helps solve the Diminished Responsibility issue but what does that mean?</p><h2>Diminished Responsibility</h2><p>Imagine an example where there a popup notification on a web page.  It's not obtrusive but should go away when the user clicks off it.  How do you do this?  The simple answer would be to add a load of (or delegate) click events and hide the element so your handlers would start to look like this [very contrived BTW],</p><div class="highlight"><pre><code><span class="nx">$</span><span class="p">(</span><span class="s2">"#somelement"</span><span class="p">).</span><span class="nx">bind</span><span class="p">(</span><span class="s2">"click"</span><span class="p">,</span> <span class="kd">function</span><span class="p">(){</span>
    <span class="k">if</span><span class="p">(</span><span class="nx">$</span><span class="p">(</span><span class="s2">"#dialog"</span><span class="p">).</span><span class="nx">is</span><span class="p">(</span><span class="s2">":visible"</span><span class="p">)){</span>
       <span class="nx">$</span><span class="p">(</span><span class="s2">"#dialog"</span><span class="p">).</span><span class="nx">hide</span><span class="p">();</span>
    <span class="p">}</span>
    <span class="c1">// perform function</span>
<span class="p">});</span>
</code></pre></div>
<p>Starts to get a bit messy if we have a lot of handlers and is actually bad practise as the popup notification should be as self contained/self managing as possible.  Outside events solve this by allowing you to push responsibility back onto the widget/element that needs to "do something".  Neat.</p><h2>The Magic</h2><p>Funny thing is while this looks like it may be complicated to implement it's really not.  Thanks to the power of event delegation and event bubbling all the plugin does is attach the inverse of the outside event (outsideclick becomes click) to the document element and when a click event is fired the handler checks if the clicked element is an element registered with outsideclick and if not fires the event. </p><p>Easy peasy though it does open up the possibility of infinite event triggers but that's not the fault of the plugin :-P. </p>