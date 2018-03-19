package edu.cnm.deepdive.checkyourself;


import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements
    OnMapReadyCallback,
    GoogleMap.OnMapClickListener,
    GoogleMap.OnMarkerClickListener {

  private static final String TAG = MapFragment.class.getSimpleName();
  private GoogleMap mMap;
  private CameraPosition mCameraPosition;

  // The entry points to the Places API.
  private GeoDataClient mGeoDataClient;
  private PlaceDetectionClient mPlaceDetectionClient;

  // The entry point to the Fused Location Provider.
  private FusedLocationProviderClient mFusedLocationProviderClient;

  // A default location (Sydney, Australia) and default zoom to use when location permission is
  // not granted.
  private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
  private static final int DEFAULT_ZOOM = 15;
  private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
  private boolean mLocationPermissionGranted;

  // The geographical location where the device is currently located. That is, the last-known
  // location retrieved by the Fused Location Provider.
  private Location mLastKnownLocation;

  // Keys for storing activity state.
  private static final String KEY_CAMERA_POSITION = "camera_position";
  private static final String KEY_LOCATION = "location";

  // Used for selecting the current place.
  private static final int M_MAX_ENTRIES = 5;
  private String[] mLikelyPlaceNames;
  private String[] mLikelyPlaceAddresses;
  private String[] mLikelyPlaceAttributions;
  private LatLng[] mLikelyPlaceLatLngs;

  public MapFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_map, container, false);
    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    // Construct a GeoDataClient.
    mGeoDataClient = Places.getGeoDataClient(getContext(), null);

    // Construct a PlaceDetectionClient.
    mPlaceDetectionClient = Places.getPlaceDetectionClient(getContext(), null);

    // Construct a FusedLocationProviderClient.
    mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    return view;
  }

  @Override
  public void onMapClick(LatLng latLng) {

  }

  @Override
  public boolean onMarkerClick(Marker marker) {
    return false;
  }

  @Override
  public void onMapReady(GoogleMap map) {
    mMap = map;

    // Do other setup activities here too, as described elsewhere in this tutorial.

    // Turn on the My Location layer and the related control on the map.
    updateLocationUI();

    // Get the current location of the device and set the position of the map.
    getDeviceLocation();
  }

  private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
    if (ContextCompat.checkSelfPermission(this.getContext(),
        android.Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      mLocationPermissionGranted = true;
    } else {
      ActivityCompat.requestPermissions(getActivity(),
          new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
          PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    mLocationPermissionGranted = false;
    switch (requestCode) {
      case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          mLocationPermissionGranted = true;
        }
      }
    }
    updateLocationUI();
  }

  private void updateLocationUI() {
    if (mMap == null) {
      return;
    }
    try {
      if (mLocationPermissionGranted) {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
      } else {
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mLastKnownLocation = null;
        getLocationPermission();
      }
    } catch (SecurityException e)  {
      Log.e("Exception: %s", e.getMessage());
    }
  }

  private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
    try {
      if (mLocationPermissionGranted) {
        Task locationResult = mFusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
          @Override
          public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
              // Set the map's camera position to the current location of the device.
              mLastKnownLocation = (Location) task.getResult();
              mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                  new LatLng(mLastKnownLocation.getLatitude(),
                      mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
            } else {
              Log.d(TAG, "Current location is null. Using defaults.");
              Log.e(TAG, "Exception: %s", task.getException());
              mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
              mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
          }
        });
      }
    } catch(SecurityException e)  {
      Log.e("Exception: %s", e.getMessage());
    }
  }

  private void showCurrentPlace() {
    if (mMap == null) {
      return;
    }

    if (mLocationPermissionGranted) {
      // Get the likely places - that is, the businesses and other points of interest that
      // are the best match for the device's current location.
      @SuppressWarnings("MissingPermission") final
      Task<PlaceLikelihoodBufferResponse> placeResult =
          mPlaceDetectionClient.getCurrentPlace(null);
      placeResult.addOnCompleteListener
          (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
              if (task.isSuccessful() && task.getResult() != null) {
                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                // Set the count, handling cases where less than 5 entries are returned.
                int count;
                if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                  count = likelyPlaces.getCount();
                } else {
                  count = M_MAX_ENTRIES;
                }

                int i = 0;
                mLikelyPlaceNames = new String[count];
                mLikelyPlaceAddresses = new String[count];
                mLikelyPlaceAttributions = new String[count];
                mLikelyPlaceLatLngs = new LatLng[count];

                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                  // Build a list of likely places to show the user.
                  mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                  mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
                      .getAddress();
                  mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                      .getAttributions();
                  mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                  i++;
                  if (i > (count - 1)) {
                    break;
                  }
                }

                // Release the place likelihood buffer, to avoid memory leaks.
                likelyPlaces.release();

                // Show a dialog offering the user the list of likely places, and add a
                // marker at the selected place.


              } else {
                Log.e(TAG, "Exception: %s", task.getException());
              }
            }
          });
    } else {
      // The user has not granted permission.
      Log.i(TAG, "The user did not grant location permission.");

      // Add a default marker, because the user hasn't selected a place.

      // Prompt the user for permission.
      getLocationPermission();
    }
  }


}
