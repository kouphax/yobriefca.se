---
date: 2011-08-31T23:00:00Z
title: Tinkering with Agility
published: true
categories: [JavaScript]
type: article
external: false
---
Having spent a few days tinkering with [agility.js][agility] I have come to the following conclusions

1. It's a bit buggy in places (it is currently on v0.1 so this is to be expected)
2. At first glance code written against [agility.js][agility] looks like a total mess though it appears to be a weird optical illusion because once you grasp the [agility.js][agility] approach the code makes total sense
3. [Agility][agility] would fit well into an existing codebase as much as into a new one.  Certainly more so that some of the other frameworks.  And finally,
4. I like it

You could almost class [agility.js][agility] as a micro MVC framework (and we all know how I like them).  I say that because it takes an incredibly simple approach to MVC as well as offering a very small but (IMHO) complete feature set.  Unlike [knockout](http://knockoutjs.com/), which also makes use of the `data-` attributes for model binding (and more), [agility.js][agility] uses the `data-bind` attribute for model binding and thats it.  

With Agility you have that ability to compose UI's from discrete little self-contained, and/or nested agility objects that behave as standalone MVC implementations.  This is _kind of_ different to the standard appraoch of other MVC-like JS frameworks which generally go for the create a single large MVC architecture and manage components within that architecture.  I am not saying this appraoch is forbidden or impossible in the other frameworks - it's just not how a noob would go about things.  At least I never did when I first started looking at them.

To play with agility I went ahead and created "yet another Todo app" ([source](https://bitbucket.org/kouphax/agility-tinkering)) complete with server side persistence and some responsive UI elements (Complete button will vanish when a Todo is completed - wow).  I'll not break the entire thing down here but here are some choice cuts (in CoffeeScript cause I am THAT cool)

    todo = $$ 
      model: {}, 
      view:
        format: '''
          <li>
            <span data-bind="description"/>
            <button class="complete-task">Complete</button>
            <button class="delete-task">Delete</button>
          </li>
        ''',
      controller: 
        'create': -> 
          @view.$().toggleClass "task-done", @model.get("complete")
        'change:complete': -> 
          @view.$().addClass "task-done"
        'click .complete-task': ->  
          @model.set "complete": true
          @persist $$.adapter.restful, collection:'todos'
          @save()
        'click .delete-task': ->  
          @persist $$.adapter.restful, collection:'todos'
          @erase()

The code above shows the creation of a Todo entity i.e. a single entry in the list.  `$$` is the Agility factory method and will create an MVC based entity out of the object passed in.  I have decalred a view, a model (albeit empty) and a controller with an assortment of tasks for the entity.

This will give you two-way model binding, event wiring up and even custom events totally for free plus a very simple way to react to events.  The controller is essentially the event handler so things like `click .delete-task` will bind the handler to the click event of the element with the `delete-task` class (the typical full range of selectors are available thanks to jQuery, Zepto integration forthcoming too).  Events like `create` and `change:complete` are custom events given to us by agility and allow us to listen to key points in the lifecycle of the object.  In fact `change:complete` is a demonstration of something truly awesome (IMHO as usual).  The `change` event will fire when model properties are changed - the `change:<property_name>` will only fire when the `complete` property changes.  Noice!

My Todo list object then creates instances of the `todo` object above after pulling the info from the server.  It uses agility's inheritance strategy to to create them from the `todo` prototype I pass in

    todos = $$
      model: {},
      view: 
        format: '''
          <div>
            <input type="text" id="newItem" />
            <button class="add-task">Add</button>
            <ol/>
          </div>
        '''
      controller:
        'click .add-task': -> 
          item = @view.$('#newItem').val()
          if item isnt ""  				
            newTodo = $$ todo, description: item
            newTodo.persist $$.adapter.restful, collection:'todos'
            newTodo.save()
            @empty()
            @gather todo, 'append', 'ol'

Next I append the `todos` object to the document (which renders it and wires things up)

    $$.document.append todos

Persistance is provided as a plugin of sorts with ability to provide your own adapters.  You get a RESTful ajax adapter with Agility.  I use this to provide persistence methods on my objects and let agility set that up.

    todo.persist $$.adapter.restful, collection:'todos'
    todos.persist()

There does seem to be a bug with the factory method and persitance enabled object forcing me to call `persist` prior to any sort of persitence operation but hopefully we will see that resolved soon.

Finally I fetch from the server and wipe the awesome sauce from my lips and beard.
 
    todos.gather todo, 'append', 'ol'
    
I've went on more than I wanted to but hey it's worth it.  Hopefully this has helped clarify agility to a few people or at least got people intrigued.  Feel free to pull down the [source](https://bitbucket.org/kouphax/agility-tinkering) and have a go yourself otherwise pop on over to the [Agility.js][agility] site and read the excellent docs.

  [agility]: http://agilityjs.com/