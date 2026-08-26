import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.vanniktech.maven.publish)
}

kotlin {
    android {
        namespace = "io.ma7moud3ly.nemo.editor"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
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

    js(){
        browser()
        nodejs()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.material3)
            implementation(libs.ui)
            implementation(libs.material.icons.extended)
            implementation(libs.ui.tooling.preview)
            implementation(libs.lifecycle.runtime.compose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    "androidRuntimeClasspath"(libs.ui.tooling)
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