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

description = "A powerful, cross-platform code editor component built with Compose Multiplatform, featuring syntax highlighting, code formatting, and advanced editing capabilities"
val javaVersion = libs.versions.java.version.get()
val projectPackageName = libs.versions.project.packageName.get()

kotlin {
    compilerOptions {
        optIn.add("io.ma7moud3ly.nemo.InternalNemoApi")
    }

    android {
        namespace = "$projectPackageName.editor"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(javaVersion))
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
            api(projects.nemoCore)
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

mavenPublishing {
    pom {
        name.set("Nemo Code Editor")
    }
}
