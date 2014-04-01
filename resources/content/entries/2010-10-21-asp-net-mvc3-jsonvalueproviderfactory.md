---
date: 2010-10-20T23:00:00Z
title: ASP.NET MVC3 JsonValueProviderFactory
published: true
categories: [.NET]
type: article
external: false
---
<p><a target="_blank" href="http://www.asp.net/mvc/mvc3">ASP.NET MVC 3</a></p><p>Anyone who's been involved in an ASP.NET MVC project that is quite Ajax heavy  will probably have noticed that something was always missing.  Imagine this  front end scenario,</p><p></p><div class="highlight"><pre><code><span class="err">&lt;</span>% using (Html.BeginForm()) { %&gt;
    <span class="nt">&lt;p&gt;</span>
        <span class="err">&lt;</span>%=Html.LabelFor(m =&gt; m.Username)%&gt;
        <span class="err">&lt;</span>%=Html.TextBoxFor(m =&gt; m.Username)%&gt;
    <span class="nt">&lt;/p&gt;</span>
    <span class="nt">&lt;p&gt;</span>
        <span class="err">&lt;</span>%=Html.LabelFor(m =&gt; m.Password)%&gt;
        <span class="err">&lt;</span>%=Html.TextBoxFor(m =&gt; m.Password)%&gt;
    <span class="nt">&lt;/p&gt;</span>
<span class="err">&lt;</span>%} %&gt;
<span class="nt">&lt;script </span><span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;</span>
    <span class="nx">$</span><span class="p">(</span><span class="s2">"form"</span><span class="p">).</span><span class="nx">submit</span><span class="p">(</span><span class="kd">function</span> <span class="p">(</span><span class="nx">evt</span><span class="p">)</span> <span class="p">{</span>   
        <span class="c1">// extract values to submit         </span>
        <span class="kd">var</span> <span class="nx">form</span> <span class="o">=</span> <span class="nx">$</span><span class="p">(</span><span class="k">this</span><span class="p">),</span>
            <span class="nx">username</span> <span class="o">=</span> <span class="nx">form</span><span class="p">.</span><span class="nx">find</span><span class="p">(</span><span class="s2">"[name=Username]"</span><span class="p">).</span><span class="nx">val</span><span class="p">(),</span>
            <span class="nx">password</span> <span class="o">=</span> <span class="nx">form</span><span class="p">.</span><span class="nx">find</span><span class="p">(</span><span class="s2">"[name=Password]"</span><span class="p">).</span><span class="nx">val</span><span class="p">(),</span>
            <span class="nx">json</span> <span class="o">=</span> <span class="nx">JSON</span><span class="p">.</span><span class="nx">stringify</span><span class="p">({</span>
                <span class="nx">Username</span><span class="o">:</span> <span class="nx">username</span><span class="p">,</span>
                <span class="nx">Password</span><span class="o">:</span> <span class="nx">password</span>
            <span class="p">});</span>

        <span class="nx">$</span><span class="p">.</span><span class="nx">ajax</span><span class="p">({</span>
            <span class="nx">url</span><span class="o">:</span> <span class="nx">form</span><span class="p">.</span><span class="nx">attr</span><span class="p">(</span><span class="s2">"action"</span><span class="p">),</span>
            <span class="nx">type</span><span class="o">:</span> <span class="s1">'POST'</span><span class="p">,</span>
            <span class="nx">contentType</span><span class="o">:</span> <span class="s1">'application/json; charset=utf-8'</span><span class="p">,</span>                
            <span class="nx">dataType</span><span class="o">:</span> <span class="s1">'json'</span><span class="p">,</span>
            <span class="nx">data</span><span class="o">:</span> <span class="nx">json</span><span class="p">,</span>                
            <span class="nx">success</span><span class="o">:</span> <span class="nx">handleLogin</span>
        <span class="p">});</span>

        <span class="c1">// stop form submitting</span>
        <span class="nx">evt</span><span class="p">.</span><span class="nx">preventDefault</span><span class="p">();</span>
    <span class="p">});</span>
<span class="nt">&lt;/script&gt;</span>
</code></pre></div>
<p>Which posts to the following action</p><p><div class="highlight"><pre><code><span class="na">[HttpPost]</span>
<span class="k">public</span> <span class="n">ActionResult</span> <span class="nf">Index</span><span class="p">(</span><span class="n">LoginModel</span> <span class="n">model</span><span class="p">)</span>
<span class="p">{</span>
    <span class="c1">// do login</span>
    <span class="k">return</span> <span class="nf">Json</span><span class="p">(</span><span class="k">new</span> 
    <span class="p">{</span>
        <span class="n">Success</span> <span class="p">=</span> <span class="k">true</span>
    <span class="p">});</span>
<span class="p">}</span>
</code></pre></div>
</p><p>We have a login screen that is submitted via ajax.  Now this is quite a  contrived example (ideally you'd be performing a normal post via ajax in  this situation) but there are many instances where this sort practise would  apply (ExtJS' RESTful DataWriters for example). </p><h2>MVC 2</h2><p>In MVC 2 this wouldn't work immediately.  The default model binder in MVC 2  uses Request parameters to bind to model properties but in this case there are  none as the ajax content is the body of the request.</p><p>To accommodate this sort of request in MVC 2 we had to provide a custom model  binder that knows how to deal with JSON requests,</p><p></p><div class="highlight"><pre><code><span class="k">public</span> <span class="k">class</span> <span class="nc">JsonModelBinder</span> <span class="p">:</span> <span class="n">IModelBinder</span>
<span class="p">{</span>
    <span class="k">public</span> <span class="kt">object</span> <span class="nf">BindModel</span><span class="p">(</span><span class="n">ControllerContext</span> <span class="n">controllerContext</span><span class="p">,</span> <span class="n">ModelBindingContext</span> <span class="n">bindingContext</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="n">HttpRequestBase</span> <span class="n">request</span> <span class="p">=</span> <span class="n">controllerContext</span><span class="p">.</span><span class="n">HttpContext</span><span class="p">.</span><span class="n">Request</span><span class="p">;</span>
        <span class="kt">string</span> <span class="n">jsonStringData</span> <span class="p">=</span> <span class="k">new</span> <span class="n">StreamReader</span><span class="p">(</span><span class="n">request</span><span class="p">.</span><span class="n">InputStream</span><span class="p">).</span><span class="n">ReadToEnd</span><span class="p">();</span>
        <span class="k">return</span> <span class="n">JsonConvert</span><span class="p">.</span><span class="n">DeserializeObject</span><span class="p">(</span><span class="n">jsonStringData</span><span class="p">,</span> <span class="n">bindingContext</span><span class="p">.</span><span class="n">ModelType</span><span class="p">);</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
<p>And attribute our actions model argument telling it to use this binder,</p><p></p><div class="highlight"><pre><code><span class="na">[HttpPost]</span>
<span class="k">public</span> <span class="n">ActionResult</span> <span class="nf">Index</span><span class="p">([</span><span class="n">ModelBinder</span><span class="p">(</span><span class="k">typeof</span><span class="p">(</span><span class="n">JsonModelBinder</span><span class="p">))]</span><span class="n">LoginModel</span> <span class="n">model</span><span class="p">)</span>
<span class="p">{</span>
    <span class="c1">// ...</span>
<span class="p">}</span>
</code></pre></div>
<p>It'll do the job but it's incredibly messy. </p><h2>MVC 3</h2><p>MCV3 fills this gap thanks to the JsonValueProviderFactory.  The JVPF  operates at a higher level than a model binder.  Basically what it does when a  JSON request is received is that it pulls the values out of the JSON body as key  value pairs which means they are available to the model binders including the  default model binder.  No special wiring required, no custom model binders  (unless of course you want one) just out-of-the-box workingness!</p><p><div class="highlight"><pre><code><span class="na">[HttpPost]</span>
<span class="k">public</span> <span class="n">ActionResult</span> <span class="nf">Index</span><span class="p">(</span><span class="n">LoginModel</span> <span class="n">model</span><span class="p">)</span>
<span class="p">{</span>
    <span class="c1">// do login</span>
    <span class="k">return</span> <span class="nf">Json</span><span class="p">(</span><span class="k">new</span> 
    <span class="p">{</span>
        <span class="n">Success</span> <span class="p">=</span> <span class="k">true</span>
    <span class="p">});</span>
<span class="p">}</span>
</code></pre></div>
</p><p>I know most of this post was taken up by MVC2 specific implementation but  isn't that really the point?  MVC3 is a nice refinement of MVC2 there isn't  anything new exactly but the core stuff that is there has been made easier and  more configurable.</p>