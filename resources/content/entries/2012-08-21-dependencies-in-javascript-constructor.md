---
date: 2012-08-20T23:00:00Z
title: 'Dependencies in JavaScript: Constructor, Setter or Global'
published: true
categories: [JavaScript]
type: article
external: false
---
There has been an interesting conversation on Twitter over the last number of hours between @derickbailey and a number of other people and it all started with this tweet,

<blockquote class="twitter-tweet tw-align-center"><p>anyone have links to articles on why setter injection is evil? /cc <a href="https://twitter.com/jbogard"><s>@</s><b>jbogard</b></a> <a href="https://twitter.com/jeremydmiller"><s>@</s><b>jeremydmiller</b></a> <a href="https://twitter.com/jflanagan"><s>@</s><b>jflanagan</b></a> <a href="https://twitter.com/chadmyers"><s>@</s><b>chadmyers</b></a></p>&mdash; derickbailey (@derickbailey) <a href="https://twitter.com/derickbailey/status/237658824163614720" data-datetime="2012-08-20T21:14:13+00:00">August 20, 2012</a></blockquote>
<script src="//platform.twitter.com/widgets.js" charset="utf-8"></script>

The conversation has been around using DI and IoC in JavaScript (yes I know they are not one in the same, and I know a Container is not a necessary part of DI or IoC thank you very much).  Simple answer is this - good patterns have a place in any language provided they aren't abused mercilessly.  But lets get back to the discussion - why would you use constructor injection over setter injection?  Is setter injection evil?  Obviously no strategy is actually evil per-say - just used in the wrong place at the wrong time. So lets go back to the first question - constructor injection vs. setter injection.

## Constructor Injection or Setter Injection

The simplest answer to this was one I gave @derickbailey earlier,

<blockquote class="twitter-tweet tw-align-center" data-in-reply-to="237664796298854401"><p><a href="https://twitter.com/derickbailey"><s>@</s><b>derickbailey</b></a> resolve mandatory dependenciesvia constructor injection, optional dependencies via setter.  This is a common convention.</p>&mdash; James Hughes (@kouphax) <a href="https://twitter.com/kouphax/status/237792703826522112" data-datetime="2012-08-21T06:06:13+00:00">August 21, 2012</a></blockquote>
<script src="//platform.twitter.com/widgets.js" charset="utf-8"></script>

This little bit of knowledge I've had since living down in the guts of Spring MVC about 5 years ago and it makes a lot of sense regardless of how those dependencies are getting in there.

## Fail Early

When your code goes into an invalid state you want to fail as quickly as possible, this makes debugging much easier and prevents an avalanche of unexpected behaviour.  Using constructor injection (and asserting the existence of dependencies) gives you this ability.

```javascript 
	var MyThingy = function(requiredA, requiredB){
		if(!requiredA) throw "Missing Dependency"	
		if(!requiredB) throw "Missing Dependency"
		
		// rest of my code
	}
	
	MyThingy.prototype.doAThing = function(){
		return this.requiredA.magic() + this.requiredB.beepboop();
	}
	
	var thing = new MyThing(); // FAIL FAIL FAIL
	thing.doAThing();
```

If you tried to achieve the same thing with setter injection you would be failing much later in the proceedings,

```javascript 
	var MyThingy = function(){}
	
	MyThingy.prototype.setRequiredA = function(requiredA){
		this.requiredA = requiredA;
	}
	
	MyThingy.prototype.setRequiredB = function(requiredB){
		this.requiredB = requiredB;
	}
	
	MyThingy.prototype.doAThing = function(){
		if(!this.requiredA) throw "Missing Dependency"	
		if(!this.requiredB) throw "Missing Dependency"
		
		return this.requiredA.magic() + this.requiredB.beepboop();
	}

	var thing = new MyThing(); 
	thing.doAThing(); // FAIL FAIL FAIL
```
	
Now imagine that the last two lines are miles apart in code-lines, you've gone and lost all context when debugging.  Sure in this trivial example it would be fairly easy to resolve this but if you've gone and lost the plot and created a heavily nested object graph you'll be crying before they day is out.

Now setter injection does have it's place - for __optional__ dependencies,

```javascript 
	var MyThingy = function(requiredA, requiredB){
		if(!requiredA) throw "Missing Dependency"	
		if(!requiredB) throw "Missing Dependency"
		
		// rest of my code
	}
	
	MyThingy.prototype.setOptionalC = function(optionalC){
		this.optionalC = optionalC;
	}
	
	MyThingy.prototype.doAThing = function(){
		if(this.optionalC) optionalC("doing a thing...")
		return this.requiredA.magic() + this.requiredB.beepboop();
	}
	
	var thing = new MyThing(a,b);

	thing.setOptionalC(function(s) { console.log(s) });
	thing.doAThing(); // logs "doing a thing..."
```
	
So the extension of the rule is that 

> Anything injected via __setter injection__ needs to be either defaulted initially or checked for existence before use.

## Readability

By following this convention, heck by following any agreed convention, you get better visibility of the expected behaviour of your code.  You know what the object needs to get the job done.  That feels like a poor argument but I think it still warrants a mention here.

## Over Engineering and Deep Graphs

Obviously if you've over-designed your classes or it has grown out of control this approach becomes difficult to manage.  I've seen something akin to this in some projects,

```javascript 
	var x = new CoordinatorOfAllTheThings(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p....)
```

Someone has decided to make every bit of functionality it's own little class or module and so the dependency graph has become incredibly broad.  In fact this might suggest they are creating a deep graph as well and simply passing the dependencies down the chain.  __If you have a lot of dependencies then there is something wrong with the object you are creating__.

For fine grained, inter-object communication dependency injection is fine but when you are scaling up to more coarse, inter-module or component communication an evented or message based approach is almost always a better option.

```javascript 
	// rest of previous code
	
	MyThingy.prototype.doAThing = function(){
		EventStream.trigger("doneathing");
	}
	
	var EventStream {
		trigger: function(){ /* ... */ }
		on: function() { /* ... */ }
	}
	
	var OtherThing = function(requiredD){
		EventStream.on("doneathing", requiredD)
	}
	
	var thing = new MyThingy(a,b);
	var other = new OtherThing(function(){ console.log("stuff done"); });
	
	thing.doAThing(); // logs "stuff done"
```
	
This is a rather contrived example but it should server a point. 

### The Global Issue

You'll notice the EventStream object in the code above is global, you'll also notice I have skipped part of this posts title  (... or Global).  Lets come to that now.  It is possible to achieve the same results as constructor/setter injection using global scope,

```javascript 
	var MyThingy = function(){
		if(!global.requiredA) throw "Missing Dependency"	
		if(!global.requiredB) throw "Missing Dependency"
		
		// rest of my code
	}
	
	MyThingy.prototype.doAThing = function(){
		return global.requiredA.magic() + global.requiredB.beepboop();
	}
	
	var thing = new MyThing(); // FAIL FAIL FAIL
	thing.doAThing();
```
	
So rather than inject the necessary dependencies in we are relying on the fact they exist in global scope.  This works fine in some situations.  The EventStream object above is a global hub that can be used throughout the application.  I'm ok with that.  The risk of global scope is that it's easy for objects to be redefined by other actors in your system so existence or behaviour is never guaranteed.  So use sparingly.

## Moptional

The other alternative approach would be to make everything Moptional.  That is, anything mandatory should be instantiated with a default and provide setter injection for changing default behaviour.

```javascript 
	var MyThingy = function(){
		this.requiredA = new DefaultRequiredA();
		this.requiredB = new DefaultRequiredB();
		
		// rest of my code
	}
	
	MyThingy.prototype.setRequiredA = function(requiredA){
		this.requiredA = requiredA;
	}
	
	MyThingy.prototype.setRequiredB = function(requiredB){
		this.requiredB = requiredB;
	}
	
	MyThingy.prototype.doAThing = function(){
		return this.requiredA.magic() + this.requiredB.beepboop();
	}
	
	var thing = new MyThing();
	this.setRequiredB(new CustomRequiredB())
	thing.doAThing();
```

You kind of get the best of worlds here - not having to worry about default behaviour but have the ability to override this (for example in testing).  But then you need to think about this.  Why are you providing this sort of strategy?  Are you simply hiding all implementation for an object but providing setters and getter to simply aid testing?  If that's the case - STOP.  Your code should be testable but you should not change your code to simply aid testing.  Perhaps its time to go back and re-think your strategy?  Also, for testing - it's entirely possible, given the dynamic nature of JavaScript, to redefine the internal dependencies prior to them being instantiated in your object anyway.  Don't add features or behaviour that you don't actually need to deliver the solution.

## Wrap Up

This was playing over and over on my ride into work this morning (I cycle I have a lot of thinking time) and I wanted to get it out there.  Hopefully be useful to people asking some questions on this topic.

Oh and IoC __Containers__ in JavaScript...... don't.

My $0.02