package com.example.ifix;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
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
    String[] Modellist;
    private ImageButton addImageButton;
    private int maxjob;
    private List<DataClass> dataList = new ArrayList<>();
    private List<Uri> imageUris = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();
    private Uri currentImageUri;
    int arrayResourceId;
    ArrayAdapter<String> arrayAdapter;
    String Status,Brand,Model,Colour,Complaint;

    private EditText  uploadName, uploadPhone,  uploadPassword;
    private AutoCompleteTextView uploadStatus, uploadColour,uploadModel,uploadBrand;
    private MultiAutoCompleteTextView uploadComplaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

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

        String[] Statuslist = getResources().getStringArray(R.array.Statuslist);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdownstatus, Statuslist);
        uploadStatus.setAdapter(arrayAdapter);
        uploadStatus.setText(Statuslist[0], false);

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
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdownstatus, Colourlist);
        uploadColour.setAdapter(arrayAdapter);
        uploadColour.setThreshold(0);
        uploadColour.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        uploadColour.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // Move focus to the next view
                    View nextView = findViewById(R.id.uploadPassword); // Replace with the ID of your next view
                    nextView.requestFocus();
                    return true; // Consume the event
                }
                return false; // Allow default behavior for other actions
            }
        });
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

        String[] Complaintlist = getResources().getStringArray(R.array.Complaintlist);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdownstatus, Complaintlist);
        uploadComplaint.setAdapter(arrayAdapter);
        uploadComplaint.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        uploadComplaint.setThreshold(0);

        uploadComplaint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Complaint=adapterView.getItemAtPosition(position).toString();
            }
        });
        uploadComplaint.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                uploadComplaint.showDropDown();
            }
        });
        uploadComplaint.setOnClickListener(v -> uploadComplaint.showDropDown());

        String[] Brandlist = getResources().getStringArray(R.array.Brandlist);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdownstatus, Brandlist);
        uploadBrand.setAdapter(arrayAdapter);
        uploadBrand.setThreshold(0);
        uploadBrand.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        uploadBrand.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // Move focus to the next view
                    View nextView = findViewById(R.id.uploadModel); // Replace with the ID of your next view
                    nextView.requestFocus();
                    return true; // Consume the event
                }
                return false; // Allow default behavior for other actions
            }
        });
        uploadBrand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Brand=adapterView.getItemAtPosition(position).toString();
                try {
                    arrayResourceId = getResources().getIdentifier(Brand, "array", getPackageName());
                    Modellist = getResources().getStringArray(arrayResourceId);
                }catch (Exception e){

                }
                if (Modellist!=null){
                    arrayResourceId = getResources().getIdentifier(Brand, "array", getPackageName());
                    Modellist = getResources().getStringArray(arrayResourceId);
                    modelcall();
                }

            }

        });
        uploadBrand.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                uploadBrand.showDropDown();
            }
        });
        uploadBrand.setOnClickListener(v -> uploadBrand.showDropDown());




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
    private void modelcall(){
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdownstatus, Modellist);
        uploadModel.setAdapter(arrayAdapter);
        uploadModel.setThreshold(0);
        uploadModel.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        uploadModel.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // Move focus to the next view
                    View nextView = findViewById(R.id.uploadColour); // Replace with the ID of your next view
                    nextView.requestFocus();
                    return true; // Consume the event
                }
                return false; // Allow default behavior for other actions
            }
        });
        uploadModel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Model=adapterView.getItemAtPosition(position).toString();
            }
        });
        uploadModel.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                uploadModel.showDropDown();
            }
        });
        uploadModel.setOnClickListener(v -> uploadModel.showDropDown());
    }


//    private void setupDropdowns() {
//        setupDropdown1(uploadStatus, R.array.Statuslist, new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                uploadStatus.setText(adapterView.getItemAtPosition(position).toString(), false);
//            }
//        });
//
//        setupDropdown2(uploadColour, R.array.Colourlist, new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                uploadColour.setText(adapterView.getItemAtPosition(position).toString(), false);
//            }
//        });
//        setupDropdown3(uploadBrand, R.array.Brandlist, new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                uploadBrand.setText(adapterView.getItemAtPosition(position).toString(), false);
//            }
//        });
//
//        setupDropdown4(uploadModel,uploadBrand.getText().toString(), new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                uploadBrand.setText(adapterView.getItemAtPosition(position).toString(), false);
//            }
//        });
//
//    }

//    private void setupDropdown1(AutoCompleteTextView dropdown, int arrayResourceId, AdapterView.OnItemClickListener onItemClickListener) {
//        String[] items = getResources().getStringArray(arrayResourceId);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdownstatus, items);
//        dropdown.setAdapter(adapter);
//        dropdown.setText(items[0], false);
//        dropdown.setOnItemClickListener(onItemClickListener);
//        dropdown.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus) {
//                dropdown.showDropDown();
//            }
//        });
//        dropdown.setOnClickListener(v -> dropdown.showDropDown());
//    }
//    private void setupDropdown2(AutoCompleteTextView dropdown, int arrayResourceId, AdapterView.OnItemClickListener onItemClickListener) {
//        String[] items = getResources().getStringArray(arrayResourceId);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdownstatus, items);
//        dropdown.setAdapter(adapter);
//        dropdown.setText(items[3], false);
//        dropdown.setOnItemClickListener(onItemClickListener);
//        dropdown.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus) {
//                dropdown.showDropDown();
//            }
//        });
//        dropdown.setOnClickListener(v -> dropdown.showDropDown());
//    }
//    private void setupDropdown3(AutoCompleteTextView dropdown, int arrayResourceId, AdapterView.OnItemClickListener onItemClickListener) {
//        String[] items = getResources().getStringArray(arrayResourceId);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdownstatus, items);
//        dropdown.setAdapter(adapter);
//        dropdown.setOnItemClickListener(onItemClickListener);
//        dropdown.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus) {
//                dropdown.showDropDown();
//            }
//        });
//        dropdown.setOnClickListener(v -> dropdown.showDropDown());
//    }
    private void setupDropdown4(AutoCompleteTextView dropdown, String arrayName, AdapterView.OnItemClickListener onItemClickListener) {
        // Get the resource ID of the string array using its name
        int arrayResourceId = getResources().getIdentifier(arrayName, "array", getPackageName());

        // Check if the resource ID is valid
        if (arrayResourceId != 0) {
            // Retrieve the string array
            String[] items = getResources().getStringArray(arrayResourceId);

            // Set up your dropdown using the retrieved string array
            // For example:
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdownstatus, items);
            dropdown.setAdapter(adapter);
            dropdown.setOnItemClickListener(onItemClickListener);
            dropdown.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    dropdown.showDropDown();
                }
            });
            dropdown.setOnClickListener(v -> dropdown.showDropDown());
        } else {
            // Handle the case where the string array resource is not found
            showToast("Not Found");
        }
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
        Date time = Calendar.getInstance().getTime();
        // Define the desired format pattern
        // Create a SimpleDateFormat instance with the desired format pattern
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
        // Format the current date and time
        String entrytime = sdf.format(time);


        if (name.isEmpty() || phone.isEmpty() || brand.isEmpty()) {
            showToast("Please fill all the fields");
            return;
        }

        String job = Integer.toString(maxjob += 1);
        DataClass dataClass = new DataClass(name, phone, brand, model, uploadColour.getText().toString(), password, complaint, uploadStatus.getText().toString(), imageUrls, entrytime, job);

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
