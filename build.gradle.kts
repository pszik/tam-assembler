plugins {
    id("java")
    antlr
}

group = "uk.ac.nott.cs.comp2013"
version = "1.2.0"

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.13.2")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.generateGrammarSource {
    maxHeapSize = "64m"
    arguments = arguments + listOf("-visitor", "-package", "uk.ac.nott.cs.comp3012.tasm")
}

val fatJar = tasks.register<Jar>("fatJar", Jar::class.java) {
    manifest {
        attributes["Implementation-Title"] = "Jar with Dependencies"
        attributes["Implementation-Version"] = version
        attributes["Main-Class"] = "uk.ac.nott.cs.comp3012.tasm.Assembler"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}

tasks.build {
    dependsOn(fatJar)
}