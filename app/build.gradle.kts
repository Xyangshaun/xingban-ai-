plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.myspatial"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myspatial"
        minSdk = 35
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.12"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            // 强制安装时解压 .so 文件到设备文件系统，
            // 避免因页对齐问题导致 dlopen "library not found" 错误
            useLegacyPackaging = true
        }
    }

    androidResources {
        noCompress.add(".bundle")
        noCompress.add(".glb")
        noCompress.add(".ktx")
        noCompress.add(".usdz")
    }
}

configurations.all {
    resolutionStrategy {
        exclude(group = "androidx.compose.ui", module = "ui")
        exclude(group = "androidx.compose.ui", module = "ui-text")
        exclude(group = "androidx.compose.ui", module = "ui-graphics")
        exclude(group = "androidx.compose.foundation", module = "foundation")
    }
}

dependencies {
    implementation(platform(libs.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.core)
    implementation(libs.platform)
    implementation(libs.foundation)
    implementation(libs.design)
    implementation(libs.sense)
    implementation(libs.tracking)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.activity.compose)
    implementation(libs.gson)
    implementation(libs.okhttp)
}