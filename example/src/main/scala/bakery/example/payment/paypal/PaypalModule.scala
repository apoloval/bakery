package bakery.example.payment.paypal

import bakery.Dependency
import bakery.example.config.SettingsModule
import bakery.example.metrics.MetricsModule
import bakery.example.payment.{PaymentProcessor, PaymentModule}

@Dependency[SettingsModule with MetricsModule]
trait PaypalModule extends PaymentModule {

  override def paymentProcessor: PaymentProcessor = new PaypalPaymentProcessor
}

object PaypalModule {

}
