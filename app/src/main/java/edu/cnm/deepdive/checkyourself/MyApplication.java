package edu.cnm.deepdive.checkyourself;

import android.app.Application;
import com.facebook.stetho.Stetho;

/**
 * A class that implements a third party library to allow direct SQLite
 * editing on the android emulators local storage.
 *
 * @author Jake Batchelor
 */
public class MyApplication extends Application {

  /**
   * Initializes the method to allow for access and editing.
   */
  public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this);
  }
}