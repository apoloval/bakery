package bakery.example.metrics.statsd

import bakery.Dependency
import bakery.example.config.SettingsModule
import bakery.example.metrics.MetricsModule

@Dependency[SettingsModule]
trait StatsdModule extends MetricsModule {
  override lazy val metrics = new StatsdMetrics()
}
