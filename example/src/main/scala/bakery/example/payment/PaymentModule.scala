package bakery.example.payment

trait PaymentModule {
  implicit def paymentProcessor: PaymentProcessor
}
