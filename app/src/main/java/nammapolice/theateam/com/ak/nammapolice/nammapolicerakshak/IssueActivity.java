package nammapolice.theateam.com.ak.nammapolice.nammapolicerakshak;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class IssueActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMyLocationChangeListener {


    public static String citizenName="";
    public static String issueId="";
    public static String citizenAddress="";
    public static String citizenPhone="";
    public static double citizenLat;
    public static double citizenLng;
        Button resolve_button;
    private GoogleMap map;
    MapView mapView;
    static String lati="0.0";
    static String lang="0.0";
    public  static   String latlangjsonString="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        Intent intent = getIntent();
        citizenName=intent.getStringExtra("citizenName");
        issueId=intent.getStringExtra("issueId");
        citizenAddress=intent.getStringExtra("address");
        citizenPhone=intent.getStringExtra("phone");
        citizenLat=intent.getDoubleExtra("latitude",0.0f);
        citizenLng=intent.getDoubleExtra("longitude",0.0f);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapView=(MapView) findViewById(R.id.MapView2);
        mapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(this);
        // Gets to GoogleMap from the MapView and does initialization stuff
        mapView.getMapAsync(this);
resolve_button=(Button) findViewById(R.id.resolve_issue_button);
        resolve_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AsyncTask<String, Void, String>() {

                    int responseCode;

                    @Override
                    protected String doInBackground(String... params) {
                        try {
                            URL url = new URL(NammaPoliceRakshak.SERVER_URL + "/help/acknowledge/");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setDoInput(true);
                            connection.setDoOutput(true);

                            Uri.Builder _data = new Uri.Builder()
                                    .appendQueryParameter("issueId", params[0]);
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
                        finish();
                        System.exit(0);

                    }
                }.execute(issueId);

            }
        });
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

        lati=Double.toString(lastKnownLocation.getLatitude());
        lang=Double.toString(lastKnownLocation.getLongitude());
        double lt=lastKnownLocation.getLatitude();
        double ln=lastKnownLocation.getLongitude();
        LatLng coordinates= new LatLng(lt,ln);
        Marker me = map.addMarker(new MarkerOptions().position(coordinates).title("You are here!!").icon(BitmapDescriptorFactory.fromResource(R.drawable.policemarker)));

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 8.0f));
        map.setOnMyLocationChangeListener(null);
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
        Marker me = map.addMarker(new MarkerOptions().position(new LatLng(citizenLat,citizenLng)).title("Mr/Ms."+citizenName+" is near "+citizenAddress+" contactInfo:"+citizenPhone).icon(BitmapDescriptorFactory.fromResource(R.drawable.citizenmarker)));
        // Location myLocation= map.getMyLocation();

    }

}
