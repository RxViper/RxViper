import org.gradle.api.tasks.compile.JavaCompile

plugins {
  java
}

configure<JavaPluginConvention> {
  sourceCompatibility = JavaVersion.VERSION_1_7
  targetCompatibility = JavaVersion.VERSION_1_7
}

compileTestJava {
  sourceCompatibility = JavaVersion.VERSION_1_8.toString()
  targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

dependencies {
  compile(libraries("rxJava"))
  testCompile(libraries("junit"))
  testCompile(libraries("mockito"))
  testCompile(libraries("truth"))
}

fun libraries(name: String) = (extra.properties["libraries"] as Map<String, Any>)[name]!!

fun Project.compileTestJava(block: JavaCompile.() -> Unit) = (tasks["compileTestJava"] as JavaCompile).apply { block.invoke(this) }