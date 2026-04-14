# Xposed Module Rules
-adaptresourcefilecontents META-INF/xposed/java_init.list
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# Keep libxposed classes
-keep class io.github.libxposed.** { *; }
-keep class de.robv.android.xposed.** { *; }

# Keep XposedModule
-keep,allowobfuscation,allowoptimization public class * extends io.github.libxposed.api.XposedModule {
    public <init>(...);
    public void onPackageLoaded(...);
    public void onPackageReady(...);
    public void onModuleLoaded(...);
}
-keep,allowoptimization,allowobfuscation @io.github.libxposed.api.annotations.* class * {
    @io.github.libxposed.api.annotations.BeforeInvocation <methods>;
    @io.github.libxposed.api.annotations.AfterInvocation <methods>;
}

# Keep Android annotations
-keep class androidx.annotation.** { *; }

# Keep Log for debugging
-keep class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# Kotlin
-keep class kotlin.jvm.internal.** { *; }
