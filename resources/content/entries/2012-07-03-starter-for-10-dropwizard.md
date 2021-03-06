---
date: 2012-07-02T23:00:00Z
title: '[Quick and Dirty, Super Ugly,] Starter for 10 - Dropwizard'
published: true
categories: [Java, DropWizard]
type: article
external: false
---
So once again I'm writing Java without a gun to my head, and once again I'm finding it enjoyable.  Don't get me wrong Java as a language feels very long winded, old and cumbersome but the frameworks and stuff that exist around it actually make those issues, less prominent, so much so I think I could easily live with the noise that Java brings.

Anywho.

[Dropwizard](http://dropwizard.codahale.com/) is a framework for creating very simple ReSTful Web Services in Java.  Not just that but it also bundles some nice best practise in with it as well.  Things like pluggable health checks, logging, configuration, content negotiation etc.  And, because it's fairly lightweight its fast.  Very fast.

In fact you can think of Dropwizard as a collection of pre-existing tools glued together with some simple features to create a cohesive stack for HTTP+JSON web services.  You've got [Jetty](http://www.eclipse.org/jetty/), [Jersey](http://jersey.java.net/), [Jackson](http://jackson.codehaus.org/), [Metrics](http://metrics.codahale.com/), [Logback](http://logback.qos.ch/), [JDBI](http://www.jdbi.org/) and more.  Dropwizard give you the glue that binds these along with a simple pipeline that makes use of these technologies.

## Simple Todo

I had intended to do something a little more substantial but I am about to make a small annoucemnet that has kind of effectivley stopped me expanding this app much further.  So lets create a simple Todo App.

### pom.xml

As with most Java projects lets start with the `pom.xml` which is effectively our project definition.  I'll not show you my entire POM, at least not on the first date, but let me reveal the important parts.
 
 Beause it's awesome I want to include WebJars so I add my repository,
 
```xml
    <repositories>
        <repository>
            <id>webjars</id>
            <url>http://webjars.github.com/m2</url>
        </repository>
    </repositories>
```

Next I add my dependencies,

1. Dropwizard Core which has everything you'll need for this
2. jQuery from the WebJar repository

```xml
    <dependencies>
        <dependency>
          <groupId>com.yammer.dropwizard</groupId>
          <artifactId>dropwizard-core</artifactId>
          <version>0.4.2</version>
        </dependency>
        <dependency>
          <groupId>com.jquery</groupId>
          <artifactId>jquery</artifactId>
          <version>1.7.2-1</version>
        </dependency>
    </dependencies>
```

I also add the [shade plugin](http://maven.apache.org/plugins/maven-shade-plugin/) which will let me build a Fat Jar (a jar with all my dependencies and what not in it).

```xml
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
        <version>1.6</version>
        <configuration>
            <createDependencyReducedPom>true</createDependencyReducedPom>
            <filters>
                <filter>
                    <artifact>*:*</artifact>
                    <excludes>
                        <exclude>META-INF/*.SF</exclude>
                        <exclude>META-INF/*.DSA</exclude>
                        <exclude>META-INF/*.RSA</exclude>
                    </excludes>
                </filter>
            </filters>
        </configuration>
        <executions>
            <execution>
                <phase>package</phase>
                <goals>
                    <goal>shade</goal>
                </goals>
                <configuration>
                    <transformers>
                        <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
                        <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                            <mainClass>se.yobriefca.TodoService</mainClass>
                        </transformer>
                    </transformers>
                </configuration>
            </execution>
        </executions>
    </plugin>
```
    
### Configuration

Dropwizard has the ability to map configuration options (from a YML or JSON file) into concrete classes that can be accessed by your services.  So first I created a todo.yml at the root of the project (beside the pom.xml) with some basic configuration

```xml
http:
    rootPath: "/app/*"
```

This configuration tells Dropwizard that my services are located at a url starting with  `/app` - eg `/app/todo`.  This is necessary as I want to create an asset later that maps to the root url `/` that is the default of Dropwizard.  At this point I haven't needed any of my own configuration but if I did I could create a POJO that maps to proeprties in this file.  The [official guide](http://dropwizard.codahale.com/getting-started/#creating-a-configuration-class) has more on this.  For now I will create an empty `Configuration` class in case I want to extend it later (yeah I know YAGNI YAGNI KISS YAGNI - shutuppayourface!)

```java
package se.yobriefca;

import com.yammer.dropwizard.config.Configuration;

public class TodoConfiguration extends Configuration {
}
```

### Service

The service is the core class in our solution.  It's the thing that has the `main` as an entry point.  The thing that "set us up the resouces" and registers healthchecks and controls our service lifecycle.  You'd think there would be loads of code here?  Right?  Wrong!

```java
package se.yobriefca;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.bundles.AssetsBundle;
import com.yammer.dropwizard.config.Environment;
import se.yobriefca.resources.TodoResource;

public class TodoService extends Service<TodoConfiguration> {
    public static void main(String[] args) throws Exception {
        new TodoService().run(args);
    }

    private TodoService() {
        super("todo");

        addBundle(new AssetsBundle("/assets", "/"));
        addBundle(new AssetsBundle("/public", "/public/"));
    }

    @Override
    protected void initialize(TodoConfiguration configuration, Environment environment) {
        environment.addResource(new TodoResource());
    }
}
```

So all services extend `Service` and have a `Configuration` type (FYI these config types can be nested so its not as restrictive as I first thought).  Basically what we have is a `main` method that create an instance of our service and runs it.  Upon creation we add two bundles to the service.  A bundle is bascially a bit of functionality and in this case we create two `AssetBundles` which can be used to serve static content (embedded resources) upon request to a specific URL (think things like CSS, JavaScript, HTML etc.)  You'll notice I am mapping 2 things bundles here and this is because I need to map to the WebJars directroy as well.  So when I got to `localhost/index.html` it will match the url pattern in the first AssetsBundle and look for a file `src/main/resources/assets/index.html`.

Finally we have the `initialize` method that allows us to decare mutliple resources and healthchecks against our environment.  We also get handed our configuration class we decalred in advance.

### A bit about healthchecks

Healthchecks are little classes that perform some sort of test on a part of your solution.  For example we could have a database connectivity check.  These healthchecks are made available on the [admin section that is bound to another port](http://dropwizard.codahale.com/manual/core/#health-checks) when your app starts.  

Now I didn't define any for this example and for that I feel absolutely terrible.  Seriously I kind of feel sick thinkging about it.  And whats worse Dropwizard kindly reminds me everytime I start the app - using LOTS of exclamantion marks and CAPS!!!  IRL you should really not follow my example here.

### Resources

Resources are resources as they are identified in ReSTy things.  For example a `User` is a resource or an `Order` etc.  These things will respond to HTTP requests matched against the URL and the HTTP Method used.  In my example I have defined a resource `Todo` that can be used to list, create and delete (I didn't need anything else for this).

```java
@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
public class TodoResource {
    private static final ArrayList<Todo> todos = Lists.newArrayList();

    @GET
    public Collection<Todo> list() {
        return todos;
    }

    @POST
    public Todo add(@Valid Todo todo){
        todos.add(todo);
        return todo;
    }

    @DELETE
    @Path("/{id}")
    public Todo delete(@PathParam("id") final LongParam id){
        Todo todo = null;
        List<Todo> todoCopy = Lists.newArrayList(todos);

        todos.clear();

        // loop past all items in the list and replace the changed one
        for (Todo element: todoCopy) {
            if(element.getId() != id.get()){
                todos.add(element);
            }else{
                todo = element;
            }
        }

        return todo;
    }
}
```

Again not a great deal of code and what code that is there is fairly straightforward - some would say boilerplate.  We use annotations to map our HTTP Methods `@GET`, `@POST` and `@DELETE` as well as a `@Path(...)` annotation to specify what url this resource resonds to.  Another nice feature we can see annotated here is we get both validation (via the @Valid annotation which is based on Hibernates Validation framework) and JSON model binding via Jackson and model annotations (get to that in a minute).

For this simple demo I am storing a list of Todos in an ArrayList in memory (top of the class).

### Model

The model is, bar all the annotations to enable serialisation, fairly POJO-like,

```java
public class Todo {

    // Not So Universally Unique!
    private static final AtomicLong NSUUID = new AtomicLong();

    @JsonProperty
    private final Long id;

    @NotEmpty
    @JsonProperty
    private final String content;

    @JsonCreator
    public Todo(@JsonProperty("content") String content) {
        this.id = NSUUID.incrementAndGet();
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public Long getId(){
        return id;
    }

}
```

I've put this in more for completeness as there is little involved in this.

### index.html

I am not going to create some elaborate over architected front end here - just a few lines of JavaScript (ignoring the rather large jQuery library included of course) and not styling.

```html
<!DOCTYPE html>
<html>
  <head>
    <title>Demo</title>
  </head>
  <body>
    <div>
      <input type="text" id="task" /> <button id="addTask">Add Task</button>
    </div>
    <ul id="todos"></ul>
    <script type='text/javascript' src='/public/jquery.min.js'></script>
    <script>
      $(function(){
        // load and replace the list
        $.getJSON("/app/todos", function(todos){
          for(var i = 0; i < todos.length; i++){
            $("#todos").append(
              "<li><input type='checkbox' data-id='" + todos[i].id +"'/>" +
                todos[i].content +
              "</li>"
            )
          }
        });

        // manage adding a new task
        $("#addTask").click(function(){
          $.ajax({
            url:         "/app/todos",
            type:        "POST",
            data:        JSON.stringify({ content: $("#task").val() }),
            contentType: "application/json; charset=utf-8",
            dataType:    "json",
            success:     function(data, textStatus, jqXHR){
              $("#todos").append(
                "<li><input type='checkbox' data-id='" + data.id +"'/>" +
                  data.content +
                "</li>"
              )
            }
          })
        })

        $("#todos").delegate("input[type=checkbox]", "click", function(){
          var chk  = $(this),
          item = chk.parent("li"),
          id   = chk.data("id");

          $.ajax({
            url:         "/app/todos/" + id,
            type:        "DELETE",
            contentType: "application/json; charset=utf-8",
            dataType:    "json",
            success:     function(data, textStatus, jqXHR){
              if(data.id === id){
                item.css({"text-decoration": "line-through"});
                chk.remove();
              }
            }
          })
        })
      })
    </script>
  </body>
</html>
```

And thats it.  Oh wait I also added a boot command that peforms the maven packing and starts the jar.

```
mvn package;java -jar target/todo-0.1.jar server todo.yml
```

Just run `./boot` (assuming you've chmod'd the file to be executable) and you'll have, assuming I (or you) haven't made any mistakes, a Todo service running on port 8080 (with admin/diagnostics stuff on port 8081).

As always the [example code is available on Github](https://github.com/kouphax/dropwizard-todo).  Sorry for the rushed nature of this post, I'll hopefully keep tweaking the example on Github till it's a bit nicer - perhaps even with tests and comments (YEAH RIGHT!!!!)

In short - Dropwizard - simple ReST services, very little config, very nice.