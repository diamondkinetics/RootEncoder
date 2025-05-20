plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly("com.android.tools.build:gradle:8.2.2")
    implementation("software.amazon.awssdk:codeartifact:2.20.68")
    implementation("pl.allegro.tech.build:axion-release-plugin:1.15.4")
}

gradlePlugin {
    plugins {
        register("projectVersioning") {
            id = "convention.project.versioning"
            implementationClass = "plugins.VersioningConventionPlugin"
        }
        register("projectPublishing") {
            id = "convention.publishing"
            implementationClass = "plugins.PublishingConventionPlugin"
        }
    }
}
