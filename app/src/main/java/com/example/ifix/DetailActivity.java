package com.example.ifix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    TextView detailJobNo, detailDelivery, detailTime, detailPhone, detailName, detailBrand, detailModel, detailColour, detailPassword, detailComplaint, detailStatus, detailExpense, detailAmount, detailPayment;
    LinearLayout imagesContainer;
    Button deleteButton, editButton, deliveredButton;
    String key;
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
            detailPhone.setText(bundle.getString("Phone"));
            detailName.setText(bundle.getString("Name"));
            detailBrand.setText(bundle.getString("Brand"));
            detailModel.setText(bundle.getString("Model"));
            detailColour.setText(bundle.getString("Colour"));
            detailPassword.setText(bundle.getString("Password"));
            detailComplaint.setText(bundle.getString("Complaint"));
            detailStatus.setText(bundle.getString("Status"));
            detailExpense.setText(bundle.getString("Expense"));
            detailAmount.setText(bundle.getString("Amount"));
            detailPayment.setText(bundle.getString("Payment"));
            detailTime.setText(bundle.getString("Time"));
            detailDelivery.setText(bundle.getString("Delivery"));
            detailJobNo.setText(bundle.getString("Job"));
            key = bundle.getString("Key");
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
                }
            }
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("EntryList").child(key);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("EntryList");
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
                    reference.child(key).removeValue();
                    Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    reference.child(key).removeValue();
                    Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
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
                        .putExtra("Key", key);
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
                        .putExtra("Images", imageUrls)
                        .putExtra("Job", detailJobNo.getText().toString())
                        .putExtra("Key", key);
                startActivity(intent);
            }
        });
    }
}
