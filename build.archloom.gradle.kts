import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("me.decce.transformingbase.gradle.transformingbase-common-conventions")
    id("dev.architectury.loom") version "1.13-SNAPSHOT"
    id("com.gradleup.shadow")
    id("me.modmuss50.mod-publish-plugin")
}

fun prop(name: String) = if (hasProperty(name)) findProperty(name) as String else throw IllegalArgumentException("$name not found")

val modid = prop("modid")
val modSourceSet = sourceSets["mod-src"]

// Need to shadow MixinExtras in <1.18.2
val jijMixinExtras = stonecutter.eval(stonecutter.current.version, ">=1.18.2")

dependencies {
    minecraft("com.mojang:minecraft:${prop("deps.minecraft")}")
    mappings(loom.officialMojangMappings())
    forge("net.minecraftforge:forge:${prop("deps.minecraft")}-${prop("deps.forge")}")

    annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.0")
    implementation("io.github.llamalad7:mixinextras-common:0.5.0")
    if (jijMixinExtras) {
        include("io.github.llamalad7:mixinextras-forge:0.5.0")
        implementation("io.github.llamalad7:mixinextras-forge:0.5.0")
    }
    else {
        shade("io.github.llamalad7:mixinextras-common:0.5.0")
    }
}

val modJar = tasks.register<Jar>("modJar") {
    from(modSourceSet.output)
    archiveFileName = "$modid-forge-mod.jar"
    manifest.attributes (
        "Automatic-Module-Name" to "me.decce.$modid"
    )

}

tasks {
    named<Jar>("jar") {
        archiveClassifier = "slim"
    }

    named<ShadowJar>("shadowJar") {
        if (!jijMixinExtras) {
            relocate("com.llamalad7.mixinextras", "me.decce.$modid)}.shadow.mixinextras")
        }
        from(files(modJar))
    }

    named<RemapJarTask>("remapJar") {
        dependsOn(shadowJar)
        inputFile = shadowJar.flatMap { it.archiveFile }
        archiveClassifier = ""
        manifest.attributes (
            "MixinConfigs" to "$modid.mixins.json",
            "Launcher-Agent-Class" to "me.decce.$modid.instrumentation.AgentMain",
            "Can-Redefine-Classes" to "true",
            "Can-Retransform-Classes" to "true",
            "Can-Set-Native-Method-Prefix" to "true"
        )
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        dependsOn(remapJar)
        from(remapJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.dir("libs"))
    }
}

publishMods {
    file = tasks.remapJar.get().archiveFile
}