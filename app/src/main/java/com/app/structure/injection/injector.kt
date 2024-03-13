package com.app.structure.injection

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.app.structure.BuildConfig
import com.app.structure.model.UserHolder
import com.app.structure.shareddata.endpoint.ApiEndPoint
import com.app.structure.shareddata.repo.UserRepo
import com.app.structure.shareddata.repo.UserRepository
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val viewModelModule = module {
    single<UserRepo> { UserRepository(get(), get()) }
}

val networkModule = module {
    single { provideHttpLogging(androidContext()) }
    single { provideRetrofit(get()) }
    single { provideApiService(get()) }
}

val sharedPreferenceModule = module {
    single { provideSharedPreference(get()) }
    single { provideUserHolder(get()) }
}

fun provideHttpLogging(androidContext: Context): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.level =
        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    return OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
//        .addNetworkInterceptor(ChuckInterceptor(androidContext))
        .addNetworkInterceptor(logging)
        .build()
}

fun provideRetrofit(client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BuildConfig.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        .client(client)
        .build()
}

fun provideApiService(retrofit: Retrofit): ApiEndPoint = retrofit.create(ApiEndPoint::class.java)

fun provideSharedPreference(context: Context): SharedPreferences {
    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val sharedPrefsFile = "secret_shared_prefs"
    return EncryptedSharedPreferences.create(
        sharedPrefsFile,
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}

fun provideUserHolder(sharedPreferences: SharedPreferences): UserHolder =
    UserHolder(sharedPreferences)

val appModules = viewModelModule + networkModule + sharedPreferenceModule
