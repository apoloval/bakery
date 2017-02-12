package bakery.example.payment

import bakery.Provide

trait PaymentModule {
  @Provide
  def paymentProcessor: PaymentProcessor
}
