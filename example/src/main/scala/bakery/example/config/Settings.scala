package bakery.example.config

trait Settings {
  def config(key: String): Option[String]
}
