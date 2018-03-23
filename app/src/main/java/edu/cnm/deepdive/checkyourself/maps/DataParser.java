package edu.cnm.deepdive.checkyourself.maps;

import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A class meant to take JSON responses and parse them into <code>List</code>'s of
 * <code>HashMap</code>'s for use in Java code/methods.
 *
 * @author Jake Batchelor
 */
public class DataParser {

  /**
   * Takes a JSON response in as a <code>String</code> and parses it into a
   * <code>List</code> of <code>HashMap</code>'s.
   *
   * @param jsonData JSON response as a <code>String</code>
   * @return <code>List</code> of <code>HashMap</code>'s
   */
  public List<HashMap<String, String>> parse(String jsonData) {
    JSONArray jsonArray = null;
    JSONObject jsonObject;
    try {
      Log.d("Places", "parse");
      jsonObject = new JSONObject(jsonData);
      jsonArray = jsonObject.getJSONArray("results");
    } catch (JSONException e) {
      Log.d("Places", "parse error");
      e.printStackTrace();
    }
    assert jsonArray != null;
    return getPlaces(jsonArray);
  }

  private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
    int placesCount = jsonArray.length();
    List<HashMap<String, String>> placesList = new ArrayList<>();
    HashMap<String, String> placeMap;
    Log.d("Places", "getPlaces");
    for (int i = 0; i < placesCount; i++) {
      try {
        placeMap = getPlace((JSONObject) jsonArray.get(i));
        placesList.add(placeMap);
        Log.d("Places", "Adding places");
      } catch (JSONException e) {
        Log.d("Places", "Error in Adding places");
        e.printStackTrace();
      }
    }
    return placesList;
  }

  private HashMap<String, String> getPlace(JSONObject googlePlaceJson) {
    HashMap<String, String> googlePlaceMap = new HashMap<>();
    String placeName = "-NA-";
    String vicinity = "-NA-";
    String latitude;
    String longitude;
    String reference;
    String types;
    Log.d("getPlace", "Entered");
    try {
      if (!googlePlaceJson.isNull("name")) {
        placeName = googlePlaceJson.getString("name");
      }
      if (!googlePlaceJson.isNull("vicinity")) {
        vicinity = googlePlaceJson.getString("vicinity");
      }
      latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location")
          .getString("lat");
      longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location")
          .getString("lng");
      reference = googlePlaceJson.getString("reference");
      types = googlePlaceJson.getJSONArray("types").toString();
      googlePlaceMap.put("place_name", placeName);
      googlePlaceMap.put("vicinity", vicinity);
      googlePlaceMap.put("lat", latitude);
      googlePlaceMap.put("lng", longitude);
      googlePlaceMap.put("reference", reference);
      googlePlaceMap.put("types", types);
      Log.d("getPlace", "Putting Places");
    } catch (JSONException e) {
      Log.d("getPlace", "Error");
      e.printStackTrace();
    }
    return googlePlaceMap;
  }
}
