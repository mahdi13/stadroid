package com.stacrypt.stadroid.profile.verification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.stacrypt.stadroid.data.Evidence
import com.stacrypt.stadroid.data.User
import com.stacrypt.stadroid.data.UserRepository

class VerificationProcessViewModel : ViewModel() {

    val client: LiveData<User> by lazy { UserRepository.getMe() }
    val evidence: LiveData<Evidence> by lazy { UserRepository.getMyEvidences() }

}
