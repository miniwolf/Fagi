plugins {
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

application {
    mainClass.set("com.fagi.main.FagiApp")
}

repositories {
    mavenCentral()
    flatDir {
        dirs("libs")
    }
}

tasks.test {
    useJUnitPlatform()
}

javafx {
    version = "21"
    modules = mutableListOf("javafx.controls", "javafx.fxml", "javafx.web", "javafx.graphics")
}

dependencies {
    implementation(project(":shared"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("org.testfx:testfx-core:4.0.18")
    testImplementation("org.testfx:testfx-junit5:4.0.18")
    testImplementation("org.openjfx:javafx-swing:21.0.3")
    testImplementation("org.testfx:openjfx-monocle:21.0.2")
}
