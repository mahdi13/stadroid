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
const val STAWALLET_API_URL = "https://localhost:7071/apiv2/"
val EMERALD_API_URL = Base64
    .decode("aHR0cH" + "M6Ly9iZXRhLn" + "RyYWRlb2ZmLnRy" + "YWRlL2FwaXYxLw", Base64.DEFAULT)!!
    .toString(Charset.forName("utf-8"))

data class BookResponse(val buys: ArrayList<Book>, val sells: ArrayList<Book>)

@Suppress("DeferredIsResult")
interface StawalletApiClient {
    @HTTP(method = "OVERVIEW", path = "balances", hasBody = false)
    fun balanceOverview(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: ""
    ): Deferred<ArrayList<BalanceOverview>>

    @HTTP(method = "HISTORY", path = "balances", hasBody = false)
    fun balanceHistory(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Query("asset") assetName: String,
        @Query("page") page: Int = 0
    ): Deferred<ArrayList<BalanceHistory>>

}

@Suppress("DeferredIsResult")
interface StemeraldApiClient {
    @GET("assets")
    fun assetList(): Deferred<ArrayList<Asset>>

    @GET("balances?key=98063e30")
    fun balanceList(): Deferred<ArrayList<BalanceOverview>>

    @GET("market-list?key=98063e30")
    fun marketList(): Deferred<ArrayList<Market>>

    @GET("market-status/{market}?key=98063e30")
    fun marketStatus(@Path("market") market: String): Deferred<MarketStatus>

    @GET("market-summary/{market}?key=98063e30")
    fun marketSummary(@Path("market") market: String, @Query("period") period: Long = 86400): Deferred<ArrayList<MarketSummary>>

    @GET("market-last/{market}?key=98063e30")
    fun marketLast(@Path("market") market: String): Deferred<MarketLast>

    @GET("kline/{market}?key=98063e30")
    fun kline(@Path("market") market: String): Deferred<ArrayList<Kline>>

    @GET("order-book/{market}?key=98063e30")
    fun book(@Path("market") market: String): Deferred<BookResponse>

    @GET("deals/{market}?key=98063e30")
    fun deal(@Path("market") market: String): Deferred<ArrayList<Deal>>

    @GET("mine/{market}?key=98063e30")
    fun mine(@Path("market") market: String): Deferred<ArrayList<Mine>>
}

@Suppress("DeferredIsResult")
interface EmeraldApiClient {

    @HTTP(method = "GET", path = "clients/me", hasBody = false)
    fun me(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: ""
    ): Deferred<User>

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

    @HTTP(method = "GET", path = "banking/accounts", hasBody = false)
    fun getBankAccounts(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: ""
    ): Deferred<List<BankAccount>>

    @HTTP(method = "GET", path = "banking/cards", hasBody = false)
    fun getBankCards(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: ""
    ): Deferred<List<BankCard>>

    @FormUrlEncoded
    @HTTP(method = "ADD", path = "banking/accounts", hasBody = true)
    fun addBankAccount(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Field("iban") iban: String,
        @Field("fiatSymbol") fiatSymbol: String,
        @Field("holder") holder: String
    ): Deferred<BankAccount>

    @FormUrlEncoded
    @HTTP(method = "ADD", path = "banking/cards", hasBody = true)
    fun addBankCard(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Field("pan") pan: String,
        @Field("fiatSymbol") fiatSymbol: String,
        @Field("holder") holder: String
    ): Deferred<BankCard>
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

var stawalletApiClient = Retrofit.Builder()
    .baseUrl(STAWALLET_API_URL)
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build()
    .create(StawalletApiClient::class.java)

var emeraldApiClient = Retrofit.Builder()
    .baseUrl(EMERALD_API_URL)
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build()
    .create(EmeraldApiClient::class.java)
