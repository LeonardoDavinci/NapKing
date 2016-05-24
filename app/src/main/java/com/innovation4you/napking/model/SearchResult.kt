package com.innovation4you.napking.model

import java.io.Serializable
import java.util.*

class SearchResult(var name: String, var occupancy: Int, var distanceFromSource: Int, var distanceToDestination: Int, var drivingDuration: Int, var restStopId: Long) : Comparable<SearchResult>, Serializable {

    fun calculateArrivalTime(): Date {
        val now = GregorianCalendar()
        now.add(Calendar.MINUTE, drivingDuration)
        return now.time
    }

    override fun compareTo(another: SearchResult): Int {
        var compare = compareInt(distanceToDestination, another.distanceToDestination)
        if (compare == 0) {
            compare = compareInt(occupancy, another.occupancy)
        }
        return compare
    }

    companion object {

        fun compareInt(lhs: Int, rhs: Int): Int {
            return if (lhs < rhs) -1 else if (lhs == rhs) 0 else 1
        }
    }
}
