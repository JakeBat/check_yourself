package edu.cnm.deepdive.checkyourself;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

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
}
