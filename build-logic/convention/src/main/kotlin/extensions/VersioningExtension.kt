package extensions

open class VersioningExtension(
    var tagPrefix: String = "",
    var useHighestVersion: Boolean = true,
    var initialVersion: String = "0.1.0",
) {

    fun tagPrefix(value: String) {
        tagPrefix = value
    }

    fun useHighestVersion(value: Boolean) {
        useHighestVersion = value
    }

    fun initialVersion(value: String) {
        initialVersion = value
    }
}
