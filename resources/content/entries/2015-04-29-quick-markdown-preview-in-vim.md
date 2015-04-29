
---
date: 2015-04-29T00:00:00Z
title: "Quick Markdown Preview in Vim"
published: true
categories: [Vim]
type: article
external: false
---

A while back a rather excellent person ([@tosborn](https://twitter.com/tosbourn)) posted an article on [previewing files in your terminal](http://tosbourn.com/view-markdown-files-terminal/).  The concept is simple.  Using [Pandoc](http://pandoc.org/) we can convert the markdown file to HTML and pipe the result into [Lynx](http://lynx.browser.org/) a terminal based browser.  While this doesn't exactly give you an accurate representation of what the output will look like in a graphical browser it gives you an overall quick view of the overall layout and structure.  Handy when you are building up a README for a library or tool.

What I'm going to show is using the same technique to render markdown files but do it without leaving the editor (assuming your editor is Vim).  I'm going to assume you have both Pandoc and Lynx installed (the article linked above will explain how to do this for Mac).

I'm a [Vim](http://www.vim.org/) user (when I can) and when I'm working with markdown files it's usually in the context of making quick edits and verifying them.  To achieve this I've lifted the original process outlined in the link above and mapped it to a key  mapping in Vim,

```
:map <Leader>x :w<cr>:!pandoc % \| lynx -stdin<cr>:redraw!<cr>
```

Lets break this down a tiny bit,

- `:map <Leader>x` - map the following to keystroke`,x` (`,` is my [leader](http://learnvimscriptthehardway.stevelosh.com/chapters/06.html) key in vim)
- `:w<cr>` - save the current file
- `:!` - start executing a shell command
- `pandoc %` - put the current file `%` through pandoc
- `\| lynx -stdin<cr>` - pipe the pandoc output into lynx and issue command
- `:redraw!<cr>` - redraw the vim window (this prevents the "Press Enter to continue prompt")

So hitting `,x` gives me a quick look at how the markdown file would look without leaving the terminal.


