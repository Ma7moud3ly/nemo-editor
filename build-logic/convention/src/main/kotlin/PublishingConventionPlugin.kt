import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.util.Properties

class PublishingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply the vanniktech maven publish plugin
            pluginManager.apply("com.vanniktech.maven.publish")

            // Load properties from gradle.properties
            val properties = Properties().apply {
                val propsFile = rootProject.file("gradle.properties")
                if (propsFile.exists()) {
                    propsFile.inputStream().use { load(it) }
                } else {
                    logger.warn("gradle.properties not found at: ${propsFile.absolutePath}")
                }
            }

            // Set project group and version
            group = properties.getProperty("GROUP") ?: "io.github.ma7moud3ly"
            version = properties.getProperty("VERSION_NAME") ?: "1.0.0"

            // Configure maven publishing
            extensions.configure<MavenPublishBaseExtension> {
                // Publish to Maven Central Portal with automatic release
                publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)

                // Sign all publications with GPG
                signAllPublications()

                // Set artifact coordinates
                coordinates(
                    groupId = group.toString(),
                    artifactId = properties.getProperty("POM_ARTIFACT_ID") ?: project.name,
                    version = version.toString()
                )

                // Configure POM metadata
                pom {
                    name.set(
                        properties.getProperty("POM_NAME")
                            ?: "Nemo Code Editor"
                    )

                    description.set(
                        properties.getProperty("POM_DESCRIPTION")
                            ?: "A lightweight, fast, and beautiful code editor built with Kotlin Multiplatform and Compose Multiplatform"
                    )

                    inceptionYear.set(
                        properties.getProperty("POM_INCEPTION_YEAR")
                            ?: "2024"
                    )

                    url.set(
                        properties.getProperty("POM_URL")
                            ?: "https://github.com/Ma7moud3ly/nemo-editor"
                    )

                    // License information
                    licenses {
                        license {
                            name.set(
                                properties.getProperty("POM_LICENCE_NAME")
                                    ?: "MIT License"
                            )
                            url.set(
                                properties.getProperty("POM_LICENCE_URL")
                                    ?: "https://opensource.org/licenses/MIT"
                            )
                            distribution.set(
                                properties.getProperty("POM_LICENCE_DIST")
                                    ?: "repo"
                            )
                        }
                    }

                    // Developer information
                    developers {
                        developer {
                            id.set(
                                properties.getProperty("POM_DEVELOPER_ID")
                                    ?: "ma7moud3ly"
                            )
                            name.set(
                                properties.getProperty("POM_DEVELOPER_NAME")
                                    ?: "Mahmoud Aly"
                            )
                            url.set(
                                properties.getProperty("POM_DEVELOPER_URL")
                                    ?: "https://github.com/Ma7moud3ly"
                            )
                            email.set(
                                properties.getProperty("POM_DEVELOPER_EMAIL")
                                    ?: ""
                            )
                        }
                    }

                    // Source control management
                    scm {
                        url.set(
                            properties.getProperty("POM_SCM_URL")
                                ?: "https://github.com/Ma7moud3ly/nemo-editor"
                        )
                        connection.set(
                            properties.getProperty("POM_SCM_CONNECTION")
                                ?: "scm:git:git://github.com/Ma7moud3ly/nemo-editor.git"
                        )
                        developerConnection.set(
                            properties.getProperty("POM_SCM_DEV_CONNECTION")
                                ?: "scm:git:ssh://git@github.com/Ma7moud3ly/nemo-editor.git"
                        )
                    }
                }
            }

            // Log successful plugin application
            logger.lifecycle("✅ PublishingConventionPlugin configured for ${project.name}")
            logger.lifecycle("   Group: $group")
            logger.lifecycle("   Version: $version")
        }
    }
}