package com.stacrypt.stadroid.data

import android.util.Base64
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.nio.charset.Charset

//const val STEMERALD_API_URL = "http://localhost:8070"
const val STEMERALD_API_URL = "https://my.api.mockaroo.com/"
val EMERALD_API_URL = Base64
    .decode("aHR0cH" + "M6Ly9iZXRhLn" + "RyYWRlb2ZmLnRy" + "YWRlL2FwaXYxLw", Base64.DEFAULT)!!
    .toString(Charset.forName("utf-8"))

data class BookResponse(val buys: ArrayList<Book>, val sells: ArrayList<Book>)

@Suppress("DeferredIsResult")
interface StemeraldApiClient {
    @GET("assets")
    fun assetList(): Deferred<ArrayList<Asset>>

    @GET("stacrypt-balances.json?key=98063e30")
    fun balanceList(): Deferred<ArrayList<Balance>>

    @GET("stacrypt-market-list.json?key=98063e30")
    fun marketList(): Deferred<ArrayList<Market>>

    @GET("stacrypt-market-status.json?key=98063e30")
//    fun marketStatus(@Path("market") market: String): Deferred<MarketStatus>
    fun marketStatus(@Query("market") market: String): Deferred<MarketStatus>

    @GET("stacrypt-market-summary.json?key=98063e30")
//    fun marketSummary(@Path("market") market: String, @Query("period") period: Long = 86400): Deferred<ArrayList<MarketSummary>>
    fun marketSummary(@Query("market") market: String, @Query("period") period: Long = 86400): Deferred<ArrayList<MarketSummary>>

    @GET("stacrypt-market-last.json?key=98063e30")
//    fun marketLast(@Path("market") market: String): Deferred<MarketLast>
    fun marketLast(@Query("market") market: String): Deferred<MarketLast>

    @GET("stacrypt-kline.json?key=98063e30")
    fun kline(): Deferred<ArrayList<Kline>>

    @GET("stacrypt-order-book.json?key=98063e30")
    fun book(): Deferred<BookResponse>

    @GET("stacrypt-deals.json?key=98063e30")
    fun deal(): Deferred<ArrayList<Deal>>

    @GET("stacrypt-mine.json?key=98063e30")
    fun mine(): Deferred<ArrayList<Mine>>
}

@Suppress("DeferredIsResult")
interface EmeraldApiClient {

    @HTTP(method = "GET", path = "clients/me", hasBody = false)
    fun me(): Deferred<User>

    @FormUrlEncoded
    @HTTP(method = "POST", path = "sessions", hasBody = true)
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Deferred<TokenResponse>

    @FormUrlEncoded
    @HTTP(method = "REGISTER", path = "clients", hasBody = true)
    fun registerNewClient(
        @Field("email") email: String,
        @Field("password") password: String
    ): Deferred<User>
}

var okHttpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor())
    .build()

var stemeraldApiClient = Retrofit.Builder()
    .baseUrl(STEMERALD_API_URL)
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build()
    .create(StemeraldApiClient::class.java)

var emeraldApiClient = Retrofit.Builder()
    .baseUrl(EMERALD_API_URL)
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build()
    .create(EmeraldApiClient::class.java)
