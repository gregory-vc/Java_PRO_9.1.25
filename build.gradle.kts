import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.JavaExec

allprojects {
    group = "org.example"
    version = "0.1.0"
}

subprojects {
    plugins.withId("java") {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(25))
            }
        }
        tasks.withType<JavaCompile>().configureEach {
            options.release.set(25)
        }

        tasks.withType<JavaExec>().configureEach {
        }

        tasks.withType<Test>().configureEach {
            useJUnitPlatform()
            testLogging {
                events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED)
            }
        }
    }
}
