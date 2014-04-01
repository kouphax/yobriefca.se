---
date: 2010-10-18T23:00:00Z
title: 'Client Side: Processing vs Responsiveness'
published: true
categories: [JavaScript]
type: article
external: false
---
<p style="padding-left: 30px;"><em>The examples below are probably best run in a browser that has a relatively slow script engine (IE for example)</em></p><p>One of the problem with the whole Web2.0/Rich Client concepts is that people seem to think it's OK to shove processing that should be done on the server on to the client. This is never the answer - you can control resources on the sever but never on some random browser half way across the world/country/street/room. There are, however, situations where client side processing is going to be mandatory (think Complex Grids and Calculations etc). This obviously leads to a loss of responsiveness on the client (to varying degrees). One of the big problem with large processing is that it obviously takes time. Imagine this scenario...</p><p></p><div class="highlight"><pre><code><span class="kd">function</span> <span class="nx">doProcessing</span><span class="p">(){</span>
 
  <span class="cm">/* get the progress indicator element */</span>
  <span class="kd">var</span> <span class="nx">el</span> <span class="o">=</span> <span class="nb">document</span><span class="p">.</span><span class="nx">getElementById</span><span class="p">(</span><span class="s2">"progress-meter"</span><span class="p">);</span>
 
  <span class="cm">/* update element */</span>
  <span class="nx">el</span><span class="p">.</span><span class="nx">innerHTML</span> <span class="o">=</span> <span class="s2">"Processing"</span><span class="p">;</span>
 
  <span class="cm">/* do long running processing */</span>
  <span class="k">for</span><span class="p">(</span><span class="kd">var</span> <span class="nx">i</span> <span class="o">=</span> <span class="mi">0</span> <span class="nx">i</span> <span class="o">&amp;</span><span class="nx">lt</span><span class="p">;</span> <span class="mi">999999</span><span class="p">;</span> <span class="nx">i</span><span class="o">++</span><span class="p">){</span>
    <span class="kd">var</span> <span class="nx">j</span> <span class="o">=</span> <span class="nb">Math</span><span class="p">.</span><span class="nx">sqrt</span><span class="p">(</span><span class="nx">i</span><span class="p">);</span>
  <span class="p">}</span>
 
  <span class="cm">/* update element */</span>
  <span class="nx">el</span><span class="p">.</span><span class="nx">innerHTML</span> <span class="o">=</span> <span class="s2">"Done!"</span><span class="p">;</span>
<span class="p">}</span>
 
<span class="nx">doProcessing</span><span class="p">()</span>
</code></pre></div>
<p>You'd expect that this code updates the "progress-meter" element with "Processing", does some long computation and then updates the "progress-meter" again to indicate it's done. But it doesn't. Screen repaints/updates do not happen until calls complete. So what is actually happening? Well the expected outcome DOES happen it's just both updates happen after the call completes so the first update occurs so fast you never see it. <a href="http://jsfiddle.net/kouphax/VtaHg/" target="_blank">See It In Action</a>. You'll also notice that the entire screen is locked while this processing occurs and that just leads to a bad user experience. So what can be done?</p><h2>Use of setTimeout()</h2><p>Javascript is single threaded which leads to the issues above but there is a way create pseudo-threading using setTimeout. The setTimeout function executes code after a set time in milliseconds (what a surprise!). So delaying this execution (even by 0 miilliseconds) allows screen updates to occur before the processing begins. This requires a small change to the code.</p><p></p><div class="highlight"><pre><code><span class="kd">function</span> <span class="nx">doProcessing</span><span class="p">(){</span>
  <span class="kd">var</span> <span class="nx">el</span> <span class="o">=</span> <span class="nb">document</span><span class="p">.</span><span class="nx">getElementById</span><span class="p">(</span><span class="s2">"progress-meter"</span><span class="p">);</span>
  <span class="nx">el</span><span class="p">.</span><span class="nx">innerHTML</span> <span class="o">=</span> <span class="s2">"Processing"</span><span class="p">;</span>
  <span class="nx">setTimeout</span><span class="p">(</span><span class="kd">function</span><span class="p">(){</span>
    <span class="k">for</span><span class="p">(</span><span class="kd">var</span> <span class="nx">i</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span> <span class="nx">i</span> <span class="o">&amp;</span><span class="nx">lt</span><span class="p">;</span> <span class="mi">9999999</span><span class="p">;</span> <span class="nx">i</span><span class="o">++</span><span class="p">){</span>
      <span class="kd">var</span> <span class="nx">j</span> <span class="o">=</span> <span class="nb">Math</span><span class="p">.</span><span class="nx">round</span><span class="p">(</span><span class="nb">Math</span><span class="p">.</span><span class="nx">sqrt</span><span class="p">(</span><span class="nx">i</span><span class="p">));</span>
    <span class="p">}</span>
    <span class="nx">el</span><span class="p">.</span><span class="nx">innerHTML</span> <span class="o">=</span> <span class="s2">"Done!"</span><span class="p">;</span>
  <span class="p">},</span><span class="mi">0</span><span class="p">);</span>
<span class="p">}</span>
 
<span class="nx">doProcessing</span><span class="p">()</span>
</code></pre></div>
<p>So if you <a href="http://jsfiddle.net/kouphax/MJFFX/" target="_blank">give this a go</a> you will see there is a much better feedback response compared to the first. This approach solves our initial problem but we still have the issue that the screen is locking up. At least now the users knows something is happening. Another issue this creates is that some browsers prompt users if scripts take to long allowing the user to cancel potentially important tasks.</p><p>[[posterous-content:jcdDmwFBuEneeneritwd]]</p><h2>Asynchronous Processing</h2><p>Now we are getting into the good stuff. To prevent the screen locking up during any complex processing activity it is possible to apply the setTimeout technique but only over a smaller subset of the processing activity each time. This will achieve a sort of asynchronous effect allowing the user to do other stuff while the processing happens in the background. This is a basic example,</p><p><div class="highlight"><pre><code><span class="kd">function</span> <span class="nx">complete</span><span class="p">(){</span>
  <span class="nx">alert</span><span class="p">(</span><span class="s2">"Processing Complete"</span><span class="p">);</span>
<span class="p">}</span>
 
<span class="kd">function</span> <span class="nx">doProcessing</span><span class="p">(</span><span class="nx">callback</span><span class="p">){</span>
 
  <span class="kd">var</span> <span class="nx">el</span> <span class="o">=</span> <span class="nb">document</span><span class="p">.</span><span class="nx">getElementById</span><span class="p">(</span><span class="s2">"progress-meter"</span><span class="p">);</span>
  <span class="nx">el</span><span class="p">.</span><span class="nx">innerHTML</span> <span class="o">=</span> <span class="s2">"Processing"</span><span class="p">;</span>
 
  <span class="cm">/* setup iteration */</span>
  <span class="kd">var</span> <span class="nx">iterations</span> <span class="o">=</span> <span class="mi">9999999</span><span class="p">;</span>
  <span class="kd">var</span> <span class="nx">chunks</span> <span class="o">=</span> <span class="nx">iterations</span><span class="o">/</span><span class="mi">100</span><span class="p">;</span>  <span class="cm">/* each chunk is 1% of the overal processing count*/</span>
  <span class="kd">var</span> <span class="nx">i</span><span class="o">=</span><span class="mi">0</span><span class="p">;</span>
 
  <span class="cm">/* self executing anonymous function */</span>
  <span class="p">(</span><span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
 
    <span class="cm">/* process chunk */</span>
    <span class="k">for</span><span class="p">(</span><span class="kd">var</span> <span class="nx">count</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span><span class="nx">i</span><span class="o">&amp;</span><span class="nx">lt</span><span class="p">;</span><span class="nx">iterations</span><span class="p">;</span><span class="nx">i</span><span class="o">++</span><span class="p">)</span> <span class="p">{</span>
      <span class="kd">var</span> <span class="nx">j</span> <span class="o">=</span> <span class="nb">Math</span><span class="p">.</span><span class="nx">round</span><span class="p">(</span><span class="nb">Math</span><span class="p">.</span><span class="nx">sqrt</span><span class="p">(</span><span class="nx">i</span><span class="p">));</span>
      <span class="nx">count</span><span class="o">++</span><span class="p">;</span>
      <span class="k">if</span><span class="p">(</span><span class="nx">count</span> <span class="o">==</span> <span class="mi">99999</span><span class="p">)</span>
        <span class="k">break</span><span class="p">;</span>
    <span class="p">}</span>
 
    <span class="cm">/* more to process */</span>
    <span class="k">if</span><span class="p">(</span><span class="nx">i</span><span class="o">&amp;</span><span class="nx">lt</span><span class="p">;</span><span class="nx">iterations</span><span class="p">){</span>
      <span class="cm">/* update screen */</span>
      <span class="nx">el</span><span class="p">.</span><span class="nx">innerHTML</span> <span class="o">=</span> <span class="nb">Math</span><span class="p">.</span><span class="nx">round</span><span class="p">((</span><span class="nx">i</span><span class="o">/</span><span class="nx">iterations</span><span class="p">)</span><span class="o">*</span><span class="mi">100</span><span class="p">)</span> <span class="o">+</span> <span class="s1">'% Complete'</span><span class="p">;</span>
 
      <span class="cm">/* recurse for next segment */</span>
      <span class="nx">setTimeout</span><span class="p">(</span><span class="nx">arguments</span><span class="p">.</span><span class="nx">callee</span><span class="p">,</span><span class="mi">0</span><span class="p">);</span>
    <span class="p">}</span><span class="k">else</span><span class="p">{</span>
      <span class="cm">/* update screen */</span>
      <span class="nx">el</span><span class="p">.</span><span class="nx">innerHTML</span> <span class="o">=</span> <span class="s2">"Done!"</span><span class="p">;</span>
 
      <span class="cm">/* call optional callback */</span>
      <span class="k">if</span><span class="p">(</span><span class="nx">callback</span><span class="p">){</span> <span class="nx">callback</span><span class="p">()</span> <span class="p">}</span>
    <span class="p">}</span>
  <span class="p">})();</span>
<span class="p">}</span>

<span class="nx">doProcessing</span><span class="p">(</span><span class="nx">complete</span><span class="p">);</span>
</code></pre></div>
</p><p>You can <a href="http://jsfiddle.net/kouphax/CKALu/" target="_blank">see this in action</a> for yourself. If you run the example you will see that there is a constant update (after each chunk is processed) and that screen control is still available. So this has solved both our issues but has introduced new considerations.</p><ul><li>Executes asynchronously therefore we need to support a callback mechanism as it's not procedural (see code)</li><li>Processing time increases (see below)</li></ul><p>You will see that the length of time it take to process the complete task is a lot longer than the other 2 examples.. this is due to the increase in complexity of the code (recursion, another function call etc) and this is the trade off. Processing time is inversely proportional to the size of each processing chunk</p><div>[[posterous-content:ImAAruEoruBBGBGeBetI]]<p>Chunk Size (y) vs Response Time (x)</p></div><p><br />As the chunk sizes get smaller you get a more responsive UI but the processing time can get scarily long. The example chunks at about 1% of the total task size and that seems to be a good balance (for this task at least) between responsiveness and processing time. I threw together a little suite of yielding techniques with various update times etc and you can play around with that <a href="http://jsfiddle.net/kouphax/Kkhc9/" target="_blank">here</a></p>