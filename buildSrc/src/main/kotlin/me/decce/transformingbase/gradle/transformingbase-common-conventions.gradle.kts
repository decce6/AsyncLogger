package me.decce.transformingbase.gradle
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import java.nio.file.Files

plugins {
    `java-library`
    id("com.gradleup.shadow")
    id("me.modmuss50.mod-publish-plugin")
}

fun prop(name: String) = if (hasProperty(name)) findProperty(name) as String else throw IllegalArgumentException("$name not found")
val modid = prop("modid")
val shade = configurations.create("shade")
configurations.implementation.get().extendsFrom(shade)

val platform = prop("deps.platform")
fun fullModVersion() = "${prop("mod_version")}+${prop("deps.minecraft")}-${platform}"

val stonecutter = project.extensions.getByType<StonecutterBuildExtension>()
val mcVersion = stonecutter.current.version
val javaVersion =
    if (stonecutter.eval(mcVersion, ">=26")) 25
    else if (stonecutter.eval(mcVersion, ">=1.20.5")) 21
    else if (stonecutter.eval(mcVersion, ">=1.18")) 17
    else 17

java {
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)
}

version = fullModVersion()
group = prop("maven_group")
base {
    archivesName = modid
}

val modSourceSet = sourceSets.create("mod-src") {
    compileClasspath += sourceSets["main"].compileClasspath
    runtimeClasspath += sourceSets["main"].runtimeClasspath
    annotationProcessorPath += sourceSets["main"].annotationProcessorPath
}
val serviceSourceSet = sourceSets.create("service") {
    compileClasspath += sourceSets["main"].compileClasspath
    runtimeClasspath += sourceSets["main"].runtimeClasspath
}

dependencies {
    shade("me.decce.transformingbase:core") {
        isTransitive = false
    }
    shade("net.lenni0451.classtransform:core:1.15.0-SNAPSHOT") {
        isTransitive = false
    }
    shade("net.lenni0451:Reflect:1.6.2")
    shade("com.electronwill.night-config:core:3.8.3")
    shade("com.electronwill.night-config:toml:3.8.3")
}

fun fetchLatestChangelog() : String {
    val str = Files.readString(layout.settingsDirectory.file("CHANGELOG.md").asFile.toPath())
    val i = str.indexOf('\n') + 2
    val r = str.indexOf("## ", i + 1)
    return str.substring(i, r - 2)
}

fun supportedVersionFabric() : String {
    var str = ""
    if (project.hasProperty("minecraft_supported_from")) {
        str += ">=${prop("minecraft_supported_from")}"
    }
    if (project.hasProperty("minecraft_supported_to")) {
        str += " <=${prop("minecraft_supported_to")}"
    }
    if (str == "") {
        str = "=${prop("deps.minecraft")}"
    }
    return str;
}

fun supportedVersionForge() : String {
    var str = "["
    if (hasProperty("minecraft_supported_from")) {
        str += "${prop("minecraft_supported_from")},"
    }
    if (hasProperty("minecraft_supported_to")) {
        str += prop("minecraft_supported_to")
    }
    if (str == "[") {
        str += prop("deps.minecraft")
    }
    str += "]"
    return str;
}

repositories {
    fun exclusiveMaven(mavenName: String, group: String, mavenUrl: String) {
        exclusiveContent {
            forRepository {
                maven {
                    name = mavenName
                    url = uri(mavenUrl)
                }
            }
            filter {
                includeGroupAndSubgroups(group)
            }
        }
    }
    exclusiveMaven("Sponge", "org.spongepowered", "https://repo.spongepowered.org/repository/maven-public")
    maven {
        name = "lenni0451"
        url = uri("https://maven.lenni0451.net/snapshots")
    }
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveClassifier = "fat"
        // Note: license files for ClassTransform and Reflect are already included by shadowing
        from (files(layout.settingsDirectory.file("thirdparty/licenses/LICENSE_NightConfig")))
        from (serviceSourceSet.output)
        relocate("me.decce.transformingbase", "me.decce.$modid")
        relocate("net.lenni0451.classtransform", "me.decce.$modid.shadow.classtransform")
        relocate("net.lenni0451.reflect", "me.decce.$modid.shadow.reflect")
        relocate("com.electronwill.nightconfig", "me.decce.$modid.shadow.nightconfig")
        configurations = listOf(shade)
        mergeServiceFiles()
    }

    withType<ProcessResources> {
        if (platform != "fabric") exclude("**/fabric.mod.json")
        if (platform != "neoforge") exclude("**/neoforge.mods.toml")
        if (platform != "forge") exclude("**/mods.toml", "**/pack.mcmeta")
        val propMap = mutableMapOf<String, Any>().apply {
            project.properties.forEach { k, v -> put(k.toString(), v.toString()) }
            put("mod_version_full", fullModVersion())
            put("minecraft_supported_fabric", supportedVersionFabric())
            put("minecraft_supported_forge", supportedVersionForge())
            put("java_version", javaVersion)
        }
        inputs.property("propMap", propMap)
        filesMatching(listOf("**/fabric.mod.json", "**/neoforge.mods.toml", "**/mods.toml", "**/pack.mcmeta")) {
            expand(propMap)
        }
    }

    named<ProcessResources>("processResources") {
        from (layout.settingsDirectory.file("LICENSE"))
    }
}

publishMods {
    type = STABLE
    version = fullModVersion()
    dryRun = providers.environmentVariable("CURSEFORGE_TOKEN").getOrNull() == null || providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null
    changelog = fetchLatestChangelog()
    displayName = "${prop("mod_name")} ${fullModVersion()}"
    modLoaders.add(prop("deps.platform"))
    if (prop("deps.platform") == "fabric") {
        modLoaders.add("quilt")
    }
    curseforge {
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        clientRequired = true
        serverRequired = true
        projectId = ""
        projectSlug = modid
        if (hasProperty("minecraft_supported_from")) {
            minecraftVersionRange {
                start = prop("minecraft_supported_from")
                end = if (hasProperty("minecraft_supported_to")) prop("minecraft_supported_to") else "latest"
            }
        }
        else {
            minecraftVersions.add(prop("deps.minecraft"))
        }
    }

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        projectId = ""
        if (hasProperty("minecraft_supported_from")) {
            minecraftVersionRange {
                start = prop("minecraft_supported_from")
                end = if (hasProperty("minecraft_supported_to")) prop("minecraft_supported_to") else "latest"
            }
        }
        else {
            minecraftVersions.add(prop("deps.minecraft"))
        }
    }
}