package com.example.myandroidgeofence;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        prefs = getApplicationContext().getSharedPreferences(Constants.SharedPrefs.Geofences, Context.MODE_PRIVATE);
        gson = new Gson();

        // 1. Get the event
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event != null) {
            if (event.hasError()) {
                onError(event.getErrorCode());
            } else {

                // 2. Get the transition type
                int transition = event.getGeofenceTransition();
                if (transition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                        transition == Geofence.GEOFENCE_TRANSITION_DWELL ||
                        transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    List<String> geofenceIds = new ArrayList<>();

                    // 3. Accumulate a list of event geofences
                    for (Geofence geofence : event.getTriggeringGeofences()) {
                        geofenceIds.add(geofence.getRequestId());
                    }
                    if (transition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                            transition == Geofence.GEOFENCE_TRANSITION_DWELL) {
                        // 4. Pass the geofence list to the notification method
                        onEnteredGeofences(geofenceIds);
                    }
                }
            }

        }
    }

    // endregion

    // region Private

    private void onEnteredGeofences(List<String> geofenceIds) {

        // 1. Outer loop over all geofenceIds
        for (String geofenceId : geofenceIds) {
            String geofenceName = "";

            // 2, Loop over all geofence keys in prefs and retrieve NamedGeofence from SharedPreferences
            Map<String, ?> keys = prefs.getAll();
            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                String jsonString = prefs.getString(entry.getKey(), null);
                NamedGeofence namedGeofence = gson.fromJson(jsonString, NamedGeofence.class);
                if (namedGeofence.id.equals(geofenceId)) {
                    geofenceName = namedGeofence.name;
                    break;
                }
            }

            // 3. Set the notification text and send the notification
            String contextText = String.format(this.getResources().getString(R.string.Notification_Text), geofenceName);

            // 1. Create a NotificationManager
            NotificationManager notificationManager =
                    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            // 2. Create a PendingIntent for AllGeofencesActivity
            Intent intent = new Intent(this, AllGeofencesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // 3. Create and send a notification
            Notification notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(this.getResources().getString(R.string.Notification_Title))
                    .setContentText(contextText)
                    .setContentIntent(pendingNotificationIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(contextText))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build();
            notificationManager.notify(0, notification);
        }

    }

    private void onError(int i) {
        Log.e(TAG, "Geofencing Error: " + i);
    }

    // endregion
}

