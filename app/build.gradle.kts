import io.github.ackeecz.ackeelities.properties.LibraryProperties
import io.github.ackeecz.ackeelities.util.Constants

plugins {
    alias(libs.plugins.ackeecz.ackeelities.android.application)
}

android {
    namespace = "${Constants.NAMESPACE_PREFIX}.sample"

    defaultConfig {
        applicationId = Constants.NAMESPACE_PREFIX
    }
}

@Suppress("UseTomlInstead")
dependencies {

    val bomVersion = LibraryProperties(project).bomArtifactProperties.version
    implementation(platform("io.github.ackeecz:ackeelities-bom:$bomVersion"))
    implementation("io.github.ackeecz:ackeelities-core")
}
