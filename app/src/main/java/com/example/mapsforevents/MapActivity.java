package com.example.mapsforevents;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mapsforevents.MainActivity;
import com.example.mapsforevents.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapActivity extends AppCompatActivity {

    private GoogleMap mMap;
    private DatabaseReference mDatabase;

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

        // Initialize Firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;
                // Customize the map as needed
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                // Set the default location to The University of Canberra
                LatLng defaultLocation = new LatLng(-35.238702, 149.085441);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));

                // Fetch event data from Firebase and add markers to the map
                fetchEventDataFromFirebase();
            }
        });
    }

    private void fetchEventDataFromFirebase() {
        mDatabase.child("events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    // Get event data from Firebase
                    String title = eventSnapshot.child("title").getValue(String.class);
                    double latitude = eventSnapshot.child("latitude").getValue(Double.class);
                    double longitude = eventSnapshot.child("longitude").getValue(Double.class);

                    // Console log the retrieved event data including latitude and longitude
                    Log.d("EventData", "Title: " + title + ", Latitude: " + latitude + ", Longitude: " + longitude);

                    // Add markers for event locations on the map
                    LatLng eventLocation = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(eventLocation).title(title));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
}
