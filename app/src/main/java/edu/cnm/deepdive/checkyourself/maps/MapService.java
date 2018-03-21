package edu.cnm.deepdive.checkyourself.maps;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import edu.cnm.deepdive.checkyourself.MainActivity;
import edu.cnm.deepdive.checkyourself.R;

public class MapService extends IntentService {

  private static final String IDENTIFIER = "LocationAlertIS";

  public MapService() {
    super(IDENTIFIER);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
    if (geofencingEvent.hasError()) {
      Log.e(IDENTIFIER, "" + getErrorString(geofencingEvent.getErrorCode()));
      return;
    }
    Log.i(IDENTIFIER, geofencingEvent.toString());
    int geofenceTransition = geofencingEvent.getGeofenceTransition();
    if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
        geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
      notifyLocationAlert();
    }
  }

  private String getErrorString(int errorCode) {
    switch (errorCode) {
      case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
        return "Geofence not available";
      case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
        return "geofence too many_geofences";
      case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
        return "geofence too many pending_intents";
      default:
        return "geofence error";
    }
  }

  private void notifyLocationAlert() {
    String CHANNEL_ID = "Check Yourself";
    NotificationCompat.Builder builder =
        new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_ic)
            .setContentTitle("Budget Left!")
            .setContentText(
                String.format("You have $%.2f to spend on %s this month", 100.45, "Food"));
    builder.setAutoCancel(true);
    NotificationManager mNotificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    mNotificationManager.notify(0, builder.build());

  }
}