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

    EditText detailPhone, detailName, detailBrand, detailModel, detailColour, detailPassword, detailComplaint, detailStatus;

    ImageView detailImage;
    Button deleteButton, editButton, deliveredButton;
    String key = "";
    DatabaseReference databaseReference;
    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailPhone = findViewById(R.id.detailPhone);
        detailPhone.setEnabled(false);
        detailImage = findViewById(R.id.detailImage);
        detailImage.setEnabled(false);
        detailName = findViewById(R.id.detailName);
        detailName.setEnabled(false);
        detailBrand = findViewById(R.id.detailBrand);
        detailBrand.setEnabled(false);
        detailModel = findViewById(R.id.detailModel);
        detailModel.setEnabled(false);
        detailColour = findViewById(R.id.detailColour);
        detailColour.setEnabled(false);
        detailPassword = findViewById(R.id.detailPassword);
        detailPassword.setEnabled(false);
        detailComplaint = findViewById(R.id.detailComplaint);
        detailComplaint.setEnabled(false);
        detailStatus = findViewById(R.id.detailStatus);
        detailStatus.setEnabled(false);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        deliveredButton=findViewById(R.id.deliveredButton);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            detailPhone.setText(bundle.getString("Phone"));
            detailName.setText(bundle.getString("Name"));
            detailBrand.setText(bundle.getString("Brand"));
            detailModel.setText(bundle.getString("Model"));
            detailColour.setText(bundle.getString("Colour"));
            detailPassword.setText(bundle.getString("Password"));
            detailComplaint.setText(bundle.getString("Complaint"));
            detailStatus.setText(bundle.getString("Status"));

            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Entry List").child(key);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Entry List");
                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child(key).removeValue();
                        Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
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
                        .putExtra("Image", imageUrl)
                        .putExtra("Key", key);
                startActivity(intent);
            }
        });
        deliveredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });
    }
    public void updateData(){
        String Name = detailName.getText().toString();
        String Phone = detailPhone.getText().toString();
        String Brand = detailBrand.getText().toString();
        String Model = detailModel.getText().toString();
        String Colour = detailColour.getText().toString();
        String Password = detailPassword.getText().toString();
        String Complaint = detailComplaint.getText().toString();
        String Status = "DELIVERED";
        DataClass dataClass = new DataClass(Name, Phone, Brand,Model,Colour,Password,Complaint,Status, imageUrl);

        databaseReference.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(DetailActivity.this, "Delivered", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailActivity.this, "Not Delivered", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
