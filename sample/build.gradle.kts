import com.android.build.gradle.AppExtension
import com.android.builder.core.DefaultApiVersion
import com.android.builder.core.DefaultProductFlavor
import com.android.builder.model.ApiVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

apply {
  plugin("com.android.application")
  plugin("com.neenbedankt.android-apt")
  plugin("me.tatarka.retrolambda")
  plugin("com.dzaitsev.rxviper")
  plugin<KotlinAndroidPluginWrapper>()
}

buildscript {
  System.setProperty("com.android.build.gradle.overrideVersionCheck", "true")
}

android {
  compileSdkVersion(23)
  buildToolsVersion("24.0.3")

  defaultConfig {
    applicationId = "com.dzaitsev.rxviper.sample"
    setMinSdkVersion(9)
    setTargetSdkVersion(24)
    versionCode = 1
    versionName = extra.properties["POM_VERSION"] as String
  }

  compileOptions.setSourceCompatibility(JavaVersion.VERSION_1_8)
  compileOptions.setTargetCompatibility(JavaVersion.VERSION_1_8)

  dataBinding.isEnabled = true
}

dependencies {
  "apt"(libraries("daggerCompiler"))
  compile(project(":rxviper"))
  compile(libraries("rxAndroid"))
  compile(libraries("dagger"))
  "provided"(libraries("jsr250"))
  compile(libraries("appCompat"))
  compile(libraries("design"))
  compile(libraries("cardView"))
  compile(libraries("constraintLayout"))
  testCompile(libraries("junit"))
}

fun Project.android(configuration: AppExtension.() -> Unit) = configure(configuration)

fun DefaultProductFlavor.setMinSdkVersion(value: Int) = setMinSdkVersion(value.asApiVersion())

fun DefaultProductFlavor.setTargetSdkVersion(value: Int) = setTargetSdkVersion(value.asApiVersion())

fun Int.asApiVersion(): ApiVersion = DefaultApiVersion.create(this)

fun libraries(name: String) = (extra.properties["libraries"] as Map<String, Any>)[name]!!