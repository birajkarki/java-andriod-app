package com.example.mapsforevents;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditExistingActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextSummary, editTextTime, editTextDate, editTextContact, editTextLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_existing);

        // Initialize UI elements
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextSummary = findViewById(R.id.editTextSummary);
        editTextTime = findViewById(R.id.editTextTime);
        editTextDate = findViewById(R.id.editTextDate);
        editTextContact = findViewById(R.id.editTextContact);
        editTextLocation = findViewById(R.id.editTextLocation);
        Button buttonUpdateActivity = findViewById(R.id.buttonUpdateActivity);

        // Retrieve data from Intent extras
        String eventName = getIntent().getStringExtra("eventName");
        String activityTitle = getIntent().getStringExtra("activityTitle");
        String activitySummary = getIntent().getStringExtra("activitySummary");
        String activityTime = getIntent().getStringExtra("activityTime");
        String activityDate = getIntent().getStringExtra("activityDate");
        String activityContact = getIntent().getStringExtra("activityContact");
        String activityLocation = getIntent().getStringExtra("activityLocation");

        // Pre-fill the form with existing activity details
        editTextTitle.setText(activityTitle);
        editTextSummary.setText(activitySummary);
        editTextTime.setText(activityTime);
        editTextDate.setText(activityDate);
        editTextContact.setText(activityContact);
        editTextLocation.setText(activityLocation);

        // Implement button click listener for update functionality
        buttonUpdateActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve updated data from EditTexts
                String updatedTitle = editTextTitle.getText().toString().trim();
                String updatedSummary = editTextSummary.getText().toString().trim();
                String updatedTime = editTextTime.getText().toString().trim();
                String updatedDate = editTextDate.getText().toString().trim();
                String updatedContact = editTextContact.getText().toString().trim();
                String updatedLocation = editTextLocation.getText().toString().trim();

                // Update the activity with the new details
                // Here you can perform database operations to update the activity

                // Start ImageUploadActivity for adding image
                Intent intent = new Intent(EditExistingActivity.this, ImageUploadActivity.class);
                intent.putExtra("eventName", eventName); // Pass event name (optional, depending on your logic)
                intent.putExtra("activityTitle", updatedTitle);
                intent.putExtra("activitySummary", updatedSummary);
                intent.putExtra("activityTime", updatedTime);
                intent.putExtra("activityDate", updatedDate);
                intent.putExtra("activityContact", updatedContact);
                intent.putExtra("activityLocation", updatedLocation);
                startActivity(intent);

                // Display a toast message indicating success
                Toast.makeText(EditExistingActivity.this, "Activity updated successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
