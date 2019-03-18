package io.stacrypt.stadroid.market

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewOrderViewModel : ViewModel() {
    val price = MutableLiveData<Long>()
    val amount = MutableLiveData<Long>()
}
