---
date: 2016-11-02T00:00:00Z
title: "Bashing Ambiguity"
published: true
categories: [craftsmanship]
type:article
external: false
---

I read a great tweet the other week,

> Was hoping to get some verbose output from pkill, ran `pkill -9 -v process_name`. Ho boy.
>   &mdash; Maxime Chevalier (@Love2Code) [September 21, 2016](https://twitter.com/Love2Code/status/778387312002510848)

I can't say I've ever used pkill so had to do a tiny bit of digging through the man pages of `pkill`

```bash
PKILL(1)                  BSD General Commands Manual                 PKILL(1)

NAME
     pgrep, pkill -- find or signal processes by name

SYNOPSIS
     pkill [-signal] [-ILafilnovx] [-F pidfile] [-G gid] [-P ppid] [-U uid] [-g pgrp] [-t tty] [-u euid] pattern ...

DESCRIPTION

     The pkill command searches the process table on the running system and signals all processes that match the criteria given on the command line.

     The following options are available:

  	 ...

     -v          Reverse the sense of the matching; display processes that do not match the given criteria.
```

So heh, `pkill` is based on `pgrep` which is based on `grep` and `-v` is the shorthand notation of `--invert-match` so the previous command becomes kill everything (with extreme prejudice) that isn't named `process_name`.  _Ho boy_ indeed.  Of course it's an easy mistake to make, I'm certain I would have fallen prey of this eventually had I not seen this tweet.  I'm known for simply throwing more than a few `-vvvvvvvvvvvvv`'s onto commands that don't behave as I'd expect.

_But AHA! I have the solution - just be more explicit and use the `--` notation_

Well.... not quite.

For some crazy bizarro reason `pkill` and `pgrep` don't have the double dash equivalent of `v/invert-match`.  I'm sure there is a sane and logical rationale for this but then again, given it's computers all the way down, maybe not.  

It's shit isn't it?  Even the most standard tools we use as developers take the [principle of least astonishment](https://en.wikipedia.org/wiki/Principle_of_least_astonishment) and just throw it into the endlessly burning tyre fire of misery and terrible user experience.

So what can we do?  Easy! __TRUST__. __NOTHING__. Never assume anything. Slow down. Think. Think even when it seems obvious.  Think even when you've written the same command for the 100th time.


