import io.github.ackeecz.ackeelities.util.Constants

plugins {
    alias(libs.plugins.ackeecz.ackeelities.kmp.library)
    alias(libs.plugins.ackeecz.ackeelities.kmp.testing)
    alias(libs.plugins.ackeecz.ackeelities.publishing)
}

kotlin {

    android {
        namespace = "${Constants.NAMESPACE_PREFIX}.coroutines"
    }

    compilerOptions {
        optIn.addAll("kotlinx.coroutines.ExperimentalCoroutinesApi")
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
                // Kotest 6 no longer brings kotlinx-coroutines-test transitively
                implementation(dependencies.platform(libs.coroutines.bom))
                implementation(libs.coroutines.test)
            }
        }
    }
}
