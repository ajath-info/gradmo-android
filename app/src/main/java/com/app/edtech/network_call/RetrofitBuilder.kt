package com.app.edtech.network_call

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {
    private const val BASE_URL = "https://hihlo.com/api/v1/"

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .addInterceptor(provideHeaderInterceptor())  // Attach header interceptor
        .addInterceptor(provideLoggingInterceptor()) // Attach logging interceptor
        .addInterceptor(TokenInterceptor())          // Attach token interceptor (new)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    private fun provideHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
            request.addHeader("Accept", "application/json")
            request.addHeader("Content-Type", "application/json")
            chain.proceed(request.build())
        }
    }

    private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
  /* // Create a trust manager that does not validate certificate chains
   val trustAllCertificates = object : X509TrustManager {
       override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
           // Trust all client certificates
       }

       override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
           // Trust all server certificates
       }

       override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
   }

    // Install the all-trusting trust manager
    val sslContext = SSLContext.getInstance("SSL")
    // Create an ssl socket factory with our all-trusting manager

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build() //Doesn't require the adapter
    }

    private val httpClient: OkHttpClient =
        OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(Interceptor(fun(chain: Interceptor.Chain): Response {
                val ongoing: Request.Builder = chain.request().newBuilder()
                ongoing.addHeader("Accept", "application/json")
                ongoing.addHeader("Content-Type", "application/json")
                return chain.proceed(ongoing.build())
            })).addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .sslSocketFactory(getSSLContext(), trustAllCertificates)
            .hostnameVerifier { _, _ -> true }
            .build()

    fun getSSLContext():SSLSocketFactory{
        sslContext.init(null, arrayOf<TrustManager>(trustAllCertificates), SecureRandom())
        return sslContext.socketFactory
    }

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)*/
}