package com.example.ifix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailActivity extends AppCompatActivity {

    TextView detailJobNo,detailDelivery,detailTime,detailPhone, detailName, detailBrand, detailModel, detailColour, detailPassword, detailComplaint, detailStatus,detailExpense,detailAmount,detailPayment;

    ImageView detailImage;
    Button deleteButton, editButton, deliveredButton;
    String key;
    DatabaseReference databaseReference;
    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        detailTime=findViewById(R.id.detailTime);
        detailPhone = findViewById(R.id.detailPhone);
        detailImage = findViewById(R.id.detailImage);
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
        detailDelivery=findViewById(R.id.detailDelivery);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        deliveredButton = findViewById(R.id.deliveredButton);
        detailJobNo=findViewById(R.id.detailJobNo);

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
            imageUrl = bundle.getString("Image");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this).load(imageUrl).into(detailImage);
            } else {
                detailImage.setImageResource(R.drawable.imgupload); // Set a placeholder image if there is no image URL
            }
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Entry List").child(key);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Entry List");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            reference.child(key).removeValue();
                            Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailActivity.this, "Failed to delete image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
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
                        .putExtra("Job",detailJobNo.getText().toString())
                        .putExtra("Image", imageUrl)
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
                        .putExtra("Image", imageUrl)
                        .putExtra("Job",detailJobNo.getText().toString())
                        .putExtra("Key", key);
                startActivity(intent);
            }
        });
    }
}

