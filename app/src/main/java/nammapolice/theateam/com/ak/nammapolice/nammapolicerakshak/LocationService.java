package nammapolice.theateam.com.ak.nammapolice.nammapolicerakshak;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Map;

/**
 * Created  on 04/10/15.
 */
public class LocationService  extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String BROADCAST_ACTION = "com.nammapolice.sos.response";

    SoSServiceBinder binder = new SoSServiceBinder();

    GoogleApiClient mGoogleApiClient;

    private Location mLocation;

    private String userId, userName;

    private LocationRequest mLocationRequest;

    private Intent intent;

    private boolean isSendLocation;


    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public class SoSServiceBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public void onDestroy() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        userId = intent.getStringExtra("USER_ID");
        userName = intent.getStringExtra("USER_NAME");
        mGoogleApiClient.connect();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        if (isSendLocation) {
            sendLocation();
        }
    }


    public void setSendLocation(boolean sendLocation) {
        isSendLocation = sendLocation;
    }

    private void sendLocation() {
        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                intent.putExtra("RESPONSE", response);
                sendBroadcast(intent);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };
        StringRequest request = new StringRequest(Request.Method.POST, "", response, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userId", userId);
                params.put("userName", userName);
                params.put("time", String.valueOf(System.currentTimeMillis()));
                params.put("lat", String.valueOf(mLocation.getLatitude()));
                params.put("lng", String.valueOf(mLocation.getLongitude()));
                params.put("head", String.valueOf(mLocation.getBearing()));
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequest(request);
    }


}
