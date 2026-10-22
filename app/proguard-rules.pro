# KELMA 216 — R8 / ProGuard rules.
#
# R8 full mode can over-optimise the Kotlin stdlib and crash at runtime with
# "IllegalAccessError: Class kotlin.sequences.d extended by class ... is inaccessible".
# These keeps are the standard remedy and are required by DataStore + org.json reflection.
-dontwarn kotlin.sequences.**
-keep class kotlin.sequences.** { *; }
-keep class kotlin.reflect.** { *; }

# Keep DataStore / coroutines internals that are reached reflectively.
-keep class androidx.datastore.** { *; }
-keepclassmembers class * extends androidx.datastore.preferences.core.** { *; }

# Keep Compose runtime metadata used by tooling and recomposition.
-keep class androidx.compose.runtime.** { *; }
