package edu.cnm.deepdive.checkyourself;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
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

public class LocationAlertActivity extends AppCompatActivity
    implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener,
    com.google.android.gms.location.LocationListener {

  private static final int LOC_PERM_REQ_CODE = 1;
  //meters
  private static final int GEOFENCE_RADIUS = 500;
  //in milli seconds
  private static final int GEOFENCE_EXPIRATION = 600;

  private GoogleMap mMap;

  private GeofencingClient geofencingClient;
  double latitude;
  double longitude;
  private int PROXIMITY_RADIUS = 10000;
  GoogleApiClient mGoogleApiClient;
  Location mLastLocation;
  Marker mCurrLocationMarker;
  LocationRequest mLocationRequest;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_location_alert);

    if (!CheckGooglePlayServices()) {
      Log.d("onCreate", "Finishing test case since Google Play Services are not available");
      finish();
    }
    else {
      Log.d("onCreate","Google Play Services available.");
    }

    Toolbar tb = findViewById(R.id.toolbar);
    setSupportActionBar(tb);
    tb.setSubtitle("Location Alert");

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

    Button btnRestaurant = (Button) findViewById(R.id.btnRestaurant);
    btnRestaurant.setOnClickListener(new View.OnClickListener() {
      String Restaurant = "restaurant";
      @Override
      public void onClick(View v) {
        Log.d("onClick", "Button is Clicked");
        mMap.clear();
        String url = getUrl(latitude, longitude, Restaurant);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        Log.d("onClick", url);
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        getNearbyPlacesData.execute(DataTransfer);
        Toast.makeText(LocationAlertActivity.this,"Nearby Restaurants", Toast.LENGTH_LONG).show();
      }
    });

    Button btnHospital = (Button) findViewById(R.id.btnHospital);
    btnHospital.setOnClickListener(new View.OnClickListener() {
      String Hospital = "hospital";
      @Override
      public void onClick(View v) {
        Log.d("onClick", "Button is Clicked");
        mMap.clear();
        String url = getUrl(latitude, longitude, Hospital);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        Log.d("onClick", url);
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        getNearbyPlacesData.execute(DataTransfer);
        Toast.makeText(LocationAlertActivity.this,"Nearby Hospitals", Toast.LENGTH_LONG).show();
      }
    });

    Button btnSchool = (Button) findViewById(R.id.btnSchool);
    btnSchool.setOnClickListener(new View.OnClickListener() {
      String School = "school";
      @Override
      public void onClick(View v) {
        Log.d("onClick", "Button is Clicked");
        mMap.clear();
        if (mCurrLocationMarker != null) {
          mCurrLocationMarker.remove();
        }
        String url = getUrl(latitude, longitude, School);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        Log.d("onClick", url);
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        getNearbyPlacesData.execute(DataTransfer);
        Toast.makeText(LocationAlertActivity.this,"Nearby Schools", Toast.LENGTH_LONG).show();
      }
    });


    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
      @Override
      public void onMapClick(LatLng latLng) {
        addLocationAlert(latLng.latitude, latLng.longitude);

      }
    });
  }

  @SuppressLint("MissingPermission")
  private void showCurrentLocationOnMap() {
    if (isLocationAccessPermitted()) {
      requestLocationAccessPermission();
    } else if (mMap != null) {
      if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
          != PackageManager.PERMISSION_GRANTED
          && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
          != PackageManager.PERMISSION_GRANTED) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return;
      }
      mMap.setMyLocationEnabled(true);
    }
  }

  private boolean isLocationAccessPermitted() {
    if (ContextCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      return true;
    } else {
      return false;
    }
  }

  private void requestLocationAccessPermission() {
    ActivityCompat.requestPermissions(this,
        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
        LOC_PERM_REQ_CODE);
  }

  @SuppressLint("MissingPermission")
  private void addLocationAlert(double lat, double lng) {
    if (isLocationAccessPermitted()) {
      requestLocationAccessPermission();
    } else {
      String key = "" + lat + "-" + lng;
      Geofence geofence = getGeofence(lat, lng, key);
      if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
          != PackageManager.PERMISSION_GRANTED) {
        Log.e("LocationAlertActivity", "Access not granted");
        return;
      }
      geofencingClient.addGeofences(getGeofencingRequest(geofence),
          getGeofencePendingIntent())
          .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
              if (task.isSuccessful()) {
                Toast.makeText(LocationAlertActivity.this,
                    "Location alter has been added",
                    Toast.LENGTH_SHORT).show();
              } else {
                Toast.makeText(LocationAlertActivity.this,
                    "Location alter could not be added",
                    Toast.LENGTH_SHORT).show();
              }
            }
          });
    }
  }
  private void removeLocationAlert(){
    if (isLocationAccessPermitted()) {
      requestLocationAccessPermission();
    } else {
      geofencingClient.removeGeofences(getGeofencePendingIntent())
          .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
              if (task.isSuccessful()) {
                Toast.makeText(LocationAlertActivity.this,
                    "Location alters have been removed",
                    Toast.LENGTH_SHORT).show();
              }else{
                Toast.makeText(LocationAlertActivity.this,
                    "Location alters could not be removed",
                    Toast.LENGTH_SHORT).show();
              }
            }
          });
    }
  }
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case LOC_PERM_REQ_CODE: {
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          showCurrentLocationOnMap();
          Toast.makeText(LocationAlertActivity.this,
              "Location access permission granted, you try " +
                  "add or remove location allerts",
              Toast.LENGTH_SHORT).show();
        }
        return;
      }

    }
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
        .setExpirationDuration(Geofence.NEVER_EXPIRE)
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
            Geofence.GEOFENCE_TRANSITION_DWELL)
        .setLoiteringDelay(1000)
        .build();
  }
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu, menu);
    return true;
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.remove_loc_alert:
        removeLocationAlert();
        return true;
      default:
        return super.onOptionsItemSelected(item);

    }
  }

  private boolean CheckGooglePlayServices() {
    GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
    int result = googleAPI.isGooglePlayServicesAvailable(this);
    if(result != ConnectionResult.SUCCESS) {
      if(googleAPI.isUserResolvableError(result)) {
        googleAPI.getErrorDialog(this, result,
            0).show();
      }
      return false;
    }
    return true;
  }

  protected synchronized void buildGoogleApiClient() {
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();
    mGoogleApiClient.connect();
  }

  private String getUrl(double latitude, double longitude, String nearbyPlace) {
    StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
    googlePlacesUrl.append("location=" + latitude + "," + longitude);
    googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
    googlePlacesUrl.append("&type=" + nearbyPlace);
    googlePlacesUrl.append("&sensor=true");
    googlePlacesUrl.append("&key=" + "AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0");
    Log.d("getUrl", googlePlacesUrl.toString());
    return (googlePlacesUrl.toString());
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(1000);
    mLocationRequest.setFastestInterval(1000);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    if (ContextCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }

  @Override
  public void onLocationChanged(Location location) {
    Log.d("onLocationChanged", "entered");

    mLastLocation = location;
    if (mCurrLocationMarker != null) {
      mCurrLocationMarker.remove();
    }

    //Place current location marker
    latitude = location.getLatitude();
    longitude = location.getLongitude();
    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.position(latLng);
    markerOptions.title("Current Position");
    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
    mCurrLocationMarker = mMap.addMarker(markerOptions);

    //move map camera
    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    Toast.makeText(LocationAlertActivity.this,"Your Current Location", Toast.LENGTH_LONG).show();

    Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f",latitude,longitude));

    //stop location updates
    if (mGoogleApiClient != null) {
      LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
      Log.d("onLocationChanged", "Removing Location Updates");
    }
    Log.d("onLocationChanged", "Exit");
  }

}