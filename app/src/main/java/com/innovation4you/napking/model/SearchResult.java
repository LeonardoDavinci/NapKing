package com.innovation4you.napking.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SearchResult implements Comparable<SearchResult>, Serializable {

	public String name;
	public int occupancy;
	public int distanceFromSource;
	public int distanceToDestination;
	public int drivingDuration;
	public long restStopId;

	public SearchResult(String name, int occupancy, int distanceFromSource, int distanceToDestination, int drivingDuration, long
			restStopId) {
		this.name = name;
		this.occupancy = occupancy;
		this.distanceFromSource = distanceFromSource;
		this.distanceToDestination = distanceToDestination;
		this.drivingDuration = drivingDuration;
		this.restStopId = restStopId;
	}

	public Date calculateArrivalTime() {
		final Calendar now = new GregorianCalendar();
		now.add(Calendar.MINUTE, drivingDuration);
		return now.getTime();
	}

	@Override
	public int compareTo(SearchResult another) {
		int compare = compareInt(distanceToDestination, another.distanceToDestination);
		if (compare == 0) {
			compare = compareInt(occupancy, another.occupancy);
		}
		return compare;
	}

	public static int compareInt(int lhs, int rhs) {
		return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
	}
}
