---
date: 2011-05-16T23:00:00Z
title: Further Adventures in Unit Testing Technologies
published: true
categories: [.NET, Testing]
type: article
external: false
---
<p>This post is a follow up to <a href="/blog/2011/04/04/a-tale-of-unit-testing-technologies/">A Tale of Unit Testing Technologies</a>.  Information about the project repository can be found on my <a href="http://github.com/kouphax/unit-testing">project page</a>.</p><p>I've finally managed to slog through the long and slightly tedious process of re-writing the same suite of tests in a number of C# and JavaScript unit testing technologies - the results of which can be seen on the <a href="http://github.com/kouphax/unit-testing">project GitHub page</a>.  My conclusion, especially in the .<span class="caps">NET</span> world, is that it doesn't really matter that much.  I know it's not that much of a brilliant ending but what did you expect?  Fireworks?</p><p>Anyways before I start amazing you all with more awe inspiring revelations let me run through the remaining technologies (until such times as more are added at least) and summarise their good and bad points.  The other technologies I brought on board include,</p><ul>	<li><a href="http://www.nunit.org/">NUnit</a></li>	<li><a href="http://msdn.microsoft.com/en-us/library/ms182486.aspx">MSTest</a></li>	<li><a href="http://nspec.org/">NSpec</a></li>	<li><a href="https://github.com/robconery/Quixote">Quixote</a></li>	<li><a href="http://pivotal.github.com/jasmine/">Jasmine</a></li>	<li><a href="http://docs.jquery.com/Qunit">QUnit</a></li></ul><p>Some obvious ones there just to round out the comparisons and a few JavaScript based ones.</p><h2><a href="http://www.nunit.org/">NUnit</a></h2><p>Good old <a href="http://www.nunit.org/">NUnit</a>.  Whats not to love?  Mark a class as a fixture and mark your methods as a test.</p><div class="highlight"><pre><code><span class="na">[TestFixture]</span>
<span class="k">public</span> <span class="k">class</span> <span class="nc">TagTest</span>
<span class="p">{</span>
<span class="na">    [Test]</span>
    <span class="k">public</span> <span class="k">void</span> <span class="nf">TagNameNormalisationTest</span><span class="p">()</span>
    <span class="p">{</span>
        <span class="n">Model</span><span class="p">.</span><span class="n">Tag</span> <span class="n">tag</span> <span class="p">=</span> <span class="k">new</span> <span class="n">Model</span><span class="p">.</span><span class="n">Tag</span> <span class="p">{</span>  <span class="n">Name</span> <span class="p">=</span> <span class="s">"My Tag Name"</span> <span class="p">};</span>
        <span class="n">Assert</span><span class="p">.</span><span class="n">AreEqual</span><span class="p">(</span><span class="n">tag</span><span class="p">.</span><span class="n">NormalisedName</span><span class="p">,</span> <span class="s">"mytagname"</span><span class="p">);</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<p>No point in mentioning too much about NUnit as most people will know the ins and outs of it already.  It has VS integration (via <a href="http://testdriven.net/">TestDriven</a>) and a nice NUnit runner capable of watching changes to DLL's and automatically rerunning the test contained within.  One caveat around that is often NUnit hangs on to the <span class="caps">DLL</span> reference and requires a restart so Visual Studio can rebuild the <span class="caps">DLL</span> which kind of misses the point - but it doesn't happen too often.</p><p>The framework itself would lend itself to a bit of betterification through sugar such as <a href="http://should.codeplex.com/">Should and Should.Fluent</a> but it does what it says on the tin.</p><h2><a href="http://msdn.microsoft.com/en-us/library/ms182486.aspx">MSTest</a></h2><p>Another staple for many people.  I'd never really used it too much prior to doing this post because of the general negative opinion of it.  I must admit I kind of agree with some of these things.  It's pretty much the MS equivalent of NUnit (in terms of features and look and feel)</p><div class="highlight"><pre><code><span class="na">[TestClass]</span>
<span class="k">public</span> <span class="k">class</span> <span class="nc">TagTest</span>
<span class="p">{</span>
<span class="na">    [TestMethod]</span>
    <span class="k">public</span> <span class="k">void</span> <span class="nf">TagNameNormalisationTest</span><span class="p">()</span>
    <span class="p">{</span>
        <span class="n">Model</span><span class="p">.</span><span class="n">Tag</span> <span class="n">tag</span> <span class="p">=</span> <span class="k">new</span> <span class="n">Model</span><span class="p">.</span><span class="n">Tag</span> <span class="p">{</span> <span class="n">Name</span> <span class="p">=</span> <span class="s">"My Tag Name"</span> <span class="p">};</span>
        <span class="n">Assert</span><span class="p">.</span><span class="n">AreEqual</span><span class="p">(</span><span class="n">tag</span><span class="p">.</span><span class="n">NormalisedName</span><span class="p">,</span> <span class="s">"mytagname"</span><span class="p">);</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<p>So why do so many people prefer NUnit over MSTest?  Well I think the main problem is that it is <a href="http://www.richard-banks.org/2010/03/mstest-sucks-for-unit-tests.html">quite slow</a> in comparison (from Visual Studio at least) but my main gripe is the amount of <a href="http://en.wiktionary.org/wiki/flob">flob</a> it generates for each test run and configuration.  It even produces solution folders for holding test runs and other files. If I want you to create those I'll ask you - don't force it on me.  It also has a very limited set of Assertion methods much like NUnit.</p><p>Enough moaning - surely it must have some benefits?  Well yes it does of course.  The main benefit <span class="caps">IMHO</span> is that it has very nice integration into Visual Studio which offers some very clean feedback.  This is something that requires <a href="http://testdriven.net/">TestDriven</a> for NUNit and the other frameworks which may or may not be free depending on your situation.</p><h2><a href="http://nspec.org/">NSpec</a></h2><p>Not to be confused with the <a href="http://nspec.tigris.org/">first hit on Google</a> which seems to be pretty much a dead project <a href="http://nspec.org">NSpec</a> (.org) is an attempt to bring <a href="http://rspec.info">RSpec</a> into the .<span class="caps">NET</span> world.</p><div class="highlight"><pre><code><span class="k">class</span> <span class="nc">describe_Tag</span> <span class="p">:</span> <span class="n">nspec</span>
<span class="p">{</span>
    <span class="k">void</span> <span class="nf">when_setting_the_tag_name_to_My_Tag_Name</span><span class="p">()</span>
    <span class="p">{</span>
        <span class="n">before</span> <span class="p">=</span> <span class="p">()</span> <span class="p">=&gt;</span> <span class="n">_tag</span> <span class="p">=</span> <span class="k">new</span> <span class="n">Tag</span> <span class="p">{</span> <span class="n">Name</span> <span class="p">=</span> <span class="s">"My Tag Name"</span> <span class="p">};</span>
        <span class="n">it</span><span class="p">[</span><span class="s">"should normalise the tag name to mytagname"</span><span class="p">]</span> <span class="p">=</span> <span class="p">()</span> <span class="p">=&gt;</span> 
            <span class="n">_tag</span><span class="p">.</span><span class="n">NormalisedName</span><span class="p">.</span><span class="n">should_be</span><span class="p">(</span><span class="s">"mytagname"</span><span class="p">);</span>
    <span class="p">}</span>

    <span class="k">private</span> <span class="n">Tag</span> <span class="n">_tag</span><span class="p">;</span>
<span class="p">}</span>
</code></pre></div>
<p>This is somewhat similar to <a href="https://github.com/machine/machine.specifications">MSpec</a> I mentioned in the last post.  My bugbear with MSpec however was that it didn't feel natural to me.  The equivalent MSpec test for the above looks like this,</p><div class="highlight"><pre><code><span class="na">[Subject("Normalise Tag Name")]</span>
<span class="k">public</span> <span class="k">class</span> <span class="nc">when_a_tag_is_created</span>
<span class="p">{</span>
    <span class="k">static</span> <span class="n">Tag</span> <span class="n">_tag</span><span class="p">;</span>

    <span class="n">Establish</span> <span class="n">context</span> <span class="p">=</span> <span class="p">()</span> <span class="p">=&gt;</span>
        <span class="n">_tag</span> <span class="p">=</span> <span class="k">new</span> <span class="n">Tag</span><span class="p">();</span>

    <span class="n">Because</span> <span class="n">of</span> <span class="p">=</span> <span class="p">()</span> <span class="p">=&gt;</span>
        <span class="n">_tag</span><span class="p">.</span><span class="n">Name</span> <span class="p">=</span> <span class="s">"New Test Tag"</span><span class="p">;</span>

    <span class="n">It</span> <span class="n">should_have_a_normalised_name_of_newtesttag</span> <span class="p">=</span> <span class="p">()</span> <span class="p">=&gt;</span>
        <span class="n">_tag</span><span class="p">.</span><span class="n">NormalisedName</span><span class="p">.</span><span class="n">ShouldEqual</span><span class="p">(</span><span class="s">"newtesttag"</span><span class="p">);</span>      
<span class="p">}</span>
</code></pre></div>
<p>Maybe it's just me but it doesn't read as well as the NSpec version.  I even found it easier to write the NSpec version - more natural.</p><p>NSpec also has a lot of extra goodies.  Currently there is no integration into Visual Studio yet but the runner is simple to enough to spin up from the NuGet Package Manager Console and it has an added bonus of a file watcher.  <a href="http://nspec.org/continuoustesting">SpecWatchr</a> is similar to NUnit it can watch for changes and automatically run the specs.  It differs from NUnit in that it watches changes to the code files rather than the <span class="caps">DLL</span> - so need to wait for builds to happen etc.  Nice.</p><p>One minor gripe with it (bar the lack of VS support - even TestDriven support would be good) is the <code>should</code> syntax. I prefer the Should.Fluent style syntax <code>Should.Be()</code> rather than the current <code>should_be</code> syntax.  It's not Ruby and we should accept that :-P.</p><p>It's still quite early for NSpec and hopefully it keeps it's momentum as it would be one of my frameworks of choice.</p><h2><a href="https://github.com/robconery/Quixote">Quixote</a></h2><p>Quixote by Rob Conery follows on from his current slew of ultra lightweight solutions to common problems (like <a href="https://github.com/robconery/Massive">Massive</a> and <a href="https://github.com/robconery/Sugar">Sugar</a>).  Quixote blends the <span class="caps">HTML</span> reporting directly with the framework and you write tests right in a Razor file.  This is the definition of lightweight.</p><div class="highlight"><pre><code>@using Quixote;
@using CodeSlice.UnitTesting.Model;

<span class="nt">&lt;link</span> <span class="na">href=</span><span class="s">'@Url.Content("~/Styles/quixote.css")'</span> <span class="na">rel=</span><span class="s">"stylesheet"</span> <span class="na">type=</span><span class="s">"text/css"</span> <span class="nt">/&gt;</span>

@TheFollowing.Describes("Tags")
    @They.Should("Normalise the Tag Name", () =&gt; {
        return new Tag { Name = "Test Name" }.NormalisedName.ShouldEqual("testname");
    })
</code></pre></div>
<p>The obvious problem with this is that you don't get continuous integration or build support.  There is no need to rebuild anything just refresh your page and the tests re-run.  Still CI/Build integration would be nice for a framework.</p><h2><a href="http://pivotal.github.com/jasmine/">Jasmine</a></h2><p>Ah Jasmine.  Jasmine is what I wanted out of a JavaScript testing framework.  Rather than going the JSpec route of creating a preprocessed <acronym title="Domain Specific Language"><span class="caps">DSL</span></acronym> Jasmine makes use of JavaScripts dynamic nature and "functions as 1st class citizens" feature to create framework that behaves like RSpec but embraces JavaScript.</p><div class="highlight"><pre><code><span class="nx">describe</span><span class="p">(</span><span class="s1">'Tag'</span><span class="p">,</span> <span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
    <span class="kd">var</span> <span class="nx">tag</span><span class="p">;</span>

    <span class="nx">beforeEach</span><span class="p">(</span><span class="kd">function</span><span class="p">()</span> <span class="p">{</span>
        <span class="nx">tag</span> <span class="o">=</span> <span class="k">new</span> <span class="nx">Tag</span><span class="p">();</span>
    <span class="p">});</span>

    <span class="nx">it</span><span class="p">(</span><span class="s2">"should normalise a tag name"</span><span class="p">,</span> <span class="kd">function</span><span class="p">(){</span>
        <span class="nx">tag</span><span class="p">.</span><span class="nx">setName</span><span class="p">(</span><span class="s1">'A Tag Name'</span><span class="p">);</span>
        <span class="nx">expect</span><span class="p">(</span><span class="nx">tag</span><span class="p">.</span><span class="nx">getNormalisedName</span><span class="p">()).</span><span class="nx">toEqual</span><span class="p">(</span><span class="s1">'atagname'</span><span class="p">);</span>
    <span class="p">});</span>
<span class="p">});</span>
</code></pre></div>
<p>Out of the box you get an extensible framework (custom matchers etc), mocking and asynchronous support.  Ajax faking isn't directly available but I recommend using <a href="http://sinonjs.org">Sinon</a> for this as it has an amazing FakeServer object that is fully configurable.</p><p>Another big advantage of Jasmine is it's build integration.  Be it node.js, Java or Ruby you can integrate Jasmine specs into your build process.  .<span class="caps">NET</span> integration isn't available yet but <a href="http://jurassic.codeplex.com">Jurassic</a> would be a suitable host if anyone wants to make it happen (hint, hint :-P).</p><h2><a href="http://docs.jquery.com/Qunit">QUnit</a></h2><p>And finally QUnit.  QUnit is the NUnit/JUnit equivalent for the JavaScript world.  Well known and mature project with some very nice <span class="caps">HTML</span> output.</p><div class="highlight"><pre><code><span class="nx">module</span><span class="p">(</span><span class="s1">'Tag Model Validation'</span><span class="p">);</span>

<span class="nx">test</span><span class="p">(</span><span class="s1">'Tag name normalisation'</span><span class="p">,</span> <span class="kd">function</span><span class="p">(){</span>
    <span class="kd">var</span> <span class="nx">tag</span> <span class="o">=</span> <span class="k">new</span> <span class="nx">Tag</span><span class="p">({</span> <span class="nx">name</span><span class="o">:</span> <span class="s1">'A Tag Name'</span> <span class="p">}),</span>
        <span class="nx">normalisedName</span> <span class="o">=</span> <span class="nx">tag</span><span class="p">.</span><span class="nx">getNormalisedName</span><span class="p">();</span>

    <span class="nx">equals</span><span class="p">(</span><span class="s1">'atagname'</span><span class="p">,</span> <span class="nx">normalisedName</span><span class="p">);</span>
<span class="p">});</span>
</code></pre></div>
<p>Not much else to say on this one right now.... must be running out of steam.</p><h2>Summing Up</h2><p>And there you have it.  The first real post since the move and it's a bit of a waffly long one.  Anyways what would be my recommendations?  Well for .<span class="caps">NET</span> I'm torn.  Currently I'd say NUnit with Should.Fluent extensions.  This is a nice combo for creating simple tests that people can execute from within Visual Studio.  However NSpec has potential - I'd like to see it grow a bit more and I'll keep watching it.  Visual Studio integration would be aces though.  JavaScript on the other hand I have a clear cut winner.  Jasmine is awesome.  It's boosted my code quality and productivity no end on recent projects and with the help of <a href="http://sinonjs.org">Sinon</a> for mocking and controlling Ajax it'll be a long time before I am this happy with a .<span class="caps">NET</span> equivalent tech.  Fingers crossed for NSpec on that front.</p>