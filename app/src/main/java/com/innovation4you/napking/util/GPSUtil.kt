package com.innovation4you.napking.util

object GPSUtil {

    /*
	 * Calculate distance between two points in latitude and longitude taking
	 * into account height difference. If you are not interested in height
	 * difference pass 0.0. Uses Haversine method as its base.
	 *
	 * lat1, lng1 Start point lat2, lng2 End point
	 * @returns Distance in Meters
	 */
    fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {

        val R = 6371 // Radius of the earth

        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lng2 - lng1)
        val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return R.toDouble() * c * 1000.0
    }

    fun claculateDrivingDuration(distance: Double, speed: Int): Int {
        return (distance / 1000.0 / speed * 60.0).toInt()
    }
}
