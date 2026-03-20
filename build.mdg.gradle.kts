plugins {
    id("me.decce.transformingbase.gradle.transformingbase-common-conventions")
    id("net.neoforged.moddev") version "2.0.140"
    id("com.gradleup.shadow")
    id("me.modmuss50.mod-publish-plugin")
}

fun prop(name: String) = if (hasProperty(name)) findProperty(name) as String else throw IllegalArgumentException("$name not found")
val modid = prop("modid");

val modSourceSet = sourceSets["mod-src"]

neoForge {
    version = prop("deps.neoforge")
}

val modJar = tasks.register<Jar>("modJar") {
    from(modSourceSet.output)
    archiveClassifier = "mod"
    manifest.attributes (
        "Automatic-Module-Name" to "me.decce.$modid"
    )
}

dependencies {
    jarJar(files(modJar))
}

val jijShadowJar = tasks.register<Jar>("jijShadowJar") {
    from(zipTree(tasks.shadowJar.map { it.archiveFile }))
    dependsOn(tasks.shadowJar)

    from(tasks.jarJar)
    dependsOn(tasks.jarJar)

    manifest.attributes (
        "Automatic-Module-Name" to "me.decce.$modid.${prop("deps.platform")}",
        "Launcher-Agent-Class" to "me.decce.$modid.instrumentation.AgentMain",
        "Can-Redefine-Classes" to "true",
        "Can-Retransform-Classes" to "true",
        "Can-Set-Native-Method-Prefix" to "true"
    )
}

tasks {
    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    named<Jar>("jar") {
        archiveClassifier = "slim"
    }

    assemble.get().dependsOn(jijShadowJar)

    register<Copy>("buildAndCollect") {
        group = "build"
        dependsOn(jijShadowJar)
        from(jijShadowJar.flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.dir("libs"))
    }
}

publishMods {
    file = jijShadowJar.get().archiveFile
}