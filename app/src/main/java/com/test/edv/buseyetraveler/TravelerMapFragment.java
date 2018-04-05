package com.test.edv.buseyetraveler;


import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.app.VoiceInteractor;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.media.audiofx.BassBoost;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */

public class TravelerMapFragment extends Fragment implements OnMapReadyCallback {

    public GoogleMap TgoogleMap;
    View TmapView;
    AutoCompleteTextView txtSearchMap;
    Marker marker;
    Marker[] busStopMaker;
    public Marker[] busMaker;
    FusedLocationProviderClient fusedLocationProviderClient;
    Button btnSearch;
    Button btnLiveLocation;
    LatLng currentLocation;
    private float zoomMap = 16;
    private double latitude;
    private double longitude;
    private boolean avalabilityButton = false;
    private static final String TAG = "GmapFragment";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean locationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST = 1234;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 5 * 1000;
    private long FASTEST_INTERVAL = 2000;
    private LocationCallback locationCallback;
    public String id;
    public String SerchText=null;
    private String[] rootNumbers;
    private String rooNumber;
    private Intent Serviceintent ;
    private Thread polll;
    private boolean tredwhie = true;
    private Receiver receiver;

    public TravelerMapFragment() {

    }

    @Override
    public void onPause() {
//        getContext().stopService(Serviceintent);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {

   //     getContext().stopService(Serviceintent);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiver=new Receiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver,
                new IntentFilter("my-event"));

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        TmapView = inflater.inflate(R.layout.fragment_traveler_map, container, false);
        btnSearch = (Button)TmapView.findViewById(R.id.btnSearch);
        btnLiveLocation=(Button)TmapView.findViewById(R.id.btnliveLocation);
        txtSearchMap=(AutoCompleteTextView) TmapView.findViewById(R.id.txtMapSearch);

        RootNumbers();

        //===============================================================================

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SerchText = txtSearchMap.getText().toString();

                if(!SerchText.isEmpty())
                {

                    rooNumber  = SerchText.replaceAll("[^0-9]+[a-z]", ""); //ToDo support 177/1
                    TgoogleMap.clear();
                    fusedLocationProviderClient.removeLocationUpdates( locationCallback);
                    getDeviceLocation();
                    BusStopLocation(rooNumber);
                    try {
                          getContext().stopService(Serviceintent);
                        }
                    catch (NullPointerException e)
                       {

                       }

                    Serviceintent=new Intent(getContext(), BusLocationService.class);
                    Serviceintent.putExtra("root",rooNumber);
                    getContext().startService(Serviceintent);

                    CameraPosition colombo = CameraPosition.builder().target(currentLocation).zoom(15).bearing(0).tilt(45).build();
                    TgoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(colombo));
                }
                else
                {
                    Toast.makeText(getContext(),"Plase enter Root Number",Toast.LENGTH_LONG).show();
                }


            }
        });

//==================================================================================

        btnLiveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SerchText = txtSearchMap.getText().toString();
                if (busStopMaker!=null && busMaker!=null){


                    if(!SerchText.isEmpty())
                    {

                        removeBusStopMakers();removeBusMakers();
                        BusStopLocation(rooNumber);

                    }
                    else
                    {
                        SerchText = null;
                        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
                        TgoogleMap.clear();

                    }

                }


                   if (fusedLocationProviderClient==null)
                   {
                       getDeviceLocation();
                   }
                   else
                   {
                       fusedLocationProviderClient.removeLocationUpdates( locationCallback);
                       getDeviceLocation();
                   }



                try {

                      CameraPosition colombo = CameraPosition.builder().target(currentLocation).zoom(15).bearing(0).tilt(45).build();
                      TgoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(colombo));
                    }
                catch (NullPointerException e)
                   {

                   }

            }
        });
        return TmapView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getLocationPermission();



        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.onCreate(null);
        fragment.onResume();
        fragment.getMapAsync(this);

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        MapsInitializer.initialize(getContext());
        TgoogleMap = googleMap;
        Log.e("baa","getDeviceLocation");
        getDeviceLocation();
        TgoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        TgoogleMap.setTrafficEnabled(true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted=false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST:{
                if (grantResults.length>0){
                    for (int i=0;i<grantResults.length;i++){
                        if (grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                            locationPermissionGranted=false;
                            return;
                        }
                    }

                    locationPermissionGranted=true;

                }
            }
        }
    }
//==============================================================================================================================
    private void getLocationPermission(){

        Log.d(TAG,"grtLocationPermission: getting LOcation permission");

        String[]permsission={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getContext(),FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(getContext(),COURSE_LOCATION)==PackageManager.PERMISSION_GRANTED){

                locationPermissionGranted = true;
                 return;

            }else {
                ActivityCompat.requestPermissions((Activity) getContext(),permsission,LOCATION_PERMISSION_REQUEST);
            }
        }else {
            ActivityCompat.requestPermissions((Activity) getContext(),permsission,LOCATION_PERMISSION_REQUEST);
        }
    }
//====================================================================================================================================
    private void getDeviceLocation()
    {
       String locationProviders = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
       if(locationProviders == null || locationProviders.equals(""))
       {
           Toast.makeText(getContext(),"Plase on GPS and click this =>",Toast.LENGTH_LONG).show();
       }
       else
       {
           fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

           try {

               mLocationRequest = new LocationRequest();
               mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
               mLocationRequest.setInterval(UPDATE_INTERVAL);
               mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

               fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, getlocationcallback(), Looper.myLooper());
           } catch (SecurityException e) {
               String a = e.toString();
               Log.e("SecurityException", a);
           }
       }

    }
//===================================================================================================================
    private LocationCallback getlocationcallback()
    {

         locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.e("Locatoion on update","getLatitude:"+locationResult.getLastLocation().getLatitude()+" getLongitude:"+locationResult.getLastLocation().getLongitude());

                currentLocation = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());

                if (marker!=null){
                    Log.e("remove","remove");
                    marker.remove();
                }
                else
                {
                    CameraPosition colombo = CameraPosition.builder().target(currentLocation).zoom(15).bearing(0).tilt(45).build();
                    TgoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(colombo));
                }
                MarkerOptions options = new MarkerOptions().position(currentLocation).title("MY LCATION").icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow_32));
                marker=TgoogleMap.addMarker(options);

            }
        };
        return locationCallback;
    }
 //=====================================================================================================================
    private  void  BusStopLocation(String busRoot)
    {
        if (busStopMaker!=null){
            removeBusStopMakers();
        }



        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String Url = "http://theslbuseye.xyz/getBusStop.php?Busrootno="+busRoot;
        StringRequest request = new StringRequest(Request.Method.GET, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try
                {
                    JSONArray jsonArray = new JSONArray(response);

                     busStopMaker = new Marker[jsonArray.length()];

                    for (int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject BusStopDetails = jsonArray.getJSONObject(i);
                        LatLng latLng = new LatLng(BusStopDetails.getDouble("Latitude"),BusStopDetails.getDouble("Longitude"));
                        MarkerOptions options = new MarkerOptions().position(latLng).title(BusStopDetails.getString("Name")).icon(BitmapDescriptorFactory.fromResource(R.drawable.busstop));
                        busStopMaker[i]=TgoogleMap.addMarker(options);


                    }

                }catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getContext(),"Error"+error,Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(request);
    }

//-=============================================================================================================================
    public void BusLocation(){
 polll = new Thread()
 {
     @Override
     public void run() {
         while (tredwhie)
         {
             try {
                 Thread.sleep(4000);
                 Log.e("Thread","Thread is runnig");
                 if (busMaker!=null){
                    // removeBusMakers();

                 }
                 RequestQueue requestQueue= Volley.newRequestQueue(getContext());
                 String url = "http://theslbuseye.xyz/getBusDetails.php?Busrootno="+rooNumber;
                 Log.e("Thread url",url);
                 StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                     @Override
                     public void onResponse(String response) {

                         try{
                             JSONArray jsonArray = new JSONArray(response);
                             busMaker=new Marker[jsonArray.length()];
                             for (int i=0;i<jsonArray.length();i++)
                             {
                                 JSONObject busObject = jsonArray.getJSONObject(i);
                                 JSONObject locationObject = busObject.getJSONObject("location");
                                 LatLng latLng = new LatLng(locationObject.getDouble("lat"),locationObject.getDouble("lng"));
                                 String RegisteredNo=busObject.getString("RegisteredNo");
                                 Log.e("ssssssssssssssssss",RegisteredNo);
                                 MarkerOptions options = new MarkerOptions().position(latLng).title(RegisteredNo).icon(BitmapDescriptorFactory.fromResource(R.drawable.busalong));
                                 Log.e("MarkerOptions",options.getPosition().toString());
                                 busMaker[i] = TgoogleMap.addMarker(options);

                             }
                         }
                         catch (JSONException e)
                         {
                             e.printStackTrace();
                         }


                     }
                 }, new Response.ErrorListener() {
                     @Override
                     public void onErrorResponse(VolleyError error) {

                     }
                 });
                 requestQueue.add(request);

             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
         }
     }
 };

polll.start();
    }
//===================================================RootNumbers======================================================================================================
    private  void RootNumbers(){
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url = "http://theslbuseye.xyz/getBusroot.php";
        final StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try
                {
                   JSONArray jsonArray = new JSONArray(response);
                    rootNumbers = new  String[jsonArray.length()];
                    for (int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject BusRoot = jsonArray.getJSONObject(i);
                        String RootID =BusRoot.getString("RootID");
                        String From = BusRoot.getString("From");
                        String To= BusRoot.getString("To");
                        Log.e("ROOT",RootID);
                        rootNumbers[i]=RootID+" "+From+" - "+To;
                        Log.e("rootNumbers",rootNumbers[i]);


                    }
                    ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,rootNumbers);
                    txtSearchMap.setThreshold(1);
                    txtSearchMap.setAdapter(adapter);

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(request);
    }

    //==============================================================================================================
    private void removeBusStopMakers()
    {
        for (int i= 0 ; i<busStopMaker.length;i++)
        {
            busStopMaker[i].remove();
        }
    }

    //================================================================================================================
    public void removeBusMakers()
    {
        for (int i= 0 ; i<busMaker.length;i++)
        {
            busMaker[i].remove();
        }
    }

    class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(busMaker!=null)
            {
                removeBusMakers();
            }

            String response =(String) intent.getExtras().get("Busdeta");
            Log.e("Busdeta",response);
            try{
                JSONArray jsonArray = new JSONArray(response);
                busMaker=new Marker[jsonArray.length()];
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject busObject = jsonArray.getJSONObject(i);
                    JSONObject locationObject = busObject.getJSONObject("location");
                    LatLng latLng = new LatLng(locationObject.getDouble("lat"),locationObject.getDouble("lng"));
                    String RegisteredNo=busObject.getString("RegisteredNo");
                    Log.e("ssssssssssssssssss",RegisteredNo);
                    MarkerOptions options = new MarkerOptions().position(latLng).title(RegisteredNo).icon(BitmapDescriptorFactory.fromResource(R.drawable.busalong));
                    Log.e("MarkerOptions",options.getPosition().toString());
                    busMaker[i] = TgoogleMap.addMarker(options);

                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

}





