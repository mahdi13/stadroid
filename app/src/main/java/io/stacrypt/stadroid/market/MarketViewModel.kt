package io.stacrypt.stadroid.market

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.stacrypt.stadroid.data.*
import io.stacrypt.stadroid.ext.parseMarketBaseCurrency
import io.stacrypt.stadroid.ext.parseMarketQuoteCurrency
import io.stacrypt.stadroid.market.data.MarketRepository
import io.stacrypt.stadroid.wallet.data.WalletRepository
import java.math.BigDecimal

class MarketViewModel : ViewModel() {

    lateinit var marketName: String

    val allMarkets: LiveData<List<Market>> = MarketRepository.getMarkets()

    val market: LiveData<Market> by lazy {
        MarketRepository.getMarket(marketName)
    }
    val last: LiveData<MarketLast> by lazy {
        MarketRepository.getMarketLast(marketName)
    }
    val summary: LiveData<MarketSummary> by lazy {
        MarketRepository.getMarketSummary(marketName)
    }
    val status: LiveData<MarketStatus> by lazy { MarketRepository.getMarketStatus(marketName) }
    val book: LiveData<BookResponse> by lazy { MarketRepository.getBook(marketName) }
    val marketDeals: LiveData<List<MarketDeal>> by lazy { MarketRepository.getMarketDeals(marketName) }
    val myDeals: LiveData<List<MyDeal>> by lazy { MarketRepository.getMyDeals(marketName) }
    val myOrders: LiveData<List<Order>> by lazy { MarketRepository.getMyActiveOrders(marketName) }
    val kline: LiveData<List<Kline>> by lazy { MarketRepository.getKline48(marketName) }

    val baseCurrency: LiveData<Currency> by lazy { WalletRepository.getCurrency(marketName.parseMarketBaseCurrency()) }
    val quoteCurrency: LiveData<Currency> by lazy { WalletRepository.getCurrency(marketName.parseMarketQuoteCurrency()) }

    val newOrderAmount = MutableLiveData<BigDecimal>().apply { postValue(BigDecimal("0")) }
    val newOrderPrice = MutableLiveData<BigDecimal>().apply { postValue(BigDecimal("0")) }
    val newOrderType = MutableLiveData<String>().apply { postValue("limit") }
}