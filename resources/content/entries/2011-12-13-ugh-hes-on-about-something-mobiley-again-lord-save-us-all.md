---
date: 2011-12-13T00:00:00Z
title: Ugh. He's on about something mobiley again. Lord save us all!
published: true
categories: [Mobile]
type: article
external: false
---
Good timing right?  Fresh of the wave of my "lets have a natter about mobile" blog posts HP went and did something good (for a change, right?).  After the hash that was made of the TouchPad HP went and open sourced the webOS software.  Bundled with this software is [Enyo](https://developer.palm.com/content/api/dev-guide/enyo.html) a mobile framework for WebKit browsers.  Enyo was to be one of the main development platforms for webOS - JavaScript on the front end with node.js services for the backend.  The future was bright.  Then the TouchPad flopped.  Ack well.

On the other hand Enyo is a very nice little framework and in the hands of the Open Source community could be a real winner.  I've had a chance to tinker with over the last few days and thought I'd help get the word out by outlining how to write the obligatory by law ToDo app.  It is currently somewhat similar to the official [FeedReader tutorial](https://developer.palm.com/content/api/dev-guide/enyo/tutorial.html) but it could be easily extended to add more features.  I recommend you look at the original [tutorial](https://developer.palm.com/content/api/dev-guide/enyo/tutorial.html) for more info about how the project is structured as there are certain conventions that may seem odd to your typical web framework.

The entire application, which looks like this (yeah yeah it's not the most beautiful thing on the planet - I suck at design).

![Sample Output for Web View](/images/blog/enyo/todo.png)

Was knocked together with the following bit of code

```javascript
enyo.kind({
  name: "TodoApp",
  kind: enyo.VFlexBox,
  components: [
      {kind: "PageHeader",  components: [
        {kind: "VFlexBox",  flex: 1, align: "center", components: [ {content: "Todos"} ]},
        {kind: "Button", caption: "Clear Complete", onclick: "clearComplete"}
      ]},
      {kind: "RowGroup",  components: [
        {name: "newTask", kind: "Input", hint: "Enter new ToDo here..."},
        {kind: "Button", caption: "Add", onclick: "addTask"}
      ]},
      {kind: "Scroller",  flex: 1,  components: [
        {name: "list", kind: "VirtualRepeater", onSetupRow: "getTask", components: [
          {kind: "Item", layoutKind: "HFlexLayout", align:"center", components: [
            {name: "taskStatus", kind: "CheckBox", style: "margin-right:10px;", onChange: "completeTask" },
            {name: "taskDescription" }
          ]}
        ]}
      ]}
  ],

  create: function(){
    this.todos = [];
    this.inherited(arguments);
  },

  addTask: function(){
    var task = this.$.newTask.getValue();
    this.todos.push({ description: task, done: false });
    this.$.list.render();
  },

  getTask: function(sender, idx) {
    var todo = this.todos[idx];
    if (todo) {
        this.$.taskDescription.setContent(todo.description);
        return true;
    }
  },

  completeTask: function(sender, evt){
    this.todos[this.$.list.fetchRowIndex()].done = sender.getChecked();
  },

  clearComplete: function(){
    var buffer = [];
    for(var i = 0; i < this.todos.length; i++) if(!this.todos[i].done){
      buffer.push(this.todos[i]);
    }
    this.todos = buffer;
    this.$.list.render();
  }

});
```

Those of you with some experience in Sencha Touchwill notice that the declarative syntax is quite similar to STs except slightly simpler.  So lets break out some of the features that brought this example together.

```javascript
{kind: "Button", caption: "Clear Complete", onclick: "clearComplete"}
```

This is one of the component declarations that make up the suite of components that form the "kind" (Enyo speak for component or object).  This is somewhat similar to the DOM (COM anyone?  Component Object Model) except in JSON form.  You can specify a component type (kind) and apply properties (which may or may not do something depending on the kind).  Events are also declared inline (see onclick) and map to function names within the main kind declaration.  Obviously if this declaration started getting too big and bloated I could decompose it into smaller components all handling their own stuff.

One other thing not highlighted in my demo is that backend service calls can also be defined within this component model and then called programatically.  While this felt alien to me initially it is pretty similar to the way inline stores are declared in Sencha - except slightly less engineered.

```javascript
{kind: "Scroller",  flex: 1,  components: [
    {name: "list", kind: "VirtualRepeater", onSetupRow: "getTask", components: [
      {kind: "Item", layoutKind: "HFlexLayout", align:"center", components: [
        {name: "taskStatus", kind: "CheckBox", style: "margin-right:10px;", onChange: "completeTask" },
        {name: "taskDescription" }
      ]}
    ]}
]}
```

The scroller definition above has a few nice features.  The use of "flex" to manage different screen sizes or viewport resizes and create very nice flexible layout.  The VirtualRepeater which is a templated control that generates items from a collection based on the defined template.  The Virtual part is useful for large lists and ensures that only the minimum amount of items are rendered (just before they are needed).

```javascript
create: function(){
    this.todos = [];
    this.inherited(arguments);
}
```

Create is essentially our component constructor and allows us to set some things up in advance.  Notice the use of the `inherited()` method - this ensures the superclasses `create` method is also called (inheritence chaining)

```javascript
this.$.list.render();
```

`this.$` is a handy property.  It is essentially a hash of all the named components that are direct and indirect children of the defined component.  This makes it very easy to access a certain component if you want to manipulate it.

```javascript
getTask: function(sender, idx) {
    var todo = this.todos[idx];
    if (todo) {
        this.$.taskDescription.setContent(todo.description);
        return true;
    }
}
```

`getTask` is the method responsible for populating an Item template in the repeater for each row.  This is where I bind the values of the current collection to the Item kind.

There you have it - yet another ToDo app spat out into the world!  Just what we needed :)

## It not all Unicorns and Rainbows

Yeah Enyo isn't perfect (shock horror).  There are some quite annoying and some serious bugs that I have noticed in my hacking.

### Data Association

When rendering the Repeater I can't see a way to easily attach data items to that item instance.  I worked it out eventually by using `this.$.list.fetchRowIndex()` but I think you would need to then manage your own data association.  Not a big issue but it didn't feel right to me at the time.

### iPad/iPhone issues

The one big issue I had with the iPad was that text fields often didn't bring the keyboard up (more often than not).  Obviously this could be a bit of a deal breaker but there could be a simple fix.  Some other minor quirks included scrolling issues when multiple scroll areas existed and choppy animations in the kitchen sink style demo (Sampler).

The example apps display and work on the iPhone but they are super tiny.  I am not sure if this is just a lack of correct formatting within my HTML or if Enyo is purely designed for larger screens.

### Events

At one point I wanted to bind to an event of a component programatically (rather than declaring it inline).  I still can;t find a way to do this and I wonder if this could be a bit of hindrence when you get into a truly dynamic application.  Thought this could be me being a bit stupid as usual.

## Conclusion

Enyo v1.0 is still young in the hands of the OSS community but it's started out strong and could well be a strong competitor to something like Sencha Touch if it keeps up this pace.  Hopefully the community embraces it and allows it to grow.  It could well be great.

So yes, so far I like it but I've only been hacking a few days.  Hopefully I get to spend more time with it over the next few weeks.

The sample project is available on my [BitBucket Repo](https://bitbucket.org/kouphax/enyo-todos) if you want to tinker.