---
date: 2011-03-28T23:00:00Z
title: Serialising .NET Resources to JSON for Web Apps
published: true
categories: [.NET]
type: article
external: false
---
<p>Pop quiz hotshot - you are building one of them massive single page web apps  using something like Sencha where the vast majority of work is pushed into the  JavaScript realm and some crazy nut wants everything localised or at least  all text strings push to RESX files.  What do you do?  WHAT DO YOU DO?</p><h2>Option 1</h2><p>Well the most obvious option, and IMHO the most horrible, would be to convert  all those JavaScript files into aspx's or cshtml's or whatever and embed the  resource references directly into the files.</p><p></p><div class="highlight"><pre><code><span class="err">&lt;</span>%@ Import Namespace="Some.Resources" %&gt;

var myWidget = new Widget({
    title:       '<span class="err">&lt;</span>%=Strings.DefaultWidgetTitle%&gt;',
    description: '<span class="err">&lt;</span>%=Strings.DefaultWidgetDescription%&gt;'
});

myWidget.show(document.body);
</code></pre></div>
<p>This is far from nice.  For one it kind of makes any static compression of  the files either impossible or at best annoyingly fiddly.  It also prevents any  sort of quick client side caching unless you use some sort of VaryByCulture  Output Caching strategy on the server side.  Blegh.</p><h2>Option 2</h2><p>The next option, and certainly much better would be to pull out the direct  &lt;%= %&gt; references and store them in another smaller file and reference  them through a global JS object</p><p></p><div class="highlight"><pre><code><span class="err">&lt;</span>%-- DECALRED WITHIN THE MAIN HTML PAGE --%&gt;
<span class="nt">&lt;script </span><span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;</span>
    <span class="kd">var</span> <span class="nx">Strings</span> <span class="o">=</span> <span class="p">{</span>
        <span class="nx">DefaultWidgetTitle</span><span class="o">:</span>       <span class="s1">'&lt;%=Strings.DefaultWidgetTitle%&gt;'</span><span class="p">,</span>
        <span class="nx">DefaultWidgetDescription</span><span class="o">:</span> <span class="s1">'&lt;%=Strings.DefaultWidgetDescription%&gt;'</span>
    <span class="p">}</span>
<span class="nt">&lt;/script&gt;</span>
</code></pre></div>
<p></p><div class="highlight"><pre><code><span class="kd">var</span> <span class="nx">myWidget</span> <span class="o">=</span> <span class="k">new</span> <span class="nx">Widget</span><span class="p">({</span>
    <span class="nx">title</span><span class="o">:</span>       <span class="nx">Strings</span><span class="p">.</span><span class="nx">DefaultWidgetTitle</span><span class="p">,</span>
    <span class="nx">description</span><span class="o">:</span> <span class="nx">Strings</span><span class="p">.</span><span class="nx">DefaultWidgetDescription</span>
<span class="p">});</span>

<span class="nx">myWidget</span><span class="p">.</span><span class="nx">show</span><span class="p">(</span><span class="nb">document</span><span class="p">.</span><span class="nx">body</span><span class="p">);</span>
</code></pre></div>
<p>This is nicer because it means you can compress and cache the JavaScript file  globally without having to worry about different cultures etc.  It does leave  one annoyance though.  The Strings object above it essentially boiler plate.   The names are a 1:1 mapping of the resource file so we have introduced a layer  of abstraction we have to write manually leaving us open to make some mistakes.   Slightly less blegh, but still blegh.</p><h2>Option 3</h2><p>The solution I like the best is to provide a mechanism for serialising the  Resource file into the equivalent JSON object.  You get all the benefits of  option 2 while not having to worry about having to write the mapping file.  So  lets keep the JS file from the second option and change the ASPX file</p><p></p><div class="highlight"><pre><code><span class="err">&lt;</span>%-- DECALRED WITHIN THE MAIN HTML PAGE --%&gt;
<span class="nt">&lt;script </span><span class="na">type=</span><span class="s">"text/javascript"</span><span class="nt">&gt;</span>
    <span class="kd">var</span> <span class="nx">Strings</span> <span class="o">=</span> <span class="o">&lt;%=</span> <span class="nx">ResourceSerialiser</span><span class="p">.</span><span class="nx">ToJson</span><span class="p">(</span><span class="k">typeof</span><span class="p">(</span><span class="nx">Some</span><span class="p">.</span><span class="nx">Resource</span><span class="p">.</span><span class="nx">Strings</span><span class="p">))</span> <span class="o">%&gt;</span>
<span class="nt">&lt;/script&gt;</span>
</code></pre></div>
<p>Now lets look at the magic behind this option - the JSON Serialiser</p><p><div class="highlight"><pre><code><span class="c1">/// &lt;summary&gt;</span>
<span class="c1">/// Utility class that allows serialisation of .NET resource files (.resx) </span>
<span class="c1">/// into different formats</span>
<span class="c1">/// &lt;/summary&gt;</span>
<span class="k">public</span> <span class="k">static</span> <span class="k">class</span> <span class="nc">ResourceSerialiser</span>
<span class="p">{</span>
    <span class="cp">#region JSON Serialisation</span>
    <span class="c1">/// &lt;summary&gt;</span>
    <span class="c1">/// Converts a resrouce type into an equivalent JSON object using the </span>
    <span class="c1">/// current Culture</span>
    <span class="c1">/// &lt;/summary&gt;</span>
    <span class="c1">/// &lt;param name="resource"&gt;The resoruce type to serialise&lt;/param&gt;</span>
    <span class="c1">/// &lt;returns&gt;A JSON string representation of the resource&lt;/returns&gt;</span>
    <span class="k">public</span> <span class="k">static</span> <span class="kt">string</span> <span class="nf">ToJson</span><span class="p">(</span><span class="n">Type</span> <span class="n">resource</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="n">CultureInfo</span> <span class="n">culture</span> <span class="p">=</span> <span class="n">CultureInfo</span><span class="p">.</span><span class="n">CurrentCulture</span><span class="p">;</span>
        <span class="k">return</span> <span class="nf">ToJson</span><span class="p">(</span><span class="n">resource</span><span class="p">,</span> <span class="n">culture</span><span class="p">);</span>
    <span class="p">}</span>

    <span class="c1">/// &lt;summary&gt;</span>
    <span class="c1">/// Converts a resrouce type into an equivalent JSON object using the </span>
    <span class="c1">/// culture derived from the language code passed in</span>
    <span class="c1">/// &lt;/summary&gt;</span>
    <span class="c1">/// &lt;param name="resource"&gt;The resoruce type to serialise&lt;/param&gt;</span>
    <span class="c1">/// &lt;param name="languageCode"&gt;The language code to derive the culture&lt;/param&gt;</span>
    <span class="c1">/// &lt;returns&gt;A JSON string representation of the resource&lt;/returns&gt;</span>
    <span class="k">public</span> <span class="k">static</span> <span class="kt">string</span> <span class="nf">ToJson</span><span class="p">(</span><span class="n">Type</span> <span class="n">resource</span><span class="p">,</span> <span class="kt">string</span> <span class="n">languageCode</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="n">CultureInfo</span> <span class="n">culture</span> <span class="p">=</span> <span class="n">CultureInfo</span><span class="p">.</span><span class="n">GetCultureInfo</span><span class="p">(</span><span class="n">languageCode</span><span class="p">);</span>
        <span class="k">return</span> <span class="nf">ToJson</span><span class="p">(</span><span class="n">resource</span><span class="p">,</span> <span class="n">culture</span><span class="p">);</span>
    <span class="p">}</span>

    <span class="c1">/// &lt;summary&gt;</span>
    <span class="c1">/// Converts a resrouce type into an equivalent JSON object</span>
    <span class="c1">/// &lt;/summary&gt;</span>
    <span class="c1">/// &lt;param name="resource"&gt;The resoruce type to serialise&lt;/param&gt;</span>
    <span class="c1">/// &lt;param name="culture"&gt;The culture to retrieve&lt;/param&gt;</span>
    <span class="c1">/// &lt;returns&gt;A JSON string representation of the resource&lt;/returns&gt;</span>
    <span class="k">public</span> <span class="k">static</span> <span class="kt">string</span> <span class="nf">ToJson</span><span class="p">(</span><span class="n">Type</span> <span class="n">resource</span><span class="p">,</span> <span class="n">CultureInfo</span> <span class="n">culture</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="n">Dictionary</span><span class="p">&lt;</span><span class="kt">string</span><span class="p">,</span> <span class="kt">string</span><span class="p">&gt;</span> <span class="n">dictionary</span> <span class="p">=</span> <span class="n">ResourceToDictionary</span><span class="p">(</span><span class="n">resource</span><span class="p">,</span> <span class="n">culture</span><span class="p">);</span>
        <span class="k">return</span> <span class="n">JsonConvert</span><span class="p">.</span><span class="n">SerializeObject</span><span class="p">(</span><span class="n">dictionary</span><span class="p">);</span>
    <span class="p">}</span>
    <span class="cp">#endregion</span>

    <span class="c1">/// &lt;summary&gt;</span>
    <span class="c1">/// Converts a resrouce type into a dictionary type while localising </span>
    <span class="c1">/// the strings using the passed in culture</span>
    <span class="c1">/// &lt;/summary&gt;</span>
    <span class="c1">/// &lt;param name="resource"&gt;The resoruce type to serialise&lt;/param&gt;</span>
    <span class="c1">/// &lt;param name="culture"&gt;The culture to retrieve&lt;/param&gt;</span>
    <span class="c1">/// &lt;returns&gt;A dictionary representation of the resource&lt;/returns&gt;</span>
    <span class="k">private</span> <span class="k">static</span> <span class="n">Dictionary</span><span class="p">&lt;</span><span class="kt">string</span><span class="p">,</span> <span class="kt">string</span><span class="p">&gt;</span> <span class="n">ResourceToDictionary</span><span class="p">(</span><span class="n">Type</span> <span class="n">resource</span><span class="p">,</span> <span class="n">CultureInfo</span> <span class="n">culture</span><span class="p">)</span>
    <span class="p">{</span>
        <span class="n">ResourceManager</span> <span class="n">rm</span> <span class="p">=</span> <span class="k">new</span> <span class="n">ResourceManager</span><span class="p">(</span><span class="n">resource</span><span class="p">);</span>
        <span class="n">PropertyInfo</span><span class="p">[]</span> <span class="n">pis</span> <span class="p">=</span> <span class="n">resource</span><span class="p">.</span><span class="n">GetProperties</span><span class="p">(</span><span class="n">BindingFlags</span><span class="p">.</span><span class="n">Public</span> <span class="p">|</span> <span class="n">BindingFlags</span><span class="p">.</span><span class="n">Static</span><span class="p">);</span>
        <span class="n">IEnumerable</span><span class="p">&lt;</span><span class="n">KeyValuePair</span><span class="p">&lt;</span><span class="kt">string</span><span class="p">,</span> <span class="kt">string</span><span class="p">&gt;&gt;</span> <span class="n">values</span> <span class="p">=</span>
            <span class="k">from</span> <span class="n">pi</span> <span class="k">in</span> <span class="n">pis</span>
            <span class="k">where</span> <span class="n">pi</span><span class="p">.</span><span class="n">PropertyType</span> <span class="p">==</span> <span class="k">typeof</span><span class="p">(</span><span class="kt">string</span><span class="p">)</span>
            <span class="k">select</span> <span class="k">new</span> <span class="n">KeyValuePair</span><span class="p">&lt;</span><span class="kt">string</span><span class="p">,</span> <span class="kt">string</span><span class="p">&gt;(</span>
                <span class="n">pi</span><span class="p">.</span><span class="n">Name</span><span class="p">,</span>
                <span class="n">rm</span><span class="p">.</span><span class="n">GetString</span><span class="p">(</span><span class="n">pi</span><span class="p">.</span><span class="n">Name</span><span class="p">,</span> <span class="n">culture</span><span class="p">));</span>
        <span class="n">Dictionary</span><span class="p">&lt;</span><span class="kt">string</span><span class="p">,</span> <span class="kt">string</span><span class="p">&gt;</span> <span class="n">dictionary</span> <span class="p">=</span> <span class="n">values</span><span class="p">.</span><span class="n">ToDictionary</span><span class="p">(</span><span class="n">k</span> <span class="p">=&gt;</span> <span class="n">k</span><span class="p">.</span><span class="n">Key</span><span class="p">,</span> <span class="n">v</span> <span class="p">=&gt;</span> <span class="n">v</span><span class="p">.</span><span class="n">Value</span><span class="p">);</span>

        <span class="k">return</span> <span class="n">dictionary</span><span class="p">;</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
</p><p>Simple enough little class that is configurable by Culture etc. so you can  pull different translation out on demand if needs be.  Obviously it doesn't do  anything around caching - SRP and all that stuff you know :-P</p><p>Yeah so I've used this on 2 projects already with great success so hopefully  someone else finds it useful.</p>