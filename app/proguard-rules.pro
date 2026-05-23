# --- Базовые настройки ---
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod,*Annotation*

# --- Настройка обфускации ---
# Сохраняем BuildConfig, так как он используется для API ключей
-keep class com.example.androidappfilmproject.BuildConfig { *; }

# --- Retrofit & OkHttp ---
-keepattributes Signature, InnerClasses, EnclosingMethod
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep interface retrofit2.** { *; }
-keepclassmembernames interface * {
    @retrofit2.http.* <methods>;
}

# --- GSON ---
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.google.gson.** { *; }

# --- Kotlin Coroutines ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.coroutines.android.HandlerContext {
    private final android.os.Handler handler;
}
-dontwarn kotlinx.coroutines.**

# --- Room ---
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# --- RxJava ---
-dontwarn java.lang.instrument.ClassFileTransformer
-dontwarn sun.misc.**
-keep class io.reactivex.rxjava3.** { *; }

# --- Glide ---
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.VideoDecoder$VideoMetadataRetrieverSettings { *; }
-keepnames class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule

# --- Koin / Dagger ---
-keep class io.insertkoin.** { *; }
-keep class com.google.dagger.** { *; }

# --- ТВОИ МОДЕЛИ ДАННЫХ (Самое важное!) ---
# Сохраняем все сущности из модулей, чтобы R8 не удалил их поля
-keep class com.example.remote_module.entity.** { *; }
-keep class com.example.database_module.entity.** { *; }

# Сохраняем интерфейс API
-keep interface com.example.remote_module.TmdbApi { *; }

# Сохраняем сгенерированные классы Dagger
-keep class **.Dagger* { *; }
-keep class *__Factory { *; }
-keep class *__MemberInjector { *; }

# Сохраняем Paging Source
-keep class androidx.paging.PagingSource { *; }
-keep class * extends androidx.paging.PagingSource { *; }

# Сохраняем Parcelable классы
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
