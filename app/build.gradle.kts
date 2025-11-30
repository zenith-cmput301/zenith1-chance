import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

val localProps = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        FileInputStream(file).use { fis ->
            load(fis)
        }
    }
}

val MAPS_API_KEY: String = localProps.getProperty("MAPS_API_KEY") ?: ""


android {
    namespace = "com.example.zenithchance"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.zenithchance"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        manifestPlaceholders["MAPS_API_KEY"] = MAPS_API_KEY

        buildConfigField(
            "String",
            "MAPS_API_KEY",
            "\"$MAPS_API_KEY\""
        )

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

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-firestore")
//    implementation("me.dm7.barcodescanner:zxing:1.9.8")
    implementation("com.karumi:dexter:6.2.2")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.espresso.core)
    implementation(libs.ext.junit)
    implementation(libs.fragment.testing)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Glide: Dependencies added  for processing image URLs better
    implementation("com.github.bumptech.glide:glide:5.0.5")
    annotationProcessor("com.github.bumptech.glide:compiler:5.0.5")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")

    // Fragment testing dependency
    debugImplementation("androidx.fragment:fragment-testing:1.8.2")

    // Google Maps API
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Places SDK (for geocoding / places)
    implementation("com.google.android.libraries.places:places:3.4.0")

}