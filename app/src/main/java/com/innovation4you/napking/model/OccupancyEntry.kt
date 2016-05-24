package com.innovation4you.napking.model

import java.util.*

class OccupancyEntry(var weekDay: Int, var hour: Int, var occupancy: Int) : Comparable<OccupancyEntry> {

    var minute: Int = 0

    override fun compareTo(another: OccupancyEntry): Int {
        var compare = compareInt(weekDay, another.weekDay)
        if (compare == 0) {
            compare = compareInt(hour, another.hour)
            if (compare == 0) {
                compare = compareInt(minute, another.minute)
            }
        }
        return compare
    }

    private fun compareInt(lhs: Int, rhs: Int): Int {
        return if (lhs < rhs) -1 else if (lhs == rhs) 0 else 1
    }

    override fun toString(): String {
        return String.format(Locale.ENGLISH, "%d-%d: %d", weekDay, hour, occupancy)
    }
}
