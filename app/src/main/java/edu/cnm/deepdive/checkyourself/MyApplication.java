package edu.cnm.deepdive.checkyourself;

import android.app.Application;
import com.facebook.stetho.Stetho;

public class MyApplication extends Application {
  public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this);
  }
}