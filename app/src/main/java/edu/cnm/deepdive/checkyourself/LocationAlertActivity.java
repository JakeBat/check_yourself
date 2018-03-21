package edu.cnm.deepdive.checkyourself;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import edu.cnm.deepdive.checkyourself.maps.DataParser;
import edu.cnm.deepdive.checkyourself.maps.DownloadUrl;
import edu.cnm.deepdive.checkyourself.maps.LocationAlertIntentService;
import java.util.HashMap;
import java.util.List;

public class LocationAlertActivity extends AppCompatActivity
    implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener,
    LocationListener {

  private static final int PROXIMITY_RADIUS = 1000;
  private static final int GEOFENCE_RADIUS = 50;
  private static final int GEOFENCE_EXPIRATION = 50000;

  private GoogleMap mMap;
  private GeofencingClient geofencingClient;
  private GoogleApiClient mGoogleApiClient;
  private Marker mCurrLocationMarker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_location_alert);
    if (!CheckGooglePlayServices()) {
      Log.d("onCreate", "Finishing test case since Google Play Services are not available");
      finish();
    } else {
      Log.d("onCreate", "Google Play Services available.");
    }
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.g_map);
    mapFragment.getMapAsync(this);
    geofencingClient = LocationServices.getGeofencingClient(this);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mMap.getUiSettings().setCompassEnabled(true);
    mMap.getUiSettings().setZoomControlsEnabled(true);
    showCurrentLocationOnMap();
    buildGoogleApiClient();
  }

  @SuppressLint("MissingPermission")
  private void showCurrentLocationOnMap() {
    if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    mMap.setMyLocationEnabled(true);
  }

  @Override
  public void onLocationChanged(Location location) {
    Log.d("onLocationChanged", "entered");
    if (mCurrLocationMarker != null) {
      mCurrLocationMarker.remove();
    }

    double latitude = location.getLatitude();
    double longitude = location.getLongitude();
    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.position(latLng);
    markerOptions.title("Current Position");
    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
    mCurrLocationMarker = mMap.addMarker(markerOptions);
    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    if (mGoogleApiClient != null) {
      LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
      Log.d("onLocationChanged", "Removing Location Updates");
    }
    Log.d("onLocationChanged", "Exit");
    String url = getUrl(latitude, longitude);
    Object[] DataTransfer = new Object[2];
    DataTransfer[0] = mMap;
    DataTransfer[1] = url;
    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
    getNearbyPlacesData.execute(DataTransfer);
  }

  private String getUrl(double latitude, double longitude) {
    StringBuilder googlePlacesUrl = new StringBuilder(
        "https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
    googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
    googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
    googlePlacesUrl.append("&type=" + "point_of_interest");
    googlePlacesUrl.append("&sensor=true");
    googlePlacesUrl.append("&key=" + "AIzaSyA1aC7-515WVJMGQOB1xMMWKh6UHL1N9GE");
    Log.d("getUrl", googlePlacesUrl.toString());
    return (googlePlacesUrl.toString());
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    LocationRequest mLocationRequest = createLocationRequest();
    if (ContextCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      LocationServices.FusedLocationApi
          .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
  }

  private LocationRequest createLocationRequest() {
    LocationRequest mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(6000);
    mLocationRequest.setFastestInterval(1000);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    return mLocationRequest;
  }

  private PendingIntent getGeofencePendingIntent() {
    Intent intent = new Intent(this, LocationAlertIntentService.class);
    return PendingIntent.getService(this, 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);
  }

  private GeofencingRequest getGeofencingRequest(Geofence geofence) {
    GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
    builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
    builder.addGeofence(geofence);
    return builder.build();
  }

  private Geofence getGeofence(double lat, double lang, String key) {
    return new Geofence.Builder()
        .setRequestId(key)
        .setCircularRegion(lat, lang, GEOFENCE_RADIUS)
        .setExpirationDuration(GEOFENCE_EXPIRATION)
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
            Geofence.GEOFENCE_TRANSITION_DWELL)
        .setLoiteringDelay(1000)
        .build();
  }

  private boolean CheckGooglePlayServices() {
    GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
    int result = googleAPI.isGooglePlayServicesAvailable(this);
    if (result != ConnectionResult.SUCCESS) {
      if (googleAPI.isUserResolvableError(result)) {
        googleAPI.getErrorDialog(this, result,
            0).show();
      }
      return false;
    }
    return true;
  }

  private synchronized void buildGoogleApiClient() {
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();
    mGoogleApiClient.connect();
  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }

  public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;

    @Override
    protected String doInBackground(Object... params) {
      try {
        Log.d("GetNearbyPlacesData", "doInBackground entered");
        mMap = (GoogleMap) params[0];
        url = (String) params[1];
        DownloadUrl downloadUrl = new DownloadUrl();
        googlePlacesData = downloadUrl.readUrl(url);
        Log.d("GooglePlacesReadTask", "doInBackground Exit");
      } catch (Exception e) {
        Log.d("GooglePlacesReadTask", e.toString());
      }
      return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
      Log.d("GooglePlacesReadTask", "onPostExecute Entered");
      List<HashMap<String, String>> nearbyPlacesList;
      DataParser dataParser = new DataParser();
      nearbyPlacesList = dataParser.parse(result);
      ShowNearbyPlaces(nearbyPlacesList);
      Log.d("GooglePlacesReadTask", "onPostExecute Exit");
    }

    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
      for (HashMap<String, String> aNearbyPlacesList : nearbyPlacesList) {
        Log.d("onPostExecute", "Entered into showing locations");
        MarkerOptions markerOptions = new MarkerOptions();
        double lat;
        lat = Double.parseDouble(aNearbyPlacesList.get("lat"));
        double lng;
        lng = Double.parseDouble(aNearbyPlacesList.get("lng"));
        String placeName = aNearbyPlacesList.get("place_name");
        String vicinity = aNearbyPlacesList.get("vicinity");
        String key = "" + lat + "-" + lng;
        Geofence geofence = getGeofence(lat, lng, key);
        if (ActivityCompat
            .checkSelfPermission(getApplicationContext(), permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
          return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(geofence),
            getGeofencePendingIntent())
            .addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {

              }
            });
        LatLng latLng = new LatLng(lat, lng);
        markerOptions.position(latLng);
        markerOptions.title(placeName + " : " + vicinity);
        mMap.addMarker(markerOptions);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
      }
    }
  }
}