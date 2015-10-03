package nammapolice.theateam.com.ak.nammapolice.nammapolicerakshak;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created  on 04/10/15.
 */
public class FragmentLogin extends Fragment {

    Button login_Button, register_Button, support_Button;
    EditText policeId_EditText, password_EditText;
    CheckBox rememberMe_CheckBox;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        policeId_EditText = (EditText) view.findViewById(R.id.policeId_login_editText);
        password_EditText = (EditText) view.findViewById(R.id.pwd_login_editText);
        rememberMe_CheckBox = (CheckBox) view.findViewById(R.id.remember_login_checkBox);
        login_Button = (Button) view.findViewById(R.id.login_login_button);
        register_Button = (Button) view.findViewById(R.id.rgstr_login_button);
        support_Button = (Button) view.findViewById(R.id.support_login_button);

        login_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<String, Void, String>() {

                    int responseCode;

                    @Override
                    protected String doInBackground(String... params) {
                        try {
                            URL url = new URL(NammaPoliceRakshak.SERVER_URL + "/police/login/");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setDoInput(true);
                            connection.setDoOutput(true);

                            Uri.Builder _data = new Uri.Builder()
                                    .appendQueryParameter("policeId", params[0])
                                    .appendQueryParameter("password", params[1]);
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
//                            System.out.print(jsonObj.toString());
                                String results = jsonObj.getString("status");
                                String userId= jsonObj.getString("userId");
                                String displayName= jsonObj.getString("displayName");
                                HashMap<String, String> userInfo = new HashMap<>();
                                userInfo.put("USER_ID",userId);
                                userInfo.put("USER_NAME",displayName);
                                userInfo.put("PHONE",userId);
//                                Log.d("results", results);
                                if (results.matches("loggedIn"))
                                {

                                    NammaPoliceRakshak.saveUser(getActivity().getApplicationContext(), userInfo);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(getActivity(), MapActivity.class);
                                            startActivity(intent);
                                            getActivity().finish();
                                        }
                                    }, 20);

                                }
                            } catch (Exception ex) {
                            }
                        } else {
                            Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();
                        }

                    }
                }.execute(policeId_EditText.getText().toString(), password_EditText.getText().toString());
            }
        });
        register_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        HashMap<String, String> current = NammaPoliceRakshak.getUser(getActivity().getApplicationContext());
        if(current != null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getActivity(), MapActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }, 20);
        }
    }


//    private class AuthAsync extends AsyncTask<Void, Void, Integer> {
//        String phno, pwd;
//
//        protected void onPreExecute() {
//            phno = phoneNumber_EditText.getText().toString();
//            pwd = password_EditText.getText().toString();
//        }
//
//        //http://namitbehl.net/hackathon/fetch_data.php?update_rider=true&riderID=8002144009&riderSource=ndkjfhnskjfdnsjkddnksJ&riderDestination=nnsfkjdnskjndskjandksjd1s53dsaa1
//        protected Integer doInBackground(Void... params) {
//            try {
//                URL url = new URL(NammaPolice.SERVER_URL + "/citizen/login");
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setDoInput(true);
//                connection.setDoOutput(true);
//                connection.setRequestMethod("POST");
//
//                Uri.Builder _data = new Uri.Builder().appendQueryParameter("phone", phno).appendQueryParameter("password", pwd);
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
//                if (results.matches("loggedIn"))
//
//
//                {
//                    Intent intent = new Intent(getActivity(), SOSActivity.class);
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
//
//        protected void onPostExecute(Integer result) {
//        }
//    }
}
