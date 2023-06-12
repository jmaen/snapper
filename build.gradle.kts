plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.junit.jupiter:junit-jupiter:5.9.3")
    implementation("org.seleniumhq.selenium:selenium-java:4.8.1")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
