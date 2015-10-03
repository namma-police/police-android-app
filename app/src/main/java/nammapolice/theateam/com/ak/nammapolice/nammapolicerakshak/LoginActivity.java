package nammapolice.theateam.com.ak.nammapolice.nammapolicerakshak;

import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    Button login_Button,register_Button,support_Button;
    EditText policeId_EditText,password_EditText;
    CheckBox rememberMe_CheckBox;
    final String url = "some/url";
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragmentSettings()).commit();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        checkGPSSettings();
//        policeId_EditText=(EditText) findViewById(R.id.policeId_login_editText);
//        password_EditText=(EditText) findViewById(R.id.pwd_login_editText);
//        rememberMe_CheckBox=(CheckBox) findViewById(R.id.remember_login_checkBox);
//        login_Button=(Button) findViewById(R.id.login_login_button);
//        register_Button=(Button) findViewById(R.id.rgstr_login_button);
//        support_Button=(Button) findViewById(R.id.support_login_button);
//
//        login_Button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                AuthAsync authAsync = new AuthAsync();
//                authAsync.execute();
//            }
//        });
//        register_Button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//                startActivity(intent);
//            }
//        });

    }

    private void checkGPSSettings(){
        try {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(LoginActivity.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        } catch (Exception ex) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkGPSSettings();
    }

    @Override
    protected void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }
}




//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_login, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//    private class AuthAsync extends AsyncTask<Void, Void, Integer> {
//        String phno,pwd;
//
//        protected void onPreExecute() {
//            phno=policeId_EditText.getText().toString();
//            pwd=password_EditText.getText().toString();
//        }
//        //http://namitbehl.net/hackathon/fetch_data.php?update_rider=true&riderID=8002144009&riderSource=ndkjfhnskjfdnsjkddnksJ&riderDestination=nnsfkjdnskjndskjandksjd1s53dsaa1
//        protected Integer doInBackground(Void... params) {
//            try {
//                URL url = new URL("http://192.168.1.3:8000/police/login");
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setDoInput(true);
//                connection.setDoOutput(true);
//                connection.setRequestMethod("POST");
//
//                Uri.Builder _data = new Uri.Builder().appendQueryParameter("policeId",phno).appendQueryParameter("password", pwd);
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
//                writer.write(_data.build().getEncodedQuery());
//                writer.flush();
//                writer.close();
//                String line;
//                String res = "";
//                String result = null;
//                InputStreamReader in = new InputStreamReader(connection.getInputStream());
//
//
//                StringBuilder jsonResults1 = new StringBuilder();
//                ArrayList<String> resultList = null;
//// Load the results into a StringBuilder
//                int read;
//                char[] buff = new char[1024];
//                while ((read = in.read(buff)) != -1) {
//                    jsonResults1.append(buff, 0, read);
//                }
//                connection.disconnect();
//                JSONObject jsonObj = new JSONObject(jsonResults1.toString());
//                System.out.print(jsonObj.toString());
//                String results = jsonObj.getString("status");
//                Log.d("results", results);
//                if(results.matches("loggedIn"))
//
//
//                {
//                    Intent intent=new Intent(LoginActivity.this,MapActivity.class);
//                    startActivity(intent);
//                }
////                Intent intent=new Intent(LoginActivity.this,SOSActivity.class);
////                startActivity(intent);
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (ProtocolException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            return 1;
//        }
//        protected void onPostExecute(Integer result) {
//        }
//    }
//
//
//
//
//
//}
