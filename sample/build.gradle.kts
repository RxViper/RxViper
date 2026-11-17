plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.dzaitsev.rxviper.sample"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.dzaitsev.rxviper.sample"
        minSdk = 23
        targetSdk = 37
        versionCode = 2
        versionName = project.version.toString()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":rxviper-core"))
    implementation(project(":rxviper-coroutines"))
    implementation(libs.coroutines.android)
    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
}
