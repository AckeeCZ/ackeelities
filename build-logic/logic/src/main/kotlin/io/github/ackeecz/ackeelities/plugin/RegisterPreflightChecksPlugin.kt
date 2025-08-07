package io.github.ackeecz.ackeelities.plugin

import io.github.ackeecz.ackeelities.util.Constants
import io.github.ackeecz.ackeelities.util.ExecuteCommand
import io.github.ackeecz.ackeelities.verification.task.VerifyBomVersionTask
import io.github.ackeecz.ackeelities.verification.task.VerifyPublishingTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.process.ExecOperations
import javax.inject.Inject

internal abstract class RegisterPreflightChecksPlugin : Plugin<Project> {

    @get:Inject
    abstract val execOperations: ExecOperations

    override fun apply(target: Project) {
        RegisterPreMergeRequestCheck(target).invoke()
        RegisterPrePublishCheck(target).invoke()
    }

    private fun Task.dependsOnTaskFromProjects(
        taskName: String,
        projects: Collection<Project>,
        failIfNotFoundInAnyProject: Boolean = true,
    ) {
        projects
            .flatMap { subProject ->
                subProject.tasks.filter { it.name == taskName }
            }
            .apply {
                if (isEmpty() && failIfNotFoundInAnyProject) {
                    throw GradleException("Task $taskName not found in any project")
                }
            }
            .forEach { dependsOn(it) }
    }

    private inner class RegisterPreMergeRequestCheck(private val currentProject: Project) {

        operator fun invoke() {
            // Changes to this task must be synchronized with the basic-preflight-check/action.yml action
            // to run the same checks on the CI as well
            currentProject.tasks.register(PRE_MERGE_REQUEST_CHECK_TASK_NAME) {
                group = Constants.ACKEE_TASKS_GROUP
                description = "Performs basic verifications before making a MR like running Detekt, tests, etc."

                dependsOnDetekt()
                dependsOnAssemble()
                dependsOnLibraryTests()
                dependsOnBinaryCompatibilityCheck()
                dependsOnBuildLogicTests()
            }
        }

        private fun Task.dependsOnDetekt() {
            dependsOnTaskFromProjects(
                taskName = "detekt",
                projects = currentProject.subprojects,
            )
        }

        private fun Task.dependsOnAssemble() {
            // KMP Android
            dependsOnTaskFromProjects(
                taskName = "assembleAndroidMain",
                projects = currentProject.subprojects,
            )
            // KMP iOS
            dependsOnTaskFromProjects(
                taskName = "compileKotlinIosSimulatorArm64",
                projects = currentProject.subprojects,
            )
            // Android library modules
            dependsOnTaskFromProjects(
                taskName = "assembleRelease",
                projects = currentProject.subprojects.filterNot { it.name == SAMPLE_APP_NAME },
                // There are currently no Android library modules in the project, so we don't want
                // to fail the check because of this, but we want to be sure that it will run if
                // there are any in the future.
                failIfNotFoundInAnyProject = false,
            )
        }

        private fun Task.dependsOnLibraryTests() {
            // KMP modules' Android host tests
            dependsOnTaskFromProjects(
                taskName = "testAndroidHostTest",
                projects = currentProject.subprojects,
            )
            // KMP modules' iOS host tests
            // TODO Currently fails on internal Kotlin error. Seems like there is some incompatibility
            //  between current Kotlin and Kotest versions.
//            dependsOnTaskFromProjects(
//                taskName = "iosSimulatorArm64Test",
//                projects = currentProject.subprojects,
//            )
            // Android library modules' unit tests
            dependsOnTaskFromProjects(
                taskName = "testReleaseUnitTest",
                projects = currentProject.subprojects.filterNot { it.name == SAMPLE_APP_NAME },
                // There are currently no Android library modules in the project, so we don't want
                // to fail the check because of this, but we want to be sure that it will run if
                // there are any in the future.
                failIfNotFoundInAnyProject = false,
            )
        }

        private fun Task.dependsOnBinaryCompatibilityCheck() {
            dependsOnTaskFromProjects(
                taskName = "checkLegacyAbi",
                projects = currentProject.subprojects,
            )
        }

        private fun Task.dependsOnBuildLogicTests() {
            dependsOn(
                currentProject.gradle
                    .includedBuild(BUILD_LOGIC_ROOT_PROJECT_NAME)
                    .task(":$BUILD_LOGIC_SUBPROJECT_NAME:test")
            )
        }
    }

    private inner class RegisterPrePublishCheck(private val currentProject: Project) {

        operator fun invoke() {
            // Changes to this task must be synchronized with the deploy.yml workflow
            // to run the same checks on the CI as well
            currentProject.tasks.register(PRE_PUBLISH_CHECK_TASK_NAME) {
                group = Constants.ACKEE_TASKS_GROUP
                description = "Performs all necessary verifications before publishing new artifacts versions"

                dependsOn(PRE_MERGE_REQUEST_CHECK_TASK_NAME)
                dependsOnVerifyPublishing()
                dependsOnVerifyBomVersion()
                executeArtifactsTests()
            }
        }

        private fun Task.dependsOnVerifyPublishing() {
            dependsOnTaskFromProjects(
                taskName = VerifyPublishingTask.NAME,
                projects = currentProject.subprojects,
            )
        }

        private fun Task.dependsOnVerifyBomVersion() {
            dependsOnTaskFromProjects(
                taskName = VerifyBomVersionTask.NAME,
                projects = currentProject.subprojects,
            )
        }

        private fun Task.executeArtifactsTests() {
            doLast {
                // We need to publish the latest versions to Maven local first before we can run tests
                // on published artifacts
                project.executeGradleTask(taskName = "publishToMavenLocal")
                project.executeGradleTask(taskName = ":$SAMPLE_APP_NAME:testDebugUnitTest")
            }
        }

        private fun Project.executeGradleTask(taskName: String) {
            val executeCommand = ExecuteCommand(execOperations)
            val rootProjectPath = project.rootProject.projectDir.absolutePath
            when (val result = executeCommand("$rootProjectPath/gradlew $taskName")) {
                is ExecuteCommand.Result.Success -> println(result.commandOutput)
                is ExecuteCommand.Result.Error -> throw GradleException(result.commandOutput)
            }
        }
    }

    companion object {

        private const val PRE_MERGE_REQUEST_CHECK_TASK_NAME = "preMergeRequestCheck"
        private const val PRE_PUBLISH_CHECK_TASK_NAME = "prePublishCheck"

        private const val SAMPLE_APP_NAME = "app"
        private const val BUILD_LOGIC_ROOT_PROJECT_NAME = "build-logic"
        private const val BUILD_LOGIC_SUBPROJECT_NAME = "logic"
    }
}
