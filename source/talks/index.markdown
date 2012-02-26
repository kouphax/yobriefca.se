---
layout: page
title: "Talks"
comments: false
sharing: true
footer: false
---

<table class="talks">
  {% for post in site.categories["Talks"] %}
  <tr class="talks-entry">
    <td width="35%">
        <br/>
        <img src="{{ root_url }}/images/talks/{{ post.thumb }}" />
    </td>
    <td>
      <h2><a href="{{ post.url }}">{{ post.title }}</a></h2>
      <div class="snip">{{ post.content | excerpt }}</div>
    </td>
  </tr>
  {% endfor %}
</table>
<script>
  $('body').addClass('collapse-sidebar');
</script>