package io.github.ackeecz.ackeelities.plugin

import io.github.ackeecz.ackeelities.util.Constants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal class AndroidApplicationPlugin : Plugin<Project> {

    private val androidPlugin = AndroidPlugin()
    private val detektPlugin = DetektPlugin()

    override fun apply(target: Project) {
        target.configure()
        target.configureKotlin()
        androidPlugin.apply(target)
        detektPlugin.apply(target)
    }

    private fun Project.configure() {
        pluginManager.apply(libs.plugins.android.application)
        pluginManager.apply(libs.plugins.kotlin.android)

        androidApp {

            defaultConfig {
                targetSdk = Constants.TARGET_SDK
                versionCode = 1
                versionName = "1.0"
            }

            buildTypes {
                release {
                    isMinifyEnabled = true
                    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                }
            }
        }
    }

    private fun Project.configureKotlin() {
        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions {
                configureApplicationOptions()
            }
        }
    }
}
