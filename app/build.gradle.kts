plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.subarudl.app"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.subarudl.app"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        // Os binários (QuickJS, Python…) precisam ser extraídos para a pasta nativa para poderem ser executados
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.youtubedl.library)
    implementation(libs.youtubedl.ffmpeg)
}
