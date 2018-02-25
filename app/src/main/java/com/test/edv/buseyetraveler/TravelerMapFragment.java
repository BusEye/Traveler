package com.test.edv.buseyetraveler;


import android.Manifest;
import android.app.Activity;
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

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Log.e("btnSearch","btnSearch");
                 SerchText = txtSearchMap.getText().toString();
                Log.e("String" , SerchText);
                if(!SerchText.isEmpty())
                {
                    BusStopLocation(SerchText);
                    BusLocation();
                }
                else
                {
                    Toast.makeText(getContext(),"Plase enter Root Number",Toast.LENGTH_LONG).show();
                }


            }
        });


        btnLiveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("btnLiveLocation","btnLiveLocation");
                fusedLocationProviderClient.removeLocationUpdates( locationCallback);
                getDeviceLocation();
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

       // googleMap.addMarker(new MarkerOptions().position(new LatLng(6.894070, 79.902481)).title("Colombo").snippet("go"));
      //  CameraPosition colombo = CameraPosition.builder().target(new LatLng(6.894070, 79.902481)).zoom(10).bearing(0).tilt(45).build();
      //  googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(colombo));

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

                LatLng latLng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());

                if (marker!=null){
                    Log.e("remove","remove");
                    marker.remove();
                }
                MarkerOptions options = new MarkerOptions().position(latLng).title("MY LCATION").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow));
                marker=TgoogleMap.addMarker(options);
                CameraPosition colombo = CameraPosition.builder().target(latLng).zoom(15).bearing(0).tilt(45).build();
                TgoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(colombo));

                if(SerchText != null)
                {
                    Log.e("remove","Bus location");
                    BusLocation();
                }

            }
        };
        return locationCallback;
    }
 //=====================================================================================================================
    private  void  BusStopLocation(String root)
    {
        Log.e("root" , root);
        MarkerOptions options = new MarkerOptions().position(new LatLng(6.906731, 79.964976)).title("MY LCATION").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_stop));
        locationMaker=TgoogleMap.addMarker(options);
    }

//-=============================================================================================================================
    private void  BusLocation(){
        LatLng latLng = new LatLng(6.912932,79.972047);
        if (DriverMaker!=null){
            Log.e("remove","DriverMaker");
            DriverMaker.remove();
        }
        MarkerOptions options = new MarkerOptions().position(latLng).title("MY LCATION").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bus));
        DriverMaker=TgoogleMap.addMarker(options);
    }

}
