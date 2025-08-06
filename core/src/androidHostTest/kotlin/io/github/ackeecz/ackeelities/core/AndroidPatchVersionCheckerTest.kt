@file:Suppress("MaximumLineLength", "MaxLineLength")

package io.github.ackeecz.ackeelities.core

import android.annotation.SuppressLint
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@SuppressLint("VisibleForTests")
class AndroidPatchVersionCheckerTest : FunSpec({

    fun createSut(
        currentSdkVersion: Int,
        currentSemanticVersion: String,
    ): AndroidPatchVersionChecker {
        return AndroidPatchVersionChecker(
            currentSdkVersion = currentSdkVersion,
            currentSemanticVersion = currentSemanticVersion,
        )
    }

    context("check if at least patch version") {
        val semanticVersion711 = SemanticVersion(major = 7, minor = 1, patch = 1)

        test("return true if current SDK version is greater than major.minor SDK version to check") {
            createSut(
                currentSdkVersion = 26,
                currentSemanticVersion = "8.0",
            ).isAtLeastPatchVersion(
                majorMinorSdkVersionToCheck = 25,
                expectedSemanticVersion = semanticVersion711,
            ) shouldBe true
        }

        test("return true if current SDK version equals major.minor SDK version to check and current semantic version is greater than expected semantic version") {
            val majorMinorSdkVersionToCheck = 25

            createSut(
                currentSdkVersion = majorMinorSdkVersionToCheck,
                currentSemanticVersion = "7.1.2",
            ).isAtLeastPatchVersion(
                majorMinorSdkVersionToCheck = majorMinorSdkVersionToCheck,
                expectedSemanticVersion = semanticVersion711,
            ) shouldBe true
        }

        test("return true if current SDK version equals major.minor SDK version to check and current semantic version equals expected semantic version") {
            val majorMinorSdkVersionToCheck = 25
            val currentSemanticVersion = "7.1.1"

            createSut(
                currentSdkVersion = majorMinorSdkVersionToCheck,
                currentSemanticVersion = currentSemanticVersion,
            ).isAtLeastPatchVersion(
                majorMinorSdkVersionToCheck = majorMinorSdkVersionToCheck,
                expectedSemanticVersion = requireNotNull(SemanticVersion.parse(currentSemanticVersion)),
            ) shouldBe true
        }

        test("return false if current SDK version equals major.minor SDK version to check and current semantic version is smaller expected semantic version") {
            val majorMinorSdkVersionToCheck = 25

            createSut(
                currentSdkVersion = majorMinorSdkVersionToCheck,
                currentSemanticVersion = "7.1.0",
            ).isAtLeastPatchVersion(
                majorMinorSdkVersionToCheck = majorMinorSdkVersionToCheck,
                expectedSemanticVersion = semanticVersion711,
            ) shouldBe false
        }

        test("return false if current SDK version equals major.minor SDK version to check and current semantic version is missing patch part") {
            val majorMinorSdkVersionToCheck = 25

            createSut(
                currentSdkVersion = majorMinorSdkVersionToCheck,
                currentSemanticVersion = "7.1",
            ).isAtLeastPatchVersion(
                majorMinorSdkVersionToCheck = majorMinorSdkVersionToCheck,
                expectedSemanticVersion = semanticVersion711,
            ) shouldBe false
        }

        test("return false if current SDK version is smaller than major.minor SDK version to check") {
            createSut(
                currentSdkVersion = 24,
                currentSemanticVersion = "7.0",
            ).isAtLeastPatchVersion(
                majorMinorSdkVersionToCheck = 25,
                expectedSemanticVersion = semanticVersion711,
            ) shouldBe false
        }

        test("return null as fallback if current semantic version is unparseable") {
            val majorMinorSdkVersionToCheck = 25

            createSut(
                currentSdkVersion = majorMinorSdkVersionToCheck,
                currentSemanticVersion = "unparseable version",
            ).isAtLeastPatchVersion(
                majorMinorSdkVersionToCheck = majorMinorSdkVersionToCheck,
                expectedSemanticVersion = semanticVersion711,
            ) shouldBe null
        }
    }
})
