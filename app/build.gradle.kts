import io.github.ackeecz.ackeelities.properties.LibraryProperties
import io.github.ackeecz.ackeelities.util.Constants

plugins {
    alias(libs.plugins.ackeecz.ackeelities.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "${Constants.NAMESPACE_PREFIX}.sample"

    defaultConfig {
        applicationId = Constants.NAMESPACE_PREFIX
    }

    buildFeatures {
        compose = true
    }
}

@Suppress("UseTomlInstead")
dependencies {

    val bomVersion = LibraryProperties(project).bomArtifactProperties.version
    implementation(platform("io.github.ackeecz:ackeelities-bom:$bomVersion"))
    implementation("io.github.ackeecz:ackeelities-core")

    implementation(libs.android.activity)
    implementation(libs.android.core)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.activity)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    debugImplementation(libs.compose.uiTooling)
    implementation(libs.compose.uiToolingPreview)
}
