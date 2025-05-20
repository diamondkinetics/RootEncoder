plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.jetbrains.kotlin)
  alias(libs.plugins.jetbrains.dokka)
  `maven-publish`

  id("convention.publishing")
}

android {
  namespace = "com.pedro.library"
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
  artifactId = "library"
  domain = "diamond-kinetics"
  domainOwner = "626803233223"
  repository = "dk-maven"
  region = "us-east-1"
}

dependencies {
  implementation(libs.kotlinx.coroutines.android)
  api(project(":encoder"))
  api(project(":rtmp"))
  api(project(":rtsp"))
  api(project(":srt"))
  api(project(":udp"))
  api(project(":common"))
}
