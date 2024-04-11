package com.example.mapsforevents;

import android.app.Activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImageUploadActivity extends Activity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Uri filePath;
    private DatabaseReference mDatabase;
    private String eventName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        imageView = findViewById(R.id.imageViewEvent);

        // Initialize Firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get the event name from the intent
        eventName = getIntent().getStringExtra("eventName");

        // Set onClickListener for buttonLoadImage
        Button buttonLoadImage = findViewById(R.id.buttonLoadImage);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Set onClickListener for buttonDone
        Button buttonDone = findViewById(R.id.buttonDone);
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Upload image logic
                if (filePath != null) {
                    // Perform upload operation
                    uploadImage();
                } else {
                    Toast.makeText(ImageUploadActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        Log.d("ImageUploadActivity", "Event Name: " + eventName);
        Log.d("ImageUploadActivity", "Event Title: " + getIntent().getStringExtra("activityTitle"));
        Log.d("ImageUploadActivity", "Event Summary: " + getIntent().getStringExtra("activitySummary"));
        Log.d("ImageUploadActivity", "Event Time: " + getIntent().getStringExtra("activityTime"));
        Log.d("ImageUploadActivity", "Event Date: " + getIntent().getStringExtra("activityDate"));
        Log.d("ImageUploadActivity", "Event Contact: " + getIntent().getStringExtra("activityContact"));
        Log.d("ImageUploadActivity", "Event Location: " + getIntent().getStringExtra("activityLocation"));
        Log.d("ImageUploadActivity", "Image URI: " + filePath.toString());
//        Log.d("ImageUploadActivity", "longitute" + getIntent().getStringExtra("activity"))

        // Now, you can store this information in Firebase Realtime Database

        String eventId = mDatabase.child("events").push().getKey();
        Map<String, Object> eventValues = new HashMap<>();
        eventValues.put("eventName", eventName);
        eventValues.put("title", getIntent().getStringExtra("activityTitle"));
        eventValues.put("summary", getIntent().getStringExtra("activitySummary"));
        eventValues.put("time", getIntent().getStringExtra("activityTime"));
        eventValues.put("date", getIntent().getStringExtra("activityDate"));
        eventValues.put("contact", getIntent().getStringExtra("activityContact"));
        eventValues.put("location", getIntent().getStringExtra("activityLocation"));
        eventValues.put("imageUri", filePath.toString());
        assert eventId != null;
        mDatabase.child("events").child(eventId).setValue(eventValues);



        // Display a toast message indicating success
        Toast.makeText(ImageUploadActivity.this, "Event details uploaded successfully!", Toast.LENGTH_SHORT).show();

        // Navigate back to the home activity
        startActivity(new Intent(ImageUploadActivity.this, MainActivity.class));
        finish(); // Close the current activity
    }
}
