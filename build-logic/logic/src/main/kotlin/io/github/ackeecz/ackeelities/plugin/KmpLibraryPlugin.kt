package io.github.ackeecz.ackeelities.plugin

import io.github.ackeecz.ackeelities.util.Constants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

internal class KmpLibraryPlugin : Plugin<Project> {

    private val detektPlugin = DetektPlugin()

    override fun apply(target: Project) {
        target.configureKmp()
        target.configureDetekt()
    }

    @Suppress("UnstableApiUsage")
    private fun Project.configureKmp() {
        pluginManager.apply(libs.plugins.kotlin.multiplatform)
        pluginManager.apply(libs.plugins.android.kmp.library)

        kotlin {
            explicitApi()

            // Kotlin 2.4 removed AbiValidationExtension.enabled (calling abiValidation() is what
            // enables it) and the klib { } block (klib dumps are now always generated for klib-based
            // targets); keepUnsupportedTargets moved up and was renamed keepLocallyUnsupportedTargets.
            @OptIn(ExperimentalAbiValidation::class)
            abiValidation {
                keepLocallyUnsupportedTargets.set(false)
            }

            compilerOptions {
                configureCommonOptions()
            }

            android {
                compileSdk = Constants.COMPILE_SDK
                minSdk = Constants.MIN_SDK

                compilations.configureEach {
                    // compilerOptions is deprecated in favor of compileTaskProvider, but it seems like
                    // KotlinMultiplatformAndroidCompilation does not provide a proper type for
                    // compilerOptions of compileTaskProvider (yet?) so it needs to be casted explicitly
                    compileTaskProvider.configure {
                        (compilerOptions as KotlinJvmCompilerOptions).configureLibraryOptions()
                    }
                }

                optimization {
                    // Consumer keep rules are picked up by convention from the
                    // src/androidMain/keepRules/*.keep source set (AGP 9.3+)
                    consumerKeepRules.publish = true
                    minify = false
                }
            }

            val xcfName = "${project.name}Kit"
            iosArm64 {
                binaries.framework {
                    baseName = xcfName
                }
            }
            iosSimulatorArm64 {
                binaries.framework {
                    baseName = xcfName
                }
            }
        }
    }

    private fun Project.configureDetekt() {
        detektPlugin.apply(this)
        addAllKmpSourceSetsToDetekt()
    }

    private fun Project.addAllKmpSourceSetsToDetekt() {
        val allKmpSources = file("${project.projectDir.absolutePath}/src")
            .listFiles()!!
            .filter { it.isDirectory }
            .map { "src/${it.name}/kotlin" }
            .let { files(it) }

        detekt {
            source.setFrom(allKmpSources)
        }
    }
}
