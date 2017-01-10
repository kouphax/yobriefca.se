---
date: 2017-01-10T00:00:00Z
title: "ansible-console: An Interactive REPL for Ansible"
published: true
categories: [DevOps, Ansible]
type: article
external: false
---

If you know about ansible-console you can ignore this.  If you don't this might be of use to you.

Something found out recently is that Ansible has an interactive REPL of sorts in `ansible-console` for doing some adhoc things on a collection of hosts.

Using it is pretty simple

```bash
> ansible-console -i preproduction

Welcome to the ansible console.
Type help or ? to list commands.

jamhughes@all (3)[f:5]$
``` 

This will bring you into the shell itself,

> the `-i preproduction` tells the console to use a certain hosts file (in my case preproduction).

From here you can use the normal ansible modules.  So for example if I wanted to get the date on all the hosts I can do this,

```bash
jamhughes@all (3)[f:5]$ shell date
frontend1.preproduction | SUCCESS | rc=0 >>
Tue Jan 10 22:04:18 GMT 2017

frontend2.preproduction | SUCCESS | rc=0 >>
Tue Jan 10 22:04:18 GMT 2017

backend1.preproduction | SUCCESS | rc=0 >>
Tue Jan 10 22:04:18 GMT 2017
```

If you have specific groups you want to work in you can `cd` into groups

```bash
jamhughes@all (3)[f:5]$ cd frontend
jamhughes@customer (3)[f:5]$ shell date
frontend1.preproduction | SUCCESS | rc=0 >>
Tue Jan 10 22:04:18 GMT 2017

frontend2.preproduction | SUCCESS | rc=0 >>
Tue Jan 10 22:04:18 GMT 2017
```

I guess it flies in the face of all this immutable infrastructure stuff but hey we are not all there yet and stuff like this can be handy.

