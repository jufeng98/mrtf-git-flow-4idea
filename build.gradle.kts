import java.net.URI

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.3.0"
}

group = "com.github.xiaolyuh"
version = "2.3.3"

repositories {
    maven { url = URI("https://maven.aliyun.com/nexus/content/groups/public/") }
    mavenLocal()
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create("IC", "2024.3")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
        bundledPlugin("Git4Idea")
    }

    testImplementation("junit:junit:4.13.1")
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "230"
            untilBuild = "252.*"
        }
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.encoding = "UTF-8"
    }

    runIde {
        autoReload = false
    }

    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    patchPluginXml {

    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

