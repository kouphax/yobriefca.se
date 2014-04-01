---
date: 2011-04-03T23:00:00Z
title: A Tale of Unit Testing Technologies
published: true
categories: [Testing, .NET, JavaScript]
type: article
external: false
---
<p>I've had a big bad dirty secret for a while now.  One I've kept hidden from my friend and colleagues for years.  But now its time to come clean...... here goes..... hold onto your pants.</p><blockquote>&lt;pause for dramatic effect&gt;<br />I hardly ever write unit tests!<br />&lt;gasps&gt;</blockquote><p>I've come clean and it feels good.  Another reason why it feels good is that it is slowly but surely becoming a falsehood.  I've had my eyes opened recently to the art of BDD and discovered that I've simply been doing it wrong - I was so busy writing code first and then tests to fit my, possibly incorrect, assumptions that tests failed to hold any worthwhile value — time wasted in my mind.  Also when writing said tests I was too worried on the internal mechanics of the "unit" being tested and not about its intended behaviour — essentially missing the point and time wasted once more.  Being a man that has no time for, well, time wasting I feel I have come full circle on why I didn't test.  In the past at least....</p><p>But I digress.... Where was I?  Ah yes I've had my eyes opened - I've been doing it right.  Yep I've gone all TDD/BDD - write a failing test, write some code to make it work, make the test fail again and so on and so forth.  Its been a good experience.  I finally see the benefit in it.  Yes, it takes slightly longer and it's hard to break old habits but the amount of times I've been tripped up by assumptions and edge cases that would only normally be found during system testing after a frustrating debugging session with plenty of hair pulling and swearing.</p><p>Since the whole behaviour driven enlightenment I've been trying to find a suitable set of technologies that allow me to perfect my new found approach and turn me into a fast and effective code cutting machine.  NUnit is fine — it does the job but it's a task in itself to express what you want out of a test which slows things down.  No if you're going to do it right you want the best tools.  So I have decided to jump in head first and discover what testing tools are the most effective.  I've been through a few already , including,</p><ul><li>SpecFlow (C#)</li><li>MSpec (Machine.Specifications) (C#) </li><li>JSpec  (JavaScript)</li><li>Should/Should.Fluent (C#)</li></ul><p>Here are a few of my thoughts so far. </p><h2>SpecFlow (<a href="http://www.specflow.org/">http://www.specflow.org/</a>)</h2><p>SpecFlow is essentially the .NET equivalent of Cucumber (from the Ruby world).  Offering natural language syntax for defining scenarios that make up the behaviour of a feature.  Its better as an example,</p><p><div class="highlight"><pre><code><span class="k">Feature:</span><span class="nf"> Tags</span>

<span class="k">Scenario:</span><span class="nf"> Normalise Tag Name</span>
<span class="k">	Given </span><span class="nf">I have created a new tag</span>
<span class="nf">	</span><span class="k">When </span><span class="nf">I set its name to "</span><span class="s">New Test Tag</span><span class="nf">"</span>
<span class="nf">	</span><span class="k">Then </span><span class="nf">it should have a normalised name of "</span><span class="s">newtesttag</span><span class="nf">"</span>
</code></pre></div>
</p><p>So you see the test here is written in pretty much straight english.  It's easy to understand and this means even domain experts (aka "the business folk") can help write them.  So how does this end up being turned into executable tests.  Well, initially you need to do a bit of wiring up using Step Definitions.</p><p></p><div class="highlight"><pre><code><span class="na">[Binding]</span>
<span class="k">public</span> <span class="k">class</span> <span class="nc">TagSteps</span>
<span class="p">{</span>
    <span class="n">Model</span><span class="p">.</span><span class="n">Tag</span> <span class="n">_tag</span><span class="p">;</span>

<span class="na">    [Given(@"I have created a new tag")]</span>
    <span class="k">public</span> <span class="k">void</span> <span class="nf">GivenIHaveCreatedANewTag</span><span class="p">()</span>
    <span class="p">{</span>
        <span class="n">_tag</span> <span class="p">=</span> <span class="k">new</span> <span class="n">Model</span><span class="p">.</span><span class="n">Tag</span><span class="p">();</span>    
    <span class="p">}</span>

<span class="na">    [When(@"I set its name to ""(.*)""")]</span>
    <span class="k">public</span> <span class="k">void</span> <span class="nf">WhenISetItsNameToNewTestTag</span><span class="p">(</span><span class="kt">string</span> <span class="n">name</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="n">_tag</span><span class="p">.</span><span class="n">Name</span> <span class="p">=</span> <span class="n">name</span><span class="p">;</span>
    <span class="p">}</span>

<span class="na">    [Then(@"it should have a normalised name of ""(.*)""")]</span>
    <span class="k">public</span> <span class="k">void</span> <span class="nf">ThenItShouldHaveANormalisedNameOfNewtestag</span><span class="p">(</span><span class="kt">string</span> <span class="n">normalisedname</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="n">_tag</span><span class="p">.</span><span class="n">NormalisedName</span><span class="p">.</span><span class="n">Should</span><span class="p">().</span><span class="n">Equal</span><span class="p">(</span><span class="n">normalisedname</span><span class="p">);</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<p>As you can see we can match up each line of the feature file with an appropriate executable action.  We even have the power of regular expressions at our disposal - allowing us to reuse steps and build up a decent library that comes with us across projects.  Some very very powerful stuff here — we can get everyone involved in writing these things,</p><ul><li>Developers and business users during design</li><li>Developers during development</li><li>Testers during system test — in fact why not write a feature as the "Steps to reproduce"?  Win, win!</li></ul><p>The thing is...... well.... Technically speaking SpecFlow isn't really aimed at the fine grained, unit test level of testing.  It's more about the higher level integration testing.  I mean it's useable but it does become a bit awkward to express things in a reusable manner.  The problem with that is things start feeling quite heavy weight and you need to do a lot of extra work to get them to fit.  No I think, while SpecFlow is great for many things (automated testing, integration testing, system testing etc.) it's not the best fit for what I am looking for in this article - unit testing tools.</p><h2>MSpec (<a href="https://github.com/machine/machine.specifications">https://github.com/machine/machine.specifications</a>)</h2><p>Machine.Specifications (MSpec for short) is a Context/Specification framework geared towards 	removing language noise and simplifying tests. </p><p>Thats the official intention and I must admit I was initially taken by it.  Rather than having a single huge class filled with methods representing tests MSpec takes the approach that a single class represents a single scenario and uses lambda expressions to offer the BDD style syntax (Because/It/Subject etc.).  So lets take the Tag scenario described above and convert it to MSpec format,</p><p></p><div class="highlight"><pre><code><span class="na">[Subject("Normalise Tag Name")]</span>
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
<p>It's quite simple and it really forces you to ensure your tests are as simple as possible.  To be honest having used it on a project I'm not 100% sold.  I think its down to my personal coding style.  I honestly think while it reduces line count it doesn't necessarily reduce language noise.  Also having used on a project I found it quite awkward to write — the style is different to what I am used to and I guess with training that would change.  As I say this is a personal opinion and I am happy to be shown a better approach.</p><h2>JSpec (<a href="http://visionmedia.github.com/jspec/">http://visionmedia.github.com/jspec/</a>)</h2><p>JSpec is a clever little JavaScript testing framework.  I's no longer supported by it's creators (booooo!) but honestly its stable enough to use anyway.  JSpec takes the RSpec DSL (a decent subset at least) and is capable of converting it to JavaScript for execution in the browser.  It's got a heap of stuff in it,</p><ul><li>Mock Ajax</li><li>Stubbing</li><li>Fake timers</li></ul><p>Lets take a look at a simple example,</p><p></p><div class="highlight"><pre><code><span class="nx">describe</span> <span class="s2">"Utils.Arrays.compare method"</span>    
    <span class="nx">it</span> <span class="s2">"should compare and sort 2 numeric arrays successfully"</span>
        <span class="nx">arr1</span> <span class="o">=</span> <span class="p">[</span><span class="mi">1</span><span class="p">,</span><span class="mi">2</span><span class="p">,</span><span class="mi">3</span><span class="p">]</span>
        <span class="nx">arr2</span> <span class="o">=</span> <span class="p">[</span><span class="mi">3</span><span class="p">,</span><span class="mi">1</span><span class="p">,</span><span class="mi">2</span><span class="p">]</span>
                
        <span class="nx">Utils</span><span class="p">.</span><span class="nx">Arrays</span><span class="p">.</span><span class="nx">compare</span><span class="p">(</span><span class="nx">arr1</span><span class="p">,</span> <span class="nx">arr2</span><span class="p">,</span> <span class="kc">true</span><span class="p">).</span><span class="nx">should</span><span class="p">.</span><span class="nx">be</span> <span class="kc">true</span>        
    <span class="nx">end</span>            
    
    <span class="nx">it</span> <span class="s2">"should compare 2 numeric arrays unsuccesfully when not sorted"</span>
        <span class="nx">arr1</span> <span class="o">=</span> <span class="p">[</span><span class="mi">1</span><span class="p">,</span><span class="mi">2</span><span class="p">,</span><span class="mi">3</span><span class="p">]</span>
        <span class="nx">arr2</span> <span class="o">=</span> <span class="p">[</span><span class="mi">3</span><span class="p">,</span><span class="mi">1</span><span class="p">,</span><span class="mi">2</span><span class="p">]</span>
                
        <span class="nx">Utils</span><span class="p">.</span><span class="nx">Arrays</span><span class="p">.</span><span class="nx">compare</span><span class="p">(</span><span class="nx">arr1</span><span class="p">,</span> <span class="nx">arr2</span><span class="p">).</span><span class="nx">should</span><span class="p">.</span><span class="nx">be</span> <span class="kc">false</span>        
        <span class="nx">Utils</span><span class="p">.</span><span class="nx">Arrays</span><span class="p">.</span><span class="nx">compare</span><span class="p">(</span><span class="nx">arr1</span><span class="p">,</span> <span class="nx">arr2</span><span class="p">,</span> <span class="kc">false</span><span class="p">).</span><span class="nx">should</span><span class="p">.</span><span class="nx">be</span> <span class="kc">false</span>        
    <span class="nx">end</span>        
<span class="nx">end</span>
</code></pre></div>
<p>People who have used RSpec before will feel right at home.  People who haven't should be able to understand exactly what is going on.  Pretty - right?  I thought so.  For the people who think — "ugh we don't need another dialect/language" - wise up!  Out of all the testing techs I've used recently this one has been the most successful and the output it generates is nice and clean.</p><h2>Should/Should.Fluent (<a href="http://should.codeplex.com/">http://should.codeplex.com/</a>)</h2><p>This little gem isn't a framework in itself and can be used with any framework you care to use.  Should provides a more expressive way of stating assertions in your code by making the code closer to natural language using extensions methods and nicer method names.  Example I hear you say?  Why certainly sirs and madams,</p><p></p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">void</span> <span class="nf">Should_fluent_assertions</span><span class="p">()</span>
<span class="p">{</span>
    <span class="kt">object</span> <span class="n">obj</span> <span class="p">=</span> <span class="k">null</span><span class="p">;</span>
    <span class="n">obj</span><span class="p">.</span><span class="n">Should</span><span class="p">().</span><span class="n">Be</span><span class="p">.</span><span class="n">Null</span><span class="p">();</span>

    <span class="n">obj</span> <span class="p">=</span> <span class="k">new</span> <span class="kt">object</span><span class="p">();</span>
    <span class="n">obj</span><span class="p">.</span><span class="n">Should</span><span class="p">().</span><span class="n">Be</span><span class="p">.</span><span class="n">OfType</span><span class="p">(</span><span class="k">typeof</span><span class="p">(</span><span class="kt">object</span><span class="p">));</span>
    <span class="n">obj</span><span class="p">.</span><span class="n">Should</span><span class="p">().</span><span class="n">Equal</span><span class="p">(</span><span class="n">obj</span><span class="p">);</span>
    <span class="n">obj</span><span class="p">.</span><span class="n">Should</span><span class="p">().</span><span class="n">Not</span><span class="p">.</span><span class="n">Be</span><span class="p">.</span><span class="n">Null</span><span class="p">();</span>
    <span class="n">obj</span><span class="p">.</span><span class="n">Should</span><span class="p">().</span><span class="n">Not</span><span class="p">.</span><span class="n">Be</span><span class="p">.</span><span class="n">SameAs</span><span class="p">(</span><span class="k">new</span> <span class="kt">object</span><span class="p">());</span>
    <span class="n">obj</span><span class="p">.</span><span class="n">Should</span><span class="p">().</span><span class="n">Not</span><span class="p">.</span><span class="n">Be</span><span class="p">.</span><span class="n">OfType</span><span class="p">&lt;</span><span class="kt">string</span><span class="p">&gt;();</span>
    <span class="n">obj</span><span class="p">.</span><span class="n">Should</span><span class="p">().</span><span class="n">Not</span><span class="p">.</span><span class="n">Equal</span><span class="p">(</span><span class="s">"foo"</span><span class="p">);</span>

    <span class="n">obj</span> <span class="p">=</span> <span class="s">"x"</span><span class="p">;</span>
    <span class="n">obj</span><span class="p">.</span><span class="n">Should</span><span class="p">().</span><span class="n">Not</span><span class="p">.</span><span class="n">Be</span><span class="p">.</span><span class="n">InRange</span><span class="p">(</span><span class="s">"y"</span><span class="p">,</span> <span class="s">"z"</span><span class="p">);</span>
    <span class="n">obj</span><span class="p">.</span><span class="n">Should</span><span class="p">().</span><span class="n">Be</span><span class="p">.</span><span class="n">InRange</span><span class="p">(</span><span class="s">"a"</span><span class="p">,</span> <span class="s">"z"</span><span class="p">);</span>
    <span class="n">obj</span><span class="p">.</span><span class="n">Should</span><span class="p">().</span><span class="n">Be</span><span class="p">.</span><span class="n">SameAs</span><span class="p">(</span><span class="s">"x"</span><span class="p">);</span>

    <span class="s">"This String"</span><span class="p">.</span><span class="n">Should</span><span class="p">().</span><span class="n">Contain</span><span class="p">(</span><span class="s">"This"</span><span class="p">);</span>
    <span class="s">"This String"</span><span class="p">.</span><span class="n">Should</span><span class="p">().</span><span class="n">Not</span><span class="p">.</span><span class="n">Be</span><span class="p">.</span><span class="n">Empty</span><span class="p">();</span>
    <span class="s">"This String"</span><span class="p">.</span><span class="n">Should</span><span class="p">().</span><span class="n">Not</span><span class="p">.</span><span class="n">Contain</span><span class="p">(</span><span class="s">"foobar"</span><span class="p">);</span>

    <span class="k">false</span><span class="p">.</span><span class="n">Should</span><span class="p">().</span><span class="n">Be</span><span class="p">.</span><span class="n">False</span><span class="p">();</span>
    <span class="k">true</span><span class="p">.</span><span class="n">Should</span><span class="p">().</span><span class="n">Be</span><span class="p">.</span><span class="n">True</span><span class="p">();</span>

    <span class="kt">var</span> <span class="n">list</span> <span class="p">=</span> <span class="k">new</span> <span class="n">List</span><span class="p">&lt;</span><span class="kt">object</span><span class="p">&gt;();</span>
    <span class="n">list</span><span class="p">.</span><span class="n">Should</span><span class="p">().</span><span class="n">Count</span><span class="p">.</span><span class="n">Zero</span><span class="p">();</span>
    <span class="n">list</span><span class="p">.</span><span class="n">Should</span><span class="p">().</span><span class="n">Not</span><span class="p">.</span><span class="n">Contain</span><span class="p">.</span><span class="n">Item</span><span class="p">(</span><span class="k">new</span> <span class="kt">object</span><span class="p">());</span>

    <span class="kt">var</span> <span class="n">item</span> <span class="p">=</span> <span class="k">new</span> <span class="kt">object</span><span class="p">();</span>
    <span class="n">list</span><span class="p">.</span><span class="n">Add</span><span class="p">(</span><span class="n">item</span><span class="p">);</span>
    <span class="n">list</span><span class="p">.</span><span class="n">Should</span><span class="p">().</span><span class="n">Not</span><span class="p">.</span><span class="n">Be</span><span class="p">.</span><span class="n">Empty</span><span class="p">();</span>
    <span class="n">list</span><span class="p">.</span><span class="n">Should</span><span class="p">().</span><span class="n">Contain</span><span class="p">.</span><span class="n">Item</span><span class="p">(</span><span class="n">item</span><span class="p">);</span>
<span class="p">};</span>
</code></pre></div>
<p>I stole this one from the Should Codeplex site (linked above) and it makes use of the fluent syntax (optional).  OK technically it doesn't do much but it really helps when trying to express assertions in your tests.  Highly recommend this one.</p><h2>Conclusion</h2><p>Thats the first lot of technologies covered but there are still plenty out there.  I think my next port of call is to actually spin up IronRuby and get RSpec involved.  After all it is the marker by which I am comparing these things so why did I not jump on it first of all?  Perhaps I like to build up suspense :-P</p><p>Also worth pointing out is that I haven't touched upon technologies for mocking and stubbing - that is for another time.</p><p>As always heap criticism my way and I'll happily fight my corner and stubbornly refuse to back down :-P (second smilie within a few paragraphs time to end this post).</p><p>UPDATE:  I've pushed some of my code to GitHub and intend to expand on this using the various other technologies.  The repository can be found on my <a href="https://github.com/kouphax/unit-testing">repo on GitHub</a></p>