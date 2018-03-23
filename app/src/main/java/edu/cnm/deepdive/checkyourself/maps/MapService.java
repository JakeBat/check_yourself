package edu.cnm.deepdive.checkyourself.maps;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.util.Log;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import edu.cnm.deepdive.checkyourself.R;
import edu.cnm.deepdive.checkyourself.UniDatabase;
import edu.cnm.deepdive.checkyourself.models.Record.Display;
import edu.cnm.deepdive.checkyourself.models.Total;
import java.util.List;

/**
 * A service that tracks if a <code>Geofence</code> is triggering
 * and sends the user a notification if certain conditions are met.
 *
 * @author Jake Batchelor
 */
public class MapService extends IntentService {

  private static final String IDENTIFIER = "LocationAlertIS";

  /**
   * Default constructor for the fragment.
   */
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
    List<Display> sums = UniDatabase.getInstance(getApplicationContext()).recordDao().getSums();
    List<Total> totals = UniDatabase.getInstance(getApplicationContext()).totalDao().getAll();
    String CHANNEL_ID = "Check Yourself";
    NotificationCompat.Builder builder =
        new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_ic)
            .setContentTitle("Just a reminder! Here's what you have left for the month:")
            .setContentText(
                "Just a Reminder! Here's what you have left for the month:")
            .setStyle(new BigTextStyle().bigText(String.format("Food: $%.2f%n"
                    + "Monthly: $%.2f%n"
                    + "Entertainment: $%.2f%n"
                    + "Misc: $%.2f%n",
                totals.get(0).getTotal() + sums.get(0).getAmount(),
                totals.get(1).getTotal() + sums.get(1).getAmount(),
                totals.get(2).getTotal() + sums.get(2).getAmount(),
                totals.get(3).getTotal() + sums.get(3).getAmount())));
    builder.setAutoCancel(true);
    NotificationManager mNotificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    mNotificationManager.notify(0, builder.build());

  }
}