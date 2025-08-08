plugins {
    id("com.android.library") version "8.5.2" // ✅ specify AGP version
    kotlin("android") version "1.9.24"        // ✅ specify Kotlin version
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka") version "1.9.10"
}

android {
    namespace = "in.paybio.sdk"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.9.10")
}

tasks.whenTaskAdded {
    if (name == "generatePomFileForReleasePublication") {
        dependsOn("assembleRelease")
    }
}

println("Sonatype username: ${project.findProperty("ossrhUsername")}")
println("Sonatype password: ${project.findProperty("ossrhPassword")}...") // Don't log full password

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {

                artifact("$buildDir/outputs/aar/${project.name}-release.aar") {
                    builtBy(tasks.getByName("assembleRelease"))
                }

                // Sources JAR
                artifact(tasks.register("sourcesJar", Jar::class) {
                    from(android.sourceSets["main"].java.srcDirs)
                    archiveClassifier.set("sources")
                    archiveExtension.set("jar")
                })

                // Javadoc JAR
                artifact(tasks.register("javadocJar", Jar::class) {
                    dependsOn("dokkaHtml")
                    from("$buildDir/dokka/html")
                    archiveClassifier.set("javadoc")
                    archiveExtension.set("jar")
                })

                groupId = "in.paybio.sdk"
                artifactId = "paybiosdk"
                version = "1.0.0"

                pom {
                    name.set("Paybio SDK")
                    description.set("SDK for Paybio services")
                    url.set("https://github.com/akimqualitydevs/paybiosdk")

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }

                    developers {
                        developer {
                            id.set("akimqualitydevs")
                            name.set("Akim")
                            email.set("akim@qualitydevs.net")
                        }
                    }

                    scm {
                        connection.set("scm:git:https://github.com/akimqualitydevs/paybiosdk.git")
                        developerConnection.set("scm:git:ssh://github.com/akimqualitydevs/paybiosdk.git")
                        url.set("https://github.com/akimqualitydevs/paybiosdk")
                    }
                }
            }
        }

        repositories {
            maven {
                name = "Sonatype"
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = project.findProperty("ossrhUsername") as String?
                    password = project.findProperty("ossrhPassword") as String?
                }
            }
        }
    }

    signing {
        sign(publishing.publications["release"])

        val signingKeyId: String? by project
        val signingKey: String? by project
        val signingPassword: String? by project

        if (signingKeyId != null && signingKey != null) {
            useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword ?: "")
        } else {
            useGpgCmd() // Fallback to GPG agent
            println("Falling back to GPG agent signing")
        }
    }
}

afterEvaluate {
    println("Publications: ${publishing.publications.names}")
}