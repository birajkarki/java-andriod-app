package com.example.mapsforevents;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mapsforevents.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class AddActivityActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextSummary, editTextTime, editTextDate, editTextContact;
    private Spinner spinnerLocation;
    private TextView textViewLocation;
    private SQLiteDatabase mDatabase;
    private String eventName;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextSummary = findViewById(R.id.editTextSummary);
        editTextTime = findViewById(R.id.editTextTime);
        editTextDate = findViewById(R.id.editTextDate);
        editTextContact = findViewById(R.id.editTextContact);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        TextView textViewEventName = findViewById(R.id.textViewEventName);
        textViewLocation = findViewById(R.id.textViewLocation);

        // Retrieve event name passed from previous activity
        eventName = getIntent().getStringExtra("eventName");
        textViewEventName.setText(eventName);

        // Initialize fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        mDatabase = dbHelper.getWritableDatabase();

        // spinner options
        setupSpinner();

        Button saveActivityButton = findViewById(R.id.buttonSaveActivity);
        saveActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve input data
                String title = editTextTitle.getText().toString().trim();
                String summary = editTextSummary.getText().toString().trim();
                String time = editTextTime.getText().toString().trim();
                String date = editTextDate.getText().toString().trim();
                String contact = editTextContact.getText().toString().trim();
                String location = spinnerLocation.getSelectedItem().toString();

                if (!title.isEmpty() && !summary.isEmpty() && !time.isEmpty() && !date.isEmpty() && !contact.isEmpty() && !location.isEmpty()) {
                    if (location.equals("Current Location")) {
                        // If "Current Location" is selected, fetch and store the current location
                        getCurrentLocation();
                    } else {
                        // If a predefined location is selected, insert its latitude and longitude into the database
                        LocationItem selectedItem = (LocationItem) spinnerLocation.getSelectedItem();
                        double latitude = selectedItem.getLatitude();
                        double longitude = selectedItem.getLongitude();
                        addActivity(title, summary, time, date, contact, location, latitude, longitude);
                        Toast.makeText(AddActivityActivity.this, "Activity added successfully", Toast.LENGTH_SHORT).show();
                        // Pass the entered data to EditExistingActivity
                        goToEditExistingActivity(title, summary, time, date, contact, location, latitude, longitude);
                    }
                } else {
                    Toast.makeText(AddActivityActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for the Get Location button
        Button getLocationButton = findViewById(R.id.buttonGetLocation);
        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check location permissions
                if (ContextCompat.checkSelfPermission(AddActivityActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, get current location
                    getCurrentLocation();
                } else {
                    // Permission not granted, request it
                    ActivityCompat.requestPermissions(AddActivityActivity.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }
            }
        });
    }

    // Method to set up spinner options
    private void setupSpinner() {
        List<LocationItem> locations = new ArrayList<>();

        // Add locations with latitude and longitude
        locations.add(new LocationItem("Library", -35.239868, 149.086441));
        locations.add(new LocationItem("Refectory", -35.235681, 149.077728));
        locations.add(new LocationItem("Building 6", -35.250191, 149.078245));
        locations.add(new LocationItem("UC Hub", -35.238995, 149.066694));
        locations.add(new LocationItem("UC Lodge", -35.219356, 149.078896));
        locations.add(new LocationItem("Current Location", 0, 0));

        // Create a custom adapter for the spinner
        ArrayAdapter<LocationItem> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locations);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(spinnerAdapter);

        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LocationItem selectedItem = (LocationItem) parent.getItemAtPosition(position);
                if (selectedItem.getName().equals("Current Location")) {
                    getCurrentLocation();
                } else {
                    double latitude = selectedItem.getLatitude();
                    double longitude = selectedItem.getLongitude();
                    textViewLocation.setText("Latitude: " + latitude + ", Longitude: " + longitude);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Method to insert activity data into SQLite database
    private void addActivity(String title, String summary, String time, String date, String contact, String location, double latitude, double longitude) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyDatabaseHelper.COLUMN_EVENT_NAME, eventName);
        contentValues.put(MyDatabaseHelper.COLUMN_TITLE, title);
        contentValues.put(MyDatabaseHelper.COLUMN_SUMMARY, summary);
        contentValues.put(MyDatabaseHelper.COLUMN_TIME, time);
        contentValues.put(MyDatabaseHelper.COLUMN_DATE, date);
        contentValues.put(MyDatabaseHelper.COLUMN_CONTACT, contact);
        contentValues.put(MyDatabaseHelper.COLUMN_LOCATION, location);
        contentValues.put(MyDatabaseHelper.COLUMN_LATITUDE, latitude);
        contentValues.put(MyDatabaseHelper.COLUMN_LONGITUDE, longitude);
        mDatabase.insert(MyDatabaseHelper.TABLE_ACTIVITIES, null, contentValues);
    }

    // Method to start EditExistingActivity
    private void goToEditExistingActivity(String title, String summary, String time, String date, String contact, String location, double latitude, double longitude) {
        Intent intent = new Intent(AddActivityActivity.this, EditExistingActivity.class);
        intent.putExtra("eventName", eventName);
        intent.putExtra("activityTitle", title);
        intent.putExtra("activitySummary", summary);
        intent.putExtra("activityTime", time);
        intent.putExtra("activityDate", date);
        intent.putExtra("activityContact", contact);
        intent.putExtra("activityLocation", location);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivity(intent);
        editTextTitle.setText("");
        editTextSummary.setText("");
        editTextTime.setText("");
        editTextDate.setText("");
        editTextContact.setText("");
    }

    // Method to retrieve the current location
    private void getCurrentLocation() {
        // Request last known location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            textViewLocation.setText("Latitude: " + latitude + ", Longitude: " + longitude);
                            addActivity("", "", "", "", "", "Current Location", latitude, longitude);
                            Toast.makeText(AddActivityActivity.this, "Current Location added successfully", Toast.LENGTH_SHORT).show();
                            goToEditExistingActivity("", "", "", "", "", "Current Location", latitude, longitude);
                        } else {
                            textViewLocation.setText("Location not available");
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // LocationItem class representing a location with latitude and longitude
    private static class LocationItem {
        private String name;
        private double latitude;
        private double longitude;

        public LocationItem(String name, double latitude, double longitude) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
