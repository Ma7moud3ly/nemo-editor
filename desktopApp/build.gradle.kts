import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

dependencies {
    implementation(projects.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)
}

compose.desktop {
    val projectPackageName = libs.versions.project.packageName.get()
    application {
        mainClass = "$projectPackageName.MainKt"

        buildTypes.release.proguard {
            isEnabled.set(false)
            obfuscate.set(false)
        }

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
            val commonIcon = rootProject.file(
                "shared/src/commonMain/composeResources/drawable/icon.ico"
            )
            windows {
                iconFile.set(commonIcon)
                shortcut = true
            }
            linux {
                iconFile.set(commonIcon)
                shortcut = true
            }
            macOS { iconFile.set(commonIcon) }
        }
    }
}
