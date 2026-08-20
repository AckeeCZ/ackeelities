package io.github.ackeecz.ackeelities.plugin

import com.android.build.api.dsl.ApplicationExtension
import io.github.ackeecz.ackeelities.util.Constants
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class AndroidPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.configure()
    }

    private fun Project.configure() {
        androidApp {
            configureSdkVersions()
            configureCompileOptions()
        }
    }

    private fun ApplicationExtension.configureSdkVersions() {
        compileSdk = Constants.COMPILE_SDK
        defaultConfig {
            minSdk = Constants.MIN_SDK
        }
    }

    private fun ApplicationExtension.configureCompileOptions() {
        compileOptions {
            sourceCompatibility = Constants.JAVA_VERSION
            targetCompatibility = Constants.JAVA_VERSION
        }
    }
}
