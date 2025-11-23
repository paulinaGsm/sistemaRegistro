plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    //alias(libs.plugins.google.services)
    id("com.google.gms.google-services")

}

android {
    namespace = "com.uaa.registro_uaa"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.uaa.registro_uaa"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
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
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation ("com.airbnb.android:lottie:4.2.1")



    implementation (platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.android.material:material:1.8.0")
    implementation("com.github.bumptech.glide:glide:4.14.2")


    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-auth:22.3.0")
    implementation ("com.google.firebase:firebase-firestore:24.10.0")

    implementation("androidx.cardview:cardview:1.0.0")
    implementation ("com.itextpdf:itextg:5.5.10")

    //mapa

    implementation("org.osmdroid:osmdroid-android:6.1.18")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.11.0")
    implementation ("com.google.android.material:material:1.11.0")

    //pruebas unitarias
    // JUnit para pruebas unitarias
    testImplementation("junit:junit:4.13.2")

    // Para pruebas de Android (opcional)
  //  androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    //androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1
    implementation(kotlin("test"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

}
