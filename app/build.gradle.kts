plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id ("com.google.gms.google-services")

}

android {
    namespace = "com.squmish.rcuapp"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }


    defaultConfig {
        applicationId = "com.squmish.rcuapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 2
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    dataBinding.enable = true
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildFeatures {
        viewBinding = true
    }

    dataBinding.enable = true
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.play.services.location)
    implementation(libs.androidx.swiperefreshlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    implementation (libs.androidx.lifecycle.runtime.ktx.v283)

    //Networking
    implementation (libs.logging.interceptor)
    implementation (libs.adapter.rxjava2)

    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    implementation (libs.rxandroid)
    implementation (libs.rxbinding)

    // Room Database
    implementation (libs.androidx.room.runtime)
    annotationProcessor (libs.androidx.room.compiler)

    //Image Process
    implementation (libs.glide)
    implementation (libs.annotations)


    implementation (libs.dexter)

    // dp and sp size for multiple layout
    implementation (libs.ssp.android)
    implementation (libs.sdp.android)

    implementation (libs.material)
    implementation (libs.androidx.work.runtime.ktx)

    implementation ("com.google.firebase:firebase-messaging-ktx:23.0.3")
    implementation ("com.google.firebase:firebase-bom:29.3.1")
    implementation ("com.google.firebase:firebase-messaging:23.0.3")

    implementation ("id.zelory:compressor:3.0.1")
}
