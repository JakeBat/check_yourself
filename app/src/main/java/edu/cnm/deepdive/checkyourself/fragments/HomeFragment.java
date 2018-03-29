package edu.cnm.deepdive.checkyourself.fragments;


import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer.GridStyle;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import edu.cnm.deepdive.checkyourself.MainActivity;
import edu.cnm.deepdive.checkyourself.R;
import edu.cnm.deepdive.checkyourself.maps.DataParser;
import edu.cnm.deepdive.checkyourself.maps.DownloadUrl;
import edu.cnm.deepdive.checkyourself.maps.MapService;
import edu.cnm.deepdive.checkyourself.models.Record.Display;
import edu.cnm.deepdive.checkyourself.models.Total;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * <code>HomeFragment</code> displays a graph that shows the remaining percentage of the categories in the
 * budgets. It also contains four text views that display the actual dollar value remaining. The last part
 * of the fragment is the Google Map that display's at the bottom to enable the location services and notification
 * to work as intended, showing all the requested POI's and geofences.
 *
 * @author Jake Batchelor
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback, ConnectionCallbacks,
    OnConnectionFailedListener,
    LocationListener {

  private static final int MAX_VALUE = 100;
  private static final int GREEN_THRESHOLD = 70;
  private static final int RED_THRESHOLD = 30;
  private static final int PROXIMITY_RADIUS = 100;
  private static final int GEOFENCE_RADIUS = 50;
  private static final int GEOFENCE_EXPIRATION = 50000;

  private List<String> labels = new ArrayList<>();
  private TextView foodLeft;
  private TextView monthlyLeft;
  private TextView enterLeft;
  private TextView miscLeft;
  private GoogleMap mMap;
  private GeofencingClient geofencingClient;
  private GoogleApiClient mGoogleApiClient;
  private Marker mCurrLocationMarker;


  /**
   * Default constructor for the fragment.
   */
  public HomeFragment() {

  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_home, container, false);
    graphSetup(view);
    setupAmountsLeft(view);
    if (!CheckGooglePlayServices()) {
      Log.d("onCreate", "Finishing test case since Google Play Services are not available");
      Activity activity = new Activity();
      activity.finish();
    } else {
      Log.d("onCreate", "Google Play Services available.");
    }
    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
        .findFragmentById(R.id.g_map);
    mapFragment.getMapAsync(this);
    geofencingClient = LocationServices.getGeofencingClient(Objects.requireNonNull(getContext()));
    return view;
  }

  /**
   * Sets up the graph for display in the <code>View</code>.
   *
   * @param view <code>View</code> that's inflated in the <code>onCreateView</code> Method
   */
  private void graphSetup(final View view) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        List<Display> sums = ((MainActivity) Objects.requireNonNull(getActivity()))
            .getDatabase(getContext()).recordDao()
            .getSums();
        List<Total> totals = ((MainActivity) getActivity()).getDatabase(getContext())
            .totalDao().getAll();

        GraphView graph = view.findViewById(R.id.graph);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(MAX_VALUE);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(5);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setGridStyle(GridStyle.NONE);

        DataPoint[] dataPoints = new DataPoint[sums.size()];
        for (int i = 0; i < sums.size(); i++) {
          double sum = 100 + ((sums.get(i).getAmount() / totals.get(i).getTotal()) * 100);
          dataPoints[i] = new DataPoint((i + 1), sum);
        }
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoints);
        graph.addSeries(series);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        labels.add("");
        for (Display sum : sums) {
          String label = sum.getTag();
          labels.add(label);
        }
        labels.add("");
        staticLabelsFormatter.setHorizontalLabels(labels.toArray(new String[labels.size()]));

        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);

        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
          @Override
          public int get(DataPoint data) {
            int color;
            double percentValue = ((data.getY() / MAX_VALUE) * 100);
            if (percentValue > GREEN_THRESHOLD) {
              color = Color.GREEN;
            } else if (percentValue < GREEN_THRESHOLD && percentValue > RED_THRESHOLD) {
              color = Color.YELLOW;
            } else {
              color = Color.RED;
            }
            return color;
          }
        });

        series.setSpacing(50);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.WHITE);
        series.setValuesOnTopSize(50);
      }
    }).start();
  }

  /**
   * Sets up the <code>TextView</code>'s displayed in the <code>View</code>.
   *
   * @param view <code>View</code> that's inflated in the <code>onCreateView</code> Method
   */
  private void setupAmountsLeft(View view) {
    foodLeft = view.findViewById(R.id.food_left);
    monthlyLeft = view.findViewById(R.id.monthly_left);
    enterLeft = view.findViewById(R.id.enter_left);
    miscLeft = view.findViewById(R.id.misc_left);

    new Thread(new Runnable() {
      @Override
      public void run() {
        final List<Display> sums = ((MainActivity) Objects.requireNonNull(getActivity()))
            .getDatabase(getContext()).recordDao()
            .getSums();
        final List<Total> totals = ((MainActivity) getActivity()).getDatabase(getContext())
            .totalDao().getAll();
        getActivity().runOnUiThread(new Runnable() {
          @SuppressLint("DefaultLocale")
          @Override
          public void run() {
            if (!sums.isEmpty()) {
              foodLeft.setText(
                  String.format("%.2f", totals.get(0).getTotal() + sums.get(0).getAmount()));
              monthlyLeft.setText(
                  String.format("%.2f", totals.get(1).getTotal() + sums.get(1).getAmount()));
              enterLeft.setText(
                  String.format("%.2f", totals.get(2).getTotal() + sums.get(2).getAmount()));
              miscLeft.setText(
                  String.format("%.2f", totals.get(3).getTotal() + sums.get(3).getAmount()));
            }
          }
        });
      }
    }).start();
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    showCurrentLocationOnMap();
    mMap.getUiSettings().setCompassEnabled(true);
    mMap.getUiSettings().setZoomControlsEnabled(true);
    buildGoogleApiClient();
  }

  /**
   * Enables the map to show user's current position.
   */
  @SuppressLint("MissingPermission")
  private void showCurrentLocationOnMap() {
    if (ActivityCompat
        .checkSelfPermission(Objects.requireNonNull(getContext()), permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(getContext(), permission.ACCESS_COARSE_LOCATION)
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
    HomeFragment.GetNearbyPlacesData getNearbyPlacesData = new HomeFragment.GetNearbyPlacesData();
    getNearbyPlacesData.execute(DataTransfer);
  }

  /**
   * Builds and returns the URL for the <code>GET</code> request based on latitude and longitude.
   *
   * @param latitude latitude of requested point
   * @param longitude longitude of requested point
   * @return constructed URL in a <code>String</code>
   */
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
    if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
        Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      LocationServices.FusedLocationApi
          .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
  }

  /**
   * Creates a <code>LocationRequest</code> that allows the use of the <code>onLocationChanged</code>
   * to be called.
   *
   * @return created <code>LocationRequest</code>
   */
  private LocationRequest createLocationRequest() {
    LocationRequest mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(6000);
    mLocationRequest.setFastestInterval(1000);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    return mLocationRequest;
  }

  /**
   * Creates a <code>PendingIntent</code> for a geofence.
   *
   * @return created <code>PendingIntent</code>
   */
  private PendingIntent getGeofencePendingIntent() {
    Intent intent = new Intent(getContext(), MapService.class);
    return PendingIntent.getService(getContext(), 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);
  }

  /**
   * Creates a <code>GeofencingRequest</code> for a geofence.
   *
   * @param geofence <code>Geofence</code> to be affected
   * @return created <code>GeofencingRequest</code>
   */
  private GeofencingRequest getGeofencingRequest(Geofence geofence) {
    GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
    builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
    builder.addGeofence(geofence);
    return builder.build();
  }

  /**
   * Creates a <code>Geofence</code> object based on latitude, longitude, and a
   * key that contains both latitude as well as longitude.
   *
   * @param lat latitude of requested point
   * @param lang longitude of requested point
   * @param key <code>String</code> containing both <code>lat</code> and <code>lang</code>
   * @return created <code>Geofence</code>
   */
  private Geofence getGeofence(double lat, double lang, String key) {
    return new Geofence.Builder()
        .setRequestId(key)
        .setCircularRegion(lat, lang, GEOFENCE_RADIUS)
        .setExpirationDuration(GEOFENCE_EXPIRATION)
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
            Geofence.GEOFENCE_TRANSITION_DWELL)
        .setLoiteringDelay(500)
        .build();
  }

  /**
   * Checks if Google Play Services is available.
   *
   * @return <code>boolean</code> depending on Google Play Services availability
   */
  private boolean CheckGooglePlayServices() {
    GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
    int result = googleAPI.isGooglePlayServicesAvailable(Objects.requireNonNull(getContext()));
    if (result != ConnectionResult.SUCCESS) {
      if (googleAPI.isUserResolvableError(result)) {
        googleAPI.getErrorDialog(getActivity(), result,
            0).show();
      }
      return false;
    }
    return true;
  }

  /**
   * Builds a <code>GoogleApiClient</code>.
   */
  private synchronized void buildGoogleApiClient() {
    mGoogleApiClient = new GoogleApiClient.Builder(Objects.requireNonNull(getContext()))
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();
    mGoogleApiClient.connect();
  }



  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(getContext(), permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
    } else {
      mMap.setMyLocationEnabled(true);
    }
  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }

  /**
   * An <code>AsyncTask</code> that makes a request to Google API's(Maps and Places)
   * to get the POI's around the requested location, mark certain types with markers,
   * and put a geofence on each POI as well.
   */
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

    /**
     * Takes a <code>List</code> made from a JSON response from the Google API's
     * and puts markers with geofences on every point in the list.
     *
     * @param nearbyPlacesList <code>List</code> made from a JSON response
     */
    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
      for (HashMap<String, String> aNearbyPlacesList : nearbyPlacesList) {
        Log.d("onPostExecute", "Entered into showing locations");
        MarkerOptions markerOptions = new MarkerOptions();
        double lat;
        lat = Double.parseDouble(aNearbyPlacesList.get("lat"));
        double lng;
        lng = Double.parseDouble(aNearbyPlacesList.get("lng"));
        String placeName = aNearbyPlacesList.get("place_name");
        String types = aNearbyPlacesList.get("types");
        LatLng latLng = new LatLng(lat, lng);
        markerOptions.position(latLng);
        markerOptions.title(placeName + " : " + types);
        mMap.addMarker(markerOptions);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        String key = "" + lat + "-" + lng;
        Geofence geofence = getGeofence(lat, lng, key);
        if (ActivityCompat
            .checkSelfPermission(Objects.requireNonNull(getContext()), permission.ACCESS_FINE_LOCATION)
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
      }
    }
  }
}
