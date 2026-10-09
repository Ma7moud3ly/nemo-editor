import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinSerialization) apply false

    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false

    alias(libs.plugins.vanniktech.maven.publish) apply false
}

val publishGroup = "io.github.ma7moud3ly"
val publishVersion = libs.versions.project.versionName.get()
val repositoryUrl = "https://github.com/Ma7moud3ly/nemo-editor"

/**
 * Maven publishing shared by every module that applies the maven-publish
 * plugin. The artifact id is the module name, and the POM description is the
 * module's own `description`.
 */
subprojects {
    val module = this
    plugins.withId("com.vanniktech.maven.publish") {
        extensions.configure<MavenPublishBaseExtension> {
            coordinates(publishGroup, module.name, publishVersion)

            pom {
                // "nemo-core" becomes "Nemo Core"
                name.set(module.name.split('-').joinToString(" ") { word ->
                    word.replaceFirstChar { it.uppercase() }
                })
                description.set(provider { module.description })
                url.set(repositoryUrl)
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
                    url.set(repositoryUrl)
                    connection.set("scm:git:git://github.com/Ma7moud3ly/nemo-editor.git")
                    developerConnection.set("scm:git:ssh://git@github.com/Ma7moud3ly/nemo-editor.git")
                }
            }

            publishToMavenCentral(automaticRelease = false)
            signAllPublications()
        }
    }
}
