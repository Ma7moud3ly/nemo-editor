import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.vanniktech.maven.publish)
}

description = "Find and replace bar and dialog for the Nemo Code Editor"
val javaVersion = libs.versions.java.version.get()
val projectPackageName = libs.versions.project.packageName.get()

kotlin {
    android {
        namespace = "$projectPackageName.search"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(javaVersion))
        }

        // Workaround for CMP-9547: compose resources aren't packaged into the
        // consuming APK unless android resources are enabled for this KMP library.
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    iosArm64()
    iosSimulatorArm64()

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
            implementation(libs.components.resources)
            implementation(libs.ui.tooling.preview)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

compose.resources {
    packageOfResClass = "$projectPackageName.search.resources"
}

dependencies {
    "androidRuntimeClasspath"(libs.ui.tooling)
}
