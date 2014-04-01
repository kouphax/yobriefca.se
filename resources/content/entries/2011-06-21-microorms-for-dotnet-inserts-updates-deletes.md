---
date: 2011-06-20T23:00:00Z
title: 'MicroORMs for .NET: Inserts, Updates &amp; Delete'
published: true
categories: [.NET]
type: article
external: false
---
<blockquote class="announce">This post is the is part of series of posts covering the various microORMs in the .<span class="caps">NET</span> world.  I intend to look at how each microORM meets certain data access and manipulation needs.  The current series includes,<ul>	<li><a href="http://yobriefca.se/blog/2011/06/15/microorms-for-net-obligatory-introduction-post">Obligatory Introduction Post</a></li>	<li><a href="http://yobriefca.se/blog/2011/06/16/microorms-for-net-syntax-selects">Syntax - SELECTs</a></li>	<li><a href="http://yobriefca.se/blog/2011/06/18/microorms-for-net-stored-procedures">Stored Procedures</a></li>	<li><a href="http://yobriefca.se/blog/2011/06/21/microorms-for-net-inserts-updates-and-delete">Inserts, Updates &amp; Deletes</a><br /></li></ul></blockquote><p>Moving swiftly on, if all our solutions only used different way to select data then how would we ever get data into our solution?  So lets look at how these microORMs handle Inserts, Updates and Deletes<sup class="footnote" id="fnr1"><a href="#fn1">1</a></sup>.  These examples will follow on from previous ones so I am only going publish the key lines.  If you want the complete code you can grab them from the <a href="https://github.com/kouphax/microorm-comparison">GitHub project</a>.</p><h2>Dapper</h2><h3>Insert</h3><p>Nothing unexpected here.  More <span class="caps">SQL</span>.  Sam Saffron previously pointed me in the direction of <a href="http://code.google.com/p/dapper-dot-net/source/browse/Dapper.Contrib/Extensions/SqlMapperExtensions.cs">Dapper.Contrib</a> that has extra extension methods for doing things like <code>INSERT</code>, <code>UPDATE</code> and <code>DELETE</code> but at the time of trying I couldn't get it to work and have an <a href="https://github.com/SamSaffron/dapper-dot-net/issues/8">open issue</a> on GitHub.  If I get an update on this I'll post the slightly cleaner syntax.</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="n">conn</span><span class="p">.</span><span class="n">Open</span><span class="p">();</span>
<span class="kt">int</span> <span class="n">count</span> <span class="p">=</span> <span class="n">conn</span><span class="p">.</span><span class="n">Execute</span><span class="p">(</span>
    <span class="s">"INSERT Authors(Username, FullName, CreatedDate) "</span> <span class="p">+</span>
    <span class="s">"VALUES (@Username, @FullName, GETDATE())"</span><span class="p">,</span>
    <span class="k">new</span> <span class="p">{</span> <span class="n">Username</span> <span class="p">=</span> <span class="s">"james@dapper.net"</span><span class="p">,</span> <span class="n">FullName</span> <span class="p">=</span> <span class="s">"James Hughes"</span> <span class="p">});</span>
</code></pre></div>
<h3>Update</h3><p>Update is very similar to insert.  The nice thing about this approach vs. using POCOs is that you only need to send the delta of the object up where as with the other approaches you may have to fetch the object before updating.</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="kt">int</span> <span class="n">count</span> <span class="p">=</span> <span class="n">conn</span><span class="p">.</span><span class="n">Execute</span><span class="p">(</span>
    <span class="s">"UPDATE Authors SET FullName = @FullName WHERE Id = @Id"</span><span class="p">,</span>
    <span class="k">new</span> <span class="p">{</span> <span class="n">FullName</span> <span class="p">=</span> <span class="s">"James Hughes"</span><span class="p">,</span> <span class="n">Id</span> <span class="p">=</span> <span class="m">1</span> <span class="p">});</span>
</code></pre></div>
<h3>Delete</h3><p>Delete is pretty much what you'd expect.  I'll update these examples when I get my problems with Dapper.Contrib sorted but when you are so close to the <span class="caps">SQL</span> metal there isn't a massive amount to discuss.</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="kt">int</span> <span class="n">count</span> <span class="p">=</span> <span class="n">conn</span><span class="p">.</span><span class="n">Execute</span><span class="p">(</span>
    <span class="s">"DELETE FROM Authors WHERE Id = @Id"</span><span class="p">,</span>
    <span class="k">new</span> <span class="p">{</span> <span class="n">Id</span> <span class="p">=</span> <span class="m">1</span> <span class="p">});</span>
</code></pre></div>
<h2>Massive</h2><h3>Insert</h3><p>Massive keeps things simple as usual.  <code>Insert</code> can take POCOs as well as dynamic object (and a number of other types) and it uses the info found in the <code>Authors</code> DynamicModel class to generate the <span class="caps">SQL</span> for you.</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="kt">var</span> <span class="n">tbl</span> <span class="p">=</span> <span class="k">new</span> <span class="n">Authors</span><span class="p">();</span>
<span class="kt">var</span> <span class="n">x</span> <span class="p">=</span> <span class="n">tbl</span><span class="p">.</span><span class="n">Insert</span><span class="p">(</span><span class="k">new</span> 
<span class="p">{</span>
    <span class="n">Username</span> <span class="p">=</span> <span class="s">"james@dapper.net"</span><span class="p">,</span>
    <span class="n">FullName</span> <span class="p">=</span> <span class="s">"James Hughes"</span><span class="p">,</span>
    <span class="n">CreatedDate</span> <span class="p">=</span> <span class="n">DateTime</span><span class="p">.</span><span class="n">Now</span>
<span class="p">});</span>
</code></pre></div>
<p>As a bonus feature Massive can wrap multiple inserts in a single transaction and perform multiple inserts at once.</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="kt">var</span> <span class="n">tbl</span> <span class="p">=</span> <span class="k">new</span> <span class="n">Authors</span><span class="p">();</span>
<span class="kt">var</span> <span class="n">x</span> <span class="p">=</span> <span class="n">tbl</span><span class="p">.</span><span class="n">InsertMany</span><span class="p">(</span> <span class="k">new</span> <span class="p">[]</span> <span class="p">{</span>
    <span class="p">{</span> <span class="n">Username</span> <span class="p">=</span> <span class="s">"james@dapper.net"</span><span class="p">,</span> <span class="n">FullName</span> <span class="p">=</span> <span class="s">"James Hughes"</span><span class="p">,</span> <span class="n">CreatedDate</span> <span class="p">=</span> <span class="n">DateTime</span><span class="p">.</span><span class="n">Now</span> <span class="p">},</span> 
    <span class="p">{</span> <span class="n">Username</span> <span class="p">=</span> <span class="s">"james@dapper.net"</span><span class="p">,</span> <span class="n">FullName</span> <span class="p">=</span> <span class="s">"James Hughes"</span><span class="p">,</span> <span class="n">CreatedDate</span> <span class="p">=</span> <span class="n">DateTime</span><span class="p">.</span><span class="n">Now</span> <span class="p">},</span> 
    <span class="p">{</span> <span class="n">Username</span> <span class="p">=</span> <span class="s">"james@dapper.net"</span><span class="p">,</span> <span class="n">FullName</span> <span class="p">=</span> <span class="s">"James Hughes"</span><span class="p">,</span> <span class="n">CreatedDate</span> <span class="p">=</span> <span class="n">DateTime</span><span class="p">.</span><span class="n">Now</span> <span class="p">}</span> 
<span class="p">});</span>
</code></pre></div>
<h3>Update</h3><p>Like Dapper, Massive allows us to send only a delta between the old and new object.</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="kt">var</span> <span class="n">tbl</span> <span class="p">=</span> <span class="k">new</span> <span class="n">Authors</span><span class="p">();</span>
            <span class="kt">var</span> <span class="n">x</span> <span class="p">=</span> <span class="n">tbl</span><span class="p">.</span><span class="n">Update</span><span class="p">(</span><span class="k">new</span>
                                 <span class="p">{</span>
                                     <span class="n">FullName</span> <span class="p">=</span> <span class="s">"James Hughes"</span>
                                 <span class="p">},</span> <span class="m">1</span><span class="p">);</span>
</code></pre></div>
<p>Massive also gets extra points for the <code>UpdateMany</code> that, not suprisingly, behaves like the <code>InsertMany</code> method mentioned above (except for updates of course).</p><h3>Delete</h3><p>Ultra simple approach to Deleting specific objects just pass in an id and you're done,</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="kt">var</span> <span class="n">tbl</span> <span class="p">=</span> <span class="k">new</span> <span class="n">Authors</span><span class="p">();</span>
<span class="kt">var</span> <span class="n">x</span> <span class="p">=</span> <span class="n">tbl</span><span class="p">.</span><span class="n">Delete</span><span class="p">(</span><span class="m">2</span><span class="p">);</span>
</code></pre></div>
<h2>PetaPoco</h2><h3>Insert</h3><p>PetaPoco offers 2 main ways to insert your data<sup class="footnote" id="fnr2"><a href="#fn2">2</a></sup>.  You can pass in a plain <span class="caps">POCO</span> (or in our case on that uses attributes to normalise the difference between the schema and the C# class),</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="n">PetaPoco</span><span class="p">.</span><span class="n">Database</span> <span class="n">db</span> <span class="p">=</span> <span class="k">new</span> <span class="n">PetaPoco</span><span class="p">.</span><span class="n">Database</span><span class="p">(</span><span class="s">"DefaultConnectionString"</span><span class="p">);</span>
<span class="kt">var</span> <span class="n">x</span> <span class="p">=</span> <span class="n">db</span><span class="p">.</span><span class="n">Insert</span><span class="p">(</span><span class="k">new</span> <span class="n">Author</span>
<span class="p">{</span>
    <span class="n">Username</span> <span class="p">=</span> <span class="s">"james@dapper.net"</span><span class="p">,</span>
    <span class="n">FullName</span> <span class="p">=</span> <span class="s">"James Hughes"</span><span class="p">,</span>
    <span class="n">CreatedDate</span> <span class="p">=</span> <span class="n">DateTime</span><span class="p">.</span><span class="n">Now</span>
<span class="p">});</span>
</code></pre></div>
<p>Or you can pass in an anonymous object specifying the table and primary key field,</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="kt">var</span> <span class="n">x</span> <span class="p">=</span> <span class="n">db</span><span class="p">.</span><span class="n">Insert</span><span class="p">(</span><span class="s">"Authors"</span><span class="p">,</span> <span class="s">"Id"</span><span class="p">,</span> <span class="k">new</span>
<span class="p">{</span>
    <span class="n">Username</span> <span class="p">=</span> <span class="s">"james@dapper.net"</span><span class="p">,</span>
    <span class="n">FullName</span> <span class="p">=</span> <span class="s">"James Hughes"</span><span class="p">,</span>
    <span class="n">CreatedDate</span> <span class="p">=</span> <span class="n">DateTime</span><span class="p">.</span><span class="n">Now</span>
<span class="p">});</span>
</code></pre></div>
<h3>Update</h3><p>There are plenty of ways to update data using PetaPoco.  The first one is to pass in your model object and the row will get updated.  I am creating a new object in this example but you could just fetch it from the database as you'd expect,</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="n">PetaPoco</span><span class="p">.</span><span class="n">Database</span> <span class="n">db</span> <span class="p">=</span> <span class="k">new</span> <span class="n">PetaPoco</span><span class="p">.</span><span class="n">Database</span><span class="p">(</span><span class="s">"DefaultConnectionString"</span><span class="p">);</span>
<span class="kt">var</span> <span class="n">x</span> <span class="p">=</span> <span class="n">db</span><span class="p">.</span><span class="n">Update</span><span class="p">(</span><span class="k">new</span> <span class="n">Author</span>
<span class="p">{</span>
    <span class="n">Id</span> <span class="p">=</span> <span class="m">9</span><span class="p">,</span>
    <span class="n">Username</span> <span class="p">=</span> <span class="s">"james@dapper.net"</span><span class="p">,</span>
    <span class="n">FullName</span> <span class="p">=</span> <span class="s">"James Hughes"</span><span class="p">,</span>
    <span class="n">CreatedDate</span> <span class="p">=</span> <span class="n">DateTime</span><span class="p">.</span><span class="n">Now</span>
<span class="p">});</span>
</code></pre></div>
<p>Another approach is to just pass up a delta of the changes,</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="kt">var</span> <span class="n">x</span> <span class="p">=</span> <span class="n">db</span><span class="p">.</span><span class="n">Update</span><span class="p">(</span><span class="s">"Authors"</span><span class="p">,</span> <span class="s">"Id"</span><span class="p">,</span> <span class="k">new</span>
<span class="p">{</span>
    <span class="n">Id</span> <span class="p">=</span> <span class="m">10</span><span class="p">,</span>
    <span class="n">FullName</span> <span class="p">=</span> <span class="s">"James Hughes"</span>
<span class="p">});</span>
</code></pre></div>
<p>Alternatively you can simply pass up the delta and specify the primary key value externally,</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="n">db</span><span class="p">.</span><span class="n">Update</span><span class="p">(</span><span class="s">"Authors"</span><span class="p">,</span> <span class="s">"Id"</span><span class="p">,</span> <span class="k">new</span> <span class="p">{</span> <span class="n">FullName</span> <span class="p">=</span> <span class="s">"James Hughes"</span> <span class="p">},</span> <span class="m">12</span><span class="p">);</span>
</code></pre></div>
<p>Variety is the spice of life in PetaPoco!</p><h3>Delete</h3><p>Thanks to the custom attribute on our Author class deleting a record is a matter of passing the Id to the <code>Delete</code> method,</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="n">db</span><span class="p">.</span><span class="n">Delete</span><span class="p">&lt;</span><span class="n">Author</span><span class="p">&gt;(</span><span class="m">8</span><span class="p">);</span>
</code></pre></div>
<h2>ServiceStack ORMLite</h2><h3>Insert</h3><p>ORMLite is pretty much inline with the other microORMs a simple <code>Insert</code> method that accepts a <span class="caps">POCO</span> or an anonymous object and generates <span class="caps">SQL</span> based on the info given.  This can be done in one of two ways,</p><p class="minimal-gist"><div class="highlight"><pre><code><span class="k">using</span> <span class="p">(</span><span class="n">IDbConnection</span> <span class="n">db</span> <span class="p">=</span> <span class="n">Program</span><span class="p">.</span><span class="n">ConnectionString</span><span class="p">.</span><span class="n">OpenDbConnection</span><span class="p">())</span>
<span class="k">using</span> <span class="p">(</span><span class="n">IDbCommand</span> <span class="n">cmd</span> <span class="p">=</span> <span class="n">db</span><span class="p">.</span><span class="n">CreateCommand</span><span class="p">())</span>
<span class="p">{</span>
    <span class="n">cmd</span><span class="p">.</span><span class="n">Insert</span><span class="p">&lt;</span><span class="n">Author</span><span class="p">&gt;(</span><span class="k">new</span> <span class="n">Author</span>
    <span class="p">{</span>
        <span class="n">Username</span> <span class="p">=</span> <span class="s">"james@dapper.net"</span><span class="p">,</span>
        <span class="n">FullName</span> <span class="p">=</span> <span class="s">"James Hughes"</span><span class="p">,</span>
        <span class="n">CreatedDate</span> <span class="p">=</span> <span class="n">DateTime</span><span class="p">.</span><span class="n">Now</span>
    <span class="p">});</span>
<span class="p">}</span>
</code></pre></div>
</p><p>Or the factory approach discussed in the first article,</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="n">IDbConnectionFactory</span> <span class="n">dbFactory</span> <span class="p">=</span> <span class="k">new</span> <span class="n">OrmLiteConnectionFactory</span><span class="p">(</span>
    <span class="n">Program</span><span class="p">.</span><span class="n">ConnectionString</span><span class="p">,</span>
    <span class="n">SqlServerOrmLiteDialectProvider</span><span class="p">.</span><span class="n">Instance</span><span class="p">);</span>

<span class="n">dbFactory</span><span class="p">.</span><span class="n">Exec</span><span class="p">(</span><span class="n">dbCmd</span> <span class="p">=&gt;</span> <span class="n">dbCmd</span><span class="p">.</span><span class="n">Insert</span><span class="p">&lt;</span><span class="n">Author</span><span class="p">&gt;(</span><span class="k">new</span> <span class="n">Author</span>
<span class="p">{</span>
    <span class="n">Username</span> <span class="p">=</span> <span class="s">"james@dapper.net"</span><span class="p">,</span>
    <span class="n">FullName</span> <span class="p">=</span> <span class="s">"James Hughes"</span><span class="p">,</span>
    <span class="n">CreatedDate</span> <span class="p">=</span> <span class="n">DateTime</span><span class="p">.</span><span class="n">Now</span>
<span class="p">}));</span>
</code></pre></div>
<h3>Update</h3><p>ORMLite sticks with one approach for updating a row - passing in a <span class="caps">POCO</span> that it can map to a table/row,</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="n">cmd</span><span class="p">.</span><span class="n">Update</span><span class="p">(</span><span class="k">new</span> <span class="n">Author</span>
<span class="p">{</span>
    <span class="n">Id</span> <span class="p">=</span> <span class="m">7</span><span class="p">,</span>
    <span class="n">Username</span> <span class="p">=</span> <span class="s">"james@dapper.net"</span><span class="p">,</span>
    <span class="n">FullName</span> <span class="p">=</span> <span class="s">"James Hughes"</span><span class="p">,</span>
    <span class="n">CreatedDate</span> <span class="p">=</span> <span class="n">DateTime</span><span class="p">.</span><span class="n">Now</span>
<span class="p">});</span>
</code></pre></div>
<p>If you really don't want to fetch your row again and only want to send up a delta you'll have to roll your own approach (simple enough though).</p><h3>Delete</h3><p>ORMLite supports a number of neat ways to delete rows,</p><ul>	<li><code>DeleteById(id)</code></li>	<li><code>Delete(where_clause, id)</code></li>	<li><code>Delete(object)</code></li></ul><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="n">cmd</span><span class="p">.</span><span class="n">DeleteById</span><span class="p">&lt;</span><span class="n">Author</span><span class="p">&gt;(</span><span class="m">5</span><span class="p">);</span>
<span class="n">cmd</span><span class="p">.</span><span class="n">Delete</span><span class="p">&lt;</span><span class="n">Author</span><span class="p">&gt;(</span><span class="s">"Id = @0"</span><span class="p">,</span> <span class="m">6</span><span class="p">);</span>
<span class="n">cmd</span><span class="p">.</span><span class="n">Delete</span><span class="p">(</span><span class="k">new</span> <span class="n">Author</span> <span class="p">{</span> <span class="n">Id</span> <span class="p">=</span> <span class="m">7</span> <span class="p">});</span>
</code></pre></div>
<h2>Simple.Data</h2><h3>Insert</h3><p>Guess what Simple.Data's approach is once again very fluent, clean and easy to understand.  OK I don't get Intellisense as the methods are dynamic but I didn't need it anyway.</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="n">Author</span> <span class="n">x</span> <span class="p">=</span>  <span class="n">Simple</span><span class="p">.</span><span class="n">Data</span><span class="p">.</span><span class="n">Database</span><span class="p">.</span><span class="n">Open</span><span class="p">().</span><span class="n">Author</span><span class="p">.</span><span class="n">Insert</span><span class="p">(</span><span class="k">new</span> <span class="n">Author</span>
<span class="p">{</span>
    <span class="n">Username</span> <span class="p">=</span> <span class="s">"james@dapper.net"</span><span class="p">,</span>
    <span class="n">FullName</span> <span class="p">=</span> <span class="s">"James Hughes"</span><span class="p">,</span>
    <span class="n">CreatedDate</span> <span class="p">=</span> <span class="n">DateTime</span><span class="p">.</span><span class="n">Now</span>
<span class="p">});</span>
</code></pre></div>
<h3>Update</h3><p>Simple.Data has 2 ways of updating.  <code>Update</code> takes a <span class="caps">POCO</span> and maps the id to the specific row,</p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="n">Simple</span><span class="p">.</span><span class="n">Data</span><span class="p">.</span><span class="n">Database</span><span class="p">.</span><span class="n">Open</span><span class="p">().</span><span class="n">Authors</span><span class="p">.</span><span class="n">Update</span><span class="p">(</span><span class="k">new</span> <span class="n">Author</span>
<span class="p">{</span>
    <span class="n">Id</span> <span class="p">=</span> <span class="m">5</span><span class="p">,</span>
    <span class="n">Username</span> <span class="p">=</span> <span class="s">"james@dapper.net"</span><span class="p">,</span>
    <span class="n">FullName</span> <span class="p">=</span> <span class="s">"James Hughes"</span><span class="p">,</span>
    <span class="n">CreatedDate</span> <span class="p">=</span> <span class="n">DateTime</span><span class="p">.</span><span class="n">Now</span>
<span class="p">});</span>
</code></pre></div>
<p>Alternatively Simple.Data supports the delta approach too using <code>UpdateById</code></p><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="n">Simple</span><span class="p">.</span><span class="n">Data</span><span class="p">.</span><span class="n">Database</span><span class="p">.</span><span class="n">Open</span><span class="p">().</span><span class="n">Authors</span><span class="p">.</span><span class="n">UpdateById</span><span class="p">(</span>
    <span class="n">Id</span><span class="p">:</span> <span class="m">6</span><span class="p">,</span> 
    <span class="n">FullName</span><span class="p">:</span> <span class="s">"James Hughes"</span><span class="p">);</span>
</code></pre></div>
<h3>Delete</h3><p>Finally Simple.Data has 2 ways to delete a record,</p><ul>	<li><code>DeleteById(id)</code></li>	<li><code>Delete(named_parameters)</code></li></ul><p class="minimal-gist"></p><div class="highlight"><pre><code><span class="n">Simple</span><span class="p">.</span><span class="n">Data</span><span class="p">.</span><span class="n">Database</span><span class="p">.</span><span class="n">Open</span><span class="p">().</span><span class="n">Authors</span><span class="p">.</span><span class="n">DeleteById</span><span class="p">(</span><span class="m">3</span><span class="p">);</span>
<span class="n">Simple</span><span class="p">.</span><span class="n">Data</span><span class="p">.</span><span class="n">Database</span><span class="p">.</span><span class="n">Open</span><span class="p">().</span><span class="n">Authors</span><span class="p">.</span><span class="n">Delete</span><span class="p">(</span><span class="n">Id</span><span class="p">:</span> <span class="m">4</span><span class="p">);</span>
</code></pre></div>
<h2>There We Have It</h2><p>OK OK I guess this post feels a bit rushed.  I started out throwing lots of details into selects and stuff but this one just isn't up to par.  I slowly discovered that I'd have to spend almost all my spare time covering all the aspects I wanted.  So I cut it back to a kind of "Starter for 10" approach.  Yeah thats right I'm leaving stuff up to you to ask and research :-P.  Anyway in these basic examples no one <span class="caps">ORM</span> really stands out too much.  Simple.Data is, as always, lovely and clean and PetaPoco offers a very flexible experience.  I like the ability to send deltas instead of full objects back (in case you hadn't guessed) and I had a few annoyances around updates using Massive and Dapper but I think it was my own stupidity (I'll update when I investigate).  If I had to choose between them I am still tending towards Simple.Data and PetaPoco (probably PetaPoco at this stage) but I do find all of them a much more pleasant experience vs. MyIbatis or NHibernate.</p><p>That should be enough to start anyone off on the right foot.</p><p class="footnote" id="fn1"><a href="#fnr1"><sup>1</sup></a> Hehehehe IUDs</p><p class="footnote" id="fn2"><a href="#fnr2"><sup>2</sup></a> You can pass a <span class="caps">SQL</span> string or a <span class="caps">SQL</span> builder as well but it's fairly straightforward so I'll not cover it here.  See the original <a href="http://yobriefca.se/blog/2011/06/16/microorms-for-net-syntax-selects"><span class="caps">SELECT</span> post</a> for a <span class="caps">SQL</span> and <span class="caps">SQL</span> Builder example.</p>