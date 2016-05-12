package com.innovation4you.napking.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SearchResult implements Serializable {

	public String name;
	public int occupancy;
	public int distance;
	public int drivingDuration;
	public long restStopId;

	public SearchResult(String name, int occupancy, int distance, int drivingDuration, long restStopId) {
		this.name = name;
		this.occupancy = occupancy;
		this.distance = distance;
		this.drivingDuration = drivingDuration;
		this.restStopId = restStopId;
	}

	public Date calculateArrivalTime() {
		final Calendar now = new GregorianCalendar();
		now.add(Calendar.MINUTE, drivingDuration);
		return now.getTime();
	}
}
