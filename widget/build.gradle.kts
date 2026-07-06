import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.ktlint)
}

ktlint {
  reporters {
    reporter(ReporterType.PLAIN)
  }
  additionalEditorconfig.set(
    mapOf(
      "indent_size" to "2",
    ),
  )
}

android {
  namespace = "com.thindie.avezer.widget"
  compileSdk = 36

  defaultConfig {
    minSdk = 26
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }

  buildFeatures {
    compose = true
  }
}

kotlin {
  compilerOptions {
    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.material3)

  implementation(libs.androidx.glance)
  implementation(libs.androidx.glance.appwidget)
  implementation(libs.androidx.glance.material3)

  implementation(libs.androidx.work.runtime.ktx)
}
