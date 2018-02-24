package com.test.edv.buseyetraveler;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class TravelerMapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap TgoogleMap;
    View TmapView;
    EditText txtSearchMap;
    Marker marker;
    Marker locationMaker;
    FusedLocationProviderClient fusedLocationProviderClient;
    ImageView imgGpsView;
    Button setBusinessLocation;
    private float zoomMap = 16;
    private double latitude;
    private double longitude;
    private boolean avalabilityButton = false;
    private static final String TAG = "GmapFragment";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean locationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST = 1234;
    public String id;
    public String nearest;

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
        getDeviceLocation();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(6.894070, 79.902481)).title("Colombo").snippet("go"));
        CameraPosition colombo = CameraPosition.builder().target(new LatLng(6.894070, 79.902481)).zoom(10).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(colombo));

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

    private void getDeviceLocation()
    {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        try {



            final Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener()
            {
                @Override
                public void onComplete(@NonNull Task task)
                {
                  try
                     {
                      if (task.isComplete())
                      {

                        Log.d(TAG, "onComplete: found the location");
                        Location currentLocation = (Location) task.getResult();
                        Log.d(TAG, "location: "+currentLocation.toString());
                        Log.d("current location",currentLocation.getLatitude()+" "+currentLocation.getLongitude());
                      //  goToLocationZoom(currentLocation.getLatitude(), currentLocation.getLongitude(), zoomMap);

                      }
                      else
                          {

                              Log.d(TAG, "onComplete: didn't found the location");
                              Toast.makeText(getContext(), "Unable to find the location", Toast.LENGTH_LONG).show();
                          }
                     }
                  catch (NullPointerException e)
                     {
                       Log.e(TAG,"NullpointException: "+e.getMessage());
                       Toast.makeText(getContext(),"Turn On the GPS",Toast.LENGTH_LONG).show();
                     }
                  catch (Exception e)
                     {
                        e.printStackTrace();
                     }
                }
            });

        }
        catch (SecurityException e)
        {

        }

    }

}
