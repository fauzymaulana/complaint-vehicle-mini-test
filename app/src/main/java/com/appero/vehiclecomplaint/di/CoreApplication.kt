package com.appero.vehiclecomplaint.di

import android.app.Application

class CoreApplication: Application() {
    val appContainer by lazy { CoreInjection() }
}