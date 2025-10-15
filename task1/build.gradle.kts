plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-csv:1.11.0")

    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    // Для unnamed class с instance main без пакета
    mainClass.set("App")
}

tasks.test {
    useJUnitPlatform()
}
