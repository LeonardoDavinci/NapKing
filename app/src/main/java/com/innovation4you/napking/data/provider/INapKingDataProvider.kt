package com.innovation4you.napking.data.provider

import com.google.android.gms.maps.model.LatLng
import com.innovation4you.napking.model.RestStop
import com.innovation4you.napking.model.SearchResult

interface INapKingDataProvider {

    fun findRestStops(condition: String): List<RestStop>

    fun search(source: LatLng, destination: LatLng, minutesLeft: Int): List<SearchResult>

    fun findRestStopById(id: Long): RestStop?
}
