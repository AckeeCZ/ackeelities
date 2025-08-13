package io.github.ackeecz.ackeelities.verification

import org.gradle.api.Project

internal class CheckArtifactUpdateStatusStub : CheckArtifactUpdateStatus {

    var artifactUpdateStatus: ArtifactUpdateStatus = ArtifactUpdateStatus.UPDATE_NEEDED

    var receivedTagResult: TagResult? = null
        private set

    override fun invoke(
        project: Project,
        tagResult: TagResult,
    ): ArtifactUpdateStatus {
        receivedTagResult = tagResult
        return artifactUpdateStatus
    }
}
