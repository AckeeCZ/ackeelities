package io.github.ackeecz.ackeelities.plugin

import io.github.ackeecz.ackeelities.util.Constants
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions

internal fun KotlinJvmCompilerOptions.configureApplicationOptions() {
    configureCommonOptions()
    configureLibraryOptions()
}

internal fun KotlinJvmCompilerOptions.configureLibraryOptions() {
    jvmTarget.set(Constants.JVM_TARGET)
}

internal fun KotlinCommonCompilerOptions.configureCommonOptions() {
    allWarningsAsErrors.set(true)
}
