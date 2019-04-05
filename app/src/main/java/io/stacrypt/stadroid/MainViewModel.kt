package io.stacrypt.stadroid

import androidx.lifecycle.ViewModel
import io.stacrypt.stadroid.data.UserRepository
import io.stacrypt.stadroid.notification.NotificationRepository

class MainViewModel: ViewModel(){
    val notificationsCount  = NotificationRepository.getNotificationsCount()
}