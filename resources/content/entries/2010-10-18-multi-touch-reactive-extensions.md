---
date: 2010-10-17T23:00:00Z
title: Multi-Touch Reactive Extensions
published: true
categories: [.NET]
type: article
external: false
---
<p>Link: <a href="http://msdn.microsoft.com/en-us/devlabs/ee794896.aspx">Reactive Extensions for .NET</a></p><p>It's been a while since I've played with MultiTouch functionality so I decided it was time to dust off the old TouchSmart and put it to good use.  The purpose wasn't to play with MultiTouch functionality but more to see how we could simplify managing isolated events using <a href="http://msdn.microsoft.com/en-us/devlabs/ee794896.aspx">RX</a>.  The MultiTouch API is good for this because by its very nature MultiTouch requires us to collect multiple events simultaneously and wire them up to create gestures.  Unfortunately the current iteration of MultiTouch API wrappers for Silverlight and WPF (<a href="http://touch.codeplex.com/">touch</a>, <a href="http://miria.codeplex.com/">MIRIA</a> etc) are quite basic and when working with custom gestures you are forced to introduce a lot of global code such as flags etc.  Not pretty!</p><p>Imagine the following scenario.</p><ul><li>2 touch points on screen (let call them HoldRight and HoldLeft).  Both expose 2 events Hold and Release</li><li>When a person presses and hold a finger over BOTH points trigger an event</li><li>If only one is being pressed and held do not fire the event</li><li>If the user lifts their finger or fingers off the points and repeats the process the event should fire again.</li></ul><p>This is a fairly simple example but actually wiring this up requires the use of nasty global variables and a bit of repetition.  Here is a very quick implementation of this.</p><p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">partial</span> <span class="k">class</span> <span class="nc">MainPage</span> <span class="p">:</span> <span class="n">UserControl</span>
<span class="p">{</span>
    <span class="k">private</span> <span class="kt">bool</span> <span class="n">_rightHeld</span> <span class="p">=</span> <span class="k">false</span><span class="p">;</span>
    <span class="k">private</span> <span class="kt">bool</span> <span class="n">_leftHeld</span> <span class="p">=</span> <span class="k">false</span><span class="p">;</span>
 
    <span class="k">public</span> <span class="nf">MainPage</span><span class="p">()</span>
    <span class="p">{</span>
        <span class="n">HoldLeft</span><span class="p">.</span><span class="n">Hold</span> <span class="p">+=</span> <span class="n">HoldLeft_Hold</span><span class="p">;</span>
        <span class="n">HoldRight</span><span class="p">.</span><span class="n">Hold</span> <span class="p">+=</span> <span class="n">HoldRight_Hold</span><span class="p">;</span>
        <span class="n">HoldLeft</span><span class="p">.</span><span class="n">Release</span> <span class="p">+=</span> <span class="n">HoldLeft_Release</span><span class="p">;</span>
        <span class="n">HoldRight</span><span class="p">.</span><span class="n">Release</span> <span class="p">+=</span> <span class="n">HoldRight_Release</span><span class="p">;</span>
    <span class="p">}</span>
 
    <span class="k">private</span> <span class="k">void</span> <span class="nf">HoldRight_Release</span><span class="p">(</span><span class="kt">object</span> <span class="n">sender</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="n">_rightHeld</span> <span class="p">=</span> <span class="k">false</span><span class="p">;</span>
        <span class="n">DoCheck</span><span class="p">();</span>
    <span class="p">}</span>
 
    <span class="k">private</span> <span class="k">void</span> <span class="nf">HoldLeft_Release</span><span class="p">(</span><span class="kt">object</span> <span class="n">sender</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="n">_leftHeld</span> <span class="p">=</span> <span class="k">false</span><span class="p">;</span>
        <span class="n">DoCheck</span><span class="p">();</span>
    <span class="p">}</span>
 
    <span class="k">private</span> <span class="k">void</span> <span class="nf">HoldRight_Hold</span><span class="p">(</span>
        <span class="kt">object</span> <span class="n">sender</span><span class="p">,</span> 
        <span class="n">GestureHoldEventArgs</span> <span class="n">e</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="n">_rightHeld</span> <span class="p">=</span> <span class="k">true</span><span class="p">;</span>
        <span class="n">DoCheck</span><span class="p">();</span>
    <span class="p">}</span>
 
    <span class="k">private</span> <span class="k">void</span> <span class="nf">HoldLeft_Hold</span><span class="p">(</span>
        <span class="kt">object</span> <span class="n">sender</span><span class="p">,</span> 
        <span class="n">GestureHoldEventArgs</span> <span class="n">e</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="n">_leftHeld</span> <span class="p">=</span> <span class="k">true</span><span class="p">;</span>
        <span class="n">DoCheck</span><span class="p">();</span>
    <span class="p">}</span>
 
    <span class="k">private</span> <span class="k">void</span> <span class="nf">DoCheck</span><span class="p">()</span>
    <span class="p">{</span>
        <span class="k">if</span> <span class="p">(</span><span class="n">_rightHeld</span> <span class="p">&amp;&amp;</span> <span class="n">_leftHeld</span><span class="p">)</span>
        <span class="p">{</span>
            <span class="c1">// Fire the event</span>
        <span class="p">}</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
</p><p>As you can see I have made use of 2 global variables a DoCheck method that checks if the 2 canvases are currently in a "held" state and fires the event if they are.  All the event handlers do nearly the same thing.  Sure I could refactor this to use maybe 2 event handlers and inspect the sender but that starts getting messy.</p><h2>Reactive Extensions to the Rescue!</h2><p>Reactive extensions let us "compose" events so we can pass them around and filter them like first class citizens.  Because of this we can actually create custom events by combining.  Tackling the same issue as above we can achieve the same functionality without the need for dodgy global vars and boilerplate code.  Lets look at the code first.</p><p></p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">partial</span> <span class="k">class</span> <span class="nc">MainPage</span> <span class="p">:</span> <span class="n">UserControl</span>
<span class="p">{</span>
    <span class="k">public</span> <span class="nf">MainPage</span><span class="p">()</span>
    <span class="p">{</span>
        <span class="c1">// compose events</span>
        <span class="kt">var</span> <span class="n">leftHold</span> <span class="p">=</span> <span class="n">Observable</span><span class="p">.</span><span class="n">FromEvent</span><span class="p">&lt;</span><span class="n">GestureHoldEventArgs</span><span class="p">&gt;(</span>
            <span class="n">HoldLeft</span><span class="p">,</span> <span class="s">"Hold"</span><span class="p">);</span>
        <span class="kt">var</span> <span class="n">rightHold</span> <span class="p">=</span> <span class="n">Observable</span><span class="p">.</span><span class="n">FromEvent</span><span class="p">&lt;</span><span class="n">GestureHoldEventArgs</span><span class="p">&gt;(</span>
            <span class="n">HoldRight</span><span class="p">,</span> <span class="s">"Hold"</span><span class="p">);</span>
        <span class="kt">var</span> <span class="n">rightRelease</span> <span class="p">=</span> <span class="n">ObservableEx</span><span class="p">.</span><span class="n">FromMultiTouchReleaseEvent</span><span class="p">(</span><span class="n">HoldRight</span><span class="p">);</span>
        <span class="kt">var</span> <span class="n">leftRelease</span> <span class="p">=</span> <span class="n">ObservableEx</span><span class="p">.</span><span class="n">FromMultiTouchReleaseEvent</span><span class="p">(</span><span class="n">HoldLeft</span><span class="p">);</span>
 
        <span class="c1">// subscribe to dual hold event</span>
        <span class="n">leftHold</span><span class="p">.</span><span class="n">Zip</span><span class="p">(</span><span class="n">rightHold</span><span class="p">,</span> <span class="p">(</span><span class="n">l</span><span class="p">,</span> <span class="n">r</span><span class="p">)</span> <span class="p">=&gt;</span> <span class="n">l</span><span class="p">)</span>
            <span class="c1">// listen until either release triggered</span>
            <span class="p">.</span><span class="n">TakeUntil</span><span class="p">(</span><span class="n">leftRelease</span><span class="p">.</span><span class="n">Amb</span><span class="p">(</span><span class="n">rightRelease</span><span class="p">))</span>
            <span class="c1">// trigger the event</span>
            <span class="p">.</span><span class="n">Subscribe</span><span class="p">(</span><span class="n">_</span> <span class="p">=&gt;</span> <span class="p">{</span> <span class="cm">/* trigger event */</span> <span class="p">});</span>
    <span class="p">}</span>
<span class="p">}</span>
 
<span class="k">public</span> <span class="k">static</span> <span class="k">class</span> <span class="nc">ObservableEx</span>
<span class="p">{</span>
    <span class="k">public</span> <span class="k">static</span> <span class="n">IObservable</span><span class="p">&lt;</span><span class="n">IEvent</span><span class="p">&lt;</span><span class="n">EventArgs</span><span class="p">&gt;&gt;</span> <span class="n">FromMultiTouchReleaseEvent</span><span class="p">(</span>
        <span class="n">TCanvas</span> <span class="n">canvas</span><span class="p">)</span> <span class="p">{</span>
        <span class="k">return</span> <span class="n">Observable</span><span class="p">.</span><span class="n">FromEvent</span><span class="p">&lt;</span><span class="n">TCanvas</span><span class="p">.</span><span class="n">ReleaseHandler</span><span class="p">,</span> <span class="n">EventArgs</span><span class="p">&gt;(</span>
                <span class="n">h</span> <span class="p">=&gt;</span> <span class="k">new</span> <span class="n">TCanvas</span><span class="p">.</span><span class="n">ReleaseHandler</span><span class="p">(</span><span class="n">e</span> <span class="p">=&gt;</span> <span class="p">{</span> <span class="p">}),</span>
                <span class="n">h</span> <span class="p">=&gt;</span> <span class="n">canvas</span><span class="p">.</span><span class="n">Release</span> <span class="p">+=</span> <span class="n">h</span><span class="p">,</span>
                <span class="n">h</span> <span class="p">=&gt;</span> <span class="n">canvas</span><span class="p">.</span><span class="n">Release</span> <span class="p">+=</span> <span class="n">h</span>
            <span class="p">);</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<p>First things first for some random reason the implementers of MIRIA decided to create en event the actually passed NO event args - this is not normal and could easily be considered and anti-pattern.  Because of this I had to use the long winded Observable.FromEvent which is created as a helper method.</p><p>So what are we doing here?</p><ol><li>Compose our events into first class citizens - Observables</li><li>Zip left and right hold events.  Zipping basically combines 2 Observables and publishes or emits a value when both observables have emitted a value.  The second argument in Zip is the transform function that converts the 2 emitted values into 1 value.  We don't care about this value so just return some arbitrary value.</li><li>Take the Zip generated observable and keep publishing it's values until Either the left or right release events are triggers (Amb = most ambitious - publishes first value to appear)</li><li>Subscribe to this super composed event.  When this happens we can fire our event</li></ol><p>What actually happens on the front end is irrelevant (the solution looks like this and will spin when the two Thumb areas are pressed and held)</p><p>[[posterous-content:ECxarBiHmvIvJutnbJaJ]]</p><p>But the fact we have managed to combine 4 isolated events without having to use boilerplate is very nice.  This gives us a lot of power to create and control complex gestures - not limited to MultiTouch but any sort of UI interactions (Mouse events, Web service calls etc).</p><p><em>* I'll keep saying this.  RX doesn't do anything new or solve any unsolvable problems but what it does do is allow us to do things in a neater way.  Now there is a bit of learning curve in it - you really need to start thinking in RX but once you wrap your head around RX is a nice tool to have on your tool belt.  No it's not complexity for complexity's sake - I honestly believe the code above, when used in the real world, will hep reduce complexity and make maintenance easier.</em></p><p>Good Fortune Awaits!</p>