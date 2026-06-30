plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.kikugie.dev/releases")
}

dependencies {
    implementation("com.gradleup.shadow:shadow-gradle-plugin:9.3.1")
    implementation("me.modmuss50:mod-publish-plugin:2.1.1")
    implementation("dev.kikugie:stonecutter:0.9.6")
}
