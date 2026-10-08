import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    android {
        namespace = "io.ma7moud3ly.nemo.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }

        // Workaround for CMP-9547: compose resources aren't packaged into the
        // consuming APK unless android resources are enabled for this KMP library.
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.nemoEditor)
            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.material3)
            implementation(libs.ui)
            implementation(libs.material.icons.extended)
            api(libs.components.resources)
            implementation(libs.ui.tooling.preview)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.material3.adaptive)

            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.json)
            implementation(libs.androidx.navigation)

            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs)

            implementation(libs.settings.noArg)
        }
        androidMain.dependencies {
            implementation(libs.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

compose.resources {
    packageOfResClass = "io.ma7moud3ly.nemo.shared.resources"
    publicResClass = true
}

dependencies {
    "androidRuntimeClasspath"(libs.ui.tooling)
}
