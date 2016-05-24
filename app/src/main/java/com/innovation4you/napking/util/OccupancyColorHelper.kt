package com.innovation4you.napking.util

import android.content.Context
import android.support.v4.content.ContextCompat

import com.innovation4you.napking.R

class OccupancyColorHelper(context: Context) {

    internal var occupancyLevelColors = IntArray(3)

    init {
        occupancyLevelColors[0] = ContextCompat.getColor(context, R.color.occupancy_level_low)
        occupancyLevelColors[1] = ContextCompat.getColor(context, R.color.occupancy_level_middle)
        occupancyLevelColors[2] = ContextCompat.getColor(context, R.color.occupancy_level_high)
    }

    fun getOccupancyColor(occupancy: Int): Int {
        var level = 0
        if (occupancy >= 65) {
            level = 2
        } else if (occupancy >= 35) {
            level = 1
        }
        return occupancyLevelColors[level]
    }
}
