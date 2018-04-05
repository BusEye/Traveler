package com.test.edv.buseyetraveler;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RatingActivity extends AppCompatActivity {

    RatingBar myratingBar;
    Button submit;
    String BID;
    String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        myratingBar = (RatingBar) findViewById(R.id.ratingBar);
        submit =(Button) findViewById(R.id.Ratebtn);

        BID = getIntent().getStringExtra("BID");
        UID = getIntent().getStringExtra("UID");

        myratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(RatingActivity.this,"Rating"+rating,Toast.LENGTH_LONG).show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rate = (int) myratingBar.getRating();
                   saveRating(UID,BID,rate);
                Toast.makeText(RatingActivity.this,"Thanks for your Rating",Toast.LENGTH_LONG).show();
            }
        });
    }


    private void saveRating(String UID,String BID,int rate)
    {
        try {
            RequestQueue requestqueue = Volley.newRequestQueue(RatingActivity.this);

            String URL ="http://theslbuseye.xyz/SaveRate.php?UID="+UID+"&BID="+BID+"&Rate="+rate;
            Log.i("url",URL);
            StringRequest stringrequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    try{

                        JSONObject jsonObj = new JSONObject(response);
                        Log.i("Full value",response);

                        String Result = jsonObj.getString("Result")+" "+jsonObj.getString("ID")+" "+jsonObj.getString("Name");
                        Log.e("resalt",Result);

                        if (jsonObj.getString("Massage").equals("Pass"))
                        {
                            Toast.makeText(RatingActivity.this,"Rating is save sucsses",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(RatingActivity.this,"Sumthing Wrong plese try again",Toast.LENGTH_LONG).show();
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
