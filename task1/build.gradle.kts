plugins {
    application
}

dependencies {
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
