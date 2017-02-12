package bakery.example.config

import bakery.Provide

trait SettingsModule {
  @Provide
  def settings: Settings
}
