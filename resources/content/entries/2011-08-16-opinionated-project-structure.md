---
date: 2011-08-15T23:00:00Z
title: An Opinionated Project Structure
published: true
categories: [.NET]
type: article
external: false
---
<p>Choice and flexibility are good, in fact they a super smashing awesome.  Sort of.  I mean if you go over the top and offer people "all the choices" they are going to get all stressed and probably procrastinate until someone else makes the decision for them.  Also if you give them so much flexibility they will probably do things differently each time - again that is not a great thing either - for many things in life and IT you shouldn't have to make a conscious decision (it's probably waste of time in this case).</p><p>I've recently been reviewing my companies default project structure and it's full of choice and flexibility and while this can cater for the vast array of different project types in our company it also imposes a certain level of cognitive-tax prior to kicking off any development to get the solution in a position that can be farmed out to the team.  It imposes this tax across <span class="caps">ALL</span> projects even the 99% of the time typical project, with their typical project structure.  It's not a costly tax but it's unnecessary.  Why not just push it onto the non-standard projects who will likely need to spend time setting things up correctly anyway?  Suck it, weird projects!  Thats what I say.</p><p>To this end I've been trying to build a fairly opinionated project structure (along with some handy tools during the development stage).  Right now it's in the early stages and open to abuse/commentary (in fact I'd welcome it, please) but I thought it makes sense to share it<sup id="fnr1" class="footnote"><a href="#fn1">1</a></sup> - for the benefit of myself and others (or selfishly unselfish as I like to call it).</p><h1>Folder Structure</h1><p>I didn't want to break from tradition here.  There is exists a fairly standard project structure that is common across many languages and environments and rather than create a clever, verbose structure I reckon it's probably best to stick with the tried and tested approach.  That way new developers, regardless of background, shouldn't have to wrestle with heavily nested folders or obscure names for folders (SolutionSource vs src) when navigating our solution.</p><p class="minimal-gist"></p><div class="highlight"><pre><code>|_ &lt;MySolution&gt;
    |_ &lt;lib&gt;
   |_ repositories.config
    |_ &lt;src&gt;
    |_ &lt;test&gt;
 |_ .gitignore   
  |_ default.ps1  
  |_ MySolution.sln
 |_ nuget.config
</code></pre></div>
<p>Most of this will be fairly self-explanatory<sup id="fnr2" class="footnote"><a href="#fn2">2</a></sup> but whats the harm in a little clarification?</p><ul>	<li><code>lib</code> this is the place either myself or Nuget (see nuget.config and default.ps1) will dump any DLLs, tools or other dependencies required by the solution.  As I will talk about later the only file I ever check in here is the repositories.config and have nuget resolve the rest (still some decisions need made around manually referenced files etc.).</li>	<li><code>src</code> hold all the projects that are actually going to be released as part of the final solution.  This folder doesn't contain any of the test projects you create for the solution - no point in muddyinbg the waters.</li>	<li><code>test</code> holds all the solutions projects that we create for running tests (unit, integration etc) to validate the solution.</li></ul><h1>Special Files</h1><p>My default project comes with a number of files that help make developing and building the solution form scratch as easy as it should be.  These files are open to be tweaked to suit the needs of the project as and when necessary.</p><h2>repositories.config</h2><p>This is a file that is used by <a href="http://nuget.org">Nuget</a> to resolve the location of all the package configuration files associated with the projects in a solution (packages.config).  When I start this file is empty but as dependencies are added via <a href="http://nuget.org">Nuget</a> this file is updated to the location of packages.config files within each project (which in turn is used to resolved the necessary <a href="http://nuget.org">Nuget</a> packages that need to be downloaded).  I include this file along with a download task in my <a href="https://github.com/JamesKovacs/psake">psake</a> build file (see later) so that we we don't have to check-in all those, potentially large, dlls and tools into our <acronym title="Version Control System"><span class="caps">VCS</span></acronym> - it's slow and annoying.  As a bonus when using some VCS's (e.g. git) this file will ensure the lib folder is committed to source control even thought it is technically empty - keeping our project structure intact.</p><h2>.gitignore</h2><p>Used to ignore certain generated and user specific files when using the git version control system.  Keeps things nice and clean and reduces annoying unnecessary conflicts.  As with most solutions out there I ignore the following patterns by default,</p><p class="minimal-gist"></p><div class="highlight"><pre><code>bin
obj
*.suo
*.csproj.user
*.cache
lib/*/
</code></pre></div>
<h2>nuget.config</h2><p>I'll jump onto the meat of the files, default.ps1, shortly but first as you can probably guess by now I assume the use of <a href="http://nuget.org">Nuget</a>.  It's there, it's great and thats all I want to say on that matter.  <code>nuget.config</code> is a simple file that tells <a href="http://nuget.org">Nuget</a> (even within Visual Studio) to put all it's downloaded files into the lib directory rather than the default <code>packages</code> folder.  Everyone understands what <code>lib</code> is, less people - packages.</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="p">&lt;</span><span class="n">settings</span><span class="p">&gt;</span>
 <span class="p">&lt;</span><span class="n">repositoryPath</span><span class="p">&gt;</span><span class="n">lib</span><span class="p">&lt;/</span><span class="n">repositoryPath</span><span class="p">&gt;</span>
<span class="p">&lt;/</span><span class="n">settings</span><span class="p">&gt;</span>
</code></pre></div>
<h2>default.ps1</h2><p>I've decided to run with <a href="https://github.com/JamesKovacs/psake">psake</a> to coordinate and perform my builds.  MSBuild, while useful, is ridden with sickeness I call <span class="caps">XML</span> (<span class="caps">BTW</span> everyone else calls it <span class="caps">XML</span> too <span class="caps">AFAIK</span>).  It's a beast to get right.  <a href="https://github.com/JamesKovacs/psake">Psake</a> on the other hand is pure script.  It's more succinct and you can even debug it - how awesome is that?  So <code>default.ps1</code> is my <a href="https://github.com/JamesKovacs/psake">psake</a> based build file.  What does it look like currently?  Glad you asked.....</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="c"># --</span>
<span class="c">#                     S C R I P T   P R O P E R T I E S </span>
<span class="c"># --</span>
<span class="n">Properties</span> <span class="p">{</span>
 <span class="nv">$solution</span> <span class="p">=</span> <span class="p">(</span><span class="nb">Get-ChildItem</span> <span class="p">*.</span><span class="n">sln</span><span class="p">).</span><span class="n">Name</span>
  <span class="nv">$solutionname</span> <span class="p">=</span> <span class="nv">$solution</span><span class="p">.</span><span class="n">Substring</span><span class="p">(</span><span class="n">0</span><span class="p">,</span> <span class="nv">$solution</span><span class="p">.</span><span class="n">LastIndexOf</span><span class="p">(</span><span class="s1">'.'</span><span class="p">))</span>
<span class="p">}</span>
</code></pre></div>
<p>I assume that the majority of projects are based on a single solution held at the root of our folder structure (as described above).  So I dynamically grab the name and relative path of the solution file for use later.</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="c"># --</span>
<span class="c">#                      H E L P E R   F U N C T I O N S</span>
<span class="c"># --</span>
<span class="k">function</span> <span class="nb">Write-LineBreak</span><span class="p">(){</span>
  <span class="nb">Write-Host</span> <span class="s2">"-"</span>
<span class="p">}</span>
</code></pre></div>
<p>Set of helper functions currently only provides a simple function to print out a flowerboxing line to make output a bit cleaner.</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="c"># Set the default framework (3.5 by default)</span>
<span class="nv">$framework</span> <span class="p">=</span> <span class="s1">'4.0'</span>
</code></pre></div>
<p>Assume that we are all living in the present (as much as possible) so switch to .<span class="caps">NET</span> 4.</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="c"># Declare default task</span>
<span class="n">Task</span> <span class="k">Default</span> <span class="n">-depends</span> <span class="n">Build</span><span class="p">,</span><span class="n">Test</span>
</code></pre></div>
<p>By default <a href="https://github.com/JamesKovacs/psake">psake</a> should do a <code>Build</code> and <code>Test</code>.</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="c"># Resolves all the requires nuget dependecies so they don't need checked in</span>
<span class="n">Task</span> <span class="n">Resolve</span> <span class="p">{</span>
  <span class="nb">Push-Location</span> <span class="s1">'.\lib\'</span>
  <span class="nb">Get-Content</span> <span class="s1">'.\repositories.config'</span> <span class="p">|</span> <span class="k">foreach</span> <span class="p">{</span>    
 <span class="k">if</span><span class="p">(</span><span class="nv">$_</span> <span class="o">-match</span> <span class="s1">'path="(.*)"'</span><span class="p">){</span>
  
      <span class="nb">Write-LineBreak</span>
     <span class="nb">Write-Host</span> <span class="s2">"Resolving Dependencies for "</span> <span class="n">-nonewline</span>
     <span class="nb">Write-Host</span> <span class="nv">$matches</span><span class="p">[</span><span class="n">1</span><span class="p">].</span><span class="n">Split</span><span class="p">(</span><span class="s1">'\'</span><span class="p">)[-</span><span class="n">2</span><span class="p">]</span> <span class="n">-ForegroundColor</span> <span class="n">Green</span>
      <span class="nb">Write-LineBreak</span>
 
      <span class="n">nuget</span> <span class="n">install</span> <span class="nv">$matches</span><span class="p">[</span><span class="n">1</span><span class="p">]</span>
   
      <span class="nb">Write-LineBreak</span>
 <span class="p">}</span>
  <span class="p">}</span>
  <span class="nb">Pop-Location</span>
<span class="p">}</span>
</code></pre></div>
<p>Resolve is the function mentioned earlier that finds the location of the repositories.config files and asks <a href="http://nuget.org">Nuget</a> to resolve all the external dependencies of the solution.  This should be done when you have pulled from a repository that included new dependencies.</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="c"># Build the first solution file you find</span>
<span class="n">Task</span> <span class="n">Build</span> <span class="p">{</span>
  
  <span class="nb">Write-LineBreak</span>
  <span class="nb">Write-Host</span> <span class="s2">"Building Solution "</span> <span class="n">-nonewline</span>
  <span class="nb">Write-Host</span> <span class="nv">$solutionname</span> <span class="n">-ForegroundColor</span> <span class="n">Green</span>
  <span class="nb">Write-LineBreak</span>
      
  <span class="n">Exec</span> <span class="p">{</span> <span class="n">msbuild</span>   <span class="p">/</span><span class="n">v</span><span class="err">:</span><span class="n">quiet</span> <span class="p">/</span><span class="n">t</span><span class="err">:</span><span class="n">Rebuild</span>  <span class="p">}</span>
  
  <span class="nb">Write-LineBreak</span>
<span class="p">}</span>
</code></pre></div>
<p>Performs a basic, quiet build of the default solution.</p><p class="minimal-gist"><div class="highlight"><pre><code><span class="c"># Run the project tests</span>
<span class="n">Task</span> <span class="n">Test</span> <span class="p">{</span>
<span class="p">}</span>
</code></pre></div>
</p><p>Executes all the tests in the solution.  Currently empty I intend to flesh this out with a default unit testing approach.</p><h1>Baby Steps</h1><p>So this is just the beginning and I hope this evolves based on observations made in our real world projects rather than a bunch of nice to haves.  It will also be nice to automate the creation of projects and solutions but thats certainly for another time.  Recommendations, comments and all that stuff welcome.  I'll try and get this basic structure up on Github too.</p><p id="fn1" class="footnote"><a href="#fnr1"><sup>1</sup></a> I mean, it has been a while since I blogged so it's about time I get back to it.  That said this isn't a "filler post".  I'm not doing this under some random contract.  Thought I wish I was - imagine that - how fun.</p><p id="fn2" class="footnote"><a href="#fnr2"><sup>2</sup></a> Cracker idea, right?  Who'd have thought it??! I'm a total genius and you can bow at my feet at will please.  kthnxbai.</p><p id="fn3" class="footnote"><a href="#fnr3"><sup>3</sup></a> All subfolders in lib - as mentioned in the previous section.</p>