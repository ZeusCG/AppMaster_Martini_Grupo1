import org.gradle.testing.jacoco.tasks.JacocoReport
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    jacoco
}

// Firma del APK release (H-027). Las credenciales viven en keystore.properties,
// que NO se sube al repo; la llave release/appmaster.jks SI se sube.
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        FileInputStream(keystorePropertiesFile).use { load(it) }
    }
}

android {
    namespace = "com.example.appmaster_martini_grupo1"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.appmaster_martini_grupo1"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        // Solo si existe keystore.properties; así un clon sin credenciales igual
        // configura el proyecto (genera APK sin firmar) y no rompe el build.
        if (keystorePropertiesFile.exists()) {
            create("release") {
                storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }
    buildTypes {
        release {
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
            optimization {
                // H-028: se deja desactivada a propósito. Activar R8/minify exige reglas `keep`
                // para Room/Retrofit/Gson/Compose y no se puede verificar en este entorno;
                // se explica en la defensa (el APK pesa más, pero es reproducible y no arriesga reflexión).
                enable = false
            }
        }
        debug {
            // Genera el .exec de cobertura de los tests unitarios (para JaCoCo).
            enableUnitTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        // Con AGP 9 el BuildConfig no se genera salvo que se active. `RetrofitClient`
        // usa `BuildConfig.DEBUG` para los logs (H-020), así que hay que habilitarlo.
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.camera.view)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)
}

// Reporte de cobertura de los tests unitarios (ViewModel / Repository / domain).
tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val excludes = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/*_Factory.*",
        "**/*_Impl.*",
        // El objetivo de la rúbrica es la LÓGICA (ViewModel / Repository / domain):
        "**/ui/**",
        "**/platform/**",
        // H-022: se excluyen solo los DAO y las entidades (código de Room), no los mappers.
        // Así el mapeo Room<->dominio (ContentMappers, ContentSeed) sí cuenta y se prueba.
        "**/data/local/**Dao*",
        "**/data/local/*Entity*",
        "**/MainActivity*"
    )
    val buildDir = layout.buildDirectory.get().asFile

    classDirectories.setFrom(
        files(
            fileTree("$buildDir/intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes") { exclude(excludes) },
            fileTree("$buildDir/intermediates/javac/debug/compileDebugJavaWithJavac/classes") { exclude(excludes) }
        )
    )
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(
        fileTree(buildDir) {
            include(
                "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
                "jacoco/testDebugUnitTest.exec"
            )
        }
    )
}
