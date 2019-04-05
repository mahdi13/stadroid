package io.stacrypt.stadroid.profile.verification

import androidx.lifecycle.Event
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.stacrypt.stadroid.data.Evidence
import io.stacrypt.stadroid.data.User
import io.stacrypt.stadroid.data.UserRepository

class VerificationProcessViewModel : ViewModel() {

    val client: LiveData<User> by lazy { UserRepository.getMe() }
    val evidence: LiveData<Evidence> by lazy { UserRepository.getMyEvidences() }

    val mobilePhoneSmsSent: MutableLiveData<Event<String>> = MutableLiveData()
    val doMobilePhoneSmsExpired: MutableLiveData<Event<String>> = MutableLiveData()
    val doMobilePhoneSmsVerified: MutableLiveData<Event<String>> = MutableLiveData()
}
