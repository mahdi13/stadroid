package com.stacrypt.stadroid

lateinit var application: Application

class Application : android.app.Application() {
    init {
        application = this
    }

}
