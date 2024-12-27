package com.example.newsapp.di

import androidx.room.Insert
import com.example.newsapp.data.remote.NewsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        interceptor: Interceptor
    ) : OkHttpClient {
         return OkHttpClient.Builder()
             .addInterceptor {chain ->
                 val request = chain.request().newBuilder()
                     .addHeader(
                         "X-Api-Key",
                         "f1a4223cd8a04d5890ea957c8ec6d101"
                     )
                     .build()
                 chain.proceed(request)
             }
             .connectTimeout(10L, TimeUnit.SECONDS)
             .addInterceptor(interceptor)
             .build()
    }

    @Provides
    @Singleton
    fun provideInterceptor() : Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideNewsApi(retrofit: Retrofit): NewsApiService {
        return retrofit.create(NewsApiService::class.java)
    }
}