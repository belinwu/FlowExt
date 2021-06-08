plugins {
    kotlin("multiplatform") version "1.5.10"
    jacoco
    id("com.diffplug.spotless") version "5.12.5"
}

group = "com.hoc081098"
version = "1.0"

repositories {
    google()
    mavenCentral()
}

jacoco {
    toolVersion = "0.8.6"
}

tasks.withType<JacocoReport> {
    reports {
        xml.isEnabled = true
    }
    dependsOn(tasks.withType<Test>())
}

val kotlinCoroutinesVersion = "1.5.0"

kotlin {
    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
            finalizedBy(tasks.withType<JacocoReport>())
        }
    }
    js(BOTH) {
        compilations.all {
            kotlinOptions.moduleKind = "commonjs"
        }
        browser()
        nodejs()
    }

    iosArm64()
    iosArm32()
    iosX64()

    macosX64()
    mingwX64()
    linuxX64()

    tvosX64()
    tvosArm64()

    watchosArm32()
    watchosArm64()
    watchosX64()
    watchosX86()


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("app.cash.turbine:turbine:0.5.2")
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependsOn(commonTest)

            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

        val nativeMain by creating {
            dependsOn(commonMain)
        }
        val nativeTest by creating {
            dependsOn(commonTest)
        }

        val appleTargets = listOf(
            "iosX64",
            "iosArm64",
            "iosArm32",
            "macosX64",
            "tvosArm64",
            "tvosX64",
            "watchosArm32",
            "watchosArm64",
            "watchosX86",
            "watchosX64",
        )

        (appleTargets + listOf("mingwX64", "linuxX64")).forEach {
            getByName("${it}Main") {
                dependsOn(nativeMain)
            }
            getByName("${it}Test") {
                dependsOn(nativeTest)
            }
        }
    }
}

spotless {
    kotlin {
        target("**/*.kt")

        ktlint("0.37.2").userData(
            mapOf(
                // TODO this should all come from editorconfig https://github.com/diffplug/spotless/issues/142
                "indent_size" to "2",
                "kotlin_imports_layout" to "ascii",
            )
        )
    }
}