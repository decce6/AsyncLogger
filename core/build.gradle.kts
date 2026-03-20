import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.1"
}

group = "me.decce.transformingbase"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.neoforged.net/releases")
    }
    maven {
        url = uri("https://maven.minecraftforge.net/")
    }
    maven {
        name="lenni0451"
        url = uri("https://maven.lenni0451.net/snapshots")
    }
}

val shade = configurations.create("shade")
configurations.implementation.get().extendsFrom(shade)

dependencies {
    shade ("net.lenni0451.classtransform:core:1.15.0-SNAPSHOT") {
        isTransitive = false
    }
    //shade ("org.ow2.asm:asm:9.9.1")
    //shade ("org.ow2.asm:asm-commons:9.9.1")
    shade ("net.lenni0451:Reflect:1.6.2")

    compileOnly ("org.apache.logging.log4j:log4j-core:2.19.0")
    compileOnly ("cpw.mods:modlauncher:10.0.9")
    compileOnly ("cpw.mods:bootstraplauncher:1.1.2")
    // compileOnly ("cpw.mods:securejarhandler:2.1.10")
    compileOnly ("net.minecraftforge:securemodules:2.2.21")

    compileOnly ("org.lwjgl:lwjgl:3.3.1")
    compileOnly ("org.lwjgl:lwjgl-glfw:3.3.1")
    compileOnly ("org.lwjgl:lwjgl-opengl:3.3.1")
}

tasks {
    named<Jar>("jar") {
        archiveClassifier = "slim"
    }

    named<ShadowJar>("shadowJar") {
        configurations = listOf(shade)
        relocate("net.lenni0451.classtransform", "me.decce.transformingbase.shadow.classtransform")
        relocate("net.lenni0451.reflect", "me.decce.transformingbase.shadow.reflect")
//        relocate("org.objectweb.asm", "me.decce.transformingbase.core.shadow.asm")
        relocate("com.electronwill.nightconfig", "me.decce.transformingbase.shadow.nightconfig")
        archiveClassifier = ""
        exclude ("/META-INF/versions/21/**")
        exclude ("/META-INF/versions/24/**")
    }

    assemble {
        dependsOn(shadowJar)
    }
}
