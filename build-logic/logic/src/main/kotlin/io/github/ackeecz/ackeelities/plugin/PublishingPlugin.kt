package io.github.ackeecz.ackeelities.plugin

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import io.github.ackeecz.ackeelities.properties.LibraryProperties
import io.github.ackeecz.ackeelities.verification.task.CheckIfUpdateNeededSinceCurrentTagTask
import io.github.ackeecz.ackeelities.verification.task.VerifyPublishingTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal class PublishingPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.configure()
    }

    private fun Project.configure() {
        // com.vanniktech.maven.publish plugin can detect and use applied Dokka plugin automatically
        pluginManager.apply(libs.plugins.dokka)
        pluginManager.apply(libs.plugins.mavenPublish)

        val libraryProperties = LibraryProperties(project)
        val artifactProperties = libraryProperties.getArtifactProperties()

        group = libraryProperties.groupId
        version = artifactProperties.version

        mavenPublishing {

            coordinates(artifactId = artifactProperties.id)

            pom {
                name.set(artifactProperties.pomName)
                description.set(artifactProperties.pomDescription)
                inceptionYear.set(artifactProperties.pomYear)
                url.set(libraryProperties.pomUrl)
                licenses {
                    license {
                        name.set(libraryProperties.pomLicenceName)
                        url.set(libraryProperties.pomLicenceUrl)
                        distribution.set(libraryProperties.pomLicenceUrl)
                    }
                }
                developers {
                    developer {
                        id.set(libraryProperties.pomDeveloperId)
                        name.set(libraryProperties.pomDeveloperName)
                        url.set(libraryProperties.pomDeveloperEmail)
                    }
                }
                scm {
                    url.set(libraryProperties.pomScmUrl)
                    connection.set(libraryProperties.pomScmConnection)
                    developerConnection.set(libraryProperties.pomScmDeveloperConnection)
                }
            }

            signAllPublications()
            publishToMavenCentral()
        }

        CheckIfUpdateNeededSinceCurrentTagTask.registerFor(project)
        VerifyPublishingTask.registerFor(project)
    }
}

private fun Project.mavenPublishing(action: MavenPublishBaseExtension.() -> Unit) {
    extensions.configure(MavenPublishBaseExtension::class, action)
}
