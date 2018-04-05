package com.test.edv.buseyetraveler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by edv on 3/23/18.
 */

public class BusLocationService extends Service {
    Thread thread;

    boolean tag =true;
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        final String root =(String) intent.getExtras().get("root");
  final TravelerMapFragment tr = new TravelerMapFragment();
        thread= new Thread()
        {
            @Override
            public void run() {
                while (tag)
                {
                    try {
                        Thread.sleep(6000);

                        if (tr.busMaker!=null){
                            tr.removeBusMakers();
                        }
                        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
                        String url = "http://theslbuseye.xyz/getBusDetails.php?Busrootno="+root;
                        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response)
                            { Log.e("onResponse","OK");

                                Intent intent = new Intent("my-event");
                                intent.putExtra("Busdeta",response);
                                intent.putExtra("message", "data");
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);


                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                        requestQueue.add(request);




                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                }

            }


        };
        thread.start();


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        tag=false;

        Log.e("Sty","====================================================xonDestroy");
        super.onDestroy();
    }
}
