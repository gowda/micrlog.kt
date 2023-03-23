import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
  id("org.springframework.boot") version "2.7.9"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  id("org.liquibase.gradle") version "2.2.0"
  kotlin("jvm") version "1.6.21"
  kotlin("plugin.spring") version "1.6.21"
  kotlin("plugin.jpa") version "1.6.21"
}

group = "org.kanur"
version = "1"
java.sourceCompatibility = JavaVersion.VERSION_11

extra["springCloudVersion"] = "2021.0.1"

repositories {
  mavenCentral()
  maven {
    url = uri("https://packages.confluent.io/maven/")
  }
}

dependencies {
  api("org.springframework.boot:spring-boot-starter-data-jpa")
  api("org.springframework.boot:spring-boot-starter-data-rest")
  api("org.springframework.boot:spring-boot-starter-web")
  api("org.springframework.boot:spring-boot-starter-web-services")
  api("org.springframework.boot:spring-boot-starter-validation")
  api("org.springframework.boot:spring-boot-configuration-processor")
  api("com.fasterxml.jackson.module:jackson-module-kotlin")
  api("org.jetbrains.kotlin:kotlin-reflect")
  api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  api("com.vladmihalcea:hibernate-types-55:2.14.0")

  runtimeOnly("mysql:mysql-connector-java:8.0.26")

  liquibaseRuntime("org.liquibase:liquibase-core")
  liquibaseRuntime("info.picocli:picocli:4.7.1")
  liquibaseRuntime("mysql:mysql-connector-java:8.0.26")

  testImplementation("org.liquibase:liquibase-core")
  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude("org.junit.vintage:junit-vintage-engine")
  }
}

dependencyManagement {
  imports {
    mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "11"
  }
}

tasks.register("bootJarPath") {
  dependsOn(configurations.compileClasspath)
  doFirst {
    println((tasks.getByPath("bootJar") as BootJar).archiveFile.get().asFile)
  }
}

tasks.withType<Test> {
  useJUnitPlatform()

  if (System.getenv("CI") != "true" && File(".env.test").exists()) {
    File(".env.test").useLines { it.toList() }.forEach { line ->
      if (line.trim().isEmpty()) {
        return@forEach
      }

      val (key, value) = line.split("=")
      environment(key, value)
    }
  }
}

liquibase {
  activities.register("main") {
    val dbConfig = listOf("DB_USERNAME", "DB_PASSWORD", "DB_HOST", "DB_PORT", "DB_NAME").associateWith { key ->
      System.getenv(key)
    }.toMutableMap()

    if (File(".env").exists()) {
      File(".env").useLines { it.toList() }.forEach { line ->
        if (line.trim().isEmpty()) {
          return@forEach
        }

        val (key, value) = line.split("=")
        if (key.startsWith("DB_")) {
          dbConfig[key] = value
        }
      }
    }

    this.arguments = mapOf(
      "logLevel" to "info",
      "classpath" to "src/main/resources",
      "changelogFile" to "/db/changelog.xml",
      "url" to "jdbc:mysql://${dbConfig["DB_HOST"]}:${dbConfig["DB_PORT"]}/${dbConfig["DB_NAME"]}?characterEncoding=UTF-8&serverTimezone=UTC",
      "username" to dbConfig["DB_USERNAME"],
      "password" to dbConfig["DB_PASSWORD"],
    )
  }
  runList = "main"
}

tasks.withType<BootRun> {
  if (System.getenv("CI") != "true" && File(".env").exists()) {
    File(".env").useLines { it.toList() }.forEach { line ->
      if (line.trim().isEmpty()) {
        return@forEach
      }

      val (key, value) = line.split("=")
      environment(key, value)
    }
  }
}
