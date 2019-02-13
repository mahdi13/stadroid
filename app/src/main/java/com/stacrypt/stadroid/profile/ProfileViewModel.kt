package com.stacrypt.stadroid.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.stacrypt.stadroid.data.*

class ProfileViewModel : ViewModel() {
    val user: LiveData<User> = UserRepository.getMe()
}