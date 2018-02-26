package edu.cnm.deepdive.checkyourself;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

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
        case R.id.navigation_dashboard:
          SpendingFragment spendingFragment = new SpendingFragment();
          transaction.replace(R.id.content, spendingFragment).addToBackStack("home").commit();
          break;
        case R.id.navigation_notifications:
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

    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

     BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
     navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    HomeFragment homeFragment = new HomeFragment();
    transaction.replace(R.id.content, homeFragment).addToBackStack("home").commit();
  }

}
