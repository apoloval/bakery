package bakery.example.metrics

import bakery.Provide

trait MetricsModule {
  @Provide
  def metrics: Metrics
}
