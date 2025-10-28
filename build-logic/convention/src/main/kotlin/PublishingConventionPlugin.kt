import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.plugins.signing.SigningExtension
import java.util.Properties

/**
 * Convention plugin for Maven Central publishing configuration.
 *
 * This plugin configures:
 * - Maven publishing with all necessary metadata
 * - GPG signing for all publications
 * - Automatic POM generation with project info
 * - Support for both releases and snapshots
 *
 * Usage: Apply this plugin in your library module's build.gradle.kts:
 * ```
 * plugins {
 *     id("PublishingConventionPlugin")
 * }
 * ```
 *
 * Required in gradle.properties:
 * - GROUP
 * - VERSION_NAME
 * - POM_NAME
 * - POM_DESCRIPTION
 * - POM_URL
 * - POM_LICENCE_NAME
 * - POM_LICENCE_URL
 * - POM_DEVELOPER_ID
 * - POM_DEVELOPER_NAME
 * - POM_SCM_URL
 * - POM_SCM_CONNECTION
 * - POM_SCM_DEV_CONNECTION
 *
 * Required in local.properties:
 * - mavenCentralUsername
 * - mavenCentralPassword
 * - signing.keyId
 * - signing.password
 * - signing.secretKeyRingFile
 */
class PublishingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply required plugins
            pluginManager.apply("maven-publish")
            pluginManager.apply("signing")

            // Load properties
            val gradleProperties = loadGradleProperties()
            val localProperties = loadLocalProperties()

            // Validate required properties
            validateProperties(gradleProperties, localProperties)

            // Extract properties
            val group = gradleProperties["GROUP"] as String
            val version = gradleProperties["VERSION_NAME"] as String
            val artifactId = project.name

            // Configure publishing
            extensions.configure<PublishingExtension> {
                publications {
                    // For Android Library, create a publication from the release variant
                    if (pluginManager.hasPlugin("com.android.library")) {
                        create<MavenPublication>("release") {
                            // Use components from Android
                            afterEvaluate {
                                from(components["release"])
                            }

                            // Set coordinates
                            groupId = group
                            this.artifactId = artifactId
                            this.version = version

                            // Configure POM
                            pom {
                                configurePom(gradleProperties)
                            }
                        }
                    }

                    // For other platforms, publications are created automatically by Kotlin Multiplatform plugin
                    // We just need to configure their POMs
                    afterEvaluate {
                        publications.withType(MavenPublication::class.java).configureEach {
                            // Set coordinates for all publications
                            groupId = group
                            this.version = version
                            // Keep the artifactId that was set by KMP plugin (includes platform suffix)

                            // Configure POM for all publications
                            pom {
                                configurePom(gradleProperties)
                            }
                        }
                    }
                }

                // Configure Maven Central repository
                repositories {
                    maven {
                        name = "MavenCentral"
                        url = if (version.endsWith("-SNAPSHOT")) {
                            uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                        } else {
                            uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                        }

                        credentials {
                            username = localProperties["mavenCentralUsername"] as? String
                            password = localProperties["mavenCentralPassword"] as? String
                        }
                    }
                }
            }

            // Configure signing
            extensions.configure<SigningExtension> {
                // Only sign if we have credentials
                isRequired = localProperties.containsKey("signing.keyId")

                if (isRequired) {
                    val keyId = localProperties["signing.keyId"] as? String
                    val password = localProperties["signing.password"] as? String
                    val secretKeyRingFile = localProperties["signing.secretKeyRingFile"] as? String

                    // Use in-memory signing if available
                    if (keyId != null && password != null && secretKeyRingFile != null) {
                        // Read the key file and encode to base64 string for useInMemoryPgpKeys
                        val keyFile = file(secretKeyRingFile)
                        if (keyFile.exists()) {
                            val keyContent = keyFile.readBytes()
                            val keyBase64 = java.util.Base64.getEncoder().encodeToString(keyContent)
                            useInMemoryPgpKeys(keyId, keyBase64, password)
                        } else {
                            logger.warn("GPG key file not found: $secretKeyRingFile")
                        }
                    }

                    // Sign all publications
                    val publishing = extensions.getByType(PublishingExtension::class.java)
                    sign(publishing.publications)
                }
            }

            // Print configuration info
            afterEvaluate {
                println("━".repeat(80))
                println("📦 PublishingConventionPlugin configured for ${project.name}")
                println("   Group: $group")
                println("   Artifact: $artifactId")
                println("   Version: $version")
                println("   Signing: ${if (localProperties.containsKey("signing.keyId")) "✓ Enabled" else "✗ Disabled"}")
                println("   Repository: ${if (version.endsWith("-SNAPSHOT")) "Snapshots" else "Releases"}")
                println("━".repeat(80))
            }
        }
    }

    /**
     * Load properties from gradle.properties
     */
    private fun Project.loadGradleProperties(): Properties {
        val properties = Properties()

        // Load from gradle.properties
        val gradlePropsFile = rootProject.file("gradle.properties")
        if (gradlePropsFile.exists()) {
            properties.load(gradlePropsFile.inputStream())
        }

        return properties
    }

    /**
     * Load properties from local.properties
     */
    private fun Project.loadLocalProperties(): Properties {
        val properties = Properties()

        // Load from local.properties
        val localPropsFile = rootProject.file("local.properties")
        if (localPropsFile.exists()) {
            properties.load(localPropsFile.inputStream())
        }

        return properties
    }

    /**
     * Validate that required properties are present
     */
    private fun validateProperties(gradleProps: Properties, localProps: Properties) {
        val requiredGradleProps = listOf(
            "GROUP",
            "VERSION_NAME",
            "POM_NAME",
            "POM_DESCRIPTION",
            "POM_URL",
            "POM_LICENCE_NAME",
            "POM_LICENCE_URL",
            "POM_DEVELOPER_ID",
            "POM_DEVELOPER_NAME",
            "POM_SCM_URL",
            "POM_SCM_CONNECTION",
            "POM_SCM_DEV_CONNECTION"
        )

        val missingGradleProps = requiredGradleProps.filter { !gradleProps.containsKey(it) }
        if (missingGradleProps.isNotEmpty()) {
            throw IllegalStateException(
                "Missing required properties in gradle.properties: ${missingGradleProps.joinToString()}"
            )
        }

        // Local properties are optional for local builds, but required for publishing
        val requiredLocalProps = listOf(
            "mavenCentralUsername",
            "mavenCentralPassword"
        )

        val missingLocalProps = requiredLocalProps.filter { !localProps.containsKey(it) }
        if (missingLocalProps.isNotEmpty()) {
            println("⚠️  Warning: Missing credentials in local.properties: ${missingLocalProps.joinToString()}")
            println("   Publishing to Maven Central will fail without these credentials.")
        }
    }

    /**
     * Configure POM with project metadata
     */
    private fun org.gradle.api.publish.maven.MavenPom.configurePom(props: Properties) {
        name.set(props["POM_NAME"] as String)
        description.set(props["POM_DESCRIPTION"] as String)
        url.set(props["POM_URL"] as String)

        inceptionYear.set(props["POM_INCEPTION_YEAR"] as? String)

        licenses {
            license {
                name.set(props["POM_LICENCE_NAME"] as String)
                url.set(props["POM_LICENCE_URL"] as String)
                distribution.set(props["POM_LICENCE_DIST"] as? String ?: "repo")
            }
        }

        developers {
            developer {
                id.set(props["POM_DEVELOPER_ID"] as String)
                name.set(props["POM_DEVELOPER_NAME"] as String)
                url.set(props["POM_DEVELOPER_URL"] as? String)
                email.set(props["POM_DEVELOPER_EMAIL"] as? String)
            }
        }

        scm {
            url.set(props["POM_SCM_URL"] as String)
            connection.set(props["POM_SCM_CONNECTION"] as String)
            developerConnection.set(props["POM_SCM_DEV_CONNECTION"] as String)
        }

        issueManagement {
            system.set("GitHub Issues")
            url.set("${props["POM_URL"]}/issues")
        }
    }
}