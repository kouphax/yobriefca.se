---
layout: page
title: "Screencasts"
comments: false
sharing: true
footer: false
---

<div class="screencasts">
    {% for post in site.categories["Screencast"] %}
    <div class="screencast-entry">
      <div>
        <a href="{{ post.url }}">
          <img src="{{ root_url }}/images/screencast/{{ post.screencast_thumb }}" />
        </a>
      </div>
      <a href="{{ post.url }}">{{ post.title }}</a>
      <div class="date-holder">
        <span class="date">{{ post.date | date_to_string }}</span>
      </div>
    </div>
    {% endfor %}
    <div style="clear: both;"></div>
</div>
