val libraryGroup: String by rootProject.extra
val vName: String by rootProject.extra
val coroutinesVersion: String by rootProject.extra

plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("maven-publish")
  id("org.jetbrains.dokka")

  id("convention.publishing")
}

android {
  namespace = "com.pedro.library"
  compileSdk = 34

  defaultConfig {
    minSdk = 16
    lint.targetSdk = 34
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
  kotlinOptions {
    jvmTarget = "17"
  }

  publishing {
    singleVariant("release")
  }
}

publishingConfig {
  groupId = libraryGroup
  artifactId = "library"
  domain = "diamond-kinetics"
  domainOwner = "626803233223"
  repository = "dk-maven"
  region = "us-east-1"
}

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
  api(project(":encoder"))
  api(project(":rtmp"))
  api(project(":rtsp"))
  api(project(":srt"))
  api(project(":udp"))
  api(project(":common"))
}
