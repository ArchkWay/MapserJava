package com.example.mapserjava;

import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mapserjava.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap = null;
    ActivityMapsBinding binding = null;
    LatLng locationA = null;
    int countMarkers = 0;
    double destination = 0.002;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map));
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        initUi();
    }

    private void initUi(){
        Button buttonClear = findViewById(R.id.btnClear);
        Button btnA = findViewById(R.id.btnA);
        Button btnB = findViewById(R.id.btnB);
        buttonClear.setOnClickListener(view -> {
            mMap.clear();
            btnA.setVisibility(VISIBLE);
            btnB.setVisibility(VISIBLE);
            countMarkers = 0;
        });
        btnA.setOnClickListener(view -> {
            createMarker(mMap, getString(R.string.a), view);
        });
        btnB.setOnClickListener(view -> {
            createMarker(mMap, getString(R.string.b), view);
        });
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng rostov = new LatLng(47.2313, 39.7233); //default location Rostov
        mMap.moveCamera(CameraUpdateFactory.newLatLng(rostov));
    }

    private void createMarker(GoogleMap mMap, String title, View button){
        if(countMarkers < 2) {
            countMarkers++;
            mMap.setOnMapClickListener(latLng -> {
                switch (countMarkers) {
                    case (1): locationA = latLng;
                    case (2): polyline(latLng);
                }
                mMap.addMarker(new MarkerOptions().position(latLng).title(title));
                mMap.setOnMapClickListener(l  -> {});
                button.setVisibility(View.GONE);
            });
        }
    }

    private void polyline(LatLng locationB) {
        createPolyline(locationA, locationB);
        createPolyline(new LatLng(locationA.latitude - destination, locationA.longitude - destination),
                new LatLng(locationB.latitude - destination, locationB.longitude - destination));
        createPolyline(new LatLng(locationA.latitude + destination, locationA.longitude + destination),
                new LatLng(locationB.latitude + destination, locationB.longitude + destination));
    }

    private void createPolyline(LatLng a, LatLng b) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(new LatLng(a.latitude, a.longitude))
                .add(new LatLng(b.latitude, b.longitude));
        mMap.addPolyline(polylineOptions);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}