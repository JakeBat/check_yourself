package edu.cnm.deepdive.checkyourself.maps;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUrl {

  public String readUrl(String strUrl) throws IOException {
    String data = "";
    HttpURLConnection urlConnection;
    URL url = new URL(strUrl);
    urlConnection = (HttpURLConnection) url.openConnection();
    urlConnection.connect();
    try (InputStream iStream = urlConnection.getInputStream()) {
      BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
      data = sb.toString();
      Log.d("downloadUrl", data);
      br.close();
    } catch (Exception e) {
      Log.d("Exception", e.toString());
    } finally {
      urlConnection.disconnect();
    }
    return data;
  }
}
