package com.example.mapstrackingsampleapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SpecificUserMapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private ArrayList<LocationModel> locationArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_user_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        countDownTimer();
    }

    private void countDownTimer() {
        new CountDownTimer(10000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                onMapReady(mMap);
            }
        }.start();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mDatabase.child("users").child("1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LocationModel locationModel = dataSnapshot.getValue(LocationModel.class);
                if (locationModel != null)
                    plotMarkers(locationModel);

                countDownTimer();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void plotMarkers(LocationModel locationModel) {
        double latitude = locationModel.getLatitude();
        double longitude = locationModel.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Position")
                .snippet("Latitude: " + latitude + ", " +
                        "Longitude: " + longitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        locationArrayList.add(locationModel);
        Log.d("Location list", "plotMarkers: " + locationArrayList.size());

        //Draw Line
        ArrayList<LatLng> points = new ArrayList<>();
        if (locationArrayList != null) {
            for (int i = 0; i < locationArrayList.size(); i++) {
                points.add(new LatLng(locationArrayList.get(i).getLatitude(), locationArrayList.get(i).getLongitude()));
                mMap.addPolyline(new PolylineOptions().
                        addAll(points)
                        .width(5)
                        .color(Color.BLUE)
                        .geodesic(true));
            }
        }
    }
}
