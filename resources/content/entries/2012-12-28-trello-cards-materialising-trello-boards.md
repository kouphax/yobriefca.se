---
date: 2012-12-28T00:00:00Z
title: 'Trello Cards: Materialising Trello Boards'
published: true
categories: [Agile]
type: article
external: false
---
> TL;DR I ported [pivotal-cards](https://github.com/psd/pivotal-cards) to [trello-cards](http://yobriefca.se/trello-cards)

I recently did a spot of work with the awesome and brilliant people of the [Government Digital Service](http://digital.cabinetoffice.gov.uk/category/gds/) and being big fans of getting stuff done they make use of whiteboards and index cards to visualise the flow of work on the various projects.  Thats all well and good but it makes reporting to senior managers, stakeholders and remote people a bit difficult (and for the uber paranoid - what about auditing!!!).  To that end this flow of work was also mirrored online - and in GDS's case the tool of choice was usually [Pivotal Tracker](http://pivotaltracker.com).  In order to bridge the gap between the online and "real" one of the smart chaps at GDS wrote [pivotal-cards](https://github.com/psd/pivotal-cards) which lets you generate printable index cards from you pivotal board.  Lovely stuff

<a href="http://www.flickr.com/photos/psd/7160723862/" title="Pivotal Cards by psd, on Flickr"><img src="http://farm8.staticflickr.com/7223/7160723862_ef5d8e59a7.jpg" width="500" height="442" alt="Pivotal Cards"></a>

Anyway - I spent some time today porting this little project to Trello.  I have been using Trello more and more on recent projects but always miss the physicality of real index cards and I'm usually strapped for time to start writing them out myself.

So here it is - [trello-cards](http://yobriefca.se/trello-cards), I've tried to keep it pretty much 1:1 with pivotal-cards but there are some `TODO`s still outstanding - the current card make up looks a bit like this.

<img src="/images/blog/cardmocks.png" />

So basically follow the instructions on the [project page](http://yobriefca.se/trello-cards/) and hopefully it should all work out.  If it doesn't - [FORK IT 'N FIX IT](https://github.com/kouphax/trello-cards) or [complain](https://github.com/kouphax/trello-cards/issues) and I'll fix it for you :) 

Some other points worth noting,

- Currently tested on Chrome 25 (OSX)
- Could do with some basic tests around it
- Currently no markdown support for descriptions
- Story points use the [Trello Scrum Chrome Extension](https://chrome.google.com/webstore/detail/jdbcdblgjdpmfninkoogcfpnkjmndgje?utm_source=chrome-ntp-icon) convention of `(POINTS)` at the start of the card name/title.  If you use that plugin then good for you, if not you can still make use of the points convention
- Tasks are derived from the first checklist on the card