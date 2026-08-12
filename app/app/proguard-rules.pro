# Comprehensive ProGuard / R8 rules for HemoScan

# --- TensorFlow Lite & Support Library ---
-keep class org.tensorflow.lite.** { *; }
-keep interface org.tensorflow.lite.** { *; }
-dontwarn org.tensorflow.lite.**
-keepclassmembers class * {
    native <methods>;
}
-keepclasseswithmembernames class * {
    native <methods>;
}

# --- Retrofit & OkHttp ---
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# --- Gson Data Models & API responses ---
-keepattributes Signature, *Annotation*, EnclosingMethod, InnerClasses
-keep class com.example.brainhemorrhage.api.** { *; }
-keepclassmembers class com.example.brainhemorrhage.api.** { *; }
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# --- Firebase Messaging ---
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# --- AndroidX Navigation & Components ---
-keep class androidx.navigation.** { *; }
-keep class com.example.brainhemorrhage.** { *; }

# --- Glide ---
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class com.bumptech.glide.** { *; }
-dontwarn com.bumptech.glide.**