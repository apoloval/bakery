package bakery.example.metrics

trait Metrics {
  def gauge(name: String, value: Double): Unit
}
