apply {
  plugin("kotlin")
}

dependencies {
  compile(gradleApi())
  compile(Libraries.kotlin)
  compile(Libraries.javaPoet)
  compile(project(":rxviper"))
  testCompile(Libraries.junit)
  testCompile(Libraries.truth)
}

applyFrom(rootProject.file("publish.gradle"))