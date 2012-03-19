---
layout: page
title: "Talks"
comments: false
sharing: true
footer: false
---

<div id="blog-archives">
{% for post in site.categories["Talks"] %}
  {% capture this_year %}{{ post.date | date: "%Y" }}{% endcapture %}
  {% unless year == this_year %}
    {% assign year = this_year %}
    <h2>{{ year }}</h2>
  {% endunless %}
  <article>
    {% capture category %}{{ post.categories | size }}{% endcapture %}
    <h1>
        <a href="{{ root_url }}{{ post.url }}">{{post.title}}</a>
    </h1>
    <time datetime="{{ post.date | datetime | date_to_xmlschema }}" pubdate>{{ post.date | date: "<span class='month'>%b</span> <span class='day'>%d</span> <span class='year'>%Y</span>"}}</time>
    <div>
      <span class="categories">{{ post.events | category_links }}</span>
    </div>
    {% if category != '1' || category != '0' %}
    <footer>
      <span class="categories">{{ post.categories | category_links }}</span>
    </footer>
    {% endif %}
  </article>
{% endfor %}
</div>