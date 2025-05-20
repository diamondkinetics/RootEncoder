val libraryGroup: String by rootProject.extra
val vName: String by rootProject.extra
val coroutinesVersion: String by rootProject.extra
val junitVersion: String by rootProject.extra
val mockitoVersion: String by rootProject.extra

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("org.jetbrains.dokka")

    id("convention.publishing")
}

android {
    namespace = "com.pedro.srt"
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
    artifactId = "srt"
    domain = "diamond-kinetics"
    domainOwner = "626803233223"
    repository = "dk-maven"
    region = "us-east-1"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    testImplementation("junit:junit:$junitVersion")
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoVersion")
    api(project(":common"))
}
