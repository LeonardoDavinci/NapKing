package com.innovation4you.napking.model;

import java.util.Calendar;
import java.util.List;

public class RestStop {

	public long id;
	public String name;
	public double lat, lng;
	public int parkingSpaces;
	public String[] facilities;
	public List<OccupancyEntry> occupancies;

	public RestStop(final long id, final String name, final List<OccupancyEntry> occupancies, final double lat, final double lng) {
		this.id = id;
		this.name = name;
		this.occupancies = occupancies;
		this.lat = lat;
		this.lng = lng;
	}

	public int getOccupancyAtTime(final Calendar calendar) {
		final int weekday = calendar.get(Calendar.DAY_OF_WEEK) - 2;
		final int hour = calendar.get(Calendar.HOUR_OF_DAY);

		for (OccupancyEntry oe : occupancies) {
			if (oe.weekDay == weekday && oe.hour == hour) {
				return oe.occupancy;
			}
		}
		return 0;
	}

	@Override
	public String toString() {
		return name;
	}
}
