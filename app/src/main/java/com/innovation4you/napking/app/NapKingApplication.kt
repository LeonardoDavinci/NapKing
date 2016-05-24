package com.innovation4you.napking.app

import android.app.Application

import com.innovation4you.napking.data.NapKingRepository
import com.innovation4you.napking.data.provider.NapKingStaticDataProvider

class NapKingApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        NapKingRepository.init(NapKingStaticDataProvider(this))
    }
}
