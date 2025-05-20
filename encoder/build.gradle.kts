val libraryGroup: String by rootProject.extra
val vName: String by rootProject.extra
val junitVersion: String by rootProject.extra

plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("maven-publish")
  id("org.jetbrains.dokka")

  id("convention.publishing")
}

android {
  namespace = "com.pedro.encoder"
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
  artifactId = "encoder"
  domain = "diamond-kinetics"
  domainOwner = "626803233223"
  repository = "dk-maven"
  region = "us-east-1"
}

dependencies {
  testImplementation("junit:junit:$junitVersion")
  api("androidx.annotation:annotation:1.7.1")
  api(project(":common"))
}
