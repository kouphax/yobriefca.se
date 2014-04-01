---
date: 2011-02-11T00:00:00Z
title: 'Entity Framework: Code First - Head First'
published: true
categories: [.NET]
type: article
external: false
---
<p>I find the data access layer on most projects to be either overly complex or  fiddly with lots of XML mapping files that are difficult to debug so anything  that could make this layer more developer friendly I'm all for it.  Though I  also want to point out that I understand why the DAL is often complex - there is  a lot to consider and so I want to also understand if these "friendlier"  technologies can handle that sort of complexity or if they simply make the happy  path easier but making the more complex scenarios more difficult or even  impossible (which is obviously a blocker).</p><p>So I've been tinkering with this new Entity Framework CTP5 release and the  "Code First" features recently.  This comes after some time-out from EF due to  some really bad experiences with EF1. I was promised that there has been  significant changes/improvements since I last dabbled and it really seems there  have been.  So I wanted to put it to the test and as one of my co-workers wanted  an "Ideas" app I thought it would be a fun [may not be anyone's definition of  fun but my own] to throw an MVC app together using EF "Code First" to model my  domain entities.  To make it all even more simple I went ahead and used SQL  Server CE 4 for persistence.  So what did the solution need to do?  The basic  requirements were,</p><ol><li>Use Windows Authentication for users </li><li>Allow users to submit an idea (Title, Description) </li><li>Allow users to tag an idea with a variable number of tags </li><li>Allow users to vote up or vote down ideas (but not their own) </li><li>Allow users to comment on ideas </li><li>Allow users to filter ideas by tags </li><li>Allow users to sort ideas by newest ideas or by most popular. </li></ol><p>Nothing too extreme involved - not unless you turn the whole thing into a  computer game style EXTREME speed run competition - man vs. machine - the  ULTIMATE [typing] battle.... with bathroom and snack breaks!  Just to make it even  more INSANE I documented my steps and created a graphical timeline of the  session in a PRETTY timeline. </p><p class="img-holder"><img src="http://farm3.static.flickr.com/2244/5720966217_edd48fc966_o.png" width="500" /></p><p>Ammmm don't mean to be rude but your jaw.... we'll it's on the floor.  Can you  pick it up please?  17:21 to 20:38 minus about an hour and a bit for bathroom,  snack and chat breaks - zero to datafied in less than 3 hours!  Few points to  note,</p><ul><li>This experiment focused on the data model, EF CTP5 and the database. </li><li>There is a working UI (MVC3) it's just not exactly pretty </li><li>I had no EF "Code First" experience before hand </li><li>I could be doing a few things incorrectly </li><li>It'll probably take me longer to write this post than it did the app. </li></ul><p>So lets look at what I produced.  The source is <a href="https://github.com/kouphax/ideas/">available on Github</a>* for  your fiddling pleasure.</p><p>I am not going to dive into the whole MVC part of it as the source is  available but I may touch on some of the interface points such as controllers  and binders.</p><h2>The Domain Models</h2><p>Lets take a high level look at our domain models.</p><p class="img-holder"><img src="http://farm4.static.flickr.com/3554/5721525430_5e527060d2_o.png" width="650" /></p><h3>DomainEntity</h3><p>The abstract domain entity is used to prevent me having to repeat common  auditing and database related stuff across all my entities.  It is not mandatory  or derived from anything related to Entity Framework - all these classes are  simple POCO's.  DomainEntity sets up the entities primary key using the Key  attribute and also gold 2 audit related properties CreatedBy and  CreatedDate.</p><p></p><div class="highlight"><pre><code><span class="c1">/// &lt;summary&gt;</span>
<span class="c1">/// Base class for domain entities responsible for holding auditing and </span>
<span class="c1">/// persistence related properties</span>
<span class="c1">/// &lt;/summary&gt;</span>
<span class="k">public</span> <span class="k">abstract</span> <span class="k">class</span> <span class="nc">DomainEntity</span>
<span class="p">{</span>
<span class="na">    [Key]</span>
    <span class="k">public</span> <span class="kt">int</span> <span class="n">Id</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>

<span class="na">    [Required]</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">CreatedBy</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>

<span class="na">    [Required]</span>
    <span class="k">public</span> <span class="n">DateTime</span> <span class="n">CreatedDate</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<h3>Idea</h3><p>Idea is our principle class in our domain.  As you can see there are various  associations set up between the other classes.  2 1-* mappings between Comment  and Vote and a *-* mapping between itself and tag (a tag can exist for any  number of ideas and an idea can have many tags).  It also holds a number of  methods related to business logic - specifically calculating Votes, number of  Comments etc.</p><p></p><div class="highlight"><pre><code><span class="c1">/// &lt;summary&gt;</span>
<span class="c1">/// Main domain object in the idea solution.  Represents an idea</span>
<span class="c1">/// &lt;/summary&gt;</span>
<span class="k">public</span> <span class="k">class</span> <span class="nc">Idea</span> <span class="p">:</span> <span class="n">DomainEntity</span>
<span class="p">{</span>
<span class="na">    [Required]</span>
<span class="na">    [MaxLength(255)]</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">Title</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>

<span class="na">    [Required]</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">Description</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>

<span class="na">    [DefaultValue(false)]</span>
    <span class="k">public</span> <span class="kt">bool</span> <span class="n">IsRejected</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>

    <span class="cp">#region Associations</span>
    <span class="k">public</span> <span class="k">virtual</span> <span class="n">ICollection</span><span class="p">&lt;</span><span class="n">Comment</span><span class="p">&gt;</span> <span class="n">Comments</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="k">public</span> <span class="k">virtual</span> <span class="n">ICollection</span><span class="p">&lt;</span><span class="n">Tag</span><span class="p">&gt;</span> <span class="n">Tags</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="k">public</span> <span class="k">virtual</span> <span class="n">ICollection</span><span class="p">&lt;</span><span class="n">Vote</span><span class="p">&gt;</span> <span class="n">Votes</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="cp">#endregion</span>

    <span class="cp">#region Business Logic</span>
    <span class="k">public</span> <span class="k">virtual</span> <span class="kt">int</span> <span class="n">Score</span>
    <span class="p">{</span>
        <span class="k">get</span> <span class="p">{</span> <span class="k">return</span> <span class="n">Votes</span> <span class="p">==</span> <span class="k">null</span> <span class="p">?</span> <span class="m">0</span> <span class="p">:</span> <span class="n">Votes</span><span class="p">.</span><span class="n">Sum</span><span class="p">(</span><span class="n">v</span> <span class="p">=&gt;</span> <span class="n">v</span><span class="p">.</span><span class="n">Value</span><span class="p">);</span> <span class="p">}</span>
    <span class="p">}</span>

    <span class="k">public</span> <span class="k">virtual</span> <span class="kt">int</span> <span class="n">VoteCount</span>
    <span class="p">{</span>
        <span class="k">get</span> <span class="p">{</span> <span class="k">return</span> <span class="n">Votes</span> <span class="p">==</span> <span class="k">null</span> <span class="p">?</span> <span class="m">0</span> <span class="p">:</span> <span class="n">Votes</span><span class="p">.</span><span class="n">Count</span><span class="p">;</span> <span class="p">}</span>
    <span class="p">}</span>

    <span class="k">public</span> <span class="k">virtual</span> <span class="kt">int</span> <span class="n">CommentCount</span>
    <span class="p">{</span>
        <span class="k">get</span> <span class="p">{</span> <span class="k">return</span> <span class="n">Comments</span> <span class="p">==</span> <span class="k">null</span> <span class="p">?</span> <span class="m">0</span> <span class="p">:</span> <span class="n">Comments</span><span class="p">.</span><span class="n">Count</span><span class="p">;</span> <span class="p">}</span>
    <span class="p">}</span>
    <span class="cp">#endregion</span>
<span class="p">}</span>
</code></pre></div>
<h3>Tag</h3><p>Tag is pretty simple.  The only interesting thing about it is the use of  NormalisedName - essentially the name field lowercased and whitespace removed.   This is used when attempting to fetch potentially existing tags from the  database.</p><p><div class="highlight"><pre><code><span class="c1">/// &lt;summary&gt;</span>
<span class="c1">/// Represents a tag in the idea solutuion</span>
<span class="c1">/// &lt;/summary&gt;</span>
<span class="k">public</span> <span class="k">class</span> <span class="nc">Tag</span> <span class="p">:</span> <span class="n">DomainEntity</span>
<span class="p">{</span>
    <span class="k">private</span> <span class="kt">string</span> <span class="n">_name</span><span class="p">;</span>
<span class="na">        </span>
<span class="na">    [Required]</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">Name</span> 
    <span class="p">{</span>
        <span class="k">get</span>
        <span class="p">{</span>
            <span class="k">return</span> <span class="n">_name</span><span class="p">;</span>
        <span class="p">}</span>
        <span class="k">set</span>
        <span class="p">{</span>
            <span class="n">_name</span> <span class="p">=</span> <span class="k">value</span><span class="p">;</span>
            <span class="n">NormalisedName</span> <span class="p">=</span> <span class="k">value</span><span class="p">.</span><span class="n">ToLower</span><span class="p">().</span><span class="n">Replace</span><span class="p">(</span><span class="s">" "</span><span class="p">,</span> <span class="kt">string</span><span class="p">.</span><span class="n">Empty</span><span class="p">);</span>
        <span class="p">}</span>
    <span class="p">}</span>

<span class="na">    [Required]</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">NormalisedName</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>

    <span class="cp">#region Associations</span>
    <span class="k">public</span> <span class="k">virtual</span> <span class="n">ICollection</span><span class="p">&lt;</span><span class="n">Idea</span><span class="p">&gt;</span> <span class="n">Ideas</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="cp">#endregion</span>
<span class="p">}</span>
</code></pre></div>
</p><h3>Vote</h3><p>Rather than just store a calculated value against an idea the Vote object  represents a rich representation of a Vote (either up or down, whom by and  when).  This allows us to provide extra validation when we need it.  For example  people not allowed to vote on their own idea or vote on an idea in any  particular direction more than once.  Having this rich association makes these  things much easier and we aren't forced to create custom objects to track this  sort of thing.</p><p></p><div class="highlight"><pre><code><span class="c1">/// &lt;summary&gt;</span>
<span class="c1">/// Represents a single vote for an idea</span>
<span class="c1">/// &lt;/summary&gt;</span>
<span class="k">public</span> <span class="k">class</span> <span class="nc">Vote</span> <span class="p">:</span> <span class="n">DomainEntity</span>
<span class="p">{</span>
<span class="na">    [Required]</span>
<span class="na">    [Range(-1,1)]</span>
    <span class="k">public</span> <span class="kt">int</span> <span class="n">Value</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>

    <span class="cp">#region Associations</span>
    <span class="k">public</span> <span class="k">virtual</span> <span class="n">Idea</span> <span class="n">Idea</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="cp">#endregion</span>
<span class="p">}</span>
</code></pre></div>
<h3>Comment</h3><p>Nothing special here. </p><p></p><div class="highlight"><pre><code><span class="c1">/// &lt;summary&gt;</span>
<span class="c1">/// Represents an ideas comment</span>
<span class="c1">/// &lt;/summary&gt;</span>
<span class="k">public</span> <span class="k">class</span> <span class="nc">Comment</span> <span class="p">:</span> <span class="n">DomainEntity</span>
<span class="p">{</span>
    <span class="c1">/// &lt;summary&gt;</span>
    <span class="c1">/// Gets or sets the comments content</span>
    <span class="c1">/// &lt;/summary&gt;</span>
<span class="na">    [Required]</span>
    <span class="k">public</span> <span class="kt">string</span> <span class="n">Text</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>

    <span class="cp">#region Associations</span>
    <span class="c1">/// &lt;summary&gt;</span>
    <span class="c1">/// Gets or sets the link to the parent idea</span>
    <span class="c1">/// &lt;/summary&gt;</span>
    <span class="k">public</span> <span class="k">virtual</span> <span class="n">Idea</span> <span class="n">Idea</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="cp">#endregion</span>
<span class="p">}</span>
</code></pre></div>
<h2>The Database Context</h2><p>This is where all the EF magic happens.  We use this class to provide an  entry point into our database.  It's possible to configure entities here in  terms of mapping and associations as well as providing a means to seed the  database with initial data but I didn't need any of that.  No I just defined my  sets and added a method for filtering/sorting ideas based on criteria.  Simple  stuff yet again.  It just extends the DbContext class from Entity Framework.</p><p></p><div class="highlight"><pre><code><span class="c1">/// &lt;summary&gt;</span>
<span class="c1">/// Data Repository for the ideas solution</span>
<span class="c1">/// &lt;/summary&gt;</span>
<span class="k">public</span> <span class="k">class</span> <span class="nc">IdeaRepository</span> <span class="p">:</span> <span class="n">DbContext</span>
<span class="p">{</span>
    <span class="cp">#region Db Sets</span>
    <span class="k">public</span> <span class="n">DbSet</span><span class="p">&lt;</span><span class="n">Comment</span><span class="p">&gt;</span> <span class="n">Comments</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="k">public</span> <span class="n">DbSet</span><span class="p">&lt;</span><span class="n">Idea</span><span class="p">&gt;</span> <span class="n">Ideas</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="k">public</span> <span class="n">DbSet</span><span class="p">&lt;</span><span class="n">Tag</span><span class="p">&gt;</span> <span class="n">Tags</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;}</span>
    <span class="k">public</span> <span class="n">DbSet</span><span class="p">&lt;</span><span class="n">Vote</span><span class="p">&gt;</span> <span class="n">Votes</span> <span class="p">{</span> <span class="k">get</span><span class="p">;</span> <span class="k">set</span><span class="p">;</span> <span class="p">}</span>
    <span class="cp">#endregion</span>

    <span class="c1">/// &lt;summary&gt;</span>
    <span class="c1">/// Main entry point for querying the ideas dat</span>
    <span class="c1">/// &lt;/summary&gt;</span>
    <span class="c1">/// &lt;param name="filters"&gt;&lt;/param&gt;</span>
    <span class="c1">/// &lt;returns&gt;&lt;/returns&gt;</span>
    <span class="k">public</span> <span class="n">IList</span><span class="p">&lt;</span><span class="n">Idea</span><span class="p">&gt;</span> <span class="n">QueryIdeas</span><span class="p">(</span><span class="n">IdeaFilter</span> <span class="n">filters</span> <span class="p">=</span> <span class="k">null</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="n">IQueryable</span><span class="p">&lt;</span><span class="n">Idea</span><span class="p">&gt;</span> <span class="n">ideas</span> <span class="p">=</span> <span class="n">Ideas</span><span class="p">;</span>
        <span class="n">IdeaFilter</span><span class="p">.</span><span class="n">OrderBy</span> <span class="n">orderBy</span> <span class="p">=</span> <span class="n">IdeaFilter</span><span class="p">.</span><span class="n">OrderBy</span><span class="p">.</span><span class="n">MostVotes</span><span class="p">;</span>
        <span class="k">if</span> <span class="p">(</span><span class="n">filters</span> <span class="p">!=</span> <span class="k">null</span><span class="p">)</span>
        <span class="p">{</span>
            <span class="k">if</span> <span class="p">(!</span><span class="kt">string</span><span class="p">.</span><span class="n">IsNullOrWhiteSpace</span><span class="p">(</span><span class="n">filters</span><span class="p">.</span><span class="n">Tag</span><span class="p">))</span>
            <span class="p">{</span>
                <span class="n">ideas</span> <span class="p">=</span> <span class="n">ideas</span><span class="p">.</span><span class="n">Where</span><span class="p">(</span><span class="n">i</span> <span class="p">=&gt;</span> <span class="n">i</span><span class="p">.</span><span class="n">Tags</span><span class="p">.</span><span class="n">Any</span><span class="p">(</span><span class="n">t</span> <span class="p">=&gt;</span> <span class="n">t</span><span class="p">.</span><span class="n">NormalisedName</span> <span class="p">==</span> <span class="n">filters</span><span class="p">.</span><span class="n">Tag</span><span class="p">));</span>
            <span class="p">}</span>

            <span class="n">orderBy</span> <span class="p">=</span> <span class="n">filters</span><span class="p">.</span><span class="n">Order</span><span class="p">.</span><span class="n">GetValueOrDefault</span><span class="p">(</span><span class="n">IdeaFilter</span><span class="p">.</span><span class="n">OrderBy</span><span class="p">.</span><span class="n">MostVotes</span><span class="p">);</span>
        <span class="p">}</span>

        <span class="k">switch</span> <span class="p">(</span><span class="n">orderBy</span><span class="p">)</span>
        <span class="p">{</span>
            <span class="k">case</span> <span class="n">IdeaFilter</span><span class="p">.</span><span class="n">OrderBy</span><span class="p">.</span><span class="n">MostVotes</span><span class="p">:</span>
                <span class="n">ideas</span> <span class="p">=</span> <span class="n">ideas</span><span class="p">.</span><span class="n">OrderByDescending</span><span class="p">(</span><span class="n">i</span> <span class="p">=&gt;</span> <span class="n">i</span><span class="p">.</span><span class="n">Votes</span><span class="p">.</span><span class="n">Sum</span><span class="p">(</span><span class="n">v</span> <span class="p">=&gt;</span> <span class="n">v</span><span class="p">.</span><span class="n">Value</span><span class="p">));</span>
                <span class="k">break</span><span class="p">;</span>
            <span class="k">case</span> <span class="n">IdeaFilter</span><span class="p">.</span><span class="n">OrderBy</span><span class="p">.</span><span class="n">Newest</span><span class="p">:</span>
                <span class="n">ideas</span> <span class="p">=</span> <span class="n">ideas</span><span class="p">.</span><span class="n">OrderByDescending</span><span class="p">(</span><span class="n">i</span> <span class="p">=&gt;</span> <span class="n">i</span><span class="p">.</span><span class="n">CreatedDate</span><span class="p">);</span>
                <span class="k">break</span><span class="p">;</span>
        <span class="p">}</span>

        <span class="k">return</span> <span class="n">ideas</span><span class="p">.</span><span class="n">ToList</span><span class="p">();</span>            
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<h2>Tag Model Binder</h2><p>This was an interesting thing I discovered.  If you are using a *-*  relationship and are associating one side with an object that already exists you  are required to fetch this object before using it.  For example when adding a  tag to an idea I need to attempt fetch that tag first of it exists.  What I  can't do is create a new tag object and assign an existing Id to it - this will  be thrown away and saved as a new instance.  To fix this problem I feel back  onto a Tag Model binder that attempts to fetch or create tags depending on their  normalised name.  It won't save new tag - simply create them (this is why I use  a shared DbContext between the controller and the binder).  The binder takes a  CSV styled string, breaks it apart, "normalises" the string and tries to fetch  tags based on their normalised name.  If it finds one it pushes it into the  collection otherwise it creates a new tag object and pushes that in instead.   Probably a better way to do that and I am open to suggestions.  But what I don't  want is saving tags that are then going to become orphaned if the other save  didn't go through for some reason.</p><p></p><div class="highlight"><pre><code><span class="c1">/// &lt;summary&gt;</span>
<span class="c1">/// Converts a string of tags (comma seperated) into a list of tags - </span>
<span class="c1">/// creating new ones where necessary and fecthing exisitng ones</span>
<span class="c1">/// &lt;/summary&gt;</span>
<span class="k">public</span> <span class="k">class</span> <span class="nc">TagCollectionModelBinder</span> <span class="p">:</span> <span class="n">DefaultModelBinder</span>
<span class="p">{</span>
    <span class="k">public</span> <span class="k">override</span> <span class="kt">object</span> <span class="nf">BindModel</span><span class="p">(</span><span class="n">ControllerContext</span> <span class="n">controllerContext</span><span class="p">,</span> <span class="n">ModelBindingContext</span> <span class="n">bindingContext</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="n">List</span><span class="p">&lt;</span><span class="n">Model</span><span class="p">.</span><span class="n">Tag</span><span class="p">&gt;</span> <span class="n">tags</span> <span class="p">=</span> <span class="k">new</span> <span class="n">List</span><span class="p">&lt;</span><span class="n">Model</span><span class="p">.</span><span class="n">Tag</span><span class="p">&gt;();</span>            
        <span class="n">HttpContextBase</span> <span class="n">ctx</span> <span class="p">=</span> <span class="n">controllerContext</span><span class="p">.</span><span class="n">HttpContext</span><span class="p">;</span>
        <span class="kt">string</span> <span class="n">user</span> <span class="p">=</span> <span class="n">ctx</span><span class="p">.</span><span class="n">User</span><span class="p">.</span><span class="n">Identity</span><span class="p">.</span><span class="n">Name</span><span class="p">;</span>
        <span class="kt">string</span> <span class="k">value</span> <span class="p">=</span> <span class="n">ctx</span><span class="p">.</span><span class="n">Request</span><span class="p">.</span><span class="n">Form</span><span class="p">[</span><span class="n">bindingContext</span><span class="p">.</span><span class="n">ModelName</span><span class="p">];</span>
        <span class="n">Model</span><span class="p">.</span><span class="n">IdeaRepository</span> <span class="n">_db</span> <span class="p">=</span> <span class="n">Utils</span><span class="p">.</span><span class="n">BaseIdeaController</span><span class="p">.</span><span class="n">DataContext</span><span class="p">;</span>

        <span class="k">if</span> <span class="p">(!</span><span class="kt">string</span><span class="p">.</span><span class="n">IsNullOrWhiteSpace</span><span class="p">(</span><span class="k">value</span><span class="p">))</span>
        <span class="p">{</span>
            <span class="kt">string</span><span class="p">[]</span> <span class="n">clientTags</span> <span class="p">=</span> <span class="k">value</span><span class="p">.</span><span class="n">Split</span><span class="p">(</span><span class="sc">','</span><span class="p">);</span>
            <span class="k">foreach</span> <span class="p">(</span><span class="kt">string</span> <span class="n">clientTag</span> <span class="k">in</span> <span class="n">clientTags</span><span class="p">)</span>
            <span class="p">{</span>
                <span class="kt">string</span> <span class="n">normalised</span> <span class="p">=</span> <span class="n">clientTag</span><span class="p">.</span><span class="n">ToLower</span><span class="p">().</span><span class="n">Replace</span><span class="p">(</span><span class="s">" "</span><span class="p">,</span> <span class="kt">string</span><span class="p">.</span><span class="n">Empty</span><span class="p">);</span>
                <span class="n">Model</span><span class="p">.</span><span class="n">Tag</span> <span class="n">tag</span> <span class="p">=</span> <span class="n">_db</span><span class="p">.</span><span class="n">Tags</span><span class="p">.</span><span class="n">FirstOrDefault</span><span class="p">(</span><span class="n">t</span> <span class="p">=&gt;</span> <span class="n">t</span><span class="p">.</span><span class="n">NormalisedName</span> <span class="p">==</span> <span class="n">normalised</span><span class="p">);</span>

                <span class="k">if</span> <span class="p">(</span><span class="n">tag</span> <span class="p">==</span> <span class="k">default</span><span class="p">(</span><span class="n">Model</span><span class="p">.</span><span class="n">Tag</span><span class="p">))</span>
                <span class="p">{</span>
                    <span class="n">tag</span> <span class="p">=</span> <span class="k">new</span> <span class="n">Model</span><span class="p">.</span><span class="n">Tag</span><span class="p">()</span>
                    <span class="p">{</span>
                        <span class="n">CreatedBy</span> <span class="p">=</span> <span class="n">user</span><span class="p">,</span>
                        <span class="n">CreatedDate</span> <span class="p">=</span> <span class="n">DateTime</span><span class="p">.</span><span class="n">Now</span><span class="p">,</span>
                        <span class="n">Name</span> <span class="p">=</span> <span class="n">clientTag</span><span class="p">.</span><span class="n">Trim</span><span class="p">()</span>
                    <span class="p">};</span>
                <span class="p">}</span>

                <span class="n">tags</span><span class="p">.</span><span class="n">Add</span><span class="p">(</span><span class="n">tag</span><span class="p">);</span>
            <span class="p">}</span>

            <span class="k">return</span> <span class="n">tags</span><span class="p">;</span>
        <span class="p">}</span>

        <span class="k">return</span> <span class="k">null</span><span class="p">;</span>
    <span class="p">}</span>

<span class="p">}</span>
</code></pre></div>
<h2>Other Things</h2><p>Not very much else worth mentioning right now as I more or less used the MVC  scaffolding to build the views (with some minor tweaking).  Validation on the UI  is pushed from the domain object making things a lot more streamlined.  The  controllers are still very light and could be made lighter by pushing stuff into  the IdeaRepository as well but that's for another day.</p><p>So there you go.  A very quick and dirty intro into the world of Entity  Framework.  There isn't anything complex going on here and I was worried that EF  would mask a lot of stuff that we would need access to but it seems there is  plenty of configuration points to hook into.  It has come on leaps and bounds  since I last dipped my toes into EF and hopefully they keep up the same  momentum.  There is still a lot of due diligence required before I'd recommend  EF over any other data access layer that we are currently using but I am  certainly keen to dig deeper and push it to it's limits.</p><p>Once again the source for the solution is <a href="https://github.com/kouphax/ideas/">available on Github</a>*.  Phew..... </p><p><em>* Expect bugs.</em></p>