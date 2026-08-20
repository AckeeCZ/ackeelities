import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

kotlin {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs.addAll(
                "-Xexplicit-api=strict",
            )
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    compileOnly(files(libs::class.java.superclass.protectionDomain.codeSource.location))
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.gradleVersions.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.mavenPublish.gradlePlugin)

    testImplementation(platform(libs.junit5.bom))
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.framework.engine)
    testRuntimeOnly(libs.kotest.runner.junit6)
}

gradlePlugin {
    plugins {
        plugin(
            dependency = libs.plugins.ackeecz.ackeelities.android.application,
            pluginClassSimpleName = "AndroidApplicationPlugin",
        )

        plugin(
            dependency = libs.plugins.ackeecz.ackeelities.dependency.updates,
            pluginClassSimpleName = "DependencyUpdatesPlugin",
        )

        plugin(
            dependency = libs.plugins.ackeecz.ackeelities.kmp.library,
            pluginClassSimpleName = "KmpLibraryPlugin",
        )

        plugin(
            dependency = libs.plugins.ackeecz.ackeelities.kmp.testing,
            pluginClassSimpleName = "KmpTestingPlugin",
        )

        plugin(
            dependency = libs.plugins.ackeecz.ackeelities.publishing,
            pluginClassSimpleName = "PublishingPlugin",
        )

        plugin(
            dependency = libs.plugins.ackeecz.ackeelities.preflightchecks,
            pluginClassSimpleName = "RegisterPreflightChecksPlugin",
        )
    }
}

private fun NamedDomainObjectContainer<PluginDeclaration>.plugin(
    dependency: Provider<out PluginDependency>,
    pluginClassSimpleName: String,
) {
    val pluginId = dependency.get().pluginId
    register(pluginId) {
        id = pluginId
        implementationClass = "io.github.ackeecz.ackeelities.plugin.$pluginClassSimpleName"
    }
}
