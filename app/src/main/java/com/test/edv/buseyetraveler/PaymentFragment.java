package com.test.edv.buseyetraveler;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.barcode.Barcode;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;


/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentFragment extends Fragment {
 View PaymentFragmentView;
 Button scanbtn,accsept;
 EditText fromtxt ,totxt, paymenttxt;
 String UID,BID;
 public  static final int PERMISSION_REQUEST=200;
    public PaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        fromtxt.setEnabled(false);
        totxt.setEnabled(false);
        paymenttxt.setEnabled(false);

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

        accsept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1=new Intent(getContext(),ConfirmPaymentActivity.class);
                startActivity(intent1);
                final SharedPreferences UsersharedPreferences = getActivity().getSharedPreferences(LoginActivity.UserPREFERENCES, Context.MODE_PRIVATE);
                UID = UsersharedPreferences.getString("ID",null);

                if(!fromtxt.getText().toString().isEmpty())
                {
                    savepayment(UID, BID, fromtxt.getText().toString(), totxt.getText().toString(), Float.parseFloat(paymenttxt.getText().toString()));
                }
                else
                {
                    Toast.makeText(getContext(),"Scane QR cord first",Toast.LENGTH_LONG).show();
                }

                Intent intent = new Intent(getContext(),RatingActivity.class);
                //=>BID,UID
                intent.putExtra("BID",BID);
                intent.putExtra("UID",UID);
                startActivity(intent);
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
                           Log.e("QRjsonObject",jsonObject.toString());
                           BID = jsonObject.getString("BID");
                           fromtxt.setText(jsonObject.getString("from"));
                           totxt.setText(jsonObject.getString("to"));
                           paymenttxt.setText(jsonObject.getString("payment"));
                        }
                    catch (JSONException e)
                        {
                          Log.e("JsonError",e.toString());
                        }

                }
            }
        }
    }

    private void savepayment(String UID,String BID,String From,String To,float amount)
    {
        try {
            RequestQueue requestqueue = Volley.newRequestQueue(getContext());

            String MoveURL ="http://theslbuseye.xyz/Savepyments.php?UID="+UID+"&BID="+BID+"&From="+From+"&To="+To+"&Amount="+amount;

            StringRequest stringrequest = new StringRequest(Request.Method.GET, MoveURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    try{

                        JSONObject jsonObj = new JSONObject(response);
                        Log.i("Full value",response);

                        String Result = jsonObj.getString("Result")+" "+jsonObj.getString("ID")+" "+jsonObj.getString("Name");
                        Log.e("resalt",Result);

                        if (jsonObj.getString("Massage").equals("Pass"))
                        {
                            Toast.makeText(getContext(),"Payment save sucsses",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(getContext(),"Sumthing Wrong plese try again",Toast.LENGTH_LONG).show();
                        }


                    }
                    catch (JSONException e)
                    {
                        Log.e("ls", "Json parsing error: " + e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Log.e("Error", error.toString());
                }
            });
            requestqueue.add(stringrequest);
        }
        catch (Exception e)
        {
            Log.e("EX", "Exception: " + e.getMessage());
        }
    }

}
