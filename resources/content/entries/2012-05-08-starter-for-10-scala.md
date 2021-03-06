---
date: 2012-05-07T23:00:00Z
title: Starter for 10 - Scala, Play 2 and Mongo
published: true
categories: [Scala, MongoDB]
type: article
external: false
---
This post is going to cover the whole "getting up an running" for a simple Scala stack - namely Scala, Play 2 and Mongo.  2012 has been the year of the polyglot for me.  I've been working on projects with .NET, Node.js, Ruby and Scala in the mix.  I've seen how Rails can really boost developer productivity (compared to the likes of ASP.NET MVC or other, less opinionated, frameworks) but I must say - In terms of developer productivity, at least for me, nothing has come close to the Scala/Play/Mongo combo.  I hadn't touched functional programming since SML in university but I found Scala allowed me to express myself in a very terse manner.  Combine that with Play and its straightforward, uncomplicated approach to building web apps and Mongo with its schemaless nature then its productivity++ all the way.

It's worth pointing out right away that James Ward already has a [similar post](http://www.jamesward.com/2012/02/21/play-framework-2-with-scala-anorm-json-coffeescript-jquery-heroku) and a [(currently Java only) tutorial](https://github.com/jamesward/play2torial/blob/master/JAVA.md).  These are  much better but they dont cover the Mongo/Caabah/Salat side of things (not that it is particularly difficult) so at least I've got a leg to stand on.

## Installing

> Versions used in this post
>
>   - Scala (2.9.1)
>   - Play (2.0.1)
>   - Mongo (2.0.4)

I'm going to assume that you are capable of installing software (by following basic instructions) so I won't waste your time covering this.  Here are some links to kick you off,

- [Scala](http://www.scala-lang.org/downloads)
- [Play 2](http://www.playframework.org/download) (it just goes on your `PATH`)
- [Mongo](http://www.mongodb.org/downloads)

Alternatively if you are on OSX - [Homebrew](http://mxcl.github.com/homebrew/) has got everything you need at the time of putting this together.

## Generating your Application

Play is a command line tool and can be used to generate your basic app skeleton,

```bash
play new <project name>
```

Just fill in the project name and you will be guided through the basic generation of the project, e.g.

```bash
--> play new sampleapp
        _            _
  _ __ | | __ _ _  _| |
| '_ \| |/ _' | || |_|
|  __/|_|\____|\__ (_)
|_|            |__/

play! 2.0.1, http://www.playframework.org

The new application will be created in /Users/kouphax/Projects/temp/sampleapp

What is the application name?
> sampleapp

Which template do you want to use for this new application?

  1 - Create a simple Scala application
  2 - Create a simple Java application
  3 - Create an empty project

> 1

OK, application sampleapp is created.

Have fun!
```

Indeed - fun you shall have.

## Dependencies

Working with Mongo directly is all well and good but it can be made a bit simpler with a few dependencies.  Namely [Casbah](http://api.mongodb.org/scala/casbah/2.0/) and [Salat](https://github.com/novus/salat).  Casbah is the official Scala Toolkit for MongoDB and Salat allows you to map to and from the generic Mongo object classes into more concrete case classes.

To add the dependencies you need to update the `Build.scala` file in the generated `project` directory.  Firstly you need to update the `appDependencies` to create the dependency on the new two libraries,

```scala
val appDependencies = Seq(
  "com.mongodb.casbah" %% "casbah" % "2.1.5-1",
  "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT"
)
```

Then you'll need to add the `resolvers` to the `main` project declaration,

```scala
val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings
  resolvers ++= Seq(
    "repo.novus snaps" at "http://repo.novus.com/snapshots/")
)
```

> UPDATE:  Since this article is a bit old but still getting plenty of hits I have noticed that people are having issues resolving dependencies on salat and casbah - this is because of a reporsitory change.  Simple fix just add the following line to the resolvers Seq above,

```scala
"casbah" at "https://oss.sonatype.org/content/groups/scala-tools/"
```

> This should be enough to get you going with the rest of this article however there will be some major changes expected in Scala 2.10 that may well fundamentally alter the need to use Salat etc.  Be warned.

And thats the project set up with all its dependencies ready to hack on.  So lets drive a spike through this stack and see what it looks like shall we?  Yes why not, tallyho old bean!

## The Model & Data Access

First things first lets create our model.  So we are going to write a very basic registration app.  People "register" on the site, for fun or something, I dunno as our stakeholders - I just write the code.

Our model is super simple,

```scala
case class Registration(
  username: String,
  password: String,
  confirm:  String,
  realName: String
)
```

Now to the interesting part.  To persist this in a Mongo data store we need to make use of the two libraries we imported previously,

```scala
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._

object Registrations {
  val registrations = MongoConnection()("sampleapp")("registrations")

  def all = registrations.map(grater[Registration].asObject(_)).toList
  def create(registration: Registration) {
    registrations += grater[Registration].asDBObject(registration)
  }
}
```

First things first we create a connection using the `MongoConnection` class and grabs the `registrations` collection from the `sampleapp` store.  Now it's worth noting that provided Mongo is up and running we don't actually need to create either the store or the collection - the act of acting on a collection is enough to create both.

Next up I defined two methods `all` and `create`.

- `all` simply pulls out all the collection data using Casbah, performs a map over the collection (which is a collection of `MongoDBObject`/`DBObject`s) and uses the `grater` object provided by Salat to map from these generic objects to our `Registration` case class.
- `create` simply adds an element (cast from a `Registration` class to an acceptable `DBObject`) to the collection.

Now isn't that a nice terse data access layer?  Yes it is.  You want to make changes?  Go ahead - the fear of change is gone or much reduced compared to the typical approach.

## Controllers

Next up we want to add a register controller and update the existing `Application` controller.

```scala
object Application extends Controller {
  def index = Action { implicit request =>
    Ok(views.html.index(Registrations.all))
  }
}
```

The only difference here is the `Registrations.all` so we can list the registrations in the UI.  Lets jump over the the register controller

```scala
object Register extends Controller {
  def registrationForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "confirm" -> nonEmptyText,
      "realName" -> text
    )(Registration.apply)(Registration.unapply)
      verifying("Passwords must match", fields => fields match {
        case Registration(_, password, confirmation, _) => password.equals(confirmation)
      })
  )

  def index = Action { implicit request =>
    Ok(views.html.register(registrationForm))
  }

  def register = Action { implicit request =>
    registrationForm.bindFromRequest.fold(
      form => BadRequest(views.html.register(form)),
      registration => {
        Registrations.create(registration)
        Redirect(routes.Application.index()).flashing("message" -> "User Registered!")
      }
    )
  }
}
```

OK thats a lot to take in so lets break it down a bit.

### The Form

```scala
def registrationForm = Form(
  mapping(
    "username" -> nonEmptyText,
    "password" -> nonEmptyText,
    "confirm" -> nonEmptyText,
    "realName" -> text
  )(Registration.apply)(Registration.unapply)
    verifying("Passwords must match", fields => fields match {
      case Registration(_, password, confirmation, _) => password.equals(confirmation)
    })
)
```

The Form concept in Play is a bit like a ViewModel of sorts.  It defines validation and mapping rules and allows you to pass a structured object between the controller and the view (inluding error information etc.).  In fact you aren't even obliged to bind this to a class, they can exists as themselves.  My example above shows you how to bind to and from our `Registration` class as well as some field and crossfield/global validation.

### Model Binding

```scala
registrationForm.bindFromRequest.fold(
  form => BadRequest(views.html.register(form)),
  registration => {
    Registrations.create(registration)
    Redirect(routes.Application.index()).flashing(
      "message" -> "User Registered!"
    )
  }
)
```

This little functional approach allows us to bind a request to our `registrationForm` and react to invalid input.  In the case of an invalid bind the first argument into `fold` is executed which returns a bad request with an instance of the form.  In the case of the happy path we simply push our `registration` instance into the data store and redirect to the `index` action along with a flash message.

### Flash

Flash is like a carefully curated session variable that exists only for the next request.  This allows you to pass messages or some very transient data between requests.  This is also accessible in the view which is handy.

## Routes

Routes in Play are written into the `config/routes` file and this is used as a basis to generate reverse routes (this gives is us statically typed routes via the `routes` object).

```scala
# Routes
# This file defines all application routes (Higher priority routes first)
# ~~~~

# Home page
GET     /                           controllers.Application.index
GET     /register                   controllers.Register.index
POST    /register                   controllers.Register.register

# Map static resources from the /public folder to the /assets URL path
GET     /assets/*file               controllers.Assets.at(path="/public", file)
```

So I added two new routes for register that allows us to view and submit our registration form.

## The Views

Right lets do something a little neat - remember I mentioned James Ward earlier?  Well he curates a little project called [webjars](http://webjars.github.com) which allows us to add client side resources as managed dependencies to our project.  So lets prettify our UI with a bit of [Twitter Bootstrap](http://twitter.github.com/bootstrap/) love.

First thing we want to do is update our `Build.scala` file with webjars resolver and our bootstrap dependency

```scala
val appDependencies = Seq(
  "com.github.twitter" %  "bootstrap"  % "2.0.2",
  // other deps...
)

val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
  resolvers ++= Seq(
    "webjars"          at "http://webjars.github.com/m2",
    // other resolvers...
  )
)
```

Then in our `main.scala.html` we can reference the bootstrap assets in the normal way,

```html
<link rel="stylesheet" media="screen" href="@routes.Assets.at("stylesheets/bootstrap.min.css")">
```

### Index (index.scala.html)

Here we simply list the current registered people,

```html
@(registrations: List[Registration])(implicit flash: Flash)
@main("Super Simple Sample") {
  @if(flash.data.contains("message")){
    <div class="alert alert-success">@flash.get("message")</div>
  }
  <a href="@routes.Register.index" class="btn btn-primary">Register</a>
  <table class="table table-bordered">
    <thead>
      <tr>
        <th>Username</th>
        <th>Real Name</th>
      </tr>
    </thead>
    <tbody>
      @registrations.map { registration =>
        <tr>
          <td>@registration.username</td>
          <td>@registration.realName</td>
        </tr>
      }
    </tbody>
  </table>
}
```

The interesting thing about Play 2 views is that they are just Scala.  Yep neat.  In fact any .NETters will feel quite at home as they are reminiscent of Razor views too.

### Register (register.scala.html)

```html
@(registrationForm: Form[models.Registration])(implicit flash: Flash)

@import play.api.i18n._
@import views.html.helper._

@input(field: Field, label: String, fieldType: Symbol = 'text) = {
  <div class="control-group @if(field.hasErrors) {error}">
    <label class="control-label" for="@field.id">@label</label>
    <div class="controls">
      <input type="@fieldType.name" value="@field.value" name="@field.id" />
      @if(field.hasErrors){
        <span class="help-inline">
          @Messages(field.error.head.message)
        </span>
      }
    </div>
  </div>
}

@main("Super Simple Sample") {
  @form(action = routes.Register.register, 'class -> "form-horizontal") {
    <fieldset>
      <legend>Registration</legend>
      @registrationForm.globalError.map { error =>
        <div class='row'>
          <div class="alert alert-error">@error.message</div>
        </div>
      }
      @input(registrationForm("name"), "Username")
      @input(registrationForm("password"), "Password", 'password)
      @input(registrationForm("confirm"), "Confirm Password", 'password)
      @input(registrationForm("realName"), "Real Name")
    </fieldset>
    <input type="submit" class="btn" value="Register"/>
  }
}
```

You can see from the source there that I've crafted my own helper function to keep things nice and DRY and avoid having to write out the twitter bootstrap required structure over and over.

## Running and Wrap Up

Right about now the solution is done so with a simple `play run` you should be able to register and view current registrations.  Excellent.  If you want to skip everything and just get my code you can clone the [Github Repo](https://github.com/kouphax/scala-sampleapp).

OK so it's not the most useful app in the world - that was never the intention.  But look at the code - it's very terse and the time required to write this post was considerably more than the time required to implement the solution.  Joy!