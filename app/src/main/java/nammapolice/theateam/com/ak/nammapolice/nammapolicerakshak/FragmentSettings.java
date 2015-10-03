package nammapolice.theateam.com.ak.nammapolice.nammapolicerakshak;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by adarshasaraff on 04/10/15.
 */
public class FragmentSettings extends Fragment {

    EditText urlEditText;
    Button setButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        urlEditText=(EditText) view.findViewById(R.id.serverIp_settings_editText);
        setButton=(Button) view.findViewById(R.id.proceed_settings_button);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NammaPoliceRakshak.SERVER_URL = urlEditText.getText().toString();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new FragmentLogin()).commit();
            }
        });



    }
}

