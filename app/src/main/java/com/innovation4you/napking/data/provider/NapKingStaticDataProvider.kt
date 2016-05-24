package com.innovation4you.napking.data.provider

import android.content.Context
import com.innovation4you.napking.model.OccupancyEntry
import com.innovation4you.napking.model.RestStop
import java.util.*

class NapKingStaticDataProvider(context: Context) : NapKingJSONDataProvider(context) {
    private var random = Random()

    override fun loadData(context: Context) {
        restStops = ArrayList<RestStop>(NUMBER_OF_REST_STOPS)
        for (i in 0..NUMBER_OF_REST_STOPS - 1) {
            restStops.add(createRestStop(i.toLong()))
        }
    }

    private fun createRestStop(id: Long): RestStop {
        val occupancies = ArrayList<OccupancyEntry>()
        for (w in 0..6) {
            for (h in 0..23) {
                occupancies.add(OccupancyEntry(w, h, random.nextInt(100)))
            }
        }
        return RestStop(id, NAMES[random.nextInt(NAMES.size - 1)], occupancies, 48.220778, 16.3100205)
    }

    companion object {

        internal val NUMBER_OF_REST_STOPS = 10

        private val NAMES = arrayOf("Wildewyn", "Falconvale", "Goldmill", "Wyvernhollow", "Shadownesse", "Springhaven", "Corbourne", "Clearbourne", "Deepbush", "Blackford", "Wyvernwynne", "Stronghurst", "Icefall")
    }

}
