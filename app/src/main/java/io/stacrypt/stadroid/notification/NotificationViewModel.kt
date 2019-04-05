package io.stacrypt.stadroid.notification

import androidx.lifecycle.ViewModel

class NotificationViewModel : ViewModel() {

    val notificationCount = NotificationRepository.getNotificationsCount()

    private val notificationsListing = NotificationRepository.getNotifications()
    val notifications = notificationsListing.pagedList
    val networkState = notificationsListing.networkState
    val refreshState = notificationsListing.refreshState
}