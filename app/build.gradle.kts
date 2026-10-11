plugins { id("com.android.application") }
android { namespace = "fr.meryl.clavierperso"; compileSdk = 35
 defaultConfig { applicationId = "fr.meryl.clavierperso"; minSdk = 26; targetSdk = 35; versionCode = 2; versionName = "1.1-diagnostic" }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}
