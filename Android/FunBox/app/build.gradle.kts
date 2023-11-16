import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
}


android {
    namespace = "com.example.funbox"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.funbox"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "MAP_API_KEY", getApiKey("MAP_API_KEY"))
        buildConfigField("String", "NAVER_LOGIN_ID_KEY", getApiKey("NAVER_LOGIN_ID_KEY"))
        buildConfigField("String", "NAVER_LOGIN_SECRET_KEY", getApiKey("NAVER_LOGIN_SECRET_KEY"))
        manifestPlaceholders["MAP_API_KEY"] = getApiKey("MAP_API_KEY")
        manifestPlaceholders["NAVER_LOGIN_ID_KEY"] = getApiKey("NAVER_LOGIN_ID_KEY")
        manifestPlaceholders["NAVER_LOGIN_SECRET_KEY"] = getApiKey("NAVER_LOGIN_SECRET_KEY")
        resValue("string", "map_api_key", getApiKey("MAP_API_KEY"))
        resValue("string", "naver_login_id_key", getApiKey("NAVER_LOGIN_ID_KEY"))
        resValue("string", "naver_login_secret_key", getApiKey("NAVER_LOGIN_SECRET_KEY"))
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        //noinspection DataBindingWithoutKapt
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }
}

fun getApiKey(key: String): String {
    return gradleLocalProperties(rootDir).getProperty(key)
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")

    implementation("com.naver.maps:map-sdk:3.17.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    implementation("io.coil-kt:coil:2.0.0-rc03")

    implementation ("com.squareup.retrofit2:retrofit:2.8.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.6.0")

    implementation ("com.navercorp.nid:oauth-jdk8:5.1.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")

    implementation ("com.jakewharton.timber:timber:5.0.1")
    
    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.squareup.retrofit2:retrofit:2.6.4")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.android.gms:play-services-location:20.0.0")
