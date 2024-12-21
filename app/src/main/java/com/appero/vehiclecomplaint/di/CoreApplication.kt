package com.appero.vehiclecomplaint.di

import android.app.Application
import com.appero.vehiclecomplaint.data.config.realm.RealmConfig
import io.realm.Realm

class CoreApplication: Application() {
    val appContainer by lazy { CoreInjection() }

    lateinit var realm: Realm

    override fun onCreate() {
        super.onCreate()

        val realmHelper = RealmConfig()
        realm = realmHelper.getRealm(this)
    }
}