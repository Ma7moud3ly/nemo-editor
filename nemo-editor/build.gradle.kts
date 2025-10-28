import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.vanniktech.maven.publish)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "NemoEditor"
            isStatic = true
        }
    }

    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    js()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.material3.adaptive)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.json)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

compose.resources {
    packageOfResClass = "nemoeditor.composeapp.generated.resources"
}

android {
    namespace = libs.versions.project.packageName.get()
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

// Maven Publishing Configuration
mavenPublishing {
    val versionName = libs.versions.project.versionName.get()
    coordinates("io.github.ma7moud3ly", "nemo-editor", versionName)

    pom {
        name.set("Nemo Code Editor")
        description.set("A powerful, cross-platform code editor component built with Compose Multiplatform, featuring syntax highlighting, code formatting, and advanced editing capabilities")
        url.set("https://github.com/Ma7moud3ly/nemo-editor")
        inceptionYear.set("2025")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("ma7moud3ly")
                name.set("Mahmoud Aly")
                email.set("engma7moud3ly@gmail.com")
                url.set("https://github.com/Ma7moud3ly")
            }
        }

        scm {
            url.set("https://github.com/Ma7moud3ly/nemo-editor")
            connection.set("scm:git:git://github.com/Ma7moud3ly/nemo-editor.git")
            developerConnection.set("scm:git:ssh://git@github.com/Ma7moud3ly/nemo-editor.git")
        }
    }

    publishToMavenCentral(automaticRelease = false)
    signAllPublications()
}