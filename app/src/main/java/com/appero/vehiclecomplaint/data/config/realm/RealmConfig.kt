package com.appero.vehiclecomplaint.data.config.realm

import android.content.Context
import android.util.Log
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.exceptions.RealmException


class RealmConfig {
    private val realmSchemaVersion = 9L
    private val realmDbPassword = "66b167524b39b89d4a7f1ef4b6ea7e40b0764e5026bfefdae6254bcf5f0d332c"

    fun getRealm(context: Context): Realm {
        try {
            Realm.init(context)
            val config: RealmConfiguration = RealmConfiguration.Builder()
                .name("com.vehicle.realm")
                .schemaVersion(realmSchemaVersion)
                .encryptionKey(realmDbPassword.toByteArray())
                .deleteRealmIfMigrationNeeded()
                .build()
            Realm.setDefaultConfiguration(config)
            return Realm.getDefaultInstance()
        } catch (e: RealmException) {
            Log.e("RealmHelper=", "getRealm: Error initializing Realm : ${e.message}", )
            throw e
        }
    }
}