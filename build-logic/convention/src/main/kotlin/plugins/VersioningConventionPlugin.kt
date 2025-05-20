package plugins

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import extensions.VersioningExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import pl.allegro.tech.build.axion.release.ReleasePlugin
import pl.allegro.tech.build.axion.release.domain.VersionConfig

@Suppress("unused")
class VersioningConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            if (this != rootProject) {
                throw GradleException(
                    "Versioning plugin can be applied to the root project only",
                )
            }

            with(pluginManager) {
                apply(ReleasePlugin::class.java)
            }

            val extension = extensions.create("versioning", VersioningExtension::class.java)

            afterEvaluate {
                configureVersioning(extension)
            }
        }
    }

    private fun Project.configureVersioning(extension: VersioningExtension) {
        val scmConfig = extensions.getByType(VersionConfig::class.java).apply {
            useHighestVersion.set(extension.useHighestVersion)
            versionCreator("versionWithBranch")            
            tag {
                prefix.set(extension.tagPrefix)
                versionSeparator.set("")
                initialVersion.set({ _, _ -> extension.initialVersion })
            }
        }
        allprojects {
            version = scmConfig.version
            setupAndroidVersioning(scmConfig)
        }
    }

    private fun Project.setupAndroidVersioning(scmConfig: VersionConfig) {
        val configureVersion: BaseExtension.(String) -> Unit = { version ->
            val minor = version.split(".")[0].toInt()
            val major = version.split(".")[1].toInt()
            val patch = version.split(".")[2].toInt()
            defaultConfig.versionCode = minor * MINOR_MULTIPLIER + major * MAJOR_MULTIPLIER + patch
            defaultConfig.versionName = "$minor.$major.$patch"
        }
        pluginManager.withPlugin("com.android.library") {
            extensions.getByType(LibraryExtension::class.java).apply {
                configureVersion(scmConfig.undecoratedVersion)
            }
        }
        pluginManager.withPlugin("com.android.application") {
            extensions.getByType(AppExtension::class.java).apply {
                configureVersion(scmConfig.undecoratedVersion)
            }
        }
    }

    companion object {
        private const val MINOR_MULTIPLIER = 1_000_000
        private const val MAJOR_MULTIPLIER = 1_000
    }
}
