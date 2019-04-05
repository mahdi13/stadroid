package io.stacrypt.stadroid.market

import androidx.lifecycle.Event
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import io.stacrypt.stadroid.data.*
import io.stacrypt.stadroid.market.data.MarketRepository
import io.stacrypt.stadroid.wallet.data.WalletRepository
import java.math.BigDecimal

class MarketViewModel : ViewModel() {

    val marketName: MutableLiveData<String> = MutableLiveData<String>().apply {
        value = "TIRR_TBTC"//FIXME
    }

    val allMarkets: LiveData<List<Market>> = MarketRepository.getMarkets()

    val market: LiveData<Market> = switchMap(marketName) {
        MarketRepository.getMarket(it)
    }
    val last: LiveData<MarketLast> = switchMap(marketName) {
        MarketRepository.getMarketLast(it)
    }
    val summary: LiveData<MarketSummary> = switchMap(marketName) { MarketRepository.getMarketSummary(it) }
    val status: LiveData<MarketStatus> = switchMap(marketName) { MarketRepository.getMarketStatus(it) }
    val book: LiveData<BookResponse> = switchMap(marketName) { MarketRepository.getBook(it) }
    val marketDeals: LiveData<List<MarketDeal>> = switchMap(marketName) { MarketRepository.getMarketDeals(it) }
    val myDeals: LiveData<List<MyDeal>> = switchMap(marketName) { MarketRepository.getMyDeals(it) }
    val myOrders: LiveData<List<Order>> = switchMap(marketName) { MarketRepository.getMyActiveOrders(it) }
    val kline: LiveData<List<Kline>> = switchMap(marketName) { MarketRepository.getKline48(it) }

    val baseCurrency: LiveData<Currency> = switchMap(market) { WalletRepository.getCurrency(it.baseCurrencySymbol!!) }
    val quoteCurrency: LiveData<Currency> = switchMap(market) { WalletRepository.getCurrency(it.quoteCurrencySymbol!!) }

    val newOrderAmount = MutableLiveData<BigDecimal>().apply { postValue(BigDecimal("0")) }
    val newOrderPrice = MutableLiveData<BigDecimal>().apply { postValue(BigDecimal("0")) }
    val newOrderType = MutableLiveData<String>().apply { postValue("limit") }

}