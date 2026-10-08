import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

// apply gms & firebase plugin only for gms build flavour
if (gradle.startParameter.taskRequests.toString().contains("gms", ignoreCase = true)) {
    pluginManager.apply(libs.plugins.google.services.get().pluginId)
    pluginManager.apply(libs.plugins.firebase.crashlytics.get().pluginId)
}

val projectPackageName = libs.versions.project.packageName.get()
val localProperties = getLocalProperties(rootProject)

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

android {
    namespace = projectPackageName
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = projectPackageName
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = libs.versions.project.versionCode.get().toInt()
        versionName = libs.versions.project.versionName.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    signingConfigs {
        if (localProperties.hasDebugStoreConfig()) {
            getByName("debug") {
                keyAlias = localProperties["DEBUG_KEY_ALIAS"] as? String
                keyPassword = localProperties["DEBUG_KEY_PASSWORD"] as? String
                storeFile = file(localProperties["DEBUG_STORE_FILE"] as String)
                storePassword = localProperties["DEBUG_STORE_PASSWORD"] as? String
            }
        }
        if (localProperties.hasReleaseStoreConfig()) {
            create("release") {
                keyAlias = localProperties["RELEASE_KEY_ALIAS"] as? String
                keyPassword = localProperties["RELEASE_KEY_PASSWORD"] as? String
                storeFile = file(localProperties["RELEASE_STORE_FILE"] as String)
                storePassword = localProperties["RELEASE_STORE_PASSWORD"] as? String
            }
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = when {
                localProperties.hasReleaseStoreConfig() -> signingConfigs.getByName("release")
                else -> signingConfigs.getByName("debug")
            }
        }
    }

    flavorDimensions += "services"
    productFlavors {
        //a build flavor with Google Analytics & crashlytics dependencies
        create("gms") {
            isDefault = true
            dimension = "services"
        }
        //a build flavor free of analytics dependencies
        create("default") {
            dimension = "services"
            dependenciesInfo {
                // Disables dependency metadata when building APKs.
                includeInApk = false
                // Disables dependency metadata when building Android App Bundles.
                includeInBundle = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.runtime)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core)
    implementation(libs.filekit.core)
    implementation(libs.filekit.dialogs)

    "gmsImplementation"(platform(libs.firebase.bom))
    "gmsImplementation"(libs.firebase.crashlytics.ktx)
    "gmsImplementation"(libs.firebase.analytics.ktx)
}

/**
 * Loads properties from the `local.properties` file in the project root.
 *
 * @param root The project root.
 * @return Properties loaded from `local.properties`
 */
fun getLocalProperties(root: Project): Properties {
    val localProperties = Properties()
    val localPropertiesFile = root.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(localPropertiesFile.inputStream())
    }
    return localProperties
}

fun Properties.hasDebugStoreConfig() = this.containsKey("DEBUG_STORE_FILE")
fun Properties.hasReleaseStoreConfig() = this.containsKey("RELEASE_STORE_FILE")
