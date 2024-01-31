import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    iosX64()
//    iosArm64()
    iosSimulatorArm64()

    configure(targets){
        if (this is KotlinNativeTarget && konanTarget.family.isAppleFamily){
            compilations.getByName("main").cinterops.create("observer"){
                // defFile(project.file("../nativeMain/observer.def"))
                packageName("com.kmmvideoplayer")
            }
        }
    }


    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            api(compose.runtime)
//            implementation ("androidx.compose.runtime:runtime:1.6.0")
//            implementation(compose.foundation)
            api(compose.material)
            api(compose.materialIconsExtended)
//            implementation(compose.ui)
//            @OptIn(ExperimentalComposeLibrary::class)
//            implementation(compose.components.resources)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            api(libs.androidx.activity.compose)

            api (libs.androidx.media3.exoplayer)
            api (libs.androidx.media3.ui)
            api (libs.androidx.media3.common)
            api (libs.androidx.media3.exoplayer.hls)
            api(libs.androidx.media3.exoplayer.dash)
            api(libs.androidx.media3.datasource.cronet)
        }
    }
}

android {
    namespace = "com.github.nabin0.kmmvideoplayer"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}
dependencies {
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
}
