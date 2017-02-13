package com.dzaitsev.rxviper.plugin.internal.dsl

import com.dzaitsev.rxviper.plugin.aClass

class UseCase(_name: String) {
  internal val name = _name.capitalize()

  var requestModel = aClass<Any>()
    @JvmName("requestModel") set

  var responseModel = aClass<Any>()
    @JvmName("responseModel") set
}