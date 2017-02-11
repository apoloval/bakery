package bakery.example.payment

trait PaymentProcessor {
  def charge(account: String, amount: Double)
}
