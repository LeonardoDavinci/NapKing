package com.innovation4you.napking.data

import com.google.android.gms.maps.model.LatLng
import com.innovation4you.napking.data.provider.INapKingDataProvider
import com.innovation4you.napking.model.RestStop
import com.innovation4you.napking.model.SearchResult

class NapKingRepository private constructor(private val dataProvider: INapKingDataProvider) : INapKingDataProvider {


    override fun findRestStops(condition: String): List<RestStop> {
        return dataProvider.findRestStops(condition)
    }

    override fun search(source: LatLng, destination: LatLng, minutesLeft: Int): List<SearchResult> {
        return dataProvider.search(source, destination, minutesLeft)
    }

    override fun findRestStopById(id: Long): RestStop? {
        return dataProvider.findRestStopById(id)
    }

    companion object Factory {

        private lateinit var instance: NapKingRepository;

        fun init(dataProvider: INapKingDataProvider) {
            instance = NapKingRepository(dataProvider)
        }

        fun get(): NapKingRepository {
            return instance
        }
    }
}
