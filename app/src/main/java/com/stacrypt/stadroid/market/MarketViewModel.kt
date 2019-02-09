package com.stacrypt.stadroid.market

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.stacrypt.stadroid.data.*

class MarketViewModel: ViewModel(){
    val market: LiveData<List<Market>> = MarketRepository.getMarkets()
    val kline: LiveData<List<Kline>> = MarketRepository.getKline("btc/usd")
    val book: LiveData<List<Book>> = MarketRepository.getBook("btc/usd")
    val deal: LiveData<List<Deal>> = MarketRepository.getDeal("btc/usd")
    val mine: LiveData<List<Mine>> = MarketRepository.getMine("btc/usd")
}