package com.innovation4you.napking.data.provider

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.innovation4you.napking.app.Cfg
import com.innovation4you.napking.model.RestStop
import com.innovation4you.napking.model.SearchResult
import com.innovation4you.napking.util.GPSUtil
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

open class NapKingJSONDataProvider(context: Context) : INapKingDataProvider {

    protected lateinit var restStops: ArrayList<RestStop>

    init {
        loadData(context)
    }

    protected open fun loadData(context: Context) {
        val gson = GsonBuilder().create()
        val listType = object : TypeToken<ArrayList<RestStop>>() {

        }.type
        try {
            restStops = gson.fromJson<ArrayList<RestStop>>(InputStreamReader(context.assets.open("data.json")), listType)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun findRestStops(condition: String): List<RestStop> {
        var condition = condition
        condition = condition.toLowerCase()

        val result = ArrayList<RestStop>()
        for (restStop in restStops) {
            if (restStop.name.toLowerCase().contains(condition)) {
                result.add(restStop)
            }
        }
        return result
    }

    override fun search(source: LatLng, destination: LatLng, minutesLeft: Int): List<SearchResult> {
        val result = ArrayList<SearchResult>()
        var now: Calendar
        var distanceFromSource: Double
        var distanceToDestination: Double
        var drivingDuration: Int
        Log.d("Search", "from: " + source.toString() + " to: " + destination.toString())

        for (restStop in restStops) {
            distanceFromSource = GPSUtil.calculateDistance(restStop.lat, restStop.lng, source.latitude, source.longitude)
            drivingDuration = GPSUtil.claculateDrivingDuration(distanceFromSource, Cfg.AVG_DRIVING_SPEED)
            Log.d("Search", restStop.name + " " + distanceFromSource + "m " + drivingDuration + "min")

            if (drivingDuration <= minutesLeft) {
                distanceToDestination = GPSUtil.calculateDistance(restStop.lat, restStop.lng, destination.latitude, destination.longitude)

                now = GregorianCalendar()
                now.add(Calendar.MINUTE, drivingDuration)
                result.add(SearchResult(restStop.name, restStop.getOccupancyAtTime(now), distanceFromSource.toInt(),
                        distanceToDestination.toInt(), drivingDuration, restStop.id))
            }
        }

        Collections.sort(result)
        return result
    }

    override fun findRestStopById(id: Long): RestStop? {
        for (restStop in restStops) {
            if (restStop.id == id) {
                return restStop
            }
        }
        return null
    }
}
