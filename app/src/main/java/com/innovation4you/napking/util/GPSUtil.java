package com.innovation4you.napking.util;

public class GPSUtil {

	/*
	 * Calculate distance between two points in latitude and longitude taking
	 * into account height difference. If you are not interested in height
	 * difference pass 0.0. Uses Haversine method as its base.
	 *
	 * lat1, lng1 Start point lat2, lng2 End point
	 * @returns Distance in Meters
	 */
	public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {

		final int R = 6371; // Radius of the earth

		Double latDistance = Math.toRadians(lat2 - lat1);
		Double lonDistance = Math.toRadians(lng2 - lng1);
		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
				* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return R * c * 1000;
	}

	public static int claculateDrivingDuration(final double distance, final int speed) {
		return (int) ((distance / 1000d) / speed * 60d);
	}
}
