package com.stacrypt.stadroid.market

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.stacrypt.stadroid.data.Kline
import com.stacrypt.stadroid.data.MarketRepository

class MarketViewModel: ViewModel(){
    val kline: LiveData<List<Kline>> = MarketRepository.getKline("btc/usd")
}