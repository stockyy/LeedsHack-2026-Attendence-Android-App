// build.gradle.kts (Project: LeedsHack...)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.compose.compiler) apply false
    // You don't need the Ktor plugin here for the Android app, usually just the backend
}

