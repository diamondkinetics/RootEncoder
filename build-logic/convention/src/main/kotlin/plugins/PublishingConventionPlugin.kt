package plugins

import extensions.PublishingConfigExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.codeartifact.CodeartifactClient
import software.amazon.awssdk.services.codeartifact.model.GetAuthorizationTokenRequest

@Suppress("unused")
class PublishingConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("maven-publish")
            }

            val extension = extensions.create("publishingConfig", PublishingConfigExtension::class.java)

            afterEvaluate {
                configurePublishing(extension)
            }
        }
    }

    private fun Project.configurePublishing(extension: PublishingConfigExtension) {
        extensions.configure<PublishingExtension> {
            publications {
                // Creates a Maven publication called "release".
                create<MavenPublication>("release") {
                    // Applies the component for the release build variant.
                    from(components["release"])

                    // You can then customize attributes of the publication as shown below.
                    groupId = extension.groupId
                    artifactId = extension.artifactId
                }
            }
            repositories {
                // CodeArtifact repository
                maven {
                    name = "CodeArtifact"
                    url = project.uri(getCodeArtifactRepoUrl(extension))
                    credentials {
                        username = "aws"
                        password = getCodeArtifactAuthToken(extension)
                    }
                }
            }
        }
    }

    @Suppress("ktlint:standard:max-line-length")
    private fun getCodeArtifactRepoUrl(extension: PublishingConfigExtension): String {
        return "https://${extension.domain}-${extension.domainOwner}.d.codeartifact.${extension.region}.amazonaws.com/maven/${extension.repository}/"
    }

    private fun getCodeArtifactAuthToken(extension: PublishingConfigExtension): String {
        val client = CodeartifactClient.builder()
            .region(Region.of(extension.region))
            .build()

        val request = GetAuthorizationTokenRequest.builder()
            .domain(extension.domain)
            .domainOwner(extension.domainOwner)
            .build()

        return client.getAuthorizationToken(request).authorizationToken()
    }
}
