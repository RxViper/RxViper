package com.dzaitsev.rxviper.plugin.internal.dsl

import com.dzaitsev.rxviper.plugin.aClass

class UseCase(_name: String) {
  internal val name = _name.capitalize()

  var requestClass = aClass<Any>()
    @JvmName("requestClass") set

  var responseClass = aClass<Any>()
    @JvmName("responseClass") set
}