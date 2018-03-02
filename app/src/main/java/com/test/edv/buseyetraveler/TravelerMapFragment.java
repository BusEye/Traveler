package com.test.edv.buseyetraveler;


import android.Manifest;
import android.app.Activity;
import android.app.VoiceInteractor;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
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

    GoogleMap TgoogleMap;
    View TmapView;
    EditText txtSearchMap;
    Marker marker;
    Marker locationMaker;
    Marker DriverMaker;
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

    public TravelerMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.e("onCreateView", "Top");
        TmapView = inflater.inflate(R.layout.fragment_traveler_map, container, false);
        btnSearch = (Button)TmapView.findViewById(R.id.btnSearch);
        btnLiveLocation=(Button)TmapView.findViewById(R.id.btnliveLocation);
        txtSearchMap=(EditText) TmapView.findViewById(R.id.txtMapSearch);

        //===============================================================================

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Log.e("btnSearch","btnSearch");
                 SerchText = txtSearchMap.getText().toString();
                Log.e("String" , SerchText);

                if(!SerchText.isEmpty())
                {

                    TgoogleMap.clear();
                    fusedLocationProviderClient.removeLocationUpdates( locationCallback);
                    getDeviceLocation();
                    BusStopLocation(SerchText);
                    BusLocation();

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
                Log.e("btnLiveLocation","btnLiveLocation");
                if (locationMaker!=null && DriverMaker!=null){
                    Log.e("remove","Bus Stop and DriverMaker");

                    if(!SerchText.isEmpty())
                    {
                        TgoogleMap.clear();
                        BusStopLocation(SerchText);
                        BusLocation();
                    }
                    else
                    {
                        SerchText = null;
                        TgoogleMap.clear();

                    }

                  //  SerchText=null;
                }
                fusedLocationProviderClient.removeLocationUpdates( locationCallback);
                getDeviceLocation();

                CameraPosition colombo = CameraPosition.builder().target(currentLocation).zoom(15).bearing(0).tilt(45).build();
                TgoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(colombo));
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

//        Log.e("carent loaction details",currentLocation.latitude+" "+currentLocation.longitude);

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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        try {

            mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

            fusedLocationProviderClient.requestLocationUpdates(mLocationRequest,getlocationcallback(), Looper.myLooper());
        }
        catch (SecurityException e)
        {

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
    private  void  BusStopLocation(String root)
    {
        Log.e("root" , root);

        if (locationMaker!=null){
            Log.e("remove","DriverMaker");
            locationMaker.remove();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String Url = "https://buseye.000webhostapp.com/BusStop.php";
        StringRequest request = new StringRequest(Request.Method.GET, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try
                {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject BusStopDetails = jsonArray.getJSONObject(i);
                        LatLng latLng = new LatLng(BusStopDetails.getDouble("Latitude"),BusStopDetails.getDouble("Longitude"));
                        Log.e("Deta BST",BusStopDetails.getString("Name")+" "+BusStopDetails.getDouble("Latitude")+" "+BusStopDetails.getDouble("Longitude"));
                        MarkerOptions options = new MarkerOptions().position(latLng).title(BusStopDetails.getString("Name")).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bustop_32));
                        locationMaker=TgoogleMap.addMarker(options);
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
    private void BusLocation(){
       LatLng latLng = new LatLng(6.912932,79.972047);

        if (DriverMaker!=null){
            Log.e("remove","DriverMaker");
            DriverMaker.remove();
        }



        MarkerOptions options = new MarkerOptions().position(latLng).title("MY LCATION").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_32));
        DriverMaker=TgoogleMap.addMarker(options);
    }

}
