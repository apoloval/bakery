package bakery.example.shop

import bakery.Dependency
import bakery.example.payment.PaymentModule

@Dependency[PaymentModule]
trait ShopModule {
  lazy val shop = new OnlineShop
}
