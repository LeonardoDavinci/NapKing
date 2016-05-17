package com.innovation4you.napking.event;

import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

public class ActivityDetectedEvent {

	public final ArrayList<DetectedActivity> detectedActivities;

	public ActivityDetectedEvent(ArrayList<DetectedActivity> detectedActivities) {
		this.detectedActivities = detectedActivities;
	}
}
