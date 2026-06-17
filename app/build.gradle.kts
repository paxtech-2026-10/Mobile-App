plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    //Plugins ksp y hilt (sin apply false)
    alias(libs.plugins.ksp) //KSP
    alias(libs.plugins.hilt) //HILT

    id("com.google.gms.google-services") //firebase
    alias(libs.plugins.firebase.appdistribution) // Agregar esta línea
}

android {
    namespace = "com.paxtech.mobileapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.paxtech.mobileapp"
        minSdk = 24
        targetSdk = 36
        // Nombre de versión estable: súbelo para publicar un nuevo release.
        // El workflow de CI construye y publica un release cuando este valor cambia.
        versionName = "1.1.0"

        // versionCode sigue auto-incrementándose con el número de commits de Git,
        // para que cada build tenga un código único y creciente.
        val gitCommitCount = providers.exec {
            commandLine("git", "rev-list", "--count", "HEAD")
        }.standardOutput.asText.get().trim().toIntOrNull() ?: 1
        versionCode = gitCommitCount

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

firebaseAppDistribution {
    appId = "1:926361378609:android:a84ac4062d1a098abfda10"
    releaseNotes = "Test Android Release"
    // Opcional: grupos de testers
    groups = "profesores, grupo"
    // Opcional: testers individuales
    // testers = "correo@profesor.com"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Material icons extended
    implementation(libs.androidx.material.icons.extended)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Hilt Navigation
    implementation(libs.androidx.hilt.navigation.compose)
    
    // Google Fonts
    implementation("androidx.compose.ui:ui-text-google-fonts:1.6.0")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    
    // Google Play Services Location
    implementation("com.google.android.gms:play-services-location:21.3.0")

    implementation("androidx.datastore:datastore-preferences:1.1.7")
// optional - RxJava2 support
    implementation("androidx.datastore:datastore-preferences-rxjava2:1.1.7")
// optional - RxJava3 support
    implementation("androidx.datastore:datastore-preferences-rxjava3:1.1.7")
}