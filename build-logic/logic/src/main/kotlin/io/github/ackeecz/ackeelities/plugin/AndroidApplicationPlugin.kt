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
        // AGP 9 compiles Kotlin itself (built-in Kotlin); org.jetbrains.kotlin.android must NOT be applied
        pluginManager.apply(libs.plugins.android.application)

        androidApp {

            defaultConfig {
                targetSdk = Constants.TARGET_SDK
                versionCode = 1
                versionName = "1.0"
            }

            buildTypes {
                release {
                    // AGP 9.3+ optimization DSL. Enabling it turns on both code optimization and
                    // optimized resource shrinking and implies the default Android keep rules, so
                    // neither isMinifyEnabled nor getDefaultProguardFile() needs to be specified.
                    // Project-specific keep rules belong to the src/release/keepRules/*.keep source set.
                    optimization {
                        enable = true
                    }
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
