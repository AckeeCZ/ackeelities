package io.github.ackeecz.ackeelities.plugin

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import org.gradle.plugin.use.PluginDependency
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.abi.AbiValidationMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

internal val Project.libs get() = the<org.gradle.accessors.dm.LibrariesForLibs>()

internal fun PluginManager.apply(plugin: Provider<PluginDependency>) {
    apply(plugin.get().pluginId)
}

internal val NamedDomainObjectContainer<KotlinSourceSet>.androidHostTest: KotlinSourceSet
    get() = getByName("androidHostTest")

internal fun Project.androidBase(action: BaseExtension.() -> Unit) {
    extensions.configure(BaseExtension::class, action)
}

internal fun Project.androidApp(action: BaseAppModuleExtension.() -> Unit) {
    extensions.configure(BaseAppModuleExtension::class, action)
}

internal fun Project.kotlin(action: KotlinMultiplatformExtension.() -> Unit) {
    extensions.configure(KotlinMultiplatformExtension::class, action)
}

internal fun KotlinMultiplatformExtension.abiValidation(action: AbiValidationMultiplatformExtension.() -> Unit) {
    extensions.configure(AbiValidationMultiplatformExtension::class, action)
}

internal fun DependencyHandlerScope.testImplementation(
    provider: Provider<MinimalExternalModuleDependency>,
    configure: ExternalModuleDependency.() -> Unit = {},
) {
    add("testImplementation", provider.get(), configure)
}

internal fun DependencyHandlerScope.testRuntimeOnly(
    provider: Provider<MinimalExternalModuleDependency>,
    configure: ExternalModuleDependency.() -> Unit = {},
) {
    add("testRuntimeOnly", provider.get(), configure)
}

internal fun DependencyHandlerScope.compileOnly(
    provider: Provider<MinimalExternalModuleDependency>,
    configure: ExternalModuleDependency.() -> Unit = {},
) {
    add("compileOnly", provider.get(), configure)
}

internal fun DependencyHandlerScope.implementation(
    provider: Provider<MinimalExternalModuleDependency>,
    configure: ExternalModuleDependency.() -> Unit = {},
) {
    add("implementation", provider.get(), configure)
}

internal fun Project.detekt(action: DetektExtension.() -> Unit) {
    extensions.configure(DetektExtension::class, action)
}

internal fun DependencyHandlerScope.detektPlugins(provider: Provider<MinimalExternalModuleDependency>) {
    add("detektPlugins", provider.get())
}
