package com.dzaitsev.rxviper.plugin

class UseCase(_name: String) {
  internal val name = _name.capitalize()

  var requestModel = clazz<Any>()
    @JvmName("requestModel") set

  var responseModel = clazz<Any>()
    @JvmName("responseModel") set
}