---
date: 2011-07-04T23:00:00Z
title: 'Debugging NSpec Tests: The DebuggerShim'
published: true
categories: [Testing, .NET]
type: article
external: false
---
<p>Busy busy busy.  I've been working away on a lot of projects and not had a lot of time to stick anything up on this blog in a while.  I've got a growing "To Blog" list but no time to do it, woe is me!!!<br /><img src="/images/woeisme.png" style="float:right;" /></p><p>Enough about my perfectly normal life, well almost.  Some of my current work has me going full tilt with <span class="caps">TDD</span> on a little C# project and I decided that <a href="http://nspec.org">NSpec</a> would help me with unit testing.  So I went about writing failing tests, writing code, passing tests on and on and on.  Things went along fairly well until I hit a little bit of a wall around a little edge case.  My tests kept failing and I couldn't understand why.  The stack trace made little to no sense and my code looked sound.  The problem I had was that <a href="http://nspec.org">NSpec</a>, out of the box at least, doesn't have any nice integration with Visual Studio or TestDriven.<span class="caps">NET</span>.  Initially I was using a throw away console application and debugging through it but as you can imagine that was time consuming.</p><p>It wasn't long though until I was pointed in the <a href="http://twitter.com/#!/amirrajan/status/87174922782253056">right direction</a>.</p><p><!-- http://twitter.com/#!/amirrajan/status/87174922782253056 --> <style type="text/css">.bbpBox87174922782253056 {background:url(http://a0.twimg.com/images/themes/theme1/bg.png) #C0DEED;padding:20px;} p.bbpTweet{background:#fff;padding:10px 12px 10px 12px;margin:0;min-height:48px;color:#000;font-size:18px !important;line-height:22px;-moz-border-radius:5px;-webkit-border-radius:5px} p.bbpTweet span.metadata{display:block;width:100%;clear:both;margin-top:8px;padding-top:12px;height:40px;border-top:1px solid #fff;border-top:1px solid #e6e6e6} p.bbpTweet span.metadata span.author{line-height:19px} p.bbpTweet span.metadata span.author img{float:left;margin:0 7px 0 0px;width:38px;height:38px} p.bbpTweet a:hover{text-decoration:underline}p.bbpTweet span.timestamp{font-size:12px;display:block}</style> </p><div class="bbpBox87174922782253056"><p class="bbpTweet">If you want to have debugger support for you NSpec specifications, use this: <a href="http://t.co/6SwZcVL" rel="nofollow">http://t.co/6SwZcVL</a> /cc @<a class="tweet-url username" href="http://twitter.com/kouphax" rel="nofollow">kouphax</a> @<a class="tweet-url username" href="http://twitter.com/mattflo" rel="nofollow">mattflo</a><span class="timestamp"><a title="Sat Jul 02 15:04:55 +0000 2011" href="http://twitter.com/#!/amirrajan/status/87174922782253056">less than a minute ago</a> via web <a href="http://twitter.com/intent/favorite?tweet_id=87174922782253056"><img src="http://si0.twimg.com/images/dev/cms/intents/icons/favorite.png" /> Favorite</a> <a href="http://twitter.com/intent/retweet?tweet_id=87174922782253056"><img src="http://si0.twimg.com/images/dev/cms/intents/icons/retweet.png" /> Retweet</a> <a href="http://twitter.com/intent/tweet?in_reply_to=87174922782253056"><img src="http://si0.twimg.com/images/dev/cms/intents/icons/reply.png" /> Reply</a></span><span class="metadata"><span class="author"><a href="http://twitter.com/amirrajan"><img src="http://a2.twimg.com/profile_images/1326219901/1613d80124a605829d755d9df0fc8b9e_normal.jpeg" /></a><strong><a href="http://twitter.com/amirrajan">Amir Rajan</a></strong><br />amirrajan</span></span></p></div> <!-- end of tweet --><p>The gist, in full below, provides a simple debugger shim over nspec that allows you to hook into the Visual Studio debugger (through something like TestDriven.<span class="caps">NET</span> for example).  I simply right clicked on the shim and select "Run With Debugger" and boom my stupid mistake was displayed to me rather quickly.</p><div class="highlight"><pre><code><span class="k">using</span> <span class="nn">System</span><span class="p">;</span>
<span class="k">using</span> <span class="nn">NUnit.Framework</span><span class="p">;</span>
<span class="k">using</span> <span class="nn">NSpec.Domain</span><span class="p">;</span>
<span class="k">using</span> <span class="nn">System.Reflection</span><span class="p">;</span>
<span class="k">using</span> <span class="nn">NSpec</span><span class="p">;</span>

<span class="k">namespace</span> <span class="nn">DynamicBlog.Tests</span>
<span class="p">{</span>
<span class="na">    [TestFixture]</span>
    <span class="k">public</span> <span class="k">class</span> <span class="nc">DebuggerShim</span>
    <span class="p">{</span>
<span class="na">        [Test]</span>
        <span class="k">public</span> <span class="k">void</span> <span class="nf">debug</span><span class="p">()</span>
        <span class="p">{</span>
            <span class="c1">//the specification class you want to test</span>
            <span class="c1">//this can be a regular expression</span>
            <span class="kt">var</span> <span class="n">testClassYouWantToDebug</span> <span class="p">=</span> <span class="s">"describe_Blog"</span><span class="p">;</span>

            <span class="c1">//initialize NSpec's specfinder</span>
            <span class="kt">var</span> <span class="n">finder</span> <span class="p">=</span> <span class="k">new</span> <span class="n">SpecFinder</span><span class="p">(</span>
                <span class="n">Assembly</span><span class="p">.</span><span class="n">GetExecutingAssembly</span><span class="p">().</span><span class="n">Location</span><span class="p">,</span> 
                <span class="k">new</span> <span class="nf">Reflector</span><span class="p">(),</span> 
                <span class="n">testClassYouWantToDebug</span><span class="p">);</span>
            
            <span class="c1">//initialize NSpec's builder</span>
            <span class="kt">var</span> <span class="n">builder</span> <span class="p">=</span> <span class="k">new</span> <span class="n">ContextBuilder</span><span class="p">(</span>
                <span class="n">finder</span><span class="p">,</span> 
                <span class="k">new</span> <span class="nf">DefaultConventions</span><span class="p">());</span>

            <span class="c1">//this line runs the tests you specified in the filter</span>
            <span class="k">new</span> <span class="nf">ContextRunner</span><span class="p">(</span><span class="n">builder</span><span class="p">,</span> <span class="k">new</span> <span class="n">ConsoleFormatter</span><span class="p">()).</span><span class="n">Run</span><span class="p">();</span>
        <span class="p">}</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<p>Handy until a more concrete solution appears.  One more thing - really enjoying nspec, it seems to be fitting well with the way I work.</p>