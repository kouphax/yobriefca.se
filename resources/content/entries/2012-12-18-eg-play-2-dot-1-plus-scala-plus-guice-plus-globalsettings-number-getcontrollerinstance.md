---
date: 2012-12-18T00:00:00Z
title: Eg. Play 2.1 + Scala + Guice
published: true
categories: [Scala]
type: article
external: false
---
> TL;DR Here is some [sample code](https://github.com/kouphax/play21guice) of how to use Play 2.1s GlobalSettings#getControllerInstance feature with Google Guice.

I've had the chance to take Play 2.1 RC 1 out for a spin recently and in a bid to make my project a bit more testable I decided to go ahead and give dependency injection a bit of a spin again.  While things like the cake pattern and general dynamic composition are alright I've never been happy with the code that I produce using them. So I decided to give [Guice](https://code.google.com/p/google-guice/) a blast as it seemed very much aligned with how I approached DI in the .NET world.

My main need was to create instances of controllers and given Plays appraoch in the past (singleton `object`s for controllers) 2.1 new `GlobalSettings#getControllerInstance` feature was what I needed.

First things first we need to add a project dependency to [Guice](https://code.google.com/p/google-guice/) and while we could just add the Guice dependencies directly I went with a library that offered slightly neater syntax in Scala - [SSE-Guice](https://github.com/sptz45/sse-guice).  In short this means instead of writing this sort of noisy code

```scala
bind(classOf[Service]).to(classOf[ServiceImpl]).in(classOf[Singleton])
```

We get to write it like this,

```scala
bind[Service].to[ServiceImpl].in[Singleton]
```

So lets add the reference to the `Build.scala` file (assuming we just generated a our Play project) for sse-guice

```scala
val appDependencies = Seq(
  jdbc,
  anorm,

  // Add your project dependencies here,
  "com.tzavellas" % "sse-guice" % "0.7.0"
)
```

Next up we need to modify the Application controller a bit

- It needs to take a dependency
- It needs to bo be instantiable (e.g. not an `object` - this might be doable but the default approach in Guice seems to conflict with Plays approach to defining an instantiable controller)

So we want to go from this,

```scala
object Application extends Controller {
  def index = Action {
    Ok(views.html.index("Some Message"))
  }
}
```

To this,

```scala
class Application @Inject()(val messeger: Messeger) extends Controller {
  def index = Action {
    Ok(views.html.index(messeger.getMessage))
  }
}
```

A few things of note here

- We use the `@Inject()` attribute here to mark this constructor as our default one (need by Guice to satisfy constructor injection)
- Application is now a class.  Small change (a Levenshtein Distance of 6 even!) big difference.  Rather than having a singleton we now have something we can construct when we want (even in tests) and set it up really simply.  Construction and lifecycle can be handled via our DI library easily.
- We are passing in some sort of dependency - in this case it's a simple string barfer (`Messeger` is a trait) and the impl looks like this

```scala
trait Messeger {
  def getMessage : String
}

object MessegerImpl extends Messeger {
  def getMessage = "The Best Super Message"
}
```

Then we need to create our Guice module that will wire up our dependencies,

```scala
import com.tzavellas.sse.guice.ScalaModule

class SimpleModule extends ScalaModule{
  def configure() {
    bind[Messeger].toInstance(MessegerImpl)
  }
}
```

The `ScalaModule` declaration is a Scala-fied version of Guices `AbstractModule` with some helper and whatnot.  Guice applies some common sense to wiring so we don't need to declare each controller type here etc.

Finally we need to start the DI mechanism and provide a way to instantiate controllers.  We do this via the Play feature of `GlobalSettings`.  In our `app` package we just put a `Global` object and set it up as needed

```scala
object Global extends GlobalSettings {

  private lazy val injector = Guice.createInjector(new SimpleModule)

  override def getControllerInstance[A](controllerClass: Class[A]) = {
    injector.getInstance(controllerClass)
  }
}
```

We use a `lazy` val to instantiate the `injector` with our `SimpleModule` and override the `getControllerInstance` method of our `GlobalSettings` class to use Guices injector.

Oh one last thing.  We need to tell Play what controllers we want to use this feature (it's not an all or nothing thing which I guess is good).  We do this by marking the route declaration with an `@` in the `routes` file.  So it becomes this (notice the `@`)

```text
# Home page
GET     /     @controllers.Application.index
```

Running the app gives us exactly what we expect but why stop there?  We need to test this stuff right?  Cool.  Testing is made easier - we can simply make use of the various helpers in Play like `FakeApplication` and `FakeRequest`,

```scala
class ApplicationSpec extends Specification {
  "Using FakeApplication" should {
    "get the default message" in {
      running(FakeApplication()) {
        val home = route(FakeRequest(GET, "/")).get
        contentAsString(home) must contain ("The Best Super Message")
      }
    }
  }
}
```

Or we can create a custom `Global` object with a `FakeApplication`

```scala
class ApplicationSpec extends Specification {

  object DummyMessenger extends Messeger {
    def getMessage = "Dummy Message"
  }

  "Using FakeApplication" should {

    object DummyGlobal extends play.api.GlobalSettings {
      override def getControllerInstance[A](c: Class[A]) = {
        new Application(DummyMessenger).asInstanceOf[A]
      }
    }

    "get the an injected message" in {
      running(FakeApplication(withGlobal = Some(DummyGlobal))) {
        val home = route(FakeRequest(GET, "/")).get
        contentAsString(home) must contain ("Dummy Message")
      }
    }
  }
}
```

And even lighter we can just test the controller as if it was just another class,

```scala
class ApplicationSpec extends Specification {

  object DummyMessenger extends Messeger {
    def getMessage = "Dummy Message"
  }

  "Using direct controller testing" should {
    "return the dummy message" in {
      val controller = new Application(DummyMessenger)
      val result = controller.index()(FakeRequest())
      contentAsString(result) must contain ("Dummy Message")
    }
  }
}
```

Hopefully this gives you a good starter for using the new Play 2.1 `getContollerInstance` feature along with Guice.