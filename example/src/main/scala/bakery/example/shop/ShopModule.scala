package bakery.example.shop

import bakery.{Provide, Dependency}
import bakery.example.payment.PaymentModule

@Dependency[PaymentModule]
trait ShopModule {
  @Provide
  lazy val shop = new OnlineShop
}
