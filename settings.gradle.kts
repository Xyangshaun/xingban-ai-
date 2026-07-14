pluginManagement {
    repositories {
        maven { url = uri("https://mirrors.aliyun.com/gradle-plugin") }
        maven { url = uri("https://mirrors.aliyun.com/maven") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven { url = uri("https://mirrors.aliyun.com/maven") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://artifact.bytedance.com/repository/Volcengine/") }
        google()
        mavenCentral()
    }
}

rootProject.name = "MySpatialApp"
include(":app")