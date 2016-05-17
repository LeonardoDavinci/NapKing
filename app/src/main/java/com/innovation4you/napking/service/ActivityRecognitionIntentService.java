package com.innovation4you.napking.service;

/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.innovation4you.napking.event.ActivityDetectedEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * IntentService for handling incoming intents that are generated as a result of requesting
 * activity updates using
 * {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates}.
 */
public class ActivityRecognitionIntentService extends IntentService {

	protected static final String TAG = "DetectedActivitiesIS";

	public ActivityRecognitionIntentService() {
		// Use the TAG to name the worker thread.
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	/**
	 * Handles incoming intents.
	 *
	 * @param intent The Intent is provided (inside a PendingIntent) when requestActivityUpdates()
	 *               is called.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

		// Get the list of the probable activities associated with the current state of the
		// device. Each activity is associated with a confidence level, which is an int between
		// 0 and 100.
		final ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

		// Log each activity.
		Log.d(TAG, "activities detected");
		for (DetectedActivity da : detectedActivities) {
			Log.i(TAG, getActivityName(da.getType()) + " " + da.getConfidence() + "%");
		}
		EventBus.getDefault().postSticky(new ActivityDetectedEvent(detectedActivities));
	}

	private String getActivityName(final int activityType) {
		switch (activityType) {
		case DetectedActivity.IN_VEHICLE:
			return "in_vehicle";
		case DetectedActivity.ON_BICYCLE:
			return "on_bicycle";
		case DetectedActivity.ON_FOOT:
			return "on_foot";
		case DetectedActivity.RUNNING:
			return "running";
		case DetectedActivity.STILL:
			return "still";
		case DetectedActivity.TILTING:
			return "tilting";
		case DetectedActivity.UNKNOWN:
			return "unknown";
		case DetectedActivity.WALKING:
			return "walking";
		default:
			return "unidentifiable activity " + activityType;
		}
	}
}