import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("me.decce.transformingbase.gradle.transformingbase-common-conventions")
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
    id("com.gradleup.shadow")
    id("me.modmuss50.mod-publish-plugin")
}

fun prop(name: String) = if (hasProperty(name)) findProperty(name) as String else throw IllegalArgumentException("$name not found")
val modid = prop("modid")

val modSourceSet = sourceSets["mod-src"]

dependencies {
    minecraft("com.mojang:minecraft:${prop("deps.minecraft")}")
    implementation("net.fabricmc:fabric-loader:0.17.2")
}

tasks {
    named<Jar>("jar") {
        archiveClassifier = "slim"
    }

    named<ShadowJar>("shadowJar") {
        from (modSourceSet.output)
        archiveClassifier = ""
        manifest.attributes (
            "Automatic-Module-Name" to "me.decce.$modid.${prop("deps.platform")}"
        )
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        dependsOn(shadowJar)
        from(shadowJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.dir("libs"))
    }
}

publishMods {
    file = tasks.shadowJar.get().archiveFile
}