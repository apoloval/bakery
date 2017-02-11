package bakery.example.shop

import bakery.example.payment.PaymentProcessor

class OnlineShop(implicit paymentProcessor: PaymentProcessor) {

  def sell(): Unit = {
    println("[Shop] Selling things")
    paymentProcessor.charge("john.dumbar@example.com", 100)
  }
}
