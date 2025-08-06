package io.github.ackeecz.ackeelities.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal class DetektPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.configure()
    }

    private fun Project.configure() {
        pluginManager.apply(libs.plugins.detekt)

        detekt {
            buildUponDefaultConfig = true
            config.setFrom(
                files("$rootDir/detekt-config.yml")
            )
            ignoreFailures = false
        }

        dependencies {
            detektPlugins(libs.detekt.formatting)
        }
    }
}
