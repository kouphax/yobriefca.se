---
date: 2017-01-06T00:00:00Z
title: "Tabularizing output of shell scripts with column"
published: true
categories: []
type: article
external: false
---

I tend to live in my terminal and as such tend to write a bunch of shell scripts for various project specific things.  One script I have lists, form a project directory, the what branch each of my repositories is on.  It looks a bit like this,

```bash
#!/bin/bash

projects=(
  super-service 
  super-client 
  authentication-service 
  infrastructure    )

for project in ${projects[*]}; do
  cd $project
  echo "$project $(git branch | sed -n -e 's/^\* \(.*\)/\1/p')"
  cd ..
done
```

When I run this the output looks like this,

```
super-service master
super-client master
authentication-service experimental
infrastructure master
```

The list is longer and the names above aren't real but you see the problem.  It's hard to see at a glance what repos are on what branches.

## Enter `column`

If you pipe the output of the script above in `column -t -s' '` the result is a bit different.

```
super-service           master
super-client            master
authentication-service  experimental
infrastructure          master
```

You can see from the output above the `column` command pretty much does what is says on the tin.  It turns output into formatted table with discernible columns.  This makes it much easier to read.  The additional parameter I passed where,

- `-t` which tells column to read the whole input and determine the number of columns automatically to print the table
- `-s' '` specifies the character to use to determine a column break when using the `-t` command.  In this case, we specify whitespace.  In fact whitespace is the default character to break on so this is actually not needed in our case but if each column was separated by a | character we could use `-s'|'` to change the default behavior.

Now go forth and prettify those scripts, they're worth it.


