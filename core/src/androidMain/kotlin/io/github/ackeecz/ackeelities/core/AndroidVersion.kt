package io.github.ackeecz.ackeelities.core

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.VisibleForTesting

/**
 * Provides methods to check the current Android version.
 *
 * This interface allows you to check if the current Android version is at least a specific version
 * using the [Build.VERSION_CODES] constants.
 */
public interface AndroidVersion {

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N)
    public fun isAtLeastN(): Boolean

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N_MR1)
    public fun isAtLeastNMR1(): Boolean

    /**
     * Corresponds to the major and minor version of [Build.VERSION_CODES.N_MR1] with a specification
     * to its 1 patch version -> Android 7.1.1. Returns null if the semantic version is unparseable
     * and can't reliably differentiate between 7.1 and 7.1.1. However, you can be sure that it is
     * at least 7.1.
     */
    public fun isAtLeastNMR1Patch1(): Boolean?

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
    public fun isAtLeastO(): Boolean

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O_MR1)
    public fun isAtLeastOMR1(): Boolean

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
    public fun isAtLeastP(): Boolean

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
    public fun isAtLeastQ(): Boolean

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
    public fun isAtLeastR(): Boolean

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    public fun isAtLeastS(): Boolean

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S_V2)
    public fun isAtLeastSV2(): Boolean

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
    public fun isAtLeastTiramisu(): Boolean

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public fun isAtLeastUpsideDownCake(): Boolean

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
    public fun isAtLeastVanillaIceCream(): Boolean

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.BAKLAVA)
    public fun isAtLeastBaklava(): Boolean

    public companion object : AndroidVersion {

        private val androidPatchVersionChecker = AndroidPatchVersionChecker(
            currentSdkVersion = Build.VERSION.SDK_INT,
            currentSemanticVersion = Build.VERSION.RELEASE,
        )

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N)
        override fun isAtLeastN(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
        }

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N_MR1)
        override fun isAtLeastNMR1(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
        }

        override fun isAtLeastNMR1Patch1(): Boolean? {
            return androidPatchVersionChecker.isAtLeastPatchVersion(
                majorMinorSdkVersionToCheck = Build.VERSION_CODES.N_MR1,
                expectedSemanticVersion = SemanticVersion(major = 7, minor = 1, patch = 1),
            )
        }

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
        override fun isAtLeastO(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        }

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O_MR1)
        override fun isAtLeastOMR1(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
        }

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
        override fun isAtLeastP(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
        }

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
        override fun isAtLeastQ(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        }

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
        override fun isAtLeastR(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
        }

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
        override fun isAtLeastS(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        }

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S_V2)
        override fun isAtLeastSV2(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2
        }

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
        override fun isAtLeastTiramisu(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        }

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        override fun isAtLeastUpsideDownCake(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
        }

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
        override fun isAtLeastVanillaIceCream(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM
        }

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.BAKLAVA)
        override fun isAtLeastBaklava(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA
        }
    }
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal class AndroidPatchVersionChecker(
    private val currentSdkVersion: Int,
    private val currentSemanticVersion: String,
) {

    /**
     * Returns true if [currentSdkVersion] is greater than [majorMinorSdkVersionToCheck] or if
     * they are equal and [currentSemanticVersion] is greater or equal to [expectedSemanticVersion].
     * In other cases returns false. Can also return null, if [currentSemanticVersion] is not
     * parseable, which might happen when [Build.VERSION.RELEASE] is used. In that case you can be
     * at least sure that [currentSdkVersion] is equal to [majorMinorSdkVersionToCheck].
     */
    @Suppress("SwallowedException")
    fun isAtLeastPatchVersion(
        majorMinorSdkVersionToCheck: Int,
        expectedSemanticVersion: SemanticVersion,
    ): Boolean? {
        return try {
            val isCurrentSdkVersionNewer = currentSdkVersion > majorMinorSdkVersionToCheck
            val isCurrentSdkVersionSameAndSemanticIsGreaterOrEqual = currentSdkVersion == majorMinorSdkVersionToCheck &&
                isCurrentSemanticGreaterOrEqualToExpected(currentSemanticVersion, expectedSemanticVersion)
            isCurrentSdkVersionNewer || isCurrentSdkVersionSameAndSemanticIsGreaterOrEqual
        } catch (e: FallbackToNullException) {
            null
        }
    }

    private fun isCurrentSemanticGreaterOrEqualToExpected(
        currentSemanticVersion: String,
        expectedSemanticVersion: SemanticVersion,
    ): Boolean {
        val parsedCurrentSemanticVersion = SemanticVersion.parse(currentSemanticVersion)
        return parsedCurrentSemanticVersion?.isPatchGreaterOrEqualToOtherPatch(expectedSemanticVersion)
            ?: throw FallbackToNullException()
    }

    private class FallbackToNullException : Exception()
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal data class SemanticVersion(
    val major: Int,
    val minor: Int,
    val patch: Int?,
) {

    fun isPatchGreaterOrEqualToOtherPatch(other: SemanticVersion): Boolean {
        return when {
            patch != null && other.patch != null -> patch >= other.patch
            patch != null && other.patch == null -> true
            patch == null && other.patch != null -> false
            // patch == null && other.patch == null
            else -> true
        }
    }

    companion object {

        @Suppress("SwallowedException")
        fun parse(semanticVersion: String): SemanticVersion? = try {
            val versionParts = semanticVersion.split(".")
            SemanticVersion(
                major = versionParts[0].toInt(),
                minor = versionParts[1].toInt(),
                patch = versionParts.getOrNull(2)?.toInt(),
            )
        } catch (e: Exception) {
            null
        }
    }
}
