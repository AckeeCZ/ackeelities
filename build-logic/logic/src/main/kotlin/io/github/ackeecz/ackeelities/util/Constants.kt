package io.github.ackeecz.ackeelities.util

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

public object Constants {

    public const val COMPILE_SDK: Int = 36
    public const val MIN_SDK: Int = 23
    public const val TARGET_SDK: Int = 36

    public val JAVA_VERSION: JavaVersion = JavaVersion.VERSION_11
    public val JVM_TARGET: JvmTarget = JvmTarget.JVM_11

    public const val ACKEE_TASKS_GROUP: String = "ackee"

    public const val NAMESPACE_PREFIX: String = "io.github.ackeecz.ackeelities"
}
