package bakery.example.metrics.statsd

import bakery.example.config.SettingsModule
import bakery.example.metrics.Metrics

class StatsdMetrics(implicit deps: SettingsModule) extends Metrics {

  private val agentConnection = deps.settings.config("statsd.agent.connection")

  override def gauge(name: String, value: Double): Unit = {
    println(s"[Statsd] Sending to $agentConnection gauge '$name': $value")
  }
}
