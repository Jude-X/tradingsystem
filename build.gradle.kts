plugins {
	java
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "com.tradingsystem"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.hibernate.validator:hibernate-validator:6.2.0.Final")
	implementation("javax.el:javax.el-api:3.0.0")
	implementation("org.glassfish.web:javax.el:2.2.6")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("com.google.guava:guava:31.1-jre")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
