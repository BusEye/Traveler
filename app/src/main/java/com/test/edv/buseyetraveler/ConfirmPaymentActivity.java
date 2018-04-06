package com.test.edv.buseyetraveler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class ConfirmPaymentActivity extends AppCompatActivity {
Button comfermpayment;
String BID,UID,from,to,paymentamunt;
private static PayPalConfiguration configuration = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK).clientId("AdH0XJgPtTLotoVcg97LuOaSmcMFpsdiltVOU3LQWx3-ZWyrT8WhhG12Tp6uJDcB1lMJuK6T5TQct3sl");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_payment);

        BID =getIntent().getStringExtra("BID");
        UID = getIntent().getStringExtra("UID");
        from =getIntent().getStringExtra("for");
        to = getIntent().getStringExtra("to");
        paymentamunt = getIntent().getStringExtra("payment");
        Log.e("ConfirmPaymentActivity","BID: "+BID+"UID: "+UID+"FROM: "+from+"TO: "+to+"PAYMENT: "+paymentamunt);



        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,configuration);
        startService(intent);

        onPayPressed(R.layout.activity_confirm_payment,paymentamunt);
        savepayment(UID,BID,from,to,Float.parseFloat(paymentamunt));


    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    public void onPayPressed(int pressed, String amount) {

        // PAYMENT_INTENT_SALE will cause the payment to complete immediately.
        // Change PAYMENT_INTENT_SALE to
        //   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
        //   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
        //     later via calls from your server.

        PayPalPayment payment = new PayPalPayment(new BigDecimal(amount), "USD", "sample item",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i("paymentExample", confirm.toJSONObject().toString(4));

                    JSONObject jsonMainObject = confirm.toJSONObject();
                    JSONObject jsonResponseObject = jsonMainObject.getJSONObject("response");
                    String state = jsonResponseObject.getString("state");
                    Log.e("ConfirmPaymentActivity",state);

                    if (state.equals("approved"))
                    {   Log.e("ConfirmPaymentActivity","state.equals");
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Transaction Complete").setTitle("Transaction state");
                        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                            }
                        });
                        builder.setPositiveButton("Reat Bus", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ConfirmPaymentActivity.this,RatingActivity.class);
                                //=>BID,UID
                                intent.putExtra("BID",BID);
                                intent.putExtra("UID",UID);
                                startActivity(intent);
                                ConfirmPaymentActivity.this.finish();
                            }
                        });

                        AlertDialog dialog =builder.create();
                        dialog.show();
                    }

                    // TODO: send 'confirm' to your server for verification.
                    // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                    // for more details.
                    //"response": {
                   // "state": "approved",


                } catch (JSONException e) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");
        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }

    private void savepayment(String UID,String BID,String From,String To,float amount)
    {
        try {
            RequestQueue requestqueue = Volley.newRequestQueue(ConfirmPaymentActivity.this);

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
                            Toast.makeText(ConfirmPaymentActivity.this,"Payment save sucsses",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(ConfirmPaymentActivity.this,"Sumthing Wrong plese try again",Toast.LENGTH_LONG).show();
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

