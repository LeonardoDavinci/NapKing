package com.innovation4you.napking.model;

import android.support.annotation.NonNull;

import java.util.Locale;

public class OccupancyEntry implements Comparable<OccupancyEntry> {

	public int weekDay;

	public int hour;

	public int minute;

	public int occupancy;

	public OccupancyEntry(int weekDay, int hour, int occupancy) {
		this.weekDay = weekDay;
		this.hour = hour;
		this.occupancy = occupancy;
	}

	@Override
	public int compareTo(@NonNull OccupancyEntry another) {
		int compare = compareInt(weekDay, another.weekDay);
		if (compare == 0) {
			compare = compareInt(hour, another.hour);
			if (compare == 0) {
				compare = compareInt(minute, another.minute);
			}
		}
		return compare;
	}

	private static int compareInt(int lhs, int rhs) {
		return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
	}

	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "%d-%d: %d", weekDay, hour, occupancy);
	}
}
