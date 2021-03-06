---
date: 2016-01-02T00:00:00Z
title: "An example of refactoring Scala code"
published: true
categories: [Scala]
type: article
external: false
---

I recently had to add code to an old and critical area of a 2 year old, rather large, Scala project.  The code which centred around a core function that accepted an object instance, performed various actions that ultimately updated/transformed some properties on the instance.  The passed in instance, a case class, used immutable values for all its properties so in order to apply these additional transforms the function created additional internal values after each transformation.  Finally, it would return the final updated instance.  

My work involved introducing yet another step in this series of transformations so I decided to play with various ways of achieving the same thing to see if there was a better way of approaching the problem.

## Set the scene

> The following code is contrived for the purposes of this article. It may seem overly simple but I didn't want to bog down the examples with more than necessary.

So let's set the scene using a stripped down example.

First of all, we have a primary `Order` model.  An order instance is created and persisted once the customer has completed the order and This has a number of optional properties that will be set as the order is processed through various states (new order, order completed, order paid etc.)

```scala
case class Order(
    id: String, 
    paymentMethod: PaymentMethod,
    requisitionRequest: Option[RequisitionRequest], 
    invoice: Option[Invoice],
    paymentId: Option[String], 
    fulfilled: Boolean, 
    processedTimestamp: Option[Long])
```

`PaymentMethod` is a trait that a number of case objects extend,

```scala
sealed trait PaymentMethod
object PaymentMethods {
  case object Cash extends PaymentMethod
  case object CreditCard extends PaymentMethod
}
```

`RequisitionRequest` and `Invoice` are empty case classes that act as stubs. Their content isn't really important for these examples.

In our example we have a core function that will process an order after payment has been completed.

```scala
def processOrderPostPayment(order: Order): Order = {
	???
}
```

This function should perform a number of things

1. It should submit the order to the stock room and update the order with a `requisitionRequest`
2. If the order is paid by cash it should generate a new invoice and add this to the order (customer has not yet actually paid but agreed to pay)
3. If the order is paid by anything other than cash then it should retrieve the payment ID from the payment system and update the order
4. Once these things have completed it should mark the order as fulfilled, and finally,
5. It should update the order with the processed date. 

We can set up some stub functions,

```scala
def submitToStockroom(order: Order): Order = {
  order.copy(requisitionRequest = Some(RequisitionRequest()))
}

def generateInvoice(order: Order): Order = {
  order.copy(invoice = Some(Invoice()))
}

def retrievePaymentNotification(order: Order): Order = {
  order.copy(paymentId = Some("payment"))
}

def markOrderProcessed(order: Order): Order = {
  order.copy(processedTimestamp = Some(1451468923165L))
}
```

Let's assume that we are adding the additional functionality of updating the `processedTimestamp` as this was, for some reason, not being done before.

## The current solution

The existing solution looked a bit like this,

```scala
def processOrderPostPayment(order: Order): Order = {
  val submittedOrder = submitToStockroom(order)

  val updatedOrder = submittedOrder match {
    case o@Order(_,PaymentMethods.Cash,_,_,_,_,_) =>
      generateInvoice(o)
    case o =>
      retrievePaymentNotification(o)
  }

  updatedOrder.copy(fulfilled = true)
}
```

After a first pass of adding the new functionality of adding the processed timestamp it looked like this,

```scala
def processOrderPostPayment(order: Order): Order = {
  val submittedOrder = submitToStockroom(order)

  val updatedOrder = submittedOrder match {
    case o@Order(_,PaymentMethods.Cash,_,_,_,_,_) => 
      generateInvoice(o)
    case o => 
      retrievePaymentNotification(o)
  }

  val fulfilledOrder = updatedOrder.copy(fulfilled = true)

  markOrderProcessed(fulfilledOrder)
}
```

My first pass was to follow a similar pattern of assigning an updated version of the order to a `val` and subsequently pass that to the new `markOrderProcessed` function.

> I think it's worth pointing out again that, yes, there are ways to make even this style a bit terser but instead focus on the overall style rather than the specifics of this example

There are a few things that bugged me about this approach.  While it was consistent and rather easy to update it did cause some issues.

- The actual code was more complex and it took some understanding where to place the actual code. Subsequent changes would further increase this complexity.
- Coming up with a meaningful name for the new value was frustrating and I simply refuse to just append a number to a values name.
- I introduced a bug because at one point I referenced the wrong transient value.

Beside the bug that, thankfully, a unit test flagged up none of this is exactly show stopping but I was curious if we could improve this function.

> One more caveat. These examples are not necessarily __better__. This is not an article about gradual refactoring to a better solution. Each example is an alternative way of expressing the same problem and each example has its flaws.  Consistency of approach across code is a godsend when trying to dive into legacy code.

## Why not just use a `var`?

Instead of using new `val` declarations for each step of the transformation why not use a `var`?  

```scala
def processOrderPostPayment(order: Order): Order = {
  var updatedOrder = submitToStockroom(order)

  updatedOrder = updatedOrder match {
    case o@Order(_,PaymentMethods.Cash,_,_,_,_,_) =>
      generateInvoice(o)
    case _ =>
      retrievePaymentNotification(updatedOrder)
  }

  updatedOrder = updatedOrder.copy(fulfilled = true)

  markOrderProcessed(updatedOrder)
}
```

I've interacted, both directly and indirectly, with many people that think having even a single `var` in your code base is an affront to the Scala gods but more times than not this appears to be down to lack of rationale thinking.  Yes, we've created mutable state but we are still dealing with immutable object and any mutation is confined to this single function - local mutable state can be acceptable.  

What this approach does is reduce the number of variable/values used to two

1. The `order` that was passed in, and,
2. The `updatedOrder` that we create and assign to multiple times

This tactic reduces the chances of using the wrong value in a function call compared to creating multiple values in the previous approach.

## Why do we even need the `var`?

So if reducing the amount of variables/values we create appears to have a positive effect - why not simply __never__ create one in this function?

```scala
def processOrderPostPayment(order: Order): Order = {
  markOrderProcessed((if(order.paymentMethod == PaymentMethods.Cash) generateInvoice _ else retrievePaymentNotification _)(submitToStockroom(order)).copy(fulfilled = true))
}
```

Yikes! So lets break this down. Based on the `paymentMethod` we are creating a partial function based on either `generateInvoice` or `retrievePaymentNotification`.  From here we are passing in the result of `submitToStockroom(order)`, updating the object using the copy constructor to mark it fulfilled and finally marking the order as processed via `markOrderProcessed`.

If I had to deconstruct this as someone else's code I'd probably need to take the rest of the day off to recover. Please never do this.

Can we improve this approach though?

```scala
def processOrderPostPayment(order: Order): Order = {
  markOrderProcessed((submitToStockroom(order) match {
    case o@Order(_,PaymentMethods.Cash,_,_,_,_,_) =>
      generateInvoice(o)
    case o =>
      retrievePaymentNotification(o)
  }).copy(fulfilled = true))
}
```

Here we have dropped the currying of functions in favour of simply nesting invocations using a `match`.  Is it any better?  Maybe.  I think it's a __tiny bit__ easier to decipher but it is still a big ball of tangled code.  The order of execution doesn't flow from top to bottom but instead from inside out, kind of.  This makes it very hard to derive execution order and so updating this code, especially when order is important, would be a massive pain.

## Improving the flow

I've written a fair bit of Clojure over the last 2 years and one of the feature I really like about it is the [threading macro](https://clojuredocs.org/clojure.core/-%3E).  It's a tiny bit of syntactic sugar that would let you unroll calls like this,

```scala
// BTW this is not clojure it's scala-ish pseudocode
head(split(replace(toUppercase("abcd"), "A", "X"), ""))
```

Into something like this,

```scala
// again this is scala-ish pseudocode
thread("abcd", toUppercase, replace(_,"A","X"), split(_,""), head) 
```

This more clearly defines the order of execution to the reader.

- Starting with the string `"abcd"`
- Make the string uppercase
- Replace "A" with "X"
- Split by character
- Get the first character

With the first approach you are forced to work from inside out to understand what is happening and in what order.

Scala doesn't have a standard threading operator or macro but it does have some ways to accomplish something similar.

## Using `Option` as a pipeline

You can think of an `Option` as a list of zero or one items that you can apply operations on.  So if we make a `Some(Order)` based on the order passed into the function we can apply operations on the contained order in a way that mimics the thread operator.

```scala
def processOrderPostPayment(order: Order): Order = {
  Some(order)
    .map(submitToStockroom)
    .collect {
      case o@Order(_,PaymentMethods.Cash,_,_,_,_,_) => generateInvoice(o)
      case o => retrievePaymentNotification(o)
    }
    .map(_.copy(fulfilled = true))
    .map(markOrderProcessed)
    .get
}
```

Here we are using the power of `map` and `collect` to transform the original order through a series of steps without having to create intermediate variables or values.  Finally we call `get` to unwrap the Order again.  This approach only really works if we are always at least accepting and returning an `Order`.

## Compose a pipeline

Using `Some` is alright I guess but for this type of problem Scala does have something we can use to create a pipeline - functional composition.

```scala
def processOrderPostPayment(order: Order): Order =
  (submitToStockroom _)
    .andThen(if (order.paymentMethod == PaymentMethods.Cash) generateInvoice else retrievePaymentNotification)
    .andThen(_.copy(fulfilled = true))
    .andThen(markOrderProcessed)(order)
```

In this example we are currying or partially applying the `submitToStockroom` function which produces a new type `scala.Function1[Order, Order]` which is a function that accepts a single `Order` and returns an `Order`.  This gives us access to the compositional functions of the `Function1` type - specifically `andThen` in our case.  Using `andThen` we compose a single function that pipes its input and output through the series of functions in order.  Finally we execute the function by invoking it with our input order `(order)`.

## So which is best?

This was never about one approach being better than another.  There are a few examples that are clearly not reasonable solutions and would in fact make everything worse but there is no clear winner.  

As you can see there are many ways to skin a cat. Depending on the context one approach may yield code that is easier to understand and maintain.
