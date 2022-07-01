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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraMoveListener {

    GoogleMap mMap = null;
    ActivityMapsBinding binding = null;
    LatLng locationA = new LatLng(0.0, 0.0);
    LatLng locationB = new LatLng(0.0, 0.0);
    int countMarkers = 0;
    double destination = 0.002;
    LatLng southWest = null;
    LatLng northEast = null;

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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rostov, 18f));
        mMap.setOnCameraMoveListener(this);
        setScreenBounds();
    }


    private void setLocations(LatLng locationB) {
                createLine(locationA, locationB);
                createLine(
                        new LatLng(locationA.latitude - destination, locationA.longitude - destination),
                        new LatLng(locationB.latitude - destination, locationB.longitude - destination));
                createLine(
                        new LatLng(locationA.latitude + destination, locationA.longitude + destination),
                        new LatLng(locationB.latitude + destination, locationB.longitude + destination));

    }

    private void createLine(LatLng a, LatLng b) {
        LatLng pointA = new LatLng(a.latitude, a.longitude);
        LatLng pointB = new LatLng(b.latitude, b.longitude);

        PolylineOptions polylineOptions = new PolylineOptions();
        List<LatLng> pointsList = generateNeedingPoints(pointA, pointB);
        for(int i = 0; i < pointsList.size(); i++) {
            polylineOptions.add(pointsList.get(i));
        }
        mMap.addPolyline(polylineOptions);
    }

    private void polyline(LatLng locationB) {
        createLine(locationA, locationB);
        createLine(new LatLng(locationA.latitude - destination, locationA.longitude - destination),
                new LatLng(locationB.latitude - destination, locationB.longitude - destination));
        createLine(new LatLng(locationA.latitude + destination, locationA.longitude + destination),
                new LatLng(locationB.latitude + destination, locationB.longitude + destination));
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void setScreenBounds() {
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        southWest = bounds.southwest;
        northEast = bounds.northeast;
    }

    private List<LatLng> generateNeedingPoints(LatLng a, LatLng b )  {
        setScreenBounds();
        LatLng c;
        LatLng d;
        double longitudeC;
        double longitudeD;
        if (a.longitude < b.longitude) longitudeC = northEast.longitude;
        else longitudeC = southWest.longitude;
        if (a.longitude < b.longitude) {
            longitudeD = southWest.longitude;
        } else longitudeD = northEast.longitude;
        c = calculateEdgeButton(a, b, longitudeC);
        d = calculateEdgeButton(b, a, longitudeD);
        List<LatLng> list = new ArrayList();
        list.add(d);
        list.add(a);
        list.add(b);
        list.add(c);
        return list;
    }

    private LatLng calculateEdgeButton(LatLng a, LatLng b, Double edgeLng) {
        double longChange = b.longitude - a.longitude;
        double latChange = b.latitude - a.latitude;
        double slope = latChange / longChange;
        double longChangePointC = edgeLng - b.longitude;
        double latChangePointC = longChangePointC * slope;
        double latitudeC = b.latitude + latChangePointC;
        return new LatLng(latitudeC, edgeLng);
    }

    private void createMarker(GoogleMap mMap, String title, View button){
        if(countMarkers < 2) {
            countMarkers++;
            mMap.setOnMapClickListener(latLng -> {
                switch (countMarkers) {
                    case (1): {
                        locationA = latLng;
                        break;
                    }
                    case (2): {
                        polyline(latLng);
                        locationB = latLng;
                        break;
                    }
                }
                mMap.addMarker(new MarkerOptions().position(latLng).title(title));
                mMap.setOnMapClickListener(l  -> {});
                button.setVisibility(View.GONE);
            });
        }
    }

    @Override
    public void onCameraMove() {
        setScreenBounds();
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(locationA));
        mMap.addMarker(new MarkerOptions().position(locationB));
        setLocations(locationB);
    }
}