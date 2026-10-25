# Add project specific ProGuard rules here.
# Keep Gemini DTOs and Gson mappings
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.astra.voiceassistant.api.** { *; }
-keep class com.google.gson.** { *; }

# Retrofit
-keepattributes EnclosingMethod
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Android Material & Lottie
-keep class com.airbnb.lottie.** { *; }