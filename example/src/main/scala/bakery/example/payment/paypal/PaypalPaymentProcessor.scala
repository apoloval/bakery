package bakery.example.payment.paypal

import bakery.example.config.Settings
import bakery.example.metrics.Metrics
import bakery.example.payment.PaymentProcessor

class PaypalPaymentProcessor(implicit settings: Settings,
                             metrics: Metrics) extends PaymentProcessor {

  private val userAccount = settings.config("paypal.user")

  override def charge(account: String, amount: Double): Unit = {
    println(s"[Paypal] Charging ${amount}€ to $account")
    metrics.gauge("gmv", amount)
  }
}
