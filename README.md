## Introduction

Bakery is a Scala library that provides a few utility abstractions to implement
the Cake Pattern.

The Cake Pattern is a popular design pattern in Scala language to have type-safe dependency injection. It basically consists in declaring modules (or components) through traits and use the [self type references][1] to indicate the dependencies among them. Doing so, the compiler can check whether the dependencies are satisfied or not, and you will be sure that your application is correctly integrated before execution.

## Cake Pattern Basics

Let's say we have a online shop application that has some modules that depend among them.

The `OnlineShopModule` depends on a `PaymentProcessorModule` to charge payments to the customers. The `PaymentProcessorModule` provides a `PaymentProcessor` instance that may have different implementations.

```scala
trait PaymentProcessor {
  def charge(account: String, amount: Double)
}

trait PaymentModule {
  def paymentProcessor: PaymentProcessor
}
```

One of the possible implementations is one that uses Paypal to charge to the customer:

```scala
class PaypalPaymentProcessor(settings: Settings) extends PaymentProcessor {
  private val userAccount = settings.config("paypal.user")

  override def charge(account: String, amount: Double): Unit = {
    println(s"[Paypal] Charging ${amount}€ to $account using $userAccount")
  }
}
```

As you can see, we have a dependency here. The `PaypalPaymentProcessor` depends on a `Settings` object that provides the Paypal user account configuration.

Coming back to the module definition, we have to implement a specialization of `PaymentModule` that provides a `PaypalPaymentProcessor` instance. This specialization is reflected using trait inheritance:

```scala
trait PaypalModule extends PaymentModule {
  override def paymentProcessor: PaymentProcessor = new PaypalPaymentProcessor
}
```

Unfortunately, this code doesn't compile. We have to provide an instance for `Settings` dependency in `PaypalPaymentProcessor`. So we twist the module definition slightly. 

```scala
trait SettingsModule {
  def settings: Settings
}

trait PaypalModule extends PaymentModule {
  this: SettingsModule =>
  
  override def paymentProcessor: PaymentProcessor = 
    new PaypalPaymentProcessor(this.settings)
}
```

**This is the most tricky part, so please pay attention**. Using the self type references in the trait we are saying to the compiler: _"this trait is not a `SettingsModule`, but any instance of `PaypalModule` must provide valid implementation for it._ In other words, `PaypalModule` cannot be seen as `SettingsModule`, but it can consider itself as that. Thanks for that, the expression `this.settings` provides a valid instance of the `Settings` class and can be injected in `PaypalPaymentProcessor` using its constructor. 

## Cake Pattern using implicits

If we combine the mechanism described above with implicits, we can make the Cake Pattern magic to be more declarative and less behavioral. Let's say we modify the `PaypalPaymentProcessor` as this:

```scala
class PaypalPaymentProcessor(
    implicit settings: Settings) extends PaymentProcessor {
    // ... rest of the code omitted in sake of legibility
}
```

We just mark the dependencies of the class constructor as implicit. Doing so, we don't have to provide them if there are implicit instances available in the scope:

```scala
trait PaypalModule extends PaymentModule {
  this: SettingsModule =>
  
  implicit val providedSettings: Setting = this.settings
  
  override def paymentProcessor = new PaypalPaymentProcessor
}
```

By declaring `providedSettings` implicit in the module, we can instantiate the `PaypayPaymentProcessor` without giving the arguments. 

## Why Bakery

The code discussed above is far from being really declarative. We have to define the self type reference manually and declare the implicit dependency values to introduce them into the scope. This is what Bakery resolves.

Bakery extends the Scala compiler using [Macro Paradise][2]. First, you have to add the following definition in your SBT file:

```scala
addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
```

With this, you are enabling the power of [Scala macro annotations][3]: language annocations that perform some macro transformations on your code.

Firstly, Bakery provides the `@Dependency` macro that can be used in this way:

```scala
@Dependency[SettingsModule]
trait PaypalModule extends PaymentModule {
  override lazy val paymentProcessor: PaymentProcessor = 
    new PaypalPaymentProcessor
}
```

`@Dependency` declares the self type reference for you and exposes implicit values with objects provided by the dependency modules. In the module trait, you only have to instantiate the objects to be provided by your module and the implicit scope will inject the appropriate instances in the provided objects.

This has to be used in combination with `@Provide`:

```scala
trait SettingsModule {
  @Provide
  def settings: Settings
}
```

This enables the `settings` field to be a dependency provider. In the modules that depends on `SettingsModule` (as in `PaypalModule`), the `settings` object will be implicitly available to be injected in the objects that depends on it.

That's all. Thanks to Bakery, you can obtain all the benefits from the Cake Pattern and still have a really declarative way to describe your modules and their dependencies. 

If you want to have a look to the complete code of this example, you will find it in the `example/` subdirectory of the project. 

## Throubleshooting

### I have NPE errors everywhere

The main cons of the Cake Pattern is that it's not easy to follow the order the different objects are instantiated. It's likely that one object is constructed before its dependencies, causing the NPE. 

You can prevent this by declaring the abstract dependencies using functions (`def`) and the provided dependencies using lazy values (`lazy val`). Doing so, unless you have circular dependencies (and you shoudn't), you will have no NPE. 

Note that in the examples above, the `PaymentModule` declares it provides a `PaymentProcessor` object using:

```scala
@Provide
def paymentProcessor: PaymentProcessor
```

Any module implementing it, in case the provided dependencies in turn depends on other objects, should declare them using lazy values as in:

```scala
  // Implicitly requires `Settings` and `Metrics`
  override lazy val paymentProcessor = new PaypalPaymentProcessor

```

### My module has more than one dependency

No problem. You have sereveral ways to represent that. Let's say `PaypalModule` depends on two modules: `SettingsModule` and `MetricsModule`.

```scala
@Dependency[(SettingsModule, MetricsModule)]
trait PaypalModule extends PaymentModule { /* ... */ }

@Dependency[SettingsModule with MetricsModule]
trait PaypalModule extends PaymentModule { /* ... */ }
```

Use the one you prefer.

### I have more problems not listed here

No problem. Just submit a GitHub issue or send an email to the author.

## Credits & license

This library is authored and maintained by Alvaro Polo (apoloval at gmail). 

It is licensed to you under the terms of [Apache License version 2][4]. 


[1]: http://docs.scala-lang.org/tutorials/tour/explicitly-typed-self-references.html
[2]: http://docs.scala-lang.org/overviews/macros/paradise.html
[3]: http://docs.scala-lang.org/overviews/macros/annotations.html
[4]: https://www.apache.org/licenses/LICENSE-2.0
