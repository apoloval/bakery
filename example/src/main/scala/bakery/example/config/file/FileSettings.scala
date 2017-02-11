package bakery.example.config.file

import bakery.example.config.Settings

class FileSettings extends Settings {
  override def config(key: String): Option[String] = {
    println(s"[FileSettings] Providing config for '$key'")
    None
  }
}
