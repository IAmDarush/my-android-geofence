package com.example.myandroidgeofence;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

public class AreWeThereIntentService extends IntentService {

    // region Properties

    private final String TAG = AreWeThereIntentService.class.getName();

    private SharedPreferences prefs;
    private Gson gson;

    // endregion

    // region Constructors

    public AreWeThereIntentService() {
        super("AreWeThereIntentService");
    }

    // endregion

    // region Overrides

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    // endregion

    // region Private

    private void onEnteredGeofences(List<String> geofenceIds) {

    }

    private void onError(int i) {
        Log.e(TAG, "Geofencing Error: " + i);
    }

    // endregion
}

