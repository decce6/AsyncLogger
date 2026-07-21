plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.21.11-fabric"

stonecutter parameters {
    var str = node.metadata.project.substringAfterLast('-');
    if (str == "forge" && stonecutter.eval(node.metadata.version, "<=1.12.2")) {
        str = "legacyforge"
    }
    constants.match(str, "fabric", "neoforge", "forge", "legacyforge")
    swaps["mod_version_short"] = "\"" + property("mod_version") + "\""
}

tasks.register("publishAll") {
    group = "publishing"
    dependsOn(stonecutter.tasks.named("publishMods"))
}

tasks.register("publishAllModrinth") {
    group = "publishing"
    dependsOn(stonecutter.tasks.named("publishModrinth"))
}

tasks.register("publishAllCurseForge") {
    group = "publishing"
    dependsOn(stonecutter.tasks.named("publishCurseforge"))
}

stonecutter.tasks {
    order("publishMods")
    order("publishModrinth")
    order("publishCurseforge")
}

