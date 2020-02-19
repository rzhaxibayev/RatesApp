package kz.rauanzk.ratesapp.di

import kz.rauanzk.ratesapp.BuildConfig
import kz.rauanzk.ratesapp.data.api.ApiDataSource
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


private const val TIME_OUT_IN_SECONDS = 60L

val networkModule = module {
    single { provideHttpLoggingInterceptor() }
    single { provideUnsafeOkHttpClient(get()) }
    single { createWebService<ApiDataSource>(get(), BuildConfig.apiURL) }
}

fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .connectTimeout(TIME_OUT_IN_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIME_OUT_IN_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIME_OUT_IN_SECONDS, TimeUnit.SECONDS)
        .build()
}

// FIXME: This is temporary for emulator on my computer, to avoid proxy issues. Use normal OkHttpClient for production
fun provideUnsafeOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {

        override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
        }

        override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }

    })

    // Install the all-trusting trust manager
    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, java.security.SecureRandom())
    // Create an ssl socket factory with our all-trusting manager
    val sslSocketFactory = sslContext.socketFactory

    return OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .connectTimeout(TIME_OUT_IN_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIME_OUT_IN_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIME_OUT_IN_SECONDS, TimeUnit.SECONDS)
        .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        .hostnameVerifier { _, _ -> true }
        .build()
}

fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = if (BuildConfig.DEBUG)
        HttpLoggingInterceptor.Level.BODY
    else
        HttpLoggingInterceptor.Level.NONE
    return interceptor
}

inline fun <reified Z> createWebService(okHttpClient: OkHttpClient, url: String): Z {
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.create(Z::class.java)
}
