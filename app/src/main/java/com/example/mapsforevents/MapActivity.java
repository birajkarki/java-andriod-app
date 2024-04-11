package com.example.mapsforevents;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mapsforevents.MainActivity;
import com.example.mapsforevents.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize back button
        ImageButton backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainActivity when back button is clicked
                Intent intent = new Intent(MapActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finish the current activity to prevent going back to it when pressing back
            }
        });

        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // Customize the map as needed
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                LatLng collegeLocation = new LatLng(35.00116, 135.7681);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(collegeLocation, 15));
            }
        });
    }
}
