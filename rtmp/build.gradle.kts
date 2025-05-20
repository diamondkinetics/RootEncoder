plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.jetbrains.kotlin)
  alias(libs.plugins.jetbrains.dokka)
  `maven-publish`

  id("convention.publishing")
}

android {
  namespace = "com.pedro.rtmp"
  compileSdk = 36

  defaultConfig {
    minSdk = 16
    lint.targetSdk = 36
  }
  buildTypes {
    release {
      isMinifyEnabled = false
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlin {
    jvmToolchain(17)
  }
  publishing {
    singleVariant("release")
  }
}

publishingConfig {
  groupId = group.toString()
  artifactId = "rtmp"
  domain = "diamond-kinetics"
  domainOwner = "626803233223"
  repository = "dk-maven"
  region = "us-east-1"
}

dependencies {
  implementation(libs.kotlinx.coroutines.android)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.junit)
  testImplementation(libs.mockito.kotlin)
  api(project(":common"))
}
