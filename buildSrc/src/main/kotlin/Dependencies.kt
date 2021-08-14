@file:Suppress("Unused", "MayBeConstant", "MemberVisibilityCanBePrivate")

internal object Version {
    const val GRADLE_ANDROID = "7.0.0"
    const val GRADLE_DETEKT = "1.17.1"
    const val GRADLE_KTLINT = "10.1.0"
    const val GRADLE_JACOCO = "0.16.0"
    const val GRADLE_VERSIONS = "0.39.0"

    const val KOTLIN = "1.5.21"
    const val COROUTINES = "1.5.1"

    const val APP_COMPAT = "1.3.1"
    const val ACTIVITY = "1.3.1"
    const val LIFECYCLE = "2.3.1"
    const val LEAK_CANARY = "2.7"

    const val TEST_JUNIT = "1.1.3"
    const val TEST_STRIKT = "0.31.0"
    const val TEST_MOCKK = "1.12.0"
}

object ProjectLib {
    const val ANDROID = "com.android.tools.build:gradle:${Version.GRADLE_ANDROID}"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.KOTLIN}"
    const val DETEKT = "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${Version.GRADLE_DETEKT}"
    const val KTLINT = "org.jlleitschuh.gradle:ktlint-gradle:${Version.GRADLE_KTLINT}"
    const val JACOCO = "com.vanniktech:gradle-android-junit-jacoco-plugin:${Version.GRADLE_JACOCO}"
    const val VERSIONS = "com.github.ben-manes:gradle-versions-plugin:${Version.GRADLE_VERSIONS}"

    val all = setOf(ANDROID, KOTLIN, DETEKT, KTLINT, JACOCO, VERSIONS)
}

object ModuleLib {
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Version.KOTLIN}"
    const val COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.COROUTINES}"
    const val COROUTINES_ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Version.COROUTINES}"

    const val APP_COMPAT = "androidx.appcompat:appcompat:${Version.APP_COMPAT}"
    const val ACTIVITY = "androidx.activity:activity-ktx:${Version.ACTIVITY}"
    const val LIFECYCLE_RUNTIME = "androidx.lifecycle:lifecycle-runtime-ktx:${Version.LIFECYCLE}"
    const val LIFECYCLE_VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Version.LIFECYCLE}"
    const val LEAK_CANARY = "com.squareup.leakcanary:leakcanary-android:${Version.LEAK_CANARY}"

    val brokerCore = setOf(KOTLIN, COROUTINES_CORE)
    val brokerLifecycle = setOf(KOTLIN, COROUTINES_ANDROID, LIFECYCLE_RUNTIME)
    val sample = setOf(KOTLIN, COROUTINES_ANDROID, APP_COMPAT, ACTIVITY, LIFECYCLE_VIEWMODEL)
}

object TestLib {
    const val JUNIT = "androidx.test.ext:junit-ktx:${Version.TEST_JUNIT}"
    const val STRIKT = "io.strikt:strikt-core:${Version.TEST_STRIKT}"
    const val MOCKK = "io.mockk:mockk:${Version.TEST_MOCKK}"
    const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Version.COROUTINES}"

    val all = setOf(JUNIT, STRIKT, MOCKK, COROUTINES)
}
