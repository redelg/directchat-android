import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.devtools.ksp") version "2.3.6"
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.10"
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        localFile.inputStream().use(::load)
    }
}

fun readLocalOrEnv(name: String, fallback: String): String {
    return localProperties.getProperty(name)
        ?: System.getenv(name)
        ?: fallback
}

fun escapeForBuildConfig(value: String): String {
    return value.replace("\\", "\\\\").replace("\"", "\\\"")
}

android {
    compileSdk = 36

    defaultConfig {
        applicationId = "com.codergang.chatdirecto"
        minSdk = 24
        targetSdk = 36
        versionCode = 3
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "RC_ENTITLEMENT_PRO", "\"Premium\"")
        buildConfigField("String", "RC_PRODUCT_LIFETIME", "\"lifetime\"")
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file("key/key")
            storePassword = "directchat2022"
            keyAlias = "directchat2022"
            keyPassword = "directchat2022"
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    buildTypes {
        debug {
            val debugRcApiKey = readLocalOrEnv(
                name = "RC_API_KEY_DEBUG",
                fallback = "test_cgvJZfCUxisxFtyfjdpMnTRxHon"
            )
            buildConfigField(
                "String",
                "RC_API_KEY",
                "\"${escapeForBuildConfig(debugRcApiKey)}\""
            )
        }

        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk.debugSymbolLevel = "FULL"
            val releaseRcApiKey = readLocalOrEnv(
                name = "RC_API_KEY_RELEASE",
                fallback = "REPLACE_WITH_REVENUECAT_ANDROID_PUBLIC_SDK_KEY"
            )
            buildConfigField(
                "String",
                "RC_API_KEY",
                "\"${escapeForBuildConfig(releaseRcApiKey)}\""
            )
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    namespace = "com.codergang.chatdirecto"
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // Core AndroidX
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.fragment:fragment-ktx:1.8.9")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation(platform("androidx.compose:compose-bom:2026.02.01"))
    implementation("androidx.activity:activity-compose:1.12.4")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.runtime:runtime-livedata")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Lifecycle
    val lifecycleVersion = "2.10.0"
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-process:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")

    // Phone metadata
    implementation("com.googlecode.libphonenumber:libphonenumber:9.0.25")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.10.0"))
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")

    // Google Ads
    implementation("com.google.android.gms:play-services-ads:25.0.0")
    implementation("com.revenuecat.purchases:purchases:9.23.1")
    implementation("com.revenuecat.purchases:purchases-ui:9.23.1")

    // Room Database
    val roomVersion = "2.8.4"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // QR Code
    implementation("com.google.zxing:core:3.5.4")

    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.2.0")

    // Retrofit
    val retrofitVersion = "3.0.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    // Indicator
    implementation("com.tbuonomo:dotsindicator:5.1.0")
}
