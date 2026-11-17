android {
    defaultConfig {
        applicationId = "com.dzaitsev.rxviper.sample"
        versionCode = 2
        versionName = project.version.toString()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// The Android app uses JVM 17; published runtime jars keep Trellis's JVM 8 default.
kotlin {
    compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
}

dependencies {
    implementation(project(":rxviper-core"))
    implementation(project(":rxviper-coroutines"))
    implementation(libs.coroutines.android)
    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
}
