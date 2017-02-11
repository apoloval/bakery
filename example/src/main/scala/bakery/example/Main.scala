package bakery.example

import bakery.example.config.file.FileSettingsModule
import bakery.example.metrics.statsd.StatsdModule
import bakery.example.payment.paypal.PaypalModule
import bakery.example.shop.ShopModule

object Main extends App
  with ShopModule
  with PaypalModule
  with FileSettingsModule
  with StatsdModule {

  shop.sell()
}
