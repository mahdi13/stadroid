package com.stacrypt.stadroid.market

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.stacrypt.stadroid.data.*

class MarketViewModel : ViewModel() {

    val currentMarketName: MutableLiveData<String> = MutableLiveData<String>()

    val marketList: LiveData<List<Market>> = MarketRepository.getMarkets()

    val currentMarket: LiveData<Market> = Transformations.switchMap(currentMarketName) { market ->
        MarketRepository.getMarket(market)
    }

    val kline: LiveData<List<Kline>> = Transformations.switchMap(currentMarketName) { market ->
        MarketRepository.getKline(market)
    }

    val book: LiveData<List<Book>> = Transformations.switchMap(currentMarketName) { market ->
        MarketRepository.getBook(market)
    }

    val deal: LiveData<List<Deal>> = Transformations.switchMap(currentMarketName) { market ->
        MarketRepository.getDeal(market)
    }

    val mine: LiveData<List<Mine>> = Transformations.switchMap(currentMarketName) { market ->
        MarketRepository.getMine(market)
    }

}