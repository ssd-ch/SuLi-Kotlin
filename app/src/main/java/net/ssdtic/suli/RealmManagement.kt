package net.ssdtic.suli

import io.realm.RealmConfiguration

class RealmManagement {

    companion object {
        val config = RealmConfiguration.Builder().build()
    }
}