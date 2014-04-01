---
date: 2010-01-13T00:00:00Z
title: CSS Hacks for IE - Unnecessary
published: true
categories: [CSS]
type: article
external: false
---
<p>There are lots of CSS tricks floating aroud to accomodate IE quirks and more specifically in most cases IE6 the problem is these hacks - which is exactly what they are - invalidate your CSS and break future-proofing (what if a another browser learns to understand this hack with different rendering effects?). These things are parsed by every browser that comes across them - the browser then has to attempt to parse it, fail and then carry on - not exactly the most performant styling mechanism! They are useful and often quite nice but aren't exactly self explanatory making them a maintenance nightmare if someone doesn;t understand that order can be very important in these cases.</p><p>So - Why hack when a system already exists?</p><p>Hello <a href="http://www.quirksmode.org/css/condcom.html">Conditional Comments</a>.</p><p>Conditional Comments are a feature implemented in IE (though other browsers COULD implement them if they wanted to but none have). It's a specially marked up HTML comment node (so you include it in your HTML not CSS) that is only parsed by IE. A few examples are below....</p><p><div class="highlight"><pre><code><span class="c">&lt;!--[if IE 6]&gt;</span>
<span class="c">IE specific Styling</span>
<span class="c">&lt;![endif]--&gt;</span>

<span class="c">&lt;!--[if IE 6]&gt;</span>
<span class="c">IE6 Styling</span>
<span class="c">&lt;![endif]--&gt;</span>

<span class="c">&lt;!--[if gt IE 6]&gt;</span>
<span class="c">IE7 + Styling</span>
<span class="c">&lt;![endif]--&gt;</span>
</code></pre></div>
</p><p>So basically you have you all normal CSS link tag then you can include a conditional comment to cater for specifics to IE or some specific version or rnge of versions. OK you would still need hacks for the other browsers but NIWater only had to use IE6 conditional comments (no hacks for other browsers) and looks fine in Chrome/Safari/FF - plus it means you CSS is valid and IE specific CSS is only pulled down by IE slightly reducing page weight (very slightly most likely!!!).</p>