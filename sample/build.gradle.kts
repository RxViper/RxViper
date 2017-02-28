import com.android.build.gradle.AppExtension
import com.android.builder.core.DefaultApiVersion
import com.android.builder.core.DefaultProductFlavor
import com.android.builder.model.ApiVersion
import com.dzaitsev.rxviper.plugin.RxViperExtension
import com.dzaitsev.rxviper.plugin.RxViperPlugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

apply {
  plugin("com.android.application")
  plugin("com.neenbedankt.android-apt")
  plugin("me.tatarka.retrolambda")
  plugin<RxViperPlugin>()
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

rxViper {
    packageName = (extensions["android"] as AppExtension).defaultConfig.applicationId!! // default "generated"
    useLambdas = true // default false
    includeInteractor = true // default true
    addMetaInfo = true // default true
    includeRouter = true // default true
    splitPackages = true // default true

  screens {
    "foo" {
      packageName = this@rxViper.packageName
      includeRouter = true
      useLambdas = true
      useCases {
        "getItems" {
          requestClass = Long::class.javaObjectType
          responseClass = String::class.javaObjectType
        }
        "watchPorn" {
          requestClass = Void::class.javaObjectType
          responseClass = Boolean::class.javaObjectType
        }
        "buyGoods" {}
      }
      routesTo = arrayOf("Home", "Settings", "Registration", "News")
    }

    "bar" {
      addMetaInfo = false
      useLambdas = false
      includeRouter = false
      useCases {
        "login" {}
        "post" {}
        "createUser" {}
      }
      routesTo = arrayOf("Auth", "Main", "UserProfile")
    }
  }
}

fun Project.android(configuration: AppExtension.() -> Unit) = configure(configuration)

fun Project.rxViper(configuration: RxViperExtension.() -> Unit) = configure(configuration)

fun DefaultProductFlavor.setMinSdkVersion(value: Int) = setMinSdkVersion(value.asApiVersion())

fun DefaultProductFlavor.setTargetSdkVersion(value: Int) = setTargetSdkVersion(value.asApiVersion())

fun Int.asApiVersion(): ApiVersion = DefaultApiVersion.create(this)

fun libraries(name: String) = (extra.properties["libraries"] as Map<String, Any>)[name]!!