package nammapolice.theateam.com.ak.nammapolice.nammapolicerakshak;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener {

    SocketService sosService;
    String UserId;
    String DisplayName;




    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            String response = intent.getStringExtra("RESULT");
            try {
                JSONObject jsonResult = new JSONObject(response);
                final String IssueId = jsonResult.getString("issueId");
                final JSONObject citizenDetails = jsonResult.getJSONObject("citizenDetails");
                JSONObject location = citizenDetails.getJSONObject("location");
                final String address = location.getString("address");
                JSONArray coordinates = location.getJSONArray("coordinates");
                final String latitude = coordinates.getString(0);
                final String longitude = coordinates.getString(1);
                final String PhoneNumber = citizenDetails.getString("phone");
                final String displayName = citizenDetails.getString("displayName");
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapActivity.this);

                // Setting Dialog Title
                alertDialog.setTitle("Mr/Ms." + displayName + " is in distress");

                // Setting Dialog Message
                alertDialog.setMessage("Will you assist?");

                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.policeberunda);

                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        new AsyncTask<String, Void, String>() {
                            int responseCode;

                            @Override
                            protected String doInBackground(String... params) {
                                try {
                                    URL url = new URL(NammaPoliceRakshak.SERVER_URL + "/request/acknowledge/");
                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                    connection.setDoInput(true);
                                    connection.setDoOutput(true);
                                    JSONObject policedetails=new JSONObject();
                                    policedetails.put("userId",UserId);
                                    policedetails.put("displayName",displayName);
                                    Uri.Builder _data = new Uri.Builder()
                                            .appendQueryParameter("issueId", params[0]).appendQueryParameter("citizenDetails",citizenDetails.toString()).appendQueryParameter("policeDetails",policedetails.toString());
                                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                                    writer.write(_data.build().getEncodedQuery());
                                    writer.flush();
                                    writer.close();

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(String s) {


                            }
                        }.execute(IssueId);





                        // User pressed YES button. Write Logic Here
                        Intent intents = new Intent(MapActivity.this, IssueActivity.class);
                        intents.putExtra("citizenName", displayName);
                        intents.putExtra("issueId", IssueId);
                        intents.putExtra("address", address);
                        intents.putExtra("phone", PhoneNumber);
                        intents.putExtra("latitude", Double.valueOf(latitude));
                        intents.putExtra("longitude", Double.valueOf(longitude));
                        startActivity(intents);

                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User pressed No button. Write Logic Here
                        Toast.makeText(getApplicationContext(), "I understand ", Toast.LENGTH_SHORT).show();
                    }
                });

                // Showing Alert Message
                alertDialog.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
//            if(bound){
//                sosService.setSendLocation(false);
//            }
        }
    };


    private GoogleMap map;
    MapView mapView;
    static String lati = "0.0";
    static String lang = "0.0";
    public static String latlangjsonString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);

        HashMap<String, String> current = NammaPoliceRakshak.getUser(getApplicationContext());

        Intent intent = new Intent(this, SocketService.class);
        UserId = current.get("USER_ID");
        DisplayName=current.get("USER_NAME");
        intent.putExtra("USER_ID", current.get("USER_ID"));
        intent.putExtra("USER_NAME", current.get("USER_NAME"));
        startService(intent);


        registerReceiver(receiver, new IntentFilter(SocketService.BROADCAST_ACTION));


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapView = (MapView) findViewById(R.id.MapView);
        mapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(this);
        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(this);


    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMyLocationChange(Location lastKnownLocation) {

        lati = Double.toString(lastKnownLocation.getLatitude());
        lang = Double.toString(lastKnownLocation.getLongitude());
        double lt = lastKnownLocation.getLatitude();
        double ln = lastKnownLocation.getLongitude();
        LatLng coordinates = new LatLng(lt, ln);
        Marker me = map.addMarker(new MarkerOptions().position(coordinates).title("You are here!!").icon(BitmapDescriptorFactory.fromResource(R.drawable.policemarker)));

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 8.0f));
        //map.setOnMyLocationChangeListener(null);
        sendLocation(lastKnownLocation);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);


        map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(this);
        // Location myLocation= map.getMyLocation();
        final JSONArray latlangArray = new JSONArray();

        try {

            latlangArray.put(0, lati);
            latlangArray.put(1, lang);
            latlangjsonString = latlangArray.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        new AsyncTask<String, Void, String>() {

            int responseCode;

            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL(NammaPoliceRakshak.SERVER_URL + "/location/police");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    Uri.Builder _data = new Uri.Builder()
                            .appendQueryParameter("coordinates", String.valueOf(latlangArray));
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                    writer.write(_data.build().getEncodedQuery());
                    writer.flush();
                    writer.close();

                    responseCode = connection.getResponseCode();

                    StringBuilder result = new StringBuilder();
                    String line;
                    if (responseCode > 199 && responseCode < 300) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        reader.close();
                        return result.toString();
                    } else {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        reader.close();
                        return result.toString();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {

                if (responseCode == 200) {
                    try {
                        JSONObject jsonObj = new JSONObject(s);
                        System.out.print(jsonObj.toString());
                        String results = jsonObj.getString("locationDetails");
                        System.out.println(results);
                        JSONArray locationDetails = jsonObj.getJSONArray("locationDetails");
                        if (locationDetails.length() > 0) {
                            for (int i = 0; i < locationDetails.length(); i++) {
                                JSONObject copLocation = locationDetails.getJSONObject(i);
                                String address = copLocation.getString("address");
                                JSONArray latlangArray = copLocation.getJSONArray("coordinates");
                                double lat = Double.valueOf(latlangArray.get(0).toString());
                                double lng = Double.valueOf(latlangArray.get(1).toString());
                                LatLng coordinates = new LatLng(lat, lng);
                                Marker me = map.addMarker(new MarkerOptions().position(coordinates).title(address).icon(BitmapDescriptorFactory.fromResource(R.drawable.policemarker)));

                            }
                        }

                    } catch (Exception ex) {
                    }
                } else {
                    Log.d("ERROR", " CONNECTION ERROR");
                }

            }
        }.execute();
    }

    int count;
    private void sendLocation(final Location mLocation) {
        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
            }
        };
        StringRequest request = new StringRequest(Request.Method.POST, NammaPoliceRakshak.SERVER_URL + "/police/location/update/", response, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                JSONArray latlangArray = new JSONArray();
                try {
                    latlangArray.put(mLocation.getLatitude());
                    latlangArray.put(mLocation.getLongitude());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                params.put("userId", UserId);
                params.put("coordinates", latlangArray.toString());

                //params.put("userName", userName);
                // params.put("time", String.valueOf(System.currentTimeMillis()));
                // params.put("lat", String.valueOf(mLocation.getLatitude()));
                // params.put("lng", String.valueOf(mLocation.getLongitude()));
                // params.put("head", String.valueOf(mLocation.getBearing()));
                return params;
            }
        };
        if ((count % 50) == 0) {
            VolleySingleton.getInstance(this).addToRequest(request);
            count = 0;
        }
        count++;
    }

}
