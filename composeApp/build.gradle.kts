import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

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
        outputModuleName.set("composeApp")
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static(rootDirPath)
                    static(projectDirPath)
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.nemoEditor)
            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.material3)
            implementation(libs.ui)
            implementation(libs.material.icons.extended)
            implementation(libs.components.resources)
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
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

compose.resources {
    packageOfResClass = "nemoeditor.composeapp.generated.resources"
}

val projectPackageName = libs.versions.project.packageName.get()

compose.desktop {
    application {
        mainClass = "$projectPackageName.MainKt"

        nativeDistributions {
            targetFormats(
                TargetFormat.Exe,
                TargetFormat.Msi,
                TargetFormat.Dmg,
                TargetFormat.Deb,
                TargetFormat.Rpm
            )
            packageName = "Nemo Editor"
            packageVersion = libs.versions.project.versionName.get()
            vendor = libs.versions.project.vendor.get()
            val commonIcon = "src/commonMain/composeResources/drawable/icon.ico"
            windows {
                iconFile.set(project.file(commonIcon))
                shortcut = true
            }
            linux {
                iconFile.set(project.file(commonIcon))
                shortcut = true
            }
            macOS { iconFile.set(project.file(commonIcon)) }

            buildTypes.release.proguard {
                isEnabled.set(false)
                obfuscate.set(false)
            }

            tasks.withType<JavaExec>().configureEach {
                if (name.contains("release", ignoreCase = true)) {
                    systemProperty("app.build.mode", "release")
                } else {
                    systemProperty("app.build.mode", "debug")
                }
            }
        }
    }
}

dependencies {
    "androidRuntimeClasspath"(libs.ui.tooling)
}
