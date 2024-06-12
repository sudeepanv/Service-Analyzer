package com.example.ifix;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import android.widget.Spinner;
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
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class UpdateActivity extends AppCompatActivity {

    ImageView updateImage;
    Button updateButton;
    EditText updateName, updatePhone, updateBrand, updateModel, updateColour,updatePassword,updateComplaint;
    String imageUrl,Status;
    String key, oldImageURL;
    Uri uri;
    AutoCompleteTextView updateStatus;
    ArrayAdapter<String> arrayAdapter;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        updateButton = findViewById(R.id.updateButton);

        updateImage = findViewById(R.id.updateImage);
        updateName = findViewById(R.id.updateName);
        updatePhone = findViewById(R.id.updatePhone);
        updateBrand = findViewById(R.id.updateBrand);
        updateModel = findViewById(R.id.updateModel);
        updateColour= findViewById(R.id.updateColour);
        updatePassword = findViewById(R.id.updatePassword);
        updateComplaint = findViewById(R.id.updateComplaint);
        String[] Statuslist = getResources().getStringArray(R.array.Statuslist);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdownstatus, Statuslist);
        updateStatus =findViewById(R.id.updateStatus);
        updateStatus.setAdapter(arrayAdapter);
        updateStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Status=adapterView.getItemAtPosition(position).toString();
            }
        });
        updateStatus.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                updateStatus.showDropDown();
            }
        });

        // Show suggestions when the field is clicked
        updateStatus.setOnClickListener(v -> updateStatus.showDropDown());

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = Objects.requireNonNull(data).getData();
                            updateImage.setImageURI(uri);
                        } else {
                            Toast.makeText(UpdateActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            Glide.with(UpdateActivity.this).load(bundle.getString("Image")).into(updateImage);
            updateName.setText(bundle.getString("Name"));
            updatePhone.setText(bundle.getString("Phone"));
            updateBrand.setText(bundle.getString("Brand"));
            updateModel.setText(bundle.getString("Model"));
            updateColour.setText(bundle.getString("Colour"));
            updatePassword.setText(bundle.getString("Password"));
            updateComplaint.setText(bundle.getString("Complaint"));
            updateStatus.setText(bundle.getString("Status"));
            key = bundle.getString("Key");
            oldImageURL = bundle.getString("Image");
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Entry List").child(key);

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
    }
    public void saveData(){
        if (uri == null) {
            // No new image selected, update data with old image URL
            imageUrl = oldImageURL;
            updateData();
        } else {
            // New image selected, upload it
            storageReference = FirebaseStorage.getInstance().getReference().child("Android Images").child(Objects.requireNonNull(uri.getLastPathSegment()));

            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
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
                    imageUrl = urlImage.toString();
                    updateData();
                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(UpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void updateData(){
        String Name = updateName.getText().toString();
        String Phone = updatePhone.getText().toString();
        String Brand = updateBrand.getText().toString();
        String Model = updateModel.getText().toString();
        String Colour = updateColour.getText().toString();
        String Password = updatePassword.getText().toString();
        String Complaint = updateComplaint.getText().toString();
        String Status = updateStatus.getText().toString();
        DataClass dataClass = new DataClass(Name, Phone, Brand,Model,Colour,Password,Complaint,Status, imageUrl);

        databaseReference.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    if (uri != null) {
                        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
                        reference.delete();
                    }
                    Toast.makeText(UpdateActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
