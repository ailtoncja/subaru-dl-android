// O AGP 9 já traz o suporte a Kotlin embutido, então não há plugin kotlin-android.
plugins {
    alias(libs.plugins.android.application) apply false
}
