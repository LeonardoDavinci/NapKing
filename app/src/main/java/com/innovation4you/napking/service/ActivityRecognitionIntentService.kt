package com.innovation4you.napking.service

/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import com.innovation4you.napking.event.ActivityDetectedEvent
import org.greenrobot.eventbus.EventBus

/**
 * IntentService for handling incoming intents that are generated as a result of requesting
 * activity updates using
 * [com.google.android.gms.location.ActivityRecognitionApi.requestActivityUpdates].
 */
class ActivityRecognitionIntentService : IntentService(ActivityRecognitionIntentService.TAG) {

    override fun onCreate() {
        super.onCreate()
    }

    /**
     * Handles incoming intents.

     * @param intent The Intent is provided (inside a PendingIntent) when requestActivityUpdates()
     * *               is called.
     */
    override fun onHandleIntent(intent: Intent) {
        val result = ActivityRecognitionResult.extractResult(intent)

        // Log each activity.
        Log.d(TAG, "activities detected")
        for (da in result.probableActivities) {
            Log.i(TAG, getActivityName(da.type) + " " + da.confidence + "%")
        }
        EventBus.getDefault().postSticky(ActivityDetectedEvent(result.probableActivities))
    }

    private fun getActivityName(activityType: Int): String {
        when (activityType) {
            DetectedActivity.IN_VEHICLE -> return "in_vehicle"
            DetectedActivity.ON_BICYCLE -> return "on_bicycle"
            DetectedActivity.ON_FOOT -> return "on_foot"
            DetectedActivity.RUNNING -> return "running"
            DetectedActivity.STILL -> return "still"
            DetectedActivity.TILTING -> return "tilting"
            DetectedActivity.UNKNOWN -> return "unknown"
            DetectedActivity.WALKING -> return "walking"
            else -> return "unidentifiable activity " + activityType
        }
    }

    companion object {

        protected val TAG = "DetectedActivitiesIS"
    }
}// Use the TAG to name the worker thread.