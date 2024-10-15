import java.net.URI

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.20"
    id("org.jetbrains.intellij") version "1.16.0"
}

group = "com.github.xiaolyuh"
version = "1.3.8"

repositories {
    maven { url = URI("https://maven.aliyun.com/nexus/content/groups/public/") }
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation("com.jcraft:jsch:0.1.54")
    testImplementation("junit:junit:4.13.1")
}

sourceSets["main"].java.srcDirs("src/main/gen")

intellij {
    version.set("2022.1.2")
    type.set("IC")
    plugins.set(
        listOf(
            "Git4Idea",
            "tasks",
            "com.intellij.java",
            "com.intellij.properties",
        )
    )
    intellij.updateSinceUntilBuild.set(false)
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
        options.encoding = "UTF-8"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    jar {
        // kt文件不知道被哪个配置影响导致被编译了两次,所以这里暂时配置下
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    runIde {
        systemProperties["idea.auto.reload.plugins"] = true
        jvmArgs = listOf(
            "-Xms1024m",
            "-Xmx2048m",
        )
    }

    patchPluginXml {
        sinceBuild.set("221")
        untilBuild.set("243.*")
        changeNotes.set(
            """
        <em>1.1.21<em></br
        <em>1. 修复gitlib 解决冲突分支相互合并的问题 <em></br>   
        
        <em>1.1.1<em></br>
        <em>1. 新增merge requet 标签功能 <em></br>   
        
        <em>1.1.0<em></br>
        <em>1. 对2021.1的支持<em></br>   
        <em>2. 支持在本地发起Merge Request<em></br>   
        
        <em>1.0.13<em></br>
        <em>1. 对2020.3的支持<em></br>   

        <em>1.0.12<em></br>
        <em>1. 对2020.2的支持<em></br>   
        
        <em>1.0.11<em></br>
        <em>1. 修正锁定时间不准确的问题<em></br>   
        
        <em>1.0.10<em></br>
        <em>1. 修改重建测试分支bug<em></br>   
             
        <em>1.0.9<em></br>
        <em>1. 获取最后一次操作人时先fetch下<em></br>        
        
        <em>1.0.8<em></br>
        <em>1. 最后一次操作人从锁定分支上面取<em></br>        
        
        <em>1.0.7<em></br>
        <em>1. 优化2020.1<em></br>
        
        <em>1.0.6<em></br>
        <em>1. 优化2020.1<em></br>
        
        <em>1.0.5<em></br>
        <em>1. 支持2020.1<em></br>
        
        <em>1.0.4<em></br>
        <em>1. 修改发布完成的弹框提示<em></br>

        </br>
        <em>1.0.3<em></br>
        <em>1. 国际化的支持<em></br>

        </br>
        <em>1.0.2<em></br>
        <em>1. merge代码优化<em></br>
        <em>2. 文档格式优化<em></br>

        </br>
        <em>1.0.1<em></br>
        <em>1. 新增GIT 命令输出<em></br>
        <em>2. 发布完成合并release到master<em></br>
        <em>3. 发布完成权限校验<em></br>
        <em>4. Tag 名称校验<em></br>
        
        </br>
        <em>1.0.0<em></br>
        <em>插件名称修改成GitFlowPlus</em></br>
        <em>发布完成和发布失败强制校验发布分支是否锁定</em></br>
      """
        )
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

