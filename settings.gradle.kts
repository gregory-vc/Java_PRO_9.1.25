pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}


rootProject.name = "java-monorepo"

// Модули (задачи) добавляются через include("taskName")
include("task1")

