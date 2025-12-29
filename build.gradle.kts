plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    
    implementation("net.minestom:minestom:2025.12.19-1.21.10")
    
    // If you want to use the integration testing library.
    testImplementation("net.minestom:testing:2025.12.19-1.21.10")
}

tasks.test {
    useJUnitPlatform()
}