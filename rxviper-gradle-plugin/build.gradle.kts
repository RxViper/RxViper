buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.1.0")
  }
}

apply {
  plugin("kotlin")
}

dependencies {
  compile(gradleApi())
  compile("org.jetbrains.kotlin:kotlin-stdlib:1.1.0")
  compile("com.squareup:javapoet:1.8.0")
  compile("com.dzaitsev.rxviper:rxviper:1.0.0-rc2")
  testCompile("junit:junit:4.12")
  testCompile("com.google.truth:truth:0.31")
}

//applyFrom(rootProject.file("publish.gradle"))

//fun libraries(name: String) = (extra.properties["libraries"] as Map<String, Any>)[name]!!