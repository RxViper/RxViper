package com.dzaitsev.rxviper.plugin.internal.codegen

import org.apache.commons.logging.Log
import org.apache.commons.logging.impl.SimpleLog

/**
 * ~ ~ ~ ~ Description ~ ~ ~ ~
 * @author Dmytro Zaitsev
 * @since 2017-May-09, 21:43
 */
class TestLog(name: String) : Log by SimpleLog(name) {
  var message: Any? = ""; private set

  override fun info(message: Any?) {
    this.message = message
    println(message)
  }
}