# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Coroutines
-dontwarn kotlinx.coroutines.**

# Serialization
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable *;
}

# Jetpack Compose
-keep class androidx.compose.** { *; }
-keep class androidx.activity.ComponentActivity
-keep class androidx.lifecycle.** { *; }

# Koin Core & General
-keep class org.koin.** { *; }
-keep class org.koin.core.registry.** { *; }
-keep class org.koin.core.scope.** { *; }
-keep class org.koin.androidx.viewmodel.scope.** { *; }
-keep class org.koin.androidx.scope.** { *; }

# If using Koin KSP generated code (highly recommended for modern Koin)
-keep class org.koin.ksp.generated.** { *; }

# Remove all calls to System.out.println (which Kotlin's println usually maps to)
-assumenosideeffects class java.io.PrintStream {
    public void println(...);
    public void print(...);
}
# Remove Logs (more concise)
-assumenosideeffects class android.util.Log {
    public static *** *(...);
}
