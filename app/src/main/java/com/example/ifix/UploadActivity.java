package com.example.ifix;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class UploadActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String IMAGE_PREFIX = "JPEG_";
    private static final String IMAGE_SUFFIX = ".jpg";
    private static final String IMAGE_DIR = "Android Images";

    private LinearLayout imageContainer;
    private Button  saveButton;
    private ImageButton addImageButton;
    private int maxjob;
    private List<DataClass> dataList = new ArrayList<>();
    private List<Uri> imageUris = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();
    private Uri currentImageUri;

    private EditText uploadBrand, uploadName, uploadPhone, uploadModel, uploadPassword, uploadComplaint;
    private AutoCompleteTextView uploadStatus, uploadColour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        initializeViews();
        setupDropdowns();
        fetchDataList();

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!imageUris.isEmpty()) {
                    saveImages();
                } else {
                    uploadData();
                }
            }
        });
    }

    private void initializeViews() {
        imageContainer = findViewById(R.id.imageContainer);
        addImageButton = findViewById(R.id.addImageButton);
        saveButton = findViewById(R.id.saveButton);
        uploadName = findViewById(R.id.uploadName);
        uploadPhone = findViewById(R.id.uploadPhone);
        uploadBrand = findViewById(R.id.uploadBrand);
        uploadModel = findViewById(R.id.uploadModel);
        uploadColour = findViewById(R.id.uploadColour);
        uploadPassword = findViewById(R.id.uploadPassword);
        uploadComplaint = findViewById(R.id.uploadComplaint);
        uploadStatus = findViewById(R.id.uploadStatus);
    }

    private void setupDropdowns() {
        setupDropdown1(uploadStatus, R.array.Statuslist, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                uploadStatus.setText(adapterView.getItemAtPosition(position).toString(), false);
            }
        });

        setupDropdown2(uploadColour, R.array.Colourlist, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                uploadColour.setText(adapterView.getItemAtPosition(position).toString(), false);
            }
        });
    }

    private void setupDropdown1(AutoCompleteTextView dropdown, int arrayResourceId, AdapterView.OnItemClickListener onItemClickListener) {
        String[] items = getResources().getStringArray(arrayResourceId);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdownstatus, items);
        dropdown.setAdapter(adapter);
        dropdown.setText(items[0], false);
        dropdown.setOnItemClickListener(onItemClickListener);
        dropdown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                dropdown.showDropDown();
            }
        });
        dropdown.setOnClickListener(v -> dropdown.showDropDown());
    }
    private void setupDropdown2(AutoCompleteTextView dropdown, int arrayResourceId, AdapterView.OnItemClickListener onItemClickListener) {
        String[] items = getResources().getStringArray(arrayResourceId);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdownstatus, items);
        dropdown.setAdapter(adapter);
        dropdown.setText(items[3], false);
        dropdown.setOnItemClickListener(onItemClickListener);
        dropdown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                dropdown.showDropDown();
            }
        });
        dropdown.setOnClickListener(v -> dropdown.showDropDown());
    }

    private void fetchDataList() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("EntryList");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                    if (dataClass != null) {
                        dataList.add(dataClass);
                        int job = parseJobNumber(dataClass.getDataJobNo());
                        if (job > maxjob) {
                            maxjob = job;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Failed to fetch data");
            }
        });
    }

    private int parseJobNumber(String jobNo) {
        try {
            return Integer.parseInt(jobNo);
        } catch (NumberFormatException e) {
            showToast("Invalid job number: " + jobNo);
            return -1;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                showToast("Error creating file");
            }
            if (photoFile != null) {
                currentImageUri = FileProvider.getUriForFile(this, "com.example.ifix.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = IMAGE_PREFIX + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, IMAGE_SUFFIX, storageDir);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageUris.add(currentImageUri);
            // Add all images to the container
            addImagesToContainer(imageUris);
        }
    }

    private void addImagesToContainer(List<Uri> uris) {
        // Clear the container before adding images
        imageContainer.removeAllViews();

        for (Uri uri : uris) {
            ImageView imageView = new ImageView(this);
            imageView.setImageURI(uri);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(5, 0, 5, 0);
            imageView.setAdjustViewBounds(true); // Adjust image bounds to maintain aspect ratio
            imageView.setScaleType(ImageView.ScaleType.FIT_START); // Scale type to fit image inside bounds
            imageView.setLayoutParams(params); // Set layout params for the image view
            imageContainer.addView(imageView); // Add the image view to the container
        }
    }


    private void saveImages() {
        showProgressDialog();
        for (Uri uri : imageUris) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(IMAGE_DIR)
                    .child(Objects.requireNonNull(uri.getLastPathSegment()));
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                imageUrls.add(task.getResult().toString());
                                if (imageUrls.size() == imageUris.size()) {
                                    uploadData();
                                    hideProgressDialog();
                                }
                            } else {
                                showToast("Failed to get image URL");
                                hideProgressDialog();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showToast("Failed to upload image");
                    hideProgressDialog();
                }
            });
        }
    }

    private void uploadData() {
        String name = uploadName.getText().toString();
        String phone = uploadPhone.getText().toString();
        String brand = uploadBrand.getText().toString();
        String model = uploadModel.getText().toString();
        String password = uploadPassword.getText().toString();
        String complaint = uploadComplaint.getText().toString();
        String time = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        if (name.isEmpty() || phone.isEmpty() || brand.isEmpty()) {
            showToast("Please fill all the fields");
            return;
        }

        String job = Integer.toString(maxjob += 1);
        DataClass dataClass = new DataClass(name, phone, brand, model, uploadColour.getText().toString(), password, complaint, uploadStatus.getText().toString(), imageUrls, time, job);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("EntryList");
        databaseReference.child(job).setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    showToast("Data uploaded successfully");
                    finish();
                } else {
                    showToast("Failed to upload data");
                }
            }
        });
    }
    private AlertDialog progressDialog;

    private void showProgressDialog() {
        // Inflate the custom progress layout
        View progressDialogView = getLayoutInflater().inflate(R.layout.progress_layout, null);

        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(progressDialogView);
        builder.setCancelable(false);

        // Create the AlertDialog
        progressDialog = builder.create();

        // Show the progress dialog
        progressDialog.show();
    }

    private void hideProgressDialog() {
        // Dismiss the progress dialog if it's showing
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
