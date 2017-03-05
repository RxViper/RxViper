apply {
  plugin("kotlin")
}

dependencies {
  compile(gradleApi())
  compile(libraries("kotlin"))
  compile(libraries("javaPoet"))
  compile(project(":rxviper"))
  testCompile(libraries("junit"))
  testCompile(libraries("truth"))
}

applyFrom(rootProject.file("publish.gradle"))

fun libraries(name: String) = (extra.properties["libraries"] as Map<String, Any>)[name]!!