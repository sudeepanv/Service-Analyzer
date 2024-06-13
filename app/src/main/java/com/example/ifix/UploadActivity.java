package com.example.ifix;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class UploadActivity extends AppCompatActivity {

    ImageView uploadImage;
    Button saveButton;
    int maxjob;
    List<DataClass> dataList = new ArrayList<>();

    EditText uploadBrand, uploadName, uploadPhone, uploadModel,uploadPassword, uploadComplaint;
    String imageURL,Status,Colour;
    AutoCompleteTextView uploadStatus,uploadColour;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload); // Ensure this matches your XML layout file name

        uploadImage = findViewById(R.id.uploadImage);
        uploadName = findViewById(R.id.uploadName);
        uploadPhone = findViewById(R.id.uploadPhone);
        uploadBrand = findViewById(R.id.uploadBrand);
        uploadModel = findViewById(R.id.uploadModel);
        uploadColour= findViewById(R.id.uploadColour);
        uploadPassword = findViewById(R.id.uploadPassword);
        uploadComplaint = findViewById(R.id.uploadComplaint);
        uploadStatus = findViewById(R.id.uploadStatus);
        saveButton = findViewById(R.id.saveButton);
        String[] Statuslist = getResources().getStringArray(R.array.Statuslist);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.dropdownstatus, Statuslist);
        uploadStatus.setAdapter(arrayAdapter);
        uploadStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Status=adapterView.getItemAtPosition(position).toString();
            }
        });
        uploadStatus.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                uploadStatus.showDropDown();
            }
        });
        uploadStatus.setOnClickListener(v -> uploadStatus.showDropDown());

        String[] Colourlist = getResources().getStringArray(R.array.Colourlist);
        ArrayAdapter<String> ColourAdapter = new ArrayAdapter<>(this, R.layout.dropdownstatus, Colourlist);
        uploadColour.setAdapter(ColourAdapter);
        uploadColour.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Colour=adapterView.getItemAtPosition(position).toString();
            }
        });
        uploadColour.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                uploadColour.showDropDown();
            }
        });
        uploadColour.setOnClickListener(v -> uploadColour.showDropDown());


        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                uri = data.getData();
                                uploadImage.setImageURI(uri);
                            } else {
                                Toast.makeText(UploadActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(UploadActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uri != null) {
                    saveData();
                } else {
                    uploadData();
//                    Toast.makeText(UploadActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });
        fetchDataList();
    }
    private void fetchDataList() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Entry List");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                    if (dataClass != null) {
                        dataList.add(dataClass);
                        int job;
                        try {
                            job = Integer.parseInt(dataClass.getDataJobNo());
                        } catch (NumberFormatException e) {
                            Toast.makeText(UploadActivity.this, "Job error", Toast.LENGTH_SHORT).show();
                            continue; // Skip this entry if job number is not valid
                        }
                        if (maxjob < job) {
                            maxjob = job;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UploadActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveData() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
                .child(Objects.requireNonNull(uri.getLastPathSegment()));

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageURL = urlImage.toString();
                uploadData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(UploadActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadData() {
        String Name = uploadName.getText().toString();
        String Phone = uploadPhone.getText().toString();
        String Brand = uploadBrand.getText().toString();
        String Model = uploadModel.getText().toString();
        String Password = uploadPassword.getText().toString();
        String Complaint = uploadComplaint.getText().toString();
        String Time = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        if (Name.isEmpty() || Phone.isEmpty() || Brand.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String job =Integer.toString(maxjob+=1);
        DataClass dataClass = new DataClass(Name, Phone, Brand,Model,Colour,Password,Complaint,Status, imageURL,Time,job);
        FirebaseDatabase.getInstance().getReference("Entry List").child(Time)
                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UploadActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(UploadActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(UploadActivity.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}