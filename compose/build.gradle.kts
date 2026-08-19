import io.github.ackeecz.ackeelities.util.Constants

plugins {
    alias(libs.plugins.ackeecz.ackeelities.kmp.library)
    alias(libs.plugins.ackeecz.ackeelities.publishing)
    alias(libs.plugins.kotlin.compose)
}

kotlin {

    android {
        namespace = "${Constants.NAMESPACE_PREFIX}.compose"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(dependencies.platform(libs.compose.bom))
                api(dependencies.platform(libs.coroutines.bom))
                api(libs.android.lifecycle.runtimeCompose)
                api(libs.compose.runtime)
                api(libs.coroutines.core)
            }
        }
    }
}
