import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.1"
}

group = "me.decce.transformingbase"


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

sourceSets.create("java17") {
    java.srcDir("src/main/java17")
    compileClasspath += sourceSets["main"].output
}

dependencies {
    shade ("net.lenni0451.classtransform:core:1.15.0-SNAPSHOT") {
        isTransitive = false
    }
    shade ("net.lenni0451:Reflect:1.6.2")

    compileOnly ("org.apache.logging.log4j:log4j-core:2.19.0")

    compileOnly ("org.lwjgl:lwjgl:3.3.1")
    compileOnly ("org.lwjgl:lwjgl-glfw:3.3.1")
    compileOnly ("org.lwjgl:lwjgl-opengl:3.3.1")

    annotationProcessor ("com.pkware.jabel:jabel-javac-plugin:1.0.1-2")
    compileOnly ("com.pkware.jabel:jabel-javac-plugin:1.0.1-2")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    named<JavaCompile>("compileJava") {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        options.release = 8
    }

    named<JavaCompile>("compileJava17Java") {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        options.release = 17
    }

    named<Jar>("jar") {
        archiveClassifier = "slim"
        manifest {
            attributes("Multi-Release" to "true")
        }
        from(sourceSets.getByName("java17").output) {
            into("META-INF/versions/17")
        }
    }

    named<ShadowJar>("shadowJar") {
        configurations = listOf(shade)
        relocate("net.lenni0451.reflect", "me.decce.transformingbase.shadow.reflect")
        relocate("com.electronwill.nightconfig", "me.decce.transformingbase.shadow.nightconfig")
        archiveClassifier = ""
    }

    assemble {
        dependsOn(shadowJar)
    }
}
