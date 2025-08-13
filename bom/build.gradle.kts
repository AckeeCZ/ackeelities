import io.github.ackeecz.ackeelities.verification.task.VerifyBomVersionTask
import org.gradle.api.internal.catalog.DelegatingProjectDependency

plugins {
    `java-platform`
    alias(libs.plugins.ackeecz.ackeelities.publishing)
}

dependencies {
    constraints {
        api(projects.core)
        api(projects.coroutines)
    }
}

private fun DependencyConstraintHandlerScope.api(dependency: DelegatingProjectDependency) {
    add("api", dependency)
}

VerifyBomVersionTask.registerFor(project)
