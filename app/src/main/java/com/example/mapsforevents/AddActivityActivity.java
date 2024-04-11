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
                    addActivity(title, summary, time, date, contact, location);
                    Toast.makeText(AddActivityActivity.this, "Activity added successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddActivityActivity.this, EditExistingActivity.class);
                    intent.putExtra("eventName", eventName);
                    intent.putExtra("activityTitle", title);
                    intent.putExtra("activitySummary", summary);
                    intent.putExtra("activityTime", time);
                    intent.putExtra("activityDate", date);
                    intent.putExtra("activityContact", contact);
                    intent.putExtra("activityLocation", location);


                    startActivity(intent);
                    editTextTitle.setText("");
                    editTextSummary.setText("");
                    editTextTime.setText("");
                    editTextDate.setText("");
                    editTextContact.setText("");
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
        List<String> locations = new ArrayList<>();
        locations.add("Select a location");
        locations.add("Library");
        locations.add("Refectory");
        locations.add("Building 6");
        locations.add("UC Hub");
        locations.add("UC Lodge");
        locations.add("Current Location");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locations);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(spinnerAdapter);

        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLocation = parent.getItemAtPosition(position).toString();
                if (selectedLocation.equals("Current Location")) {
                    getCurrentLocation();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Method to insert activity data into SQLite database
    private void addActivity(String title, String summary, String time, String date, String contact, String location) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyDatabaseHelper.COLUMN_EVENT_NAME, eventName);
        contentValues.put(MyDatabaseHelper.COLUMN_TITLE, title);
        contentValues.put(MyDatabaseHelper.COLUMN_SUMMARY, summary);
        contentValues.put(MyDatabaseHelper.COLUMN_TIME, time);
        contentValues.put(MyDatabaseHelper.COLUMN_DATE, date);
        contentValues.put(MyDatabaseHelper.COLUMN_CONTACT, contact);
        contentValues.put(MyDatabaseHelper.COLUMN_LOCATION, location);
        mDatabase.insert(MyDatabaseHelper.TABLE_ACTIVITIES, null, contentValues);
    }

    // Method to retrieve the current location
    private void getCurrentLocation() {
        // Request last known location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions

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
}
