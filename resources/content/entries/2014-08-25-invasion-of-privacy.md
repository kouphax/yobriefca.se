---
date: 2014-08-25T00:00:00Z
title: "Invasion of Privacy"
published: true
categories: [Java]
type: article
external: false
---

There is a pattern I see more and more often in Java projects that use Spring as an IoC container thingymajig.  I doubt it's limited to Spring and probably common among other IoC containers but I don't really get a lot of exposure to others because,

1. When it's my own project I generally don't need one, and,
2. When it's a project I'm involved in most people tend toward Spring (bar that one time Guice was preferred).

So don't consider this a rant against Spring and more around a common pattern that seems to be creeping in to many codebases.

But yes, the problem, check this out.

```java
@Component
public class MyBestService {

	@Inject
	private ADependency dependency;

	public void doAThing() {
		dependency.doStuff();
		// more things
	}
}
```

I see this in high ranking Google search results and documentation as an accepted standard approach.  Problem is this introduces two very terrible problems.

1. It hides dependency knowledge 
2. It __poisons__ your domain with technical design considerations
3. It bleeds across the codebase.

## Hiding Knowledge

This is easily the worst problem IMHO. Without peering into this the code for this class there is simply no way to know what this class depends on.  Even then there is little way to know if the dependency is __required__ or __optional__.

The minute you remove explicitness from code you've started down a dangerous path.  It becomes impossible to reason about a system without knowing the entire system.  The friction this causes grows with time.

## Poisoning your Domain

If you are injecting private fields into your classes you'll probably need some sort of mechanism to do this.  Enter annotations.  Commonly you'll see `@Inject` from the `javax.inject` namespace being used.  While `@Inject` is more generic than framework specific annotations it is still a __technical consideration__ it isn't something related to your __domain__.  Your classes should be __pure__ in the sense they should be simple and reflect the domain rather than how you built it.  The mechanism for IoC is completely irrelevant to your solution.

Worse, now you've made injection a special case, your tests need  built in a special way - `@InjectMocks` springs to mind (no pun intended).

## Bleeding Across the Codebase

When you use private field injection you see `@Inject` everywhere. Jumping to what is injected is a matter of understanding the dependency chain which is spread across multiple files.  There is no centralised pool of knowledge about how your system is wired together and this makes it harder to reason about.

## Remediation

First and foremost __build your system without a DI framework as a first class citizen__.  Of course use IoC to prevent heavy coupling and potential God classes but do it au naturale.  

Secondly use __constructor injection__.  Somehow the people leaning on frameworks forgot about constructor injection.  They also forgot about setter injection but I've often found __optional__ dependencies are also a bit of a design smell so I'm not really recommending them.  Of course the frameworks themselves already support constructor injection so it won't be a big leap to change your evil ways.  

So this,

```java
@Component
public class MyBestService {

	@Inject
	private ADependency dependency;

	public void doAThing() {
		dependency.doStuff();
	}
}
```

Becomes,

```java
public class MyBestService {

	private ADependency dependency;

	public MyBestService(ADependency dependency) {
		this.dependency = dependency;
	}
	
	public void doAThing() {
		dependency.doStuff();
	}
}
```

Constructor injection is beautifully explicit.  You can't construct a new instance of a class without explicitly declaring its dependencies - its right there for all the world to see.  You could argue that this makes passing many dependencies very noisy.  You'd be right. __Bad design should be noisy__ and passing 30 dependencies into a constructor is __bad design__. This is something `@Inject` can easily hide as you add more and more dependencies to a class as there is no requirement for it to be centralised.  

This has a knock on effect on your tests too - it makes them __normal__.  You are no longer bound by whatever framework you use to inject mocks and you can explicitly pass stubs without having to understand any particular magic.  Or - if you're still so inclined you can still use your framework of choice.

Finally __use a centralised way to bootstrap your application__.  Spring has `AppConfig`, Guice has `Module` and framework-less stacks use `new` in the right place, you do remember `new` right?  This allows you to push the logic for constructing your dependency graph into a central place that can and should be readable making it much easier to understand the design of your system and refactor easier.

So there you go - use containers if it makes your life easier but try and avoid making your code either magic or noisy.
