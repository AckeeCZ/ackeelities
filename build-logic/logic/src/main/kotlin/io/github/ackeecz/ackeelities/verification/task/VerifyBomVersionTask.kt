package io.github.ackeecz.ackeelities.verification.task

import io.github.ackeecz.ackeelities.util.Constants
import io.github.ackeecz.ackeelities.util.getTaskName
import io.github.ackeecz.ackeelities.verification.VerifyBomVersion
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Task for BOM version verification. For more information see [VerifyBomVersion].
 */
public abstract class VerifyBomVersionTask : DefaultTask() {

    @get:Inject
    public abstract val execOperations: ExecOperations

    private val verifyBomVersion by lazy { VerifyBomVersion(execOperations) }

    @TaskAction
    public fun verify() {
        when (val result = verifyBomVersion(project)) {
            VerifyBomVersion.Result.Success -> Unit
            is VerifyBomVersion.Result.Error -> {
                logger.error(result.message)
                throw GradleException(result.message)
            }
        }
    }

    public companion object {

        public val NAME: String = VerifyBomVersionTask::class.java.getTaskName()

        public fun registerFor(project: Project) {
            project.tasks.register(NAME, VerifyBomVersionTask::class.java) {
                group = Constants.ACKEE_TASKS_GROUP
                description = "Verifies that current BOM version matches the current tag version"
            }
        }
    }
}
