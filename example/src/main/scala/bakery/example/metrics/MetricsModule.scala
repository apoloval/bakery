package bakery.example.metrics

trait MetricsModule {
  implicit def metrics: Metrics
}
