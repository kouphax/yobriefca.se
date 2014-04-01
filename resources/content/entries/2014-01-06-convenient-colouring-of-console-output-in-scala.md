---
date: 2014-01-06T00:00:00Z
title: Convenient Colouring of Console Output in Scala
published: true
categories: [Scala]
type: article
external: false
---
If you're writing console based applications or anything that may write meaningful information out to a console/terminal then you should probably consider making the output less bland.  It's not just because people using your software like shiny things but it helps identify the severity or context of certain output.  Look at tools like [grunt](http://gruntjs.com/) or any of the modern test frameworks compared to [maven](http://maven.apache.org/).  For a start they are much less noisy, maven is a bit of a chatty cathy, but they also make clever use of colours - red for bad things, green for good etc.  This is something that maven could do with, discerning useful information out of the monochrome wall of text that maven spews out is a slow task.

In a recent Scala project this was something that I needed to do and found the default way of doing it rather noisy.  For example writing out a red line and resetting the output requires this,

```scala
print("${Console.RED}[ERROR] ")
println("${Console.WHITE}$errorMsg")
```

Thats just to spit out a tiny bit of red, imagine what this looks like for more complex examples.  So to tidy this up a bit I created an implicit class to add some colour features to strings (I come from a C# background and always liked __sensible__ use of [extension methods](http://msdn.microsoft.com/en-us//library/bb383977.aspx), and therefore implicit classes).

```scala
implicit class ConsoleColorise(val str: String) extends AnyVal {
  import Console._

  def black     = s"$BLACK$str"
  def red       = s"$RED$str"
  def green     = s"$GREEN$str"
  def yellow    = s"$YELLOW$str"
  def blue      = s"$BLUE$str"
  def magenta   = s"$MAGENTA$str"
  def cyan      = s"$CYAN$str"
  def white     = s"$WHITE$str"

  def blackBg   = s"$BLACK_B$str"
  def redBg     = s"$RED_B$str"
  def greenBg   = s"$GREEN_B$str"
  def yellowBg  = s"$YELLOW_B$str"
  def blueBg    = s"$BLUE_B$str"
  def magentaBg = s"$MAGENTA_B$str"
  def cyanBg    = s"$CYAN_B$str"
  def whiteBg   = s"$WHITE_B$str"
}
```

You can use this class in your code by importing it and then,

```scala
print("[ERROR] ".red)
println(errorMsg.white)
```

You can see it in action below,

<script type="text/javascript" src="http://asciinema.org/a/7084.js" id="asciicast-7084" async></script>