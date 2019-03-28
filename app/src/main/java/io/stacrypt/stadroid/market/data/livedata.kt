package io.stacrypt.stadroid.market.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.switchMap
import io.stacrypt.stadroid.data.*

class MarketLiveData(val marketName: String) : LiveData<Market>() {

    init {
        MarketRepository.getMarket(marketName)
    }

    val last: LiveData<MarketLast> by lazy { MarketRepository.getMarketLast(marketName) }

    val summary: LiveData<MarketSummary> by lazy { MarketRepository.getMarketSummary(marketName) }

    val status: LiveData<MarketStatus> by lazy { MarketRepository.getMarketStatus(marketName) }

    val book: LiveData<BookResponse> by lazy { MarketRepository.getBook(marketName) }

    val deal: LiveData<List<Deal>> by lazy { MarketRepository.getDeal(marketName) }

    val mine: LiveData<List<Mine>> by lazy { MarketRepository.getMineOverview(marketName) }

    val myActiveOrders: LiveData<List<Order>> by lazy { MarketRepository.getMyActiveOrders(marketName) }

    val kline: LiveData<List<Kline>> by lazy { MarketRepository.getKline(marketName) }

}