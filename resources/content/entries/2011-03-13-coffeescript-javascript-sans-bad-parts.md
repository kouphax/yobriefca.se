---
date: 2011-03-13T00:00:00Z
title: 'CoffeeScript: JavaScript sans Bad Parts'
published: true
categories: [JavaScript]
type: article
external: false
---
<blockquote>This was written a long time ago when coffeescript was in early beta and I've  decided to push it into the public domain.  I've updated it a bit but I  apologise for any oversights.<p>Also worth mentioning that I am a lot more taken with it than I was at the time of the original blog.  I will be revisiting this again soon.</p></blockquote><p>If I've said it once, I've said it a million times - JavaScript is  misunderstood.  Sure it's got it's bad parts (eval, ==, typed wrappers etc.) and  it's got it's VERY bad parts (global variables, scope, typeof etc) but it's also  got a lot of beautiful parts (functions as first class objects, object and array  literals, dynamic objects and prototypal inheritance etc.).  JSLint validates  your code against the good subset to ensure you aren't using all that nasty  stuff that is going to break your code eventually but can we take it a step  further?  What if we could take out the good subset and create a subset of the  language so we couldn't ever use the bad stuff?  Well turns out you can.</p><h2>Enter CoffeeScript</h2><p><a href="http://jashkenas.github.com/coffee-script/">CoffeeScript</a> is a  full featured subset of JavaScript.  Essentially it is a meta-language that  compiles into JavaScript.  Its syntax is a bit different, more like Perl, Python  or Ruby and the JavaScript it generates only uses the "good parts" of JavaScript  and passes all strict JSLint tests.</p><h3>Syntax</h3><p>CoffeeScripts syntax is close to JavaScript and besides a few extra goodies  it is nearly a 1:1 mapping.  However it strips away a lot of the boilerplate  stuff to make the code you write slightly more succinct.  Another feature of the  syntax is that indentation is important (like Perl or Python, I can never  remember which) as there are no line ending tokens.  So lets look at a few  statements</p><p><span style=""></span></p><div class="highlight"><pre><code><span class="err">#</span> <span class="nx">Assignment</span><span class="o">:</span>
<span class="nx">number</span>   <span class="o">=</span> <span class="mi">42</span>
<span class="nx">opposite</span> <span class="o">=</span> <span class="kc">true</span>

<span class="c1">//-- GENERATED JS </span>

<span class="c1">// Assignment:</span>
<span class="kd">var</span> <span class="nx">number</span><span class="p">,</span> <span class="nx">opposite</span><span class="p">;</span>
<span class="nx">number</span> <span class="o">=</span> <span class="mi">42</span><span class="p">;</span>
<span class="nx">opposite</span> <span class="o">=</span> <span class="kc">true</span><span class="p">;</span>
</code></pre></div>
<p>This simple example of variable creation and assignment demonstrates how  CoffeeScripts avoids one of the most common and potentially fatal mistakes made  by many JavaScript developers - Inadvertent global variable declaration.  In  plain old JavaScript leaving out a var statement on a variable declaration  results in the variable being declared (or overwritten -eek!) in the global  scope.  In CoffeeScript var is optional and all variable declarations are made  in the current scope (and pushed to the top of the scope as is the JavaScript  way).  Want global scope declaration for some random reason?  Use  window.variable name and then ask yourself why you are doing it wrong.</p><p><span style=""></span></p><div class="highlight"><pre><code><span class="err">#</span> <span class="nx">Conditions</span><span class="o">:</span>
<span class="nx">number</span> <span class="o">=</span> <span class="o">-</span><span class="mi">42</span> <span class="k">if</span> <span class="nx">opposite</span>

<span class="c1">//-- GENERATED JS </span>

<span class="c1">// Conditions:</span>
<span class="kd">var</span> <span class="nx">number</span><span class="p">;</span>
<span class="k">if</span> <span class="p">(</span><span class="nx">opposite</span><span class="p">)</span> <span class="p">{</span>
  <span class="nx">number</span> <span class="o">=</span> <span class="o">-</span><span class="mi">42</span><span class="p">;</span>
<span class="p">}</span> 
</code></pre></div>
<p><span style=""><a title="http://gist.github.com/868210" href="http://gist.github.com/868210"></a></span><span style=""></span></p><div class="highlight"><pre><code><span class="err">#</span> <span class="nx">Functions</span><span class="o">:</span>
<span class="nx">square</span> <span class="o">=</span> <span class="p">(</span><span class="nx">x</span><span class="p">)</span> <span class="o">-&gt;</span> <span class="nx">x</span> <span class="o">*</span> <span class="nx">x</span>

<span class="c1">//-- GENERATED JS </span>

<span class="c1">// Functions:</span>
<span class="kd">var</span> <span class="nx">square</span><span class="p">;</span>
<span class="nx">square</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">x</span><span class="p">)</span> <span class="p">{</span>
  <span class="k">return</span> <span class="nx">x</span> <span class="o">*</span> <span class="nx">x</span><span class="p">;</span>
<span class="p">};</span> 
</code></pre></div>
<p>Function declarations are a good example of how succinct CoffeeScript can be  in comparison to JavaScript.  In fact anyone keen on C#'s lambdas or Groovys  syntax will find this quite familiar.  Gone are the bloating function and return  keywords ( -&gt; is the empty function - function() {})</p><p><span style=""></span></p><div class="highlight"><pre><code><span class="err">#</span> <span class="nx">Objects</span><span class="o">:</span>
<span class="nx">math</span> <span class="o">=</span>
  <span class="nx">root</span><span class="o">:</span>   <span class="nb">Math</span><span class="p">.</span><span class="nx">sqrt</span>
  <span class="nx">square</span><span class="o">:</span> <span class="nx">square</span>
  <span class="nx">cube</span><span class="o">:</span>   <span class="p">(</span><span class="nx">x</span><span class="p">)</span> <span class="o">-&gt;</span> <span class="nx">x</span> <span class="o">*</span> <span class="nx">square</span> <span class="nx">x</span> 

<span class="c1">//-- GENERATED JS </span>

<span class="c1">// Objects:</span>
<span class="kd">var</span> <span class="nx">math</span><span class="p">;</span>
<span class="nx">math</span> <span class="o">=</span> <span class="p">{</span>
  <span class="nx">root</span><span class="o">:</span> <span class="nb">Math</span><span class="p">.</span><span class="nx">sqrt</span><span class="p">,</span>
  <span class="nx">square</span><span class="o">:</span> <span class="nx">square</span><span class="p">,</span>
  <span class="nx">cube</span><span class="o">:</span> <span class="kd">function</span><span class="p">(</span><span class="nx">x</span><span class="p">)</span> <span class="p">{</span>
    <span class="k">return</span> <span class="nx">x</span> <span class="o">*</span> <span class="nx">square</span><span class="p">(</span><span class="nx">x</span><span class="p">);</span>
  <span class="p">}</span>
<span class="p">};</span> 
</code></pre></div>
<p><span style=""><a title="http://gist.github.com/868212" href="http://gist.github.com/868212"></a></span><span style=""></span></p><div class="highlight"><pre><code><span class="err">#</span> <span class="nx">Existence</span><span class="o">:</span>
<span class="nx">alert</span> <span class="s2">"I knew it!"</span> <span class="k">if</span> <span class="nx">elvis</span><span class="o">?</span>

<span class="c1">//-- GENERATED JS </span>

<span class="c1">// Existence:</span>
<span class="k">if</span> <span class="p">(</span><span class="k">typeof</span> <span class="nx">elvis</span> <span class="o">!=</span> <span class="s2">"undefined"</span> <span class="o">&amp;&amp;</span> <span class="nx">elvis</span> <span class="o">!==</span> <span class="kc">null</span><span class="p">)</span> <span class="p">{</span>
  <span class="nx">alert</span><span class="p">(</span><span class="s2">"I knew it!"</span><span class="p">);</span>
<span class="p">}</span>
</code></pre></div>
<p>Existence is a nice feature as it keeps non-boolean falsy values nice and  strict and saves a lot of guessing.  That's the basics but as I mentioned  earlier there are some nice bonus features that can make some common tasks very  clean.</p><h3><strong>The Goodie Bag</strong></h3><h4>Array Comprehension</h4><h4><span style="font-weight: normal;"></span></h4><div class="highlight"><pre><code><span class="err">#</span> <span class="nb">Array</span> <span class="nx">Comprehension</span><span class="o">:</span>
<span class="nx">squares</span> <span class="o">=</span> <span class="p">((</span><span class="nx">x</span><span class="p">)</span> <span class="o">-&gt;</span> <span class="nx">x</span> <span class="o">*</span> <span class="nx">x</span><span class="p">)</span> <span class="nx">n</span> <span class="k">for</span> <span class="nx">n</span> <span class="k">in</span> <span class="p">[</span><span class="mi">1</span><span class="p">,</span><span class="mi">2</span><span class="p">,</span><span class="mi">3</span><span class="p">]</span>

<span class="c1">//-- GENERATED JS </span>

<span class="c1">// Array Comprehension:</span>
<span class="kd">var</span> <span class="nx">n</span><span class="p">,</span> <span class="nx">squares</span><span class="p">,</span> <span class="nx">_i</span><span class="p">,</span> <span class="nx">_len</span><span class="p">,</span> <span class="nx">_ref</span><span class="p">;</span>
<span class="nx">_ref</span> <span class="o">=</span> <span class="p">[</span><span class="mi">1</span><span class="p">,</span> <span class="mi">2</span><span class="p">,</span> <span class="mi">3</span><span class="p">];</span>
<span class="k">for</span> <span class="p">(</span><span class="nx">_i</span> <span class="o">=</span> <span class="mi">0</span><span class="p">,</span> <span class="nx">_len</span> <span class="o">=</span> <span class="nx">_ref</span><span class="p">.</span><span class="nx">length</span><span class="p">;</span> <span class="nx">_i</span> <span class="o">&lt;</span> <span class="nx">_len</span><span class="p">;</span> <span class="nx">_i</span><span class="o">++</span><span class="p">)</span> <span class="p">{</span>
  <span class="nx">n</span> <span class="o">=</span> <span class="nx">_ref</span><span class="p">[</span><span class="nx">_i</span><span class="p">];</span>
  <span class="nx">squares</span> <span class="o">=</span> <span class="p">(</span><span class="kd">function</span><span class="p">(</span><span class="nx">x</span><span class="p">)</span> <span class="p">{</span>
    <span class="k">return</span> <span class="nx">x</span> <span class="o">*</span> <span class="nx">x</span><span class="p">;</span>
  <span class="p">})(</span><span class="nx">n</span><span class="p">);</span>
<span class="p">}</span>
</code></pre></div>
<p>Array comprehension is quite a common task in many JavaScript solutions.   Simply put this is the Map part of any typical MapReduce situations (common in  many NoSQL style databases and Ajax situations).  MapReduce is essentially -  given an array of items,</p><ul><li>Map: Transform that list into what you want (SQLs SELECT) </li><li>Reduce: Based on that mapping remove unwanted items</li></ul><p>In the Ajax world you'd probably need to do this when dealing with JSON or  XML responses which are quite often generally result sets.  The example above  shows just how easy it is to apply an arbitrary function over an array of  items.  Now I guess the equivalent JS could be written slightly better by hand  but would it not take longer and be harder to maintain?  Probably.  This being  quite a common task the less you have to write the better!</p><h4>Splats</h4><p>Splats are a convenient way to work with the arguments object in JavaScript  while clearly outlining required and optional arguments.  Lets take this  example.</p><p></p><div class="highlight"><pre><code><span class="err">#</span> <span class="nx">Splats</span><span class="o">:</span>
<span class="nx">log</span> <span class="o">=</span> <span class="nx">console</span><span class="o">?</span><span class="p">.</span><span class="nx">log</span> <span class="nx">or</span> <span class="p">(</span><span class="nx">o</span><span class="p">)</span> <span class="o">-&gt;</span> <span class="nx">alert</span> <span class="nx">o</span>
<span class="nx">printResults</span> <span class="o">=</span> <span class="p">(</span><span class="nx">winner</span><span class="p">,</span> <span class="nx">runners</span><span class="p">...)</span> <span class="o">-&gt;</span>
  <span class="nx">log</span> <span class="s2">"Winner:"</span><span class="p">,</span> <span class="nx">winner</span>
  <span class="k">if</span> <span class="nx">runners</span><span class="o">?</span>
    <span class="p">((</span><span class="nx">r</span><span class="p">)</span> <span class="o">-&gt;</span> <span class="nx">log</span> <span class="s2">"Runner up"</span><span class="p">,</span> <span class="nx">r</span><span class="p">)</span> <span class="k">for</span> <span class="nx">r</span> <span class="k">in</span> <span class="nx">runners</span>

<span class="nx">printResults</span> <span class="s2">"James"</span><span class="p">,</span> <span class="s2">"Paul"</span><span class="p">,</span> <span class="s2">"Martin"</span><span class="p">,</span> <span class="s2">"John"</span>

<span class="c1">//-- GENERATED JS </span>

<span class="c1">// Splats:</span>
<span class="kd">var</span> <span class="nx">log</span><span class="p">,</span> <span class="nx">printResults</span><span class="p">;</span>
<span class="kd">var</span> <span class="nx">__slice</span> <span class="o">=</span> <span class="nb">Array</span><span class="p">.</span><span class="nx">prototype</span><span class="p">.</span><span class="nx">slice</span><span class="p">;</span>
<span class="nx">log</span> <span class="o">=</span> <span class="p">(</span><span class="k">typeof</span> <span class="nx">console</span> <span class="o">!=</span> <span class="s2">"undefined"</span> <span class="o">&amp;&amp;</span> <span class="nx">console</span> <span class="o">!==</span> <span class="kc">null</span> <span class="o">?</span> <span class="nx">console</span><span class="p">.</span><span class="nx">log</span> <span class="o">:</span> <span class="k">void</span> <span class="mi">0</span><span class="p">)</span> <span class="o">||</span> <span class="kd">function</span><span class="p">(</span><span class="nx">o</span><span class="p">)</span> <span class="p">{</span>
  <span class="k">return</span> <span class="nx">alert</span><span class="p">(</span><span class="nx">o</span><span class="p">);</span>
<span class="p">};</span>
<span class="nx">printResults</span> <span class="o">=</span> <span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
  <span class="kd">var</span> <span class="nx">r</span><span class="p">,</span> <span class="nx">runners</span><span class="p">,</span> <span class="nx">winner</span><span class="p">,</span> <span class="nx">_i</span><span class="p">,</span> <span class="nx">_len</span><span class="p">,</span> <span class="nx">_results</span><span class="p">;</span>
  <span class="nx">winner</span> <span class="o">=</span> <span class="nx">arguments</span><span class="p">[</span><span class="mi">0</span><span class="p">],</span> <span class="nx">runners</span> <span class="o">=</span> <span class="mi">2</span> <span class="o">&lt;=</span> <span class="nx">arguments</span><span class="p">.</span><span class="nx">length</span> <span class="o">?</span> <span class="nx">__slice</span><span class="p">.</span><span class="nx">call</span><span class="p">(</span><span class="nx">arguments</span><span class="p">,</span> <span class="mi">1</span><span class="p">)</span> <span class="o">:</span> <span class="p">[];</span>
  <span class="nx">log</span><span class="p">(</span><span class="s2">"Winner:"</span><span class="p">,</span> <span class="nx">winner</span><span class="p">);</span>
  <span class="k">if</span> <span class="p">(</span><span class="nx">runners</span> <span class="o">!=</span> <span class="kc">null</span><span class="p">)</span> <span class="p">{</span>
    <span class="nx">_results</span> <span class="o">=</span> <span class="p">[];</span>
    <span class="k">for</span> <span class="p">(</span><span class="nx">_i</span> <span class="o">=</span> <span class="mi">0</span><span class="p">,</span> <span class="nx">_len</span> <span class="o">=</span> <span class="nx">runners</span><span class="p">.</span><span class="nx">length</span><span class="p">;</span> <span class="nx">_i</span> <span class="o">&lt;</span> <span class="nx">_len</span><span class="p">;</span> <span class="nx">_i</span><span class="o">++</span><span class="p">)</span> <span class="p">{</span>
      <span class="nx">r</span> <span class="o">=</span> <span class="nx">runners</span><span class="p">[</span><span class="nx">_i</span><span class="p">];</span>
      <span class="nx">_results</span><span class="p">.</span><span class="nx">push</span><span class="p">((</span><span class="kd">function</span><span class="p">(</span><span class="nx">r</span><span class="p">)</span> <span class="p">{</span>
        <span class="k">return</span> <span class="nx">log</span><span class="p">(</span><span class="s2">"Runner up"</span><span class="p">,</span> <span class="nx">r</span><span class="p">);</span>
      <span class="p">}));</span>
    <span class="p">}</span>
    <span class="k">return</span> <span class="nx">_results</span><span class="p">;</span>
  <span class="p">}</span>
<span class="p">};</span>
<span class="nx">printResults</span><span class="p">(</span><span class="s2">"James"</span><span class="p">,</span> <span class="s2">"Paul"</span><span class="p">,</span> <span class="s2">"Martin"</span><span class="p">,</span> <span class="s2">"John"</span><span class="p">);</span>
</code></pre></div>
<p>Look familiar to you .NET people?  It should because for all intents and  purposes this is the same as the param keyword.  Implementing this sort of thing  in JavaScript is no easy task.  Again the generated JavaScript could be  simplified if written by a human bean but could you get it right first time  without seeing this generated code?</p><h4>Destructing Assignment</h4><p>Already part of the JavaScript spec but not present in all implementations  (see MDC article) destructing assignments allow the assigning of multiple values  in one fell swoop e.g.</p><p><div class="highlight"><pre><code><span class="err">#</span> <span class="nx">Destructing</span> <span class="nx">Assignments</span>
<span class="nx">a</span> <span class="o">=</span> <span class="mi">1000</span>
<span class="nx">b</span> <span class="o">=</span> <span class="mi">0</span>

<span class="p">[</span><span class="nx">a</span><span class="p">,</span> <span class="nx">b</span><span class="p">]</span> <span class="o">=</span> <span class="p">[</span><span class="mi">1000</span><span class="p">,</span> <span class="mi">0</span><span class="p">]</span>
<span class="err">#</span> <span class="nx">a</span> <span class="nx">now</span> <span class="o">=</span> <span class="mi">0</span><span class="p">,</span> <span class="nx">b</span> <span class="nx">now</span> <span class="o">=</span> <span class="mi">1000</span> 

<span class="err">#</span> <span class="nx">Multiple</span> <span class="k">return</span> <span class="nx">values</span>
<span class="nx">weatherReport</span> <span class="o">=</span> <span class="p">(</span><span class="nx">loc</span><span class="p">)</span> <span class="o">-&gt;</span> <span class="p">[</span><span class="nx">loc</span><span class="p">,</span> <span class="mi">20</span><span class="p">,</span> <span class="s2">"Sunny"</span><span class="p">]</span>

<span class="p">[</span><span class="nx">city</span><span class="p">,</span> <span class="nx">temp</span><span class="p">,</span> <span class="nx">forecast</span><span class="p">]</span> <span class="o">=</span> <span class="nx">weatherReport</span> <span class="s2">"Belfast"</span>

<span class="err">#</span> <span class="nx">city</span> <span class="o">=</span> <span class="s2">"Belfast"</span>
<span class="err">#</span> <span class="nx">temp</span> <span class="o">=</span> <span class="mi">20</span>
<span class="err">#</span> <span class="nx">forecast</span> <span class="o">=</span> <span class="s2">"Sunny"</span>

<span class="c1">//-- GENERATED JS </span>

<span class="c1">// Destructing Assignments</span>
<span class="kd">var</span> <span class="nx">a</span><span class="p">,</span> <span class="nx">b</span><span class="p">,</span> <span class="nx">city</span><span class="p">,</span> <span class="nx">forecast</span><span class="p">,</span> <span class="nx">temp</span><span class="p">,</span> <span class="nx">weatherReport</span><span class="p">,</span> <span class="nx">_ref</span><span class="p">,</span> <span class="nx">_ref2</span><span class="p">;</span>
<span class="nx">a</span> <span class="o">=</span> <span class="mi">1000</span><span class="p">;</span>
<span class="nx">b</span> <span class="o">=</span> <span class="mi">0</span><span class="p">;</span>

<span class="nx">_ref</span> <span class="o">=</span> <span class="p">[</span><span class="mi">1000</span><span class="p">,</span> <span class="mi">0</span><span class="p">],</span> <span class="nx">a</span> <span class="o">=</span> <span class="nx">_ref</span><span class="p">[</span><span class="mi">0</span><span class="p">],</span> <span class="nx">b</span> <span class="o">=</span> <span class="nx">_ref</span><span class="p">[</span><span class="mi">1</span><span class="p">];</span>
<span class="c1">// a now = 0, b now = 1000 </span>

<span class="nx">weatherReport</span> <span class="o">=</span> <span class="kd">function</span><span class="p">(</span><span class="nx">loc</span><span class="p">)</span> <span class="p">{</span>
  <span class="k">return</span> <span class="p">[</span><span class="nx">loc</span><span class="p">,</span> <span class="mi">20</span><span class="p">,</span> <span class="s2">"Sunny"</span><span class="p">];</span>
<span class="p">};</span>
<span class="nx">_ref2</span> <span class="o">=</span> <span class="nx">weatherReport</span><span class="p">(</span><span class="s2">"Belfast"</span><span class="p">),</span> <span class="nx">city</span> <span class="o">=</span> <span class="nx">_ref2</span><span class="p">[</span><span class="mi">0</span><span class="p">],</span> <span class="nx">temp</span> <span class="o">=</span> <span class="nx">_ref2</span><span class="p">[</span><span class="mi">1</span><span class="p">],</span> <span class="nx">forecast</span> <span class="o">=</span> <span class="nx">_ref2</span><span class="p">[</span><span class="mi">2</span><span class="p">];</span>
<span class="c1">// city = "Belfast"</span>
<span class="c1">// temp = 20</span>
<span class="c1">// forecast = "Sunny"</span>
</code></pre></div>
</p><p>I've never really come across a massive need for something like this but I  guess it's another handy tool to have in the scripting world.</p><h3>Should We All Be Using It?</h3><p>If you think I am going to say "Yes" immediatley you are wrong.  And if you are now  asking yourself why I even bothered typing this.... lets just say train rides are  boring but not as boring as most of the TV shows my darling wife makes me sit  through EVERY FLIPPING NIGHT OF MY LIFE!!!!</p><p>The big reason I am not recommending it yet is that I am not 100% convinced  of the benefits of using it above plain old JavaScript.  It's a bit like  marmite.  Many node.js developers write in CoffeeScript, and ONLY CoffeeScript  but many still simply reject it.  I can understand both standpoints so until I  can say without faltering that one side has got it right I am not taking it  seriously.  That said I prefer to form my own opinions anyway so I need to spend a lot more time with it before I can jump to any conclusions.</p><p>Oh and, honestly, who actually really wants to spend what limited time they  have trying new things, hacking around, experimenting, taking risks, getting  frustrated and making plenty of mistakes?  Anyone? Anyone at all?  Nah I didn't think so.  Computers are just a 9-5 job for all of us ;-P</p>