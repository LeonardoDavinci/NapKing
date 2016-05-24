package com.innovation4you.napking.model

import java.util.*

class RestStop(var id: Long, var name: String, var occupancies: List<OccupancyEntry>, var lat: Double, var lng: Double) {
    var parkingSpaces: Int = 0
    var facilities: Array<String>? = null

    fun getOccupancyAtTime(calendar: Calendar): Int {
        val weekday = calendar.get(Calendar.DAY_OF_WEEK) - 2
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        for (oe in occupancies) {
            if (oe.weekDay == weekday && oe.hour == hour) {
                return oe.occupancy
            }
        }
        return 0
    }

    override fun toString(): String {
        return name
    }
}
