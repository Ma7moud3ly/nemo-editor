import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.vanniktech.maven.publish)
}

val javaVersion = libs.versions.java.version.get()
val projectPackageName = libs.versions.project.packageName.get()

kotlin {
    compilerOptions {
        optIn.add("io.ma7moud3ly.nemo.InternalNemoApi")
    }

    android {
        namespace = "$projectPackageName.core"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(javaVersion))
        }
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
            implementation(libs.runtime)
            implementation(libs.ui)
            implementation(libs.material3)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

// Maven Publishing Configuration
mavenPublishing {
    val versionName = libs.versions.project.versionName.get()
    coordinates("io.github.ma7moud3ly", "nemo-core", versionName)

    pom {
        name.set("Nemo Core")
        description.set("State, settings and themes shared by the Nemo Code Editor libraries")
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