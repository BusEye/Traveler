package com.test.edv.buseyetraveler;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserprofileFragment extends Fragment {

    EditText Name,Addres,Gender,NIC,Email,TP,UserName;

    public UserprofileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View userProfileView = inflater.inflate(R.layout.fragment_userprofile, container, false);
        Name = (EditText) userProfileView.findViewById(R.id.Nametxt);
        Addres = (EditText) userProfileView.findViewById(R.id.addtxt);
        Gender = (EditText) userProfileView.findViewById(R.id.gendertxt);
        NIC = (EditText) userProfileView.findViewById(R.id.nictxt);
        Email = (EditText) userProfileView.findViewById(R.id.emailtxt);
        TP = (EditText) userProfileView.findViewById(R.id.Tptxt);
        UserName = (EditText) userProfileView.findViewById(R.id.usernametxt);

        Name.setEnabled(false);
        Addres.setEnabled(false);
        Gender.setEnabled(false);
        NIC.setEnabled(false);
        Email.setEnabled(false);
        TP.setEnabled(false);
        UserName.setEnabled(false);

        final SharedPreferences UsersharedPreferences = this.getActivity().getSharedPreferences(LoginActivity.UserPREFERENCES, Context.MODE_PRIVATE);

        Name.setText(UsersharedPreferences.getString("Name",null));
        Addres.setText(UsersharedPreferences.getString("Addres",null));
        Gender.setText(UsersharedPreferences.getString("Gender",null));
        NIC.setText(UsersharedPreferences.getString("NIC",null));
        Email.setText(UsersharedPreferences.getString("Emails",null));
        TP.setText(UsersharedPreferences.getString("TP",null));
        UserName.setText(UsersharedPreferences.getString("UserName",null));


        return userProfileView;
    }

}
