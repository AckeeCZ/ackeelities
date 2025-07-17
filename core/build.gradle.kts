import io.github.ackeecz.ackeelities.util.Constants

plugins {
    alias(libs.plugins.ackeecz.ackeelities.kmp.library)
    alias(libs.plugins.ackeecz.ackeelities.kmp.testing)
    alias(libs.plugins.ackeecz.ackeelities.publishing)
}

kotlin {

    androidLibrary {
        namespace = "${Constants.NAMESPACE_PREFIX}.core"
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(dependencies.platform(libs.coroutines.bom))
                implementation(libs.coroutines.core)
            }
        }

        commonTest {
            dependencies {
            }
        }
    }
}
