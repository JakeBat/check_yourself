package edu.cnm.deepdive.checkyourself;

import static edu.cnm.deepdive.checkyourself.maps.MapActivity.MY_PERMISSIONS_REQUEST_LOCATION;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import edu.cnm.deepdive.checkyourself.fragments.HomeFragment;
import edu.cnm.deepdive.checkyourself.fragments.InputFragment;
import edu.cnm.deepdive.checkyourself.fragments.SpendingFragment;
import edu.cnm.deepdive.checkyourself.maps.MapActivity;
import edu.cnm.deepdive.checkyourself.maps.MapService;

public class MainActivity extends AppCompatActivity {

  private static UniDatabase database;

  private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
      = new BottomNavigationView.OnNavigationItemSelectedListener() {

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

      switch (item.getItemId()) {
        case R.id.navigation_home:
          HomeFragment homeFragment = new HomeFragment();
          transaction.replace(R.id.content, homeFragment).addToBackStack("home").commit();
          break;
        case R.id.navigation_spending:
          SpendingFragment spendingFragment = new SpendingFragment();
          transaction.replace(R.id.content, spendingFragment).addToBackStack("home").commit();
          break;
        case R.id.navigation_input:
          InputFragment inputFragment = new InputFragment();
          transaction.replace(R.id.content, inputFragment).addToBackStack("home").commit();
          break;
      }
      return true;
    }

  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      checkLocationPermission();
    }

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

    BottomNavigationView navigation = findViewById(R.id.navigation);
    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    HomeFragment homeFragment = new HomeFragment();
    transaction.replace(R.id.content, homeFragment).addToBackStack("home").commit();
  }

  public UniDatabase getDatabase(Context context) {
    if (database == null) {
      database = UniDatabase.getInstance(context);
    }
    return database;
  }

  public boolean checkLocationPermission(){
    if (ContextCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(this,
          Manifest.permission.ACCESS_FINE_LOCATION)) {
        ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            MY_PERMISSIONS_REQUEST_LOCATION);
      } else {
        ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            MY_PERMISSIONS_REQUEST_LOCATION);
      }
      return false;
    } else {
      return true;
    }
  }
}
