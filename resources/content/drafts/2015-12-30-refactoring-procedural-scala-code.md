---
date: 2015-12-30T00:00:00Z
title: "Refactoring procedural Scala code"
published: true
categories: [Scala]
type: article
external: false
---

I recently had to add additional code to an old and critical area of a 2 year old, rather large, Scala project.  The code which centered around a core function accepted an object instance and through various steps transformed it by updating various properties of the passed in instance.  The passed in instance, a case class, used immutable values for all its properties so in order to apply these additional transforms the function created additional internal values after each transformation.  Eventually it would return the final updated instance.  My work involed introducing yet another step in this series of transformations so I decided to play with various ways of achieving the same thing to see if there was a better way of approaching the problem.

## Set the scene

So lets set the scene using a stripped down example.

First of all we have a primary `Order` model.  An order instance is created and persisted once the customer has completed the order and This has a number of optional properties that will be set as the order is processed through various states (new order, order completed, order paid etc.)

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

`RequisitionRequest` and `Invoice` are empty case classes that act as stubs as their content isn't really important for these examples.

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

We can set up some stub function for some of these steps but in reality you'd hope these functions did a lot more than this,

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

Lets assume that we are adding the additional functionality of updating the `processedTimestamp` as this was, for some reason, not being done before.

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

All I did initially was capture order after fulfillment and pass this into the new `markOrderProcessed` function.

> It's worth pointing out that, yes, there are ways to make even this style a bit more terse but instead focus on the overall style rather than the specifics of this example

But there are a few things that bugged me about this approach.  While it was consistent and rather easy to update it did cause some issues.

- The actual code was more complex and it took some understanding where to place the actual code. Subsequent changes would further increase this complexity
- Coming up with a meaningful name for the new value was frustrating.
- I introduced a bug because at one point where I referenced the wrong transient value

Beside the bug that a unit test flagged up none of this is exactly show stopping but I was curious if we could improve this method.

## Alternatives

### Use a var

Instead of using new `val` declarations for each step of the transformation why not use a `var`.  

```scala
def processOrderPostPayment_2(order: Order): Order = {
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


I hear groans from the audience but given that our original instance is still an immutable object and we only have local-only mutability this isn't the worst approach.  It does solve the issue around having to declare and name intermediiary `vals` which means you are less likely to introduce an issue by referencing the wrong `val` (I say less because you still have 2 - the passed in order and the updateOrder variable).



def processOrderPostPayment_1_1(order: Order): Order = {
  markOrderProcessed((submitToStockroom(order) match {
    case o@Order(_,PaymentMethods.Cash,_,_,_,_,_) =>
      generateInvoice(o)
    case o =>
      retrievePaymentNotification(o)
  }).copy(fulfilled = true))
}

def processOrderPostPayment_2(order: Order): Order = {
  var updatedOrder = submitToStockroom(order)

  updatedOrder = updatedOrder match {
    case Order(_,PaymentMethods.Cash,_,_,_,_,_) =>
      generateInvoice(updatedOrder)
    case _ =>
      retrievePaymentNotification(updatedOrder)
  }

  updatedOrder = updatedOrder.copy(fulfilled = true)

  markOrderProcessed(updatedOrder)
}

def processOrderPostPayment_3(order: Order): Order = {
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

def processOrderPostPayment_4(order: Order): Order = {
  markOrderProcessed((if(order.paymentMethod == PaymentMethods.Cash) generateInvoice _ else retrievePaymentNotification _)(submitToStockroom(order)).copy(fulfilled = true))
}

def processOrderPostPayment_5(order: Order): Order =
  (submitToStockroom _)
    .andThen(if (order.paymentMethod == PaymentMethods.Cash) generateInvoice else retrievePaymentNotification)
    .andThen(_.copy(fulfilled = true))
    .andThen(markOrderProcessed)(order)


def processOrderPostPayment_6(order: Order): Order =
  (submitToStockroom _)
    .andThen(order match {
      case Order(_, PaymentMethods.Cash,_,_,_,_,_) => generateInvoice
      case _ => retrievePaymentNotification
    })
    .andThen(_.copy(fulfilled = true))
    .andThen(markOrderProcessed)(order)


