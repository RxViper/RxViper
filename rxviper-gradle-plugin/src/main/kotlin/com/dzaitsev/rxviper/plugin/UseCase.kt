package com.dzaitsev.rxviper.plugin

class UseCase(_name: String) {
  internal val name = _name.capitalize()

  var requestModel = aClass<Any>()
    @JvmName("requestModel") set

  var responseModel = aClass<Any>()
    @JvmName("responseModel") set
}