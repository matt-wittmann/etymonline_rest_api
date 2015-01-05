package com.mattwittmann

import com.typesafe.config.ConfigFactory

object ConnectionConfig {
  private[this] val config = ConfigFactory.load().getConfig("etymonline.connection")
  val hostName = config.getString("hostName")
  val port = config.getInt("port")
}
