// Top-level build file where you can add configuration options common to all sub-projects/modules.
allprojects {
  group = "com.github.pedroSG94.RootEncoder"
  version = "2.6.4"
}

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.jetbrains.kotlin) apply false
  alias(libs.plugins.jetbrains.dokka) apply true

  id("convention.project.versioning")
}

tasks.register("clean") {
  delete(layout.buildDirectory)
}

tasks.dokkaHtmlMultiModule.configure {
  outputDirectory.set(File("docs"))
}