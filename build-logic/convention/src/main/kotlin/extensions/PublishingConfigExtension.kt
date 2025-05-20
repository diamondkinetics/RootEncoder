package extensions

open class PublishingConfigExtension(
    var groupId: String = "",
    var artifactId: String = "",
    // CodeArtifact specific
    var domain: String = "",
    var domainOwner: String = "",
    var repository: String = "",
    var region: String = "us-east-1",
) {
    fun groupId(value: String) {
        groupId = value
    }
    fun artifactId(value: String) {
        artifactId = value
    }
    fun domain(value: String) {
        domain = value
    }
    fun domainOwner(value: String) {
        domainOwner = value
    }
    fun repository(value: String) {
        repository = value
    }
    fun region(value: String) {
        region = value
    }
}
