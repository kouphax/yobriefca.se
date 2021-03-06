---
date: 2012-04-02T23:00:00Z
title: Rails is not a gateway drug
published: true
categories: [Ruby]
type: article
external: false
---
> WARNING!!! - a negative opinion on Rails lies ahead.  Everything here needs to be taken with an __IMHO__ and, if you feel so inclined, tell me to go mind my own business.

I know I'm stepping into very dangerous territory here but I've come to the very solid conclusion that [Rails](http://rubyonrails.org/), Ruby's heavily opinionated web framework, is not the gateway drug people claim.  I've heard the claims that if you give [Rails](http://rubyonrails.org/) a try, coming from some other web stack on any other language, you'll fall in love with Ruby and Rails and write a big long blog post about how you are leaving .NET (or Java or whatever) behind.  I really dont get it.... the only feeling that Rails leaves me with - having been using it in anger for about a month now - is frustration.  But before you drive rusty nails into my hands and feet let me at least explain where I am coming from.

## Previous attempts

I've been binging on Ruby for the last few months but this hasn't been my first time - it's been my only successful time but certianly not my first.  A year previous I had given Ruby/Rails a shot and it never stuck (in fact not once but at least twice).  Coming from a predominantly .NET/ASP.NET MVC background I decided the best approach would be to cut my teeth on a medium sized web based project and therefore jumped right into Rails.  It only took a short time before the whole effort felt like a slog full of weird semi-magic behaviour.  I gave up.

## Doing things differently

This time around I decided to do things a little differently - rather than run with a web project I just tinkered with the language and eventually tried porting a [.NET project over to Ruby](http://yobriefca.se/blog/2012/03/12/sentamentalizer/).  Fair bit of Ruby code and a small [sinatra](http://sinatrarb.com) app later - it clicked.  I got the whole Ruby love affair, it was lovely and I was three lines into my _"F**k you .NET, you're worse than Java and I hate you and you smell"_ blog post (not factual!!) when I realised I needed to give Ruby more time to bed in and, out of fairness, I had to at least experience Rails.  

## Present Day

And with that I discovered that Rails had been my problem.  I find it incredibly frustrating.  I don't want to dive into bashing Rails, thats not my intent, - I'm sure people find it really productive - but it's not for me.  So here is a very high level executive summary of the problems I've had with Rails

__The Command Line Interface__ 

The CLI stuff has just a little bit too many features and options that make it a difficult thing to approach without extensive research.  When I watched some of the [Railscasts](http://railscasts.com/) I found myself going "wait, HOW did he do that?" more than once.  Choice is good but surely Rails is supposed to be easy for the beginner?  Yet I feel the default options are not the ones I would have picked.  You know what - I want to be hand-held once in a while, I'm proud to admit that.

__Too Much Magic__

Rails is very heavily convention based but often there is simply too much convention and I've been left baffled a few times.  Some of the convention is well documented and some of it less so.  When a convention is not obvious it becomes magic and it really feels Rails has a lot of "magic".  Thats not something I want.  To be honest I'd rather write the same 2/3 lines of boilerplate if it meant that, to a new developer, they could understand the convention better.  Take this very trivial example,

```ruby 
class HomeController < ApplicationController
  def index
  end
end
```

And the same in ASP.NET MVC

```csharp 
public class HomeController: Controller
{
	public ActionResult Index()
	{
		return View();
	}
}
```

To me that one line `return View()` tell me that this action will return a View, and now I can infer the convention of `home/index.cshtml` that wee bit easier.  When you start adding in more conventions, a little bit of extra code makes all the difference.  Conventions don't scale unless clearly marked IMHO.

__Bloatyness__

When you generate scaffold in a Rails app, as a typical new developer would, you end up with a awful lot of files.  If I generate a new entity for example this is what I am greeted with,

```bash 
--> rails generate scaffold MyEntity name:String age:Int                         !6580
      invoke  active_record
      create    db/migrate/20120403130542_create_my_entities.rb
      create    app/models/my_entity.rb
      invoke    test_unit
      create      test/unit/my_entity_test.rb
      create      test/fixtures/my_entities.yml
       route  resources :my_entities
      invoke  scaffold_controller
      create    app/controllers/my_entities_controller.rb
      invoke    slim
      create      app/views/my_entities
      create      app/views/my_entities/index.html.slim
      create      app/views/my_entities/edit.html.slim
      create      app/views/my_entities/show.html.slim
      create      app/views/my_entities/new.html.slim
      create      app/views/my_entities/_form.html.slim
      invoke    test_unit
      create      test/functional/my_entities_controller_test.rb
      invoke    helper
      create      app/helpers/my_entities_helper.rb
      invoke      test_unit
      create        test/unit/helpers/my_entities_helper_test.rb
      invoke  assets
      invoke    coffee
      create      app/assets/javascripts/my_entities.js.coffee
      invoke    scss
      create      app/assets/stylesheets/my_entities.css.scss
      invoke  scss
      create    app/assets/stylesheets/scaffolds.css.scss
```

Don't get me wrong - it's nice to be given all these things for free, but it's ultimatley stuff I end up ripping out anyway.  In .NET there is a running joke that the "Empty ASP.NET MVC Project" Template is anything but.  To me this is the same issue except it's something that I'd have to clean up everytime I use the `generate scaffold` command.  This seems worse to me.

## Current Thoughts

I'm not sure were Rails is supposed to stand.  I hear it's great for beginners or for big sites but I couldn't recommend it for either right now.  I realise I should really give it more time before rejecting it entirely but I feel that would be more time wasted.  Rails doesn't make me more productive, at least not right now... perhaps once I repeat the same steps over and over and they become natural I'll see a greater boost in productivity (Ruby on the other hand felt natural and great right away).  Perhaps if I shun the generators and roll my own stuff then I'll be more productive - but then why not use another framework that comes without all the gubbins I don't need?  Sinatra is nice and simple and I can build upon it rather than breakling it apart.  [Ramaze](http://ramaze.net/) seems like a great "next step up" from Sinatra but without as much (perceived) bloat as Rails.  I've also followed the recent [Kickstarter debacle](http://www.kickstarter.com/projects/1397300529/railsapp) from Yehuda - and I agree with him and his mission.

So what I am trying to say is - if you are a person wanting to give the Ruby world a fair shake - DONT start on Rails.  Start simpler and later, if you want, when you've been throughly sold on the Ruby way, try Rails to see what you think.  Start with Ruby, port some code, write a Sinatra app, watch the [Railscasts](http://railscasts.com/) as they are amazing then consider immersing yourself in Rails.

## Educate Me

Finally - educate me.  I'm not some sort of oracle proclaiming the death of a framework that thousands of people love.  I'm not going to make anyone stop and think "I've been doing wrong all my life - screw Rails".  If I'm wrong - tell me.  If you think I need to give Rails another look - tell me why, tell me how.  If you care - show me what I'm missing.