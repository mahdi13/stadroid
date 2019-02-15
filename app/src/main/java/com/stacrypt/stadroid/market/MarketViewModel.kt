package com.stacrypt.stadroid.market

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.stacrypt.stadroid.data.*

class MarketViewModel : ViewModel() {

    val currentMarket = MutableLiveData<String>()

    val market: LiveData<List<Market>> = MarketRepository.getMarkets()

    val kline: LiveData<List<Kline>> = Transformations.switchMap(currentMarket) { market ->
        MarketRepository.getKline(market)
    }

    val book: LiveData<List<Book>> = Transformations.switchMap(currentMarket) { market ->
        MarketRepository.getBook(market)
    }

    val deal: LiveData<List<Deal>> = Transformations.switchMap(currentMarket) { market ->
        MarketRepository.getDeal(market)
    }

    val mine: LiveData<List<Mine>> = Transformations.switchMap(currentMarket) { market ->
        MarketRepository.getMine(market)
    }

}