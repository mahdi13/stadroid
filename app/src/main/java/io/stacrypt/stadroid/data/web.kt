package io.stacrypt.stadroid.data

import android.util.Base64
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.File
import java.nio.charset.Charset
import okhttp3.*
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.google.gson.GsonBuilder
import io.stacrypt.stadroid.application
import retrofit2.HttpException
import kotlin.collections.ArrayList

// const val STEMERALD_API_URL = "http://localhost:8070"
const val MOCK_STEMERALD_API_URL = "https://my.api.mockaroo.com/"
// const val STAWALLET_API_URL = "http://10.0.2.2:7071/apiv2/"
// const val STEMERALD_API_URL = "http://10.0.2.2:7071/apiv2/"
// const val STEMERALD_API_URL = "http://stemerald.stacrypt.io/apiv2/"
const val STEMERALD_API_URL = "http://116.203.119.119/apiv2/"
val EMERALD_API_URL = Base64
    .decode("aHR0cH" + "M6Ly9iZXRhLn" + "RyYWRlb2ZmLnRy" + "YWRlL2FwaXYxLw", Base64.DEFAULT)!!
    .toString(Charset.forName("utf-8"))

data class BookResponse(val buys: List<Book>, val sells: List<Book>)

// @Suppress("DeferredIsResult")
// interface StawalletApiClient {
//    @HTTP(method = "OVERVIEW", path = "balances", hasBody = true)
//    fun balanceOverview(
//        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: ""
//    ): Deferred<ArrayList<BalanceOverview>>
//
//    @HTTP(method = "HISTORY", path = "balances", hasBody = true)
//    fun balanceHistory(
//        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
//        @Query("asset") assetName: String,
//        @Query("page") page: Int = 0
//    ): Deferred<ArrayList<BalanceHistory>>
//
// }

@Suppress("DeferredIsResult")
interface StemeraldV2ApiClient {

    /**
     * Assets
     */
    @HTTP(method = "LIST", path = "assets", hasBody = true)
    fun assetList(): Deferred<ArrayList<Asset>>

    @HTTP(method = "GET", path = "currencies", hasBody = false)
    fun currencyList(): Deferred<ArrayList<Currency>>

    /**
     * Balances
     */
    @HTTP(method = "OVERVIEW", path = "balances", hasBody = true)
    fun balanceList(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: ""
    ): Deferred<ArrayList<BalanceOverview>>

    @HTTP(method = "OVERVIEW", path = "balances", hasBody = true)
    fun balanceOverview(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: ""
    ): Deferred<Array<BalanceOverview>>

    @HTTP(method = "HISTORY", path = "balances", hasBody = true)
    fun balanceHistory(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Query("asset") assetName: String,
        @Query("page") page: Int = 0
    ): Deferred<ArrayList<BalanceHistory>>

    /**
     * Markets
     */
    @HTTP(method = "LIST", path = "markets", hasBody = true)
    fun marketList(): Deferred<List<Market>>

    @HTTP(method = "STATUS", path = "markets/{market}", hasBody = true)
    fun marketStatus(@Path("market") market: String, @Query("period") period: Long = 86400): Deferred<MarketStatus>

    @HTTP(method = "SUMMARY", path = "markets/{market}", hasBody = true)
    fun marketSummary(@Path("market") market: String): Deferred<List<MarketSummary>>

    @HTTP(method = "LAST", path = "markets/{market}", hasBody = true)
    fun marketLast(@Path("market") market: String): Deferred<MarketLast>

    @HTTP(method = "KLINE", path = "markets/{market}", hasBody = true)
    fun kline(
        @Path("market") market: String,
        @Query("start") start: Int,
        @Query("end") end: Int,
        @Query("interval") interval: Int = 86400
    ): Deferred<ArrayList<Kline>>

    @HTTP(method = "BOOK", path = "markets/{market}", hasBody = true)
    fun book(@Path("market") market: String, @Query("side") side: String): Deferred<List<Book>>

    @HTTP(method = "DEPTH", path = "markets/{market}", hasBody = true)
    fun depth(
        @Path("market") market: String,
        @Query("interval") interval: String,
        @Query("limit") limit: Int
    ): Deferred<Depth>

    @HTTP(method = "PEEK", path = "markets/{market}/marketdeals", hasBody = true)
    fun getMarketDeals(
        @Path("market") market: String,
        @Query("limit") take: Int = 20,
        @Query("lastId") lastId: Int = 0
    ): Deferred<List<MarketDeal>>

    @HTTP(method = "PEEK", path = "markets/{market}/mydeals", hasBody = true)
    fun getMyDeals(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Path("market") market: String,
        @Query("limit") take: Int = 20,
        @Query("offset") skip: Int = 0
    ): Deferred<List<MyDeal>>

    /**
     * Orders
     */
    @HTTP(method = "GET", path = "orders", hasBody = false)
    fun getOrders(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Query("marketName") marketName: String,
        @Query("status") status: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Deferred<List<Order>>

    @HTTP(method = "CANCEL", path = "orders/{order}", hasBody = true)
    fun cancelOrder(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Path("order") orderId: Int,
        @Query("marketName") marketName: String
    ): Deferred<Unit>

    @FormUrlEncoded
    @HTTP(method = "CREATE", path = "orders", hasBody = true)
    fun putMarketOrder(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Field("marketName") marketName: String,
        @Field("amount") amount: String,
        @Field("side") side: String,
        @Field("type") type: String = "market"
    ): Deferred<Order>

    @FormUrlEncoded
    @HTTP(method = "CREATE", path = "orders", hasBody = true)
    fun putLimitOrder(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Field("marketName") marketName: String,
        @Field("amount") amount: String,
        @Field("price") price: String,
        @Field("side") side: String,
        @Field("type") type: String = "limit"
    ): Deferred<Order>

    /**
     * Wallet
     */
    @HTTP(method = "PEEK", path = "markets/{market}/mydeals", hasBody = true)
    fun mine(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Path("market") market: String,
        @Query("offset") skip: Int = 0,
        @Query("limit") take: Int = 20
    ): Deferred<ArrayList<MyDeal>>

    @HTTP(method = "GET", path = "transactions/payment-methods", hasBody = false)
    fun getPaymentMethods(): Deferred<List<PaymentMethod>>

    /**
     * Deposits
     */
    @HTTP(method = "LIST", path = "deposits", hasBody = true)
    fun getDeposits(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Query("cryptocurrencySymbol") assetName: String,
        @Query("page") page: Int = 0
    ): Deferred<ArrayList<DepositDetail>>

    @HTTP(method = "GET", path = "deposits/{depositId}", hasBody = false)
    fun getDepositDetail(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Path("depositId") depositId: Int,
        @Query("cryptocurrencySymbol") cryptocurrencySymbol: String
    ): Deferred<DepositDetail>

    @HTTP(method = "SHOW", path = "deposits", hasBody = true)
    fun showDepositInfo(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Query("cryptocurrencySymbol") assetName: String
    ): Deferred<DepositInfo>

    @HTTP(method = "RENEW", path = "deposits", hasBody = true)
    fun renewDepositInfo(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Query("cryptocurrencySymbol") assetName: String
    ): Deferred<DepositInfo>

    /**
     * Withdraw
     */
    @HTTP(method = "LIST", path = "withdraws", hasBody = true)
    fun getWithdraws(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Query("cryptocurrencySymbol") assetName: String,
        @Query("page") page: Int = 0
    ): Deferred<ArrayList<Withdraw>>

    @HTTP(method = "GET", path = "withdraws/{withdrawId}", hasBody = false)
    fun getWithdrawDetail(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Path("withdrawId") withdrawId: Int,
        @Query("cryptocurrencySymbol") cryptocurrencySymbol: String
    ): Deferred<Withdraw>

    @FormUrlEncoded
    @HTTP(method = "SCHEDULE", path = "withdraws", hasBody = true)
    fun scheduleWithdraw(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Field("cryptocurrencySymbol") assetName: String,
        @Field("amount") amount: String,
        @Field("address") address: String,
        @Field("businessUid") businessUid: Int
    ): Deferred<Withdraw>

    /**
     * Shaparak
     */
    @FormUrlEncoded
    @HTTP(method = "CREATE", path = "transactions/cashins", hasBody = true)
    fun createCashin(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Field("amount") amount: String,
        @Field("bankingIdId") bankingIdId: Int,
        @Field("paymentMethodId") paymentMethodId: Int
    ): Deferred<BankingTransaction>

    @FormUrlEncoded
    @HTTP(method = "CLAIM", path = "transactions/cashins/{id}", hasBody = true)
    fun claimCashin(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Path("id") cashingId: Int,
        @Field("referenceId") referenceId: String
    ): Deferred<BankingTransaction>

    @FormUrlEncoded
    @HTTP(method = "SCHEDULE", path = "transactions/cashouts", hasBody = true)
    fun scheduleCashout(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Field("amount") amount: String,
        @Field("bankingIdId") bankingIdId: Int,
        @Field("paymentMethodId") paymentMethodId: Int
    ): Deferred<BankingTransaction>

    /**
     * Banking Transactions
     */
    @HTTP(method = "GET", path = "transactions", hasBody = false)
    fun getBankingTransactions(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Query("fiatSymbol") fiatSymbol: String?,
        @Query("type") type: String?,
        @Query("take") take: Int? = null,
        @Query("skip") skip: Int? = null
    ): Deferred<List<BankingTransaction>>

    @HTTP(method = "GET", path = "transactions/{id}", hasBody = false)
    fun getBankingTransactionById(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Path("id") id: Int
    ): Deferred<BankingTransaction>

    /**
     * Membership
     */
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

    /**
     * Email verification
     */
    @HTTP(method = "SCHEDULE", path = "clients/email-verifications", hasBody = true)
    fun schedulEmailVerification(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: ""
    ): Deferred<Unit>

    @FormUrlEncoded
    @HTTP(method = "VERIFY", path = "clients/email-verifications", hasBody = true)
    fun verifyEmailVerification(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Field("token") token: String
    ): Deferred<User>

    /**
     * Phone verification
     */
    @FormUrlEncoded
    @HTTP(method = "SCHEDULE", path = "clients/mobile-phone-verifications", hasBody = true)
    fun schedulMobilePhoneVerification(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Field("phone") phone: String
    ): Deferred<Unit>

    @FormUrlEncoded
    @HTTP(method = "SCHEDULE", path = "clients/fixed-phone-verifications", hasBody = true)
    fun schedulFixedPhoneVerification(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Field("phone") phone: String
    ): Deferred<Unit>

    @FormUrlEncoded
    @HTTP(method = "VERIFY", path = "clients/mobile-phone-verifications", hasBody = true)
    fun verifyMobilePhoneVerification(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Field("phone") phone: String,
        @Field("code") code: String
    ): Deferred<User>

    @FormUrlEncoded
    @HTTP(method = "VERIFY", path = "clients/fixed-phone-verifications", hasBody = true)
    fun verifyFixedPhoneVerification(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Field("phone") phone: String,
        @Field("code") code: String
    ): Deferred<User>

    /**
     * Bank Accounts
     */
    @HTTP(method = "GET", path = "banking/accounts", hasBody = false)
    fun getBankAccounts(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Query("skip") skip: Int = 0,
        @Query("take") take: Int = 20,
        @Query("fiatSymbol") fiatSymbol: String? = null
    ): Deferred<List<BankAccount>>

    @FormUrlEncoded
    @HTTP(method = "ADD", path = "banking/accounts", hasBody = true)
    fun addBankAccount(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Field("iban") iban: String,
        @Field("fiatSymbol") fiatSymbol: String,
        @Field("owner") owner: String
    ): Deferred<BankAccount>

    /**
     * Bank Cards
     */
    @HTTP(method = "GET", path = "banking/cards", hasBody = false)
    fun getBankCards(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Query("skip") skip: Int = 0,
        @Query("take") take: Int = 20,
        @Query("fiatSymbol") fiatSymbol: String? = null
    ): Deferred<List<BankCard>>

    @FormUrlEncoded
    @HTTP(method = "ADD", path = "banking/cards", hasBody = true)
    fun addBankCard(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Field("pan") pan: String,
        @Field("fiatSymbol") fiatSymbol: String,
        @Field("holder") holder: String
    ): Deferred<BankCard>

    /**
     * Evidences
     */
    @HTTP(method = "GET", path = "clients/me/evidences", hasBody = false)
    fun getMyEvidences(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: ""
    ): Deferred<Evidence>

    @Multipart
    @HTTP(method = "SUBMIT", path = "clients/evidences", hasBody = true)
    fun submitMyEvidences(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Part firstName: MultipartBody.Part,
        @Part lastName: MultipartBody.Part,
        // @Part("gender") gender: Gender,
        @Part gender: MultipartBody.Part,
        // @Part("birthday") birthday: Date,
        @Part birthday: MultipartBody.Part,
        @Part cityId: MultipartBody.Part,
        @Part nationalCode: MultipartBody.Part,
        @Part address: MultipartBody.Part,
        // @PartMap(encoding = "utf8") partMap: Map<String, String>,
        @Part idCard: MultipartBody.Part,
        @Part idCardSecondary: MultipartBody.Part
    ): Deferred<Evidence>

    /**
     * Password
     */
    @FormUrlEncoded
    @HTTP(method = "CHANGE", path = "clients/passwords", hasBody = true)
    fun changePassword(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Field("currentPassword") currentPassword: String,
        @Field("newPassword") newPassword: String
    ): Deferred<User>

    @FormUrlEncoded
    @HTTP(method = "RESET", path = "clients/passwords", hasBody = true)
    fun doResetPasswordPassword(
        @Field("token") token: String,
        @Field("password") password: String
    ): Deferred<Unit>

    @FormUrlEncoded
    @HTTP(method = "SCHEDULE", path = "clients/passwords", hasBody = true)
    fun scheduleResetPassword(
        @Field("email") email: String
    ): Deferred<Unit>

    /**
     * Second Factor
     */
    @FormUrlEncoded
    @HTTP(method = "ENABLE", path = "clients/secondfactors", hasBody = true)
    fun enableSecondFactor(@Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: ""): Deferred<Unit>

    @FormUrlEncoded
    @HTTP(method = "PROVISION", path = "clients/secondfactors", hasBody = true)
    fun provisionSecondFactor(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: ""
    ): Deferred<String>

    @FormUrlEncoded
    @HTTP(method = "DISABLE", path = "clients/secondfactors", hasBody = true)
    fun disableSecondFactor(@Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: ""): Deferred<Unit>

    /**
     * Security logs
     */
    @HTTP(method = "GET", path = "logs", hasBody = false)
    fun getSecurityLogs(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Query("skip") skip: Int = 0,
        @Query("take") take: Int = 20,
        @Query("type") type: String? = null
    ): Deferred<List<SecurityLog>>

    /**
     * Sessions
     */
    @HTTP(method = "GET", path = "sessions", hasBody = false)
    fun getSessions(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: ""
    ): Deferred<List<Session>>

    @HTTP(method = "GET", path = "sessions/{sessionId}", hasBody = true)
    fun terminateSessions(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Path("sessionId") sessionId: String
    ): Deferred<Unit>

    /**
     * Notifications
     */
    @HTTP(method = "GET", path = "notifications", hasBody = false)
    fun getNotifications(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Query("skip") offset: Int,
        @Query("take") limit: Int
    ): Deferred<List<Notification>>

    @HTTP(method = "READ", path = "notifications/{notificationId}", hasBody = true)
    fun readNotification(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Path("notificationId") notificationId: Int
    ): Deferred<Notification>

    @HTTP(method = "COUNT", path = "notifications", hasBody = true)
    fun countNotifications(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: ""
    ): Deferred<NotificationCount>

    /**
     * Territories
     */
    @HTTP(method = "GET", path = "territories/countries", hasBody = false)
    fun getCountries(): Deferred<List<Country>>

    @HTTP(method = "GET", path = "territories/states", hasBody = false)
    fun getStates(@Query("countryId") countryId: Int): Deferred<List<State>>

    @HTTP(method = "GET", path = "territories/cities", hasBody = false)
    fun getCities(@Query("stateId") stateId: Int): Deferred<List<City>>

    /**
     * Ticketing
     */
    @HTTP(method = "GET", path = "tickets/departments", hasBody = false)
    fun getDepartments(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: ""
    ): Deferred<List<TicketDepartment>>

    @HTTP(method = "GET", path = "tickets", hasBody = false)
    fun getTickets(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Query("take") take: Int? = null,
        @Query("skip") skip: Int? = null
    ): Deferred<List<Ticket>>

    @HTTP(method = "GET", path = "tickets/{id}/messages", hasBody = false)
    fun getTicketMessages(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Path("id") ticketId: Int,
        @Query("take") take: Int? = null,
        @Query("skip") skip: Int? = null
    ): Deferred<List<TicketMessage>>

    @FormUrlEncoded
    @HTTP(method = "APPEND", path = "tickets/{id}", hasBody = true)
    fun appendTicketMessages(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Path("id") ticketId: Int,
        @Field("message") message: String
    ): Deferred<TicketMessage>

    @FormUrlEncoded
    @HTTP(method = "CLOSE", path = "tickets/{id}", hasBody = true)
    fun closeTicket(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Path("id") ticketId: Int
    ): Deferred<Ticket>

    @FormUrlEncoded
    @HTTP(method = "CREATE", path = "tickets", hasBody = true)
    fun createTicket(
        @Header("Authorization") jwtToken: String = sessionManager.jwtToken ?: "",
        @Field("departmentId") departmentId: Int,
        @Field("title") title: String,
        @Field("message") message: String
    ): Deferred<Ticket>
}

// @Suppress("DeferredIsResult")
// interface MockStemeraldApiClient {
//    @GET("assets")
//    fun assetList(): Deferred<ArrayList<Asset>>
//
//    @GET("balances?key=98063e30")
//    fun balanceList(): Deferred<ArrayList<BalanceOverview>>
//
//    @GET("balance-history?key=98063e30")
//    fun balanceHistory(
//        @Query("asset") assetName: String,
//        @Query("page") page: Int = 0
//    ): Deferred<ArrayList<BalanceHistory>>
//
//    @GET("market-list?key=98063e30")
//    fun allMarkets(): Deferred<ArrayList<Market>>
//
//    @GET("market-status/{market}?key=98063e30")
//    fun marketStatus(@Path("market") market: String): Deferred<MarketStatus>
//
//    @GET("market-summary/{market}?key=98063e30")
//    fun marketSummary(@Path("market") market: String, @Query("period") period: Long = 86400): Deferred<ArrayList<MarketSummary>>
//
//    @GET("market-last/{market}?key=98063e30")
//    fun marketLast(@Path("market") market: String): Deferred<MarketLast>
//
//    @GET("kline/{market}?key=98063e30")
//    fun kline(@Path("market") market: String): Deferred<ArrayList<Kline>>
//
//    @GET("order-book/{market}?key=98063e30")
//    fun book(@Path("market") market: String): Deferred<BookResponse>
//
//    @GET("deals/{market}?key=98063e30")
//    fun deal(@Path("market") market: String): Deferred<ArrayList<Deal>>
//
//    @GET("myDeals/{market}?key=98063e30")
//    fun mine(@Path("market") market: String): Deferred<ArrayList<MyDeal>>
// }

// @Suppress("DeferredIsResult")
// interface EmeraldApiClient {
//
//
// }

val cookieJar by lazy { PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(application)) }

val okHttpClient by lazy {
    OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .addInterceptor { chain ->

            val newRequest = chain.request()
                .newBuilder()
                .addHeader("X-Firebase-Token", sessionManager.fbToken ?: "")
                .build()

            val response = chain.proceed(newRequest)

            val newToken = response.header("X-New-JWT-Token")
            if (newToken != null) {
                sessionManager.login(newToken)
            }

            // FIXME: More strict check (sometimes 401 is about s.th else, e.g. forgetting passing the authorization header)
            // FIXME: More strict check (sometimes 400 means unauthorized)
            if (response.code() == 401) {
                // TODO: Force logout
                sessionManager.logout()
            }
            return@addInterceptor response
        }
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()
}

// var stemeraldApiClient = Retrofit.Builder()
//    .baseUrl(STEMERALD_API_URL)
//    .addCallAdapterFactory(CoroutineCallAdapterFactory())
//    .addConverterFactory(GsonConverterFactory.create())
//    .client(okHttpClient)
//    .build()
//    .create(StemeraldApiClient::class.java)

var stemeraldApiClient = Retrofit.Builder()
    .baseUrl(STEMERALD_API_URL)
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS").create()))
    .client(okHttpClient)
    .build()
    .create(StemeraldV2ApiClient::class.java)

// var emeraldApiClient = Retrofit.Builder()
//    .baseUrl(EMERALD_API_URL)
//    .addCallAdapterFactory(CoroutineCallAdapterFactory())
//    .addConverterFactory(GsonConverterFactory.create())
//    .client(okHttpClient)
//    .build()
//    .create(EmeraldApiClient::class.java)

// var mockStemeraldApiClient = Retrofit.Builder()
//    .baseUrl(MOCK_STEMERALD_API_URL)
//    .addCallAdapterFactory(CoroutineCallAdapterFactory())
//    .addConverterFactory(GsonConverterFactory.create())
//    .client(okHttpClient)
//    .build()
//    .create(MockStemeraldApiClient::class.java)

// class TokenAuthenticator : Authenticator {
//    override fun authenticate(route: Route?, response: Response): Request? {
//        if (response.header("X-New-JWT-Token") == 200) {
//
//        } else if (response.code() == 401) {
//            val refreshCall = refereshAccessToken(refereshToken)
//
//            //make it as retrofit synchronous call
//            val refreshResponse = refreshCall.execute()
//            if (refreshResponse != null && refreshResponse.code() == 200) {
//                //read new JWT value from response body or interceptor depending upon your JWT availability logic
//                newCookieValue = readNewJwtValue()
//                return response.request().newBuilder()
//                    .header("basic-auth", newCookieValue)
//                    .build()
//            } else {
//                // TODO Handle
//                return null
//            }
//        }
//    }
//
//    @Throws(IOException::class)
//    fun authenticate(route: Route, response: Response<*>): Request? {
//
//    }
// }

fun File.mimeType(): MediaType? = when (extension) {
    "jpeg" -> MediaType.parse("image/jpeg")
    "jpg" -> MediaType.parse("image/jpeg")
    "png" -> MediaType.parse("image/png")
    else -> null
}

fun HttpException.verboseLocalizedMessage() = StringBuilder()
    .append("Error code:").append(this.code()).append(": ")
    .append("\n").append("Message: ").append(response().message() ?: "Unknown")
    .append("\n").append("Body: ")
    .append(
        response().errorBody()?.string()?.run {
            if (length > 50) take(20) + " ... " + takeLast(20) else this
        } ?: "Unknown"
    )
    .append("\n").append("Reason: ").append(response().headers().get("X-Reason")?.snakeToHuman() ?: "Unknown")
    .toString()

fun String.snakeToHuman() = if (isNullOrEmpty()) "" else this
    .replaceRange(0, 1, this[0].toUpperCase().toString())
    .replace("([A-Za-z0-9]+)-".toRegex(), "$1")