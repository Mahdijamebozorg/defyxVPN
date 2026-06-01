import java.util.Properties

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("key.properties")

if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("dev.flutter.flutter-gradle-plugin")
}

android {
    namespace = "de.unboundtech.defyxvpn"
    compileSdk = flutter.compileSdkVersion
    ndkVersion = flutter.ndkVersion  

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // AGP +9 migration
    // kotlinOptions {
    //     jvmTarget = JavaVersion.VERSION_17.toString()
    // }
    defaultConfig {
        applicationId = "de.unboundtech.defyxvpn"
        minSdk = flutter.minSdkVersion
        targetSdk = flutter.targetSdkVersion
        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }

    signingConfigs {
        if (keystorePropertiesFile.exists()) {
            create("release") {
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig =
                if (keystorePropertiesFile.exists()) signingConfigs.getByName("release")
                else signingConfigs.getByName("debug")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // exclude some common metadata files that inflate APK size
    packagingOptions {
        jniLibs { useLegacyPackaging = true }
        resources {
            excludes +=
                    setOf(
                            "META-INF/*.kotlin_module",
                            "META-INF/*.version",
                            "META-INF/AL2.0",
                            "META-INF/LGPL2.1",
                            "META-INF/LICENSE*",
                            "META-INF/NOTICE*",
                            "META-INF/DEPENDENCIES",
                            "META-INF/proguard/*",
                            "META-INF/gradle/incremental.annotation.processors"
                    )
        }
    }
}

// AGP +9 migration
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

flutter { source = "../.." }

repositories {
    flatDir { dirs("libs") }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.android.ump:user-messaging-platform:3.1.0")

    // Android TV support
    implementation("androidx.leanback:leanback:1.0.0")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(files("libs/DXcore.aar"))

    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
