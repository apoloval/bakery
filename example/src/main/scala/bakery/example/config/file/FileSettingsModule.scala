package bakery.example.config.file

import bakery.example.config.{Settings, SettingsModule}

trait FileSettingsModule extends SettingsModule {
  override def settings: Settings = new FileSettings
}
