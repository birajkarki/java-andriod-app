package com.example.mapsforevents;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editTextEventName;
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        editTextEventName = findViewById(R.id.editTextEventName);
        Button addActivityButton = findViewById(R.id.addActivityButton);
        Button openMapButton = findViewById(R.id.openMapButton);

        // Create an instance of MyDatabaseHelper
        dbHelper = new MyDatabaseHelper(this);

        // Add click listener for Add Activity button
        addActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve event name entered by user
                String eventName = editTextEventName.getText().toString().trim();
                // Store event name in SQLite database
                if (!eventName.isEmpty()) {
                    // Add the event to the database
                    long newRowId = addEvent(eventName);
                    // Navigate to AddActivity activity
                    Intent intent = new Intent(MainActivity.this, AddActivityActivity.class);
                    // Pass the event name and ID to AddActivity
                    intent.putExtra("eventName", eventName);
                    intent.putExtra("eventId", newRowId);
                    startActivity(intent);
                } else {
                    // Show toast if event name is empty
                    Toast.makeText(MainActivity.this, "Please enter an event name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add click listener for Open Map button
        openMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the MapActivity
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }

    private long addEvent(String eventName) {
        long newRowId = -1; // Initialize newRowId with an invalid value

        try {
            // Get writable database
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Prepare the content values to insert
            ContentValues contentValues = new ContentValues();
            contentValues.put(MyDatabaseHelper.COLUMN_EVENT_NAME, eventName);

            // Insert the event into the database
            newRowId = db.insertOrThrow(MyDatabaseHelper.TABLE_ACTIVITIES, null, contentValues);

            // No need to close the database here because it's managed by try-with-resources
        } catch (Exception e) {
            // Handle any exceptions, such as SQLiteConstraintException
            e.printStackTrace(); // Print the exception stack trace for debugging
        }

        return newRowId;
    }
}
