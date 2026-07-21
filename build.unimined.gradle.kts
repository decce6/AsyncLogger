import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("me.decce.transformingbase.gradle.transformingbase-common-conventions")
    id("xyz.wagyourtail.unimined") version "1.4.1"
    id("com.gradleup.shadow")
    id("me.modmuss50.mod-publish-plugin")
}

fun prop(name: String) = if (hasProperty(name)) findProperty(name) as String else throw IllegalArgumentException("$name not found")
val modid = prop("modid");

val modSourceSet = sourceSets["mod-src"]
val shadowJar = tasks.named("shadowJar")

unimined.minecraft {
    version(prop("deps.minecraft"))
    mappings {
        searge()
        mcp("stable", "39-1.12")
    }
    minecraftForge {
        loader(prop("deps.forge"))
    }
    defaultRemapJar = true
}

unimined.minecraft(modSourceSet) {
    combineWith(sourceSets["main"])
}

unimined.minecraft(sourceSets["service"]) {
    combineWith(sourceSets["main"])
}

tasks {
    named<Jar>("jar") {
        archiveClassifier = "slim"
    }
    named<ShadowJar>("shadowJar") {
        from(modSourceSet.output)
        archiveClassifier = ""
        manifest.attributes(
            "FMLCorePlugin" to "me.decce.asynclogger.service.legacyforge.AsyncLoggerPlugin",
            "FMLCorePluginContainsFMLMod" to true,
            "ForceLoadAsMod" to true
        )
        // Workaround for FML nuance, with error message like:
        // There was a problem reading the entry META-INF/versions/17/me/decce/asynclogger/shadow/nightconfig/core/serde/ConfigToPojoDeserializer.class in the jar <path to asynclogger-2.1.2+1.12.2-forge.jar> - probably a corrupt zip
        // The classes we exclude are for the serde feature of night config, which we do not use. Ideally we should move config loading to core and enable minimize.
        exclude("META-INF/versions/**")
    }

    assemble.get().dependsOn(shadowJar)

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