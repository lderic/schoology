plugins {
    kotlin("jvm") version "1.6.10"
    java
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-client-core:1.6.7")
    implementation("io.ktor:ktor-client-cio:1.6.7")
    implementation("io.ktor:ktor-client-serialization:1.6.7")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.3.2")

    val miraiVersion = "2.9.1"
    api("net.mamoe", "mirai-core-api", miraiVersion)     // 编译代码使用
    runtimeOnly("net.mamoe", "mirai-core", miraiVersion) // 运行时使用
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}