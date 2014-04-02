---
date: 2012-03-22T00:00:00Z
title: Slim Generator for Octopress
published: true
categories: [Ruby]
type: article
external: false
---
So I've been overhauling my web site, which is based on [Octopress](http://octopress.org), lately and have had to jump into the working of Octopress/Jekyll.  It's obviously not the most complicated system in the world but hey it was new to me.

Anyway my current setup features a lot of html based pages (the default markdown approach didn't fit due to styling needs) and it was getting rather noisy.  So I wrote, as there didn't seem to be one out there, a [Slim](http://slim-lang.com/) generator.  And here it is,  step back it's VERY COMPLEX....

```ruby 
module Jekyll
  require 'slim'
  class SlimConverter < Converter
    safe true
    priority :low

    def matches(ext)
      ext =~ /slim/i
    end

    def output_ext(ext)
      ".html"
    end

    def convert(content)
      begin
        Slim::Template.new { content }.render
      rescue StandardError => e
        puts "!!! SLIM Error: " + e.message
      end
    end
  end
end
```

Just drop this file (call it anything) into your plugins folder in Jekyll/Octopress and any file with the `slim` extension will use this converter to generate HTML.  No more noisy HTML for me!