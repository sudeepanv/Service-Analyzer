package com.example.ifix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    TextView detailJobNo,detailEstimate, detailDelivery, detailTime, detailPhone, detailName, detailBrand, detailModel, detailColour, detailPassword, detailComplaint, detailStatus, detailExpense, detailAmount, detailPayment;
    LinearLayout imagesContainer;
    Button deleteButton, editButton, deliveredButton;
    String key,dateOnly;
    DatabaseReference databaseReference;
    ArrayList<String> imageUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailTime = findViewById(R.id.detailTime);
        detailPhone = findViewById(R.id.detailPhone);
        imagesContainer = findViewById(R.id.imageContainer);
        detailName = findViewById(R.id.detailName);
        detailBrand = findViewById(R.id.detailBrand);
        detailModel = findViewById(R.id.detailModel);
        detailEstimate = findViewById(R.id.detailEstimate);
        detailColour = findViewById(R.id.detailColour);
        detailPassword = findViewById(R.id.detailPassword);
        detailComplaint = findViewById(R.id.detailComplaint);
        detailStatus = findViewById(R.id.detailStatus);
        detailExpense = findViewById(R.id.detailExpense);
        detailAmount = findViewById(R.id.detailAmount);
        detailPayment = findViewById(R.id.detailPayment);
        detailDelivery = findViewById(R.id.detailDelivery);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        deliveredButton = findViewById(R.id.deliveredButton);
        detailJobNo = findViewById(R.id.detailJobNo);



        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            detailPhone.setText(bundle.getString("Phone", ""));
            detailName.setText(bundle.getString("Name", ""));
            detailBrand.setText(bundle.getString("Brand", ""));
            detailModel.setText(bundle.getString("Model", ""));
            detailColour.setText(bundle.getString("Colour", ""));
            detailPassword.setText(bundle.getString("Password", ""));
            detailComplaint.setText(bundle.getString("Complaint", ""));
            detailStatus.setText(bundle.getString("Status", ""));
            detailExpense.setText(bundle.getString("Expense", ""));
            detailEstimate.setText(bundle.getString("Estimate", ""));
            detailAmount.setText(bundle.getString("Amount", ""));
            detailPayment.setText(bundle.getString("Payment", ""));
            detailTime.setText(bundle.getString("Time", ""));
            detailDelivery.setText(bundle.getString("Delivery", ""));
            detailJobNo.setText(bundle.getString("Job", ""));
            key = bundle.getString("Key", "");
            imageUrls = bundle.getStringArrayList("Images");
            if (imageUrls != null && !imageUrls.isEmpty()) {
                for (String imageUrl : imageUrls) {
                    ImageView imageView = new ImageView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(5, 0, 5, 0);
                    imageView.setAdjustViewBounds(true); // Adjust image bounds to maintain aspect ratio
                    imageView.setScaleType(ImageView.ScaleType.FIT_START); // Scale type to fit image inside bounds
                    Glide.with(this).load(imageUrl).into(imageView);
                    imageView.setLayoutParams(params); // Set layout params for the image view
                    imagesContainer.addView(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Retrieve the image URI or URL associated with the clicked ImageView
                            // For example, you can retrieve it from a list or directly from the ImageView's tag

                            // Example: Uri imageUri = (Uri) imageView.getTag();

                            // Once you have the URI or URL, you can open the image using an Intent
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(imageUrl), "image/*");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            // Check if there's an app available to handle the intent
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            } else {
                                // Handle the case where no app can handle the intent
                                Toast.makeText(getApplicationContext(), "No app available to open image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
            Date date = null;
            try {
                date = inputFormat.parse(detailTime.getText().toString());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            dateOnly = outputFormat.format(date);
        }
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Entry List").child(dateOnly).child(detailJobNo.getText().toString());
                if (imageUrls != null && !imageUrls.isEmpty()) {
                    for (String imageUrl : imageUrls) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Image deleted successfully
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(DetailActivity.this, "Failed to delete image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                DataClass dataClass=new DataClass(detailName.getText().toString(), detailPhone.getText().toString(), detailBrand.getText().toString(),detailModel.getText().toString(),
                        detailColour.getText().toString(), detailPassword.getText().toString(), detailComplaint.getText().toString(),
                        detailStatus.getText().toString(), imageUrls, detailTime.getText().toString(), detailJobNo.getText().toString(),
                        detailEstimate.getText().toString(),detailExpense.getText().toString(),detailAmount.getText().toString(),detailPayment.getText().toString(),detailDelivery.getText().toString());
//                    reference.child(key).removeValue();
//                    Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(DetailActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
                DatabaseReference recycleBinRef = FirebaseDatabase.getInstance().getReference("RecycleBin");

                // Add item to RecycleBin
                recycleBinRef.child(dateOnly).child(detailJobNo.getText().toString()).setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Item added to RecycleBin, now remove it from Entry List
                            reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(DetailActivity.this, "Moved to BIN", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(DetailActivity.this, "Failed to DELETE", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(DetailActivity.this, "Failed to move to BIN", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, UpdateActivity.class)
                        .putExtra("Name", detailName.getText().toString())
                        .putExtra("Phone", detailPhone.getText().toString())
                        .putExtra("Brand", detailBrand.getText().toString())
                        .putExtra("Model", detailModel.getText().toString())
                        .putExtra("Colour", detailColour.getText().toString())
                        .putExtra("Password", detailPassword.getText().toString())
                        .putExtra("Complaint", detailComplaint.getText().toString())
                        .putExtra("Status", detailStatus.getText().toString())
                        .putExtra("Job", detailJobNo.getText().toString())
                        .putExtra("Images", imageUrls)
                        .putExtra("Time",detailTime.getText().toString())
                        .putExtra("Estimate",detailEstimate.getText().toString())
                        .putExtra("Key", key)
                        .putExtra("Date", dateOnly);
                startActivity(intent);
            }
        });

        deliveredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, Delivery.class)
                        .putExtra("Name", detailName.getText().toString())
                        .putExtra("Phone", detailPhone.getText().toString())
                        .putExtra("Brand", detailBrand.getText().toString())
                        .putExtra("Model", detailModel.getText().toString())
                        .putExtra("Colour", detailColour.getText().toString())
                        .putExtra("Password", detailPassword.getText().toString())
                        .putExtra("Complaint", detailComplaint.getText().toString())
                        .putExtra("Status", detailStatus.getText().toString())
                        .putExtra("Expense", detailExpense.getText().toString())
                        .putExtra("Amount", detailAmount.getText().toString())
                        .putExtra("Payment", detailPayment.getText().toString())
                        .putExtra("Estimate",detailEstimate.getText().toString())
                        .putExtra("Images", imageUrls)
                        .putExtra("Job", detailJobNo.getText().toString())
                        .putExtra("Time",detailTime.getText().toString())
                        .putExtra("Key", key)
                        .putExtra("Date", dateOnly);
                startActivity(intent);
            }
        });
    }
}
