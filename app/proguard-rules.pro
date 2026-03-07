# Keep line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── Room ──
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# ── Retrofit / OkHttp ──
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# ── RevenueCat ──
-keep class com.revenuecat.purchases.** { *; }

# ── Google Ads ──
-keep class com.google.android.gms.ads.** { *; }

# ── Kotlin Serialization / Coroutines ──
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }

# ── App data classes ──
-keep class com.codergang.chatdirecto.data.entity.** { *; }
-keep class com.codergang.chatdirecto.data.preferences.** { *; }
