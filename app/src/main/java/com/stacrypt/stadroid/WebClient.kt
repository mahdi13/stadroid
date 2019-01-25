package com.stacrypt.stadroid

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.tinder.scarlet.Stream
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import kotlinx.coroutines.Deferred
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

const val STEMERALD_API_URL = "http://localhost:8070"

var nextId: Int = 0
    get() {
        field += 1
        return field
    }

interface StemeraldApiClient {
    @GET
    fun marketList(): Deferred<Market>
}

//interface WebsocketClientInterface {
//    @Receive
//    fun receive(): ReceiveChannel<StexchangeResponse>
//
//    @Send
//    fun send(request: StexchangeRequest)
//
//    @Receive
////    fun observeWebSocketEvent(): Deferred<WebSocket.Event>
//
////    @Send
////    fun sendSubscribe(subscribe: Subscribe)
//
////    @Receive
////    fun observeTicker(): Deferred<Ticker>
//}

var okHttpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor())
    .build()

var stemeraldApiClient = Retrofit.Builder().baseUrl(STEMERALD_API_URL)
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build()
    .create(StemeraldApiClient::class.java)

//val scarletInstance = Scarlet.Builder()
//    .webSocketFactory(okHttpClient.newWebSocketFactory("ws://localhost:8090/"))
//    .addMessageAdapterFactory(GsonMessageAdapter.Factory())
//    .addStreamAdapterFactory(CoroutinesStreamAdapterFactory())
//    .build()

data class StexchangeRequest(val method: String, val params: List<*> = ArrayList<String>(), val id: Int = nextId)
data class StexchangeResponse(val error: String?, val result: String?, val id: Int?)

object stexchangeApiClient {

//    val stexchangeApiClientService = scarletInstance.create<WebsocketClientInterface>()
//
//    suspend fun ping(): String {
//        stexchangeApiClientService.send(StexchangeRequest("server.ping"))
////        stexchangeApiClientService.send(StexchangeRequest("server.time"))
//        return stexchangeApiClientService.receive().receive().result.toString()

//    suspend fun ping(): String {
//    }

    fun start() {
//        val request = Request.Builder().url("ws://localhost:8090/").build()
//        val listener = object : WebSocketListener() {
//            override fun onOpen(webSocket: WebSocket, response: Response) {
//                super.onOpen(webSocket, response)
//            }
//
//            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
//                super.onFailure(webSocket, t, response)
//            }
//
//            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
//                super.onClosing(webSocket, code, reason)
//            }
//
//            override fun onMessage(webSocket: WebSocket, text: String) {
//                super.onMessage(webSocket, text)
//            }
//
//            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
//                super.onClosed(webSocket, code, reason)
//            }
//        }
        val ws = okHttpClient.newWebSocketFactory("ws://localhost:8090/").create().open().start(object :
            Stream.Observer<com.tinder.scarlet.WebSocket.Event> {
            override fun onComplete() {
            }

            override fun onError(throwable: Throwable) {
            }

            override fun onNext(data: com.tinder.scarlet.WebSocket.Event) {
            }
        })
    }

    fun shutdown() {
        okHttpClient.dispatcher().executorService().shutdown()

    }


}