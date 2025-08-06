plugins {
    alias(libs.plugins.ackeecz.ackeelities.preflightchecks) apply true
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kmp.library) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.gradle.testLogger) apply false
    alias(libs.plugins.kotest.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.mavenPublish) apply false
}
