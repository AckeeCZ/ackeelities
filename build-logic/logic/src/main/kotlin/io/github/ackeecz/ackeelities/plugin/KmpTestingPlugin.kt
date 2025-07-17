package io.github.ackeecz.ackeelities.plugin

import com.android.build.api.dsl.androidLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType

/**
 * Contains testing build logic common to any KMP module that contains any tests
 */
internal class KmpTestingPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.configure()
    }

    @Suppress("UnstableApiUsage")
    private fun Project.configure() {
        pluginManager.apply(libs.plugins.kotest.multiplatform)
        pluginManager.apply(libs.plugins.gradle.testLogger)

        kotlin {
            androidLibrary {
                // This nice config enables Android host (local unit) tests in KMP module 🫠
                withHostTestBuilder {}.configure {}
            }

            sourceSets.commonTest.dependencies {

                // Coroutines
                implementation(dependencies.platform(libs.coroutines.bom))
                implementation(libs.coroutines.test)

                // Kotest
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotest.framework.api)
                implementation(libs.kotest.framework.datatest)
            }

            sourceSets.androidHostTest.dependencies {
                implementation(dependencies.platform(libs.junit5.bom))
                runtimeOnly(libs.kotest.runner.junit5)
            }
        }

        // It seems like there is currently no way how enable JUnit Platform using Android KMP
        // plugin (androidLibrary {})
        tasks.withType<Test> {
            useJUnitPlatform()
        }
    }
}
