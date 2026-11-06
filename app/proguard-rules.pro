# Proguard rules for Nova Stream AI
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* *;
}
-keepclassmembers class com.novastream.ai.data.model.** { *; }
