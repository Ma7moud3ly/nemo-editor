plugins {
    `kotlin-dsl`
}

group = "io.github.ma7moud3ly.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    implementation(libs.vanniktech.maven.publish)
}

gradlePlugin {
    plugins {
        register("PublishingPlugin") {
            id = "maven.publishing"
            implementationClass = "PublishingConventionPlugin"
        }
    }
}