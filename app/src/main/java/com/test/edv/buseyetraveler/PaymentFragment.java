package com.test.edv.buseyetraveler;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentFragment extends Fragment {
 View PaymentFragmentView;
 Button scanbtn,accsept;
 EditText fromtxt ,totxt, paymenttxt;
 public  static final int PERMISSION_REQUEST=200;
    public PaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PaymentFragmentView =inflater.inflate(R.layout.fragment_payment, container, false);
        scanbtn= (Button) PaymentFragmentView.findViewById(R.id.scanbtn);
        accsept = (Button) PaymentFragmentView.findViewById(R.id.acceptbtn);
        fromtxt=(EditText) PaymentFragmentView.findViewById(R.id.fromtxt);
        totxt=(EditText) PaymentFragmentView.findViewById(R.id.totxt);
        paymenttxt=(EditText) PaymentFragmentView.findViewById(R.id.paymenttxt);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},PERMISSION_REQUEST);
        }
        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),CameraActivity.class);
                startActivityForResult(intent,100);
            }
        });

        return PaymentFragmentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null)
        {
            if (requestCode==100&&resultCode==-1)
            {
                if (data != null)
                {
                    final Barcode barcode = data.getParcelableExtra("barcordDeta");
                    Log.e("Brcord Data",barcode.displayValue);
                    String qrdeta = barcode.displayValue;
                    try {
                           JSONObject jsonObject = new JSONObject(qrdeta);

                           fromtxt.setText(jsonObject.getString("id"));
                           totxt.setText(jsonObject.getString("Name"));
                           paymenttxt.setText(jsonObject.getString("G"));
                        }
                    catch (JSONException e)
                        {
                           e.printStackTrace();
                        }

                }
            }
        }
    }
}
