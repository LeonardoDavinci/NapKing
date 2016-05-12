package com.innovation4you.napking.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.innovation4you.napking.R;

public class OccupancyColorHelper {

	int[] occupancyLevelColors = new int[3];

	public OccupancyColorHelper(final Context context) {
		occupancyLevelColors[0] = ContextCompat.getColor(context, R.color.occupancy_level_low);
		occupancyLevelColors[1] = ContextCompat.getColor(context, R.color.occupancy_level_middle);
		occupancyLevelColors[2] = ContextCompat.getColor(context, R.color.occupancy_level_high);
	}

	public int getOccupancyColor(final int occupancy) {
		int level = 0;
		if (occupancy >= 65) {
			level = 2;
		} else if (occupancy >= 35) {
			level = 1;
		}
		return occupancyLevelColors[level];
	}
}
