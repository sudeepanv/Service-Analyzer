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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
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
import com.google.firebase.storage.UploadTask;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class UpdateActivity extends AppCompatActivity {

    ImageView updateImage;
    Button updateButton;
    String[] Modellist;
    int arrayResourceId;
    EditText updateName, updatePhone,updatePassword,updateEstimate;
    String Status,Colour,dateonly;
    String key, jobno,time,Brand,Model,Expense,Amount,Payment,Time,sparefrom;
    List<String> oldImageURL,imageUrl;
    Uri uri;
    AutoCompleteTextView updateStatus,updateColour, updateBrand, updateModel;
    MultiAutoCompleteTextView updateComplaint;
    ArrayAdapter<String> arrayAdapter;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        updateButton = findViewById(R.id.updateButton);
        updateName = findViewById(R.id.updateName);
        updatePhone = findViewById(R.id.updatePhone);
        updateEstimate = findViewById(R.id.updateEstimate);
        updateBrand = findViewById(R.id.updateBrand);
        updateModel = findViewById(R.id.updateModel);
        updateColour= findViewById(R.id.updateColour);
        updatePassword = findViewById(R.id.updatePassword);
        updateComplaint = findViewById(R.id.updateComplaint);
        updateStatus =findViewById(R.id.updateStatus);

        String[] Statuslist = getResources().getStringArray(R.array.Statuslist);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdownstatus, Statuslist);
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
        updateStatus.setOnClickListener(v -> updateStatus.showDropDown());

        String[] Complaintlist = getResources().getStringArray(R.array.Complaintlist);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdownstatus, Complaintlist);
        updateComplaint.setAdapter(arrayAdapter);
        updateComplaint.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        updateComplaint.setThreshold(0);
        updateComplaint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String Complaint = adapterView.getItemAtPosition(position).toString();
            }
        });
        updateComplaint.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                updateComplaint.showDropDown();
            }
        });
        updateComplaint.setOnClickListener(v -> updateComplaint.showDropDown());

        String[] Colourlist = getResources().getStringArray(R.array.Colourlist);
        ArrayAdapter<String> ColourAdapter = new ArrayAdapter<>(this, R.layout.dropdownstatus, Colourlist);
        updateColour.setAdapter(ColourAdapter);
        updateColour.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Colour=adapterView.getItemAtPosition(position).toString();
            }
        });
        updateColour.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                updateColour.showDropDown();
            }
        });
        updateColour.setOnClickListener(v -> updateColour.showDropDown());

        String[] Brandlist = getResources().getStringArray(R.array.Brandlist);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdownstatus, Brandlist);
        updateBrand.setAdapter(arrayAdapter);
        updateBrand.setThreshold(0);
        updateBrand.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        updateBrand.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // Move focus to the next view
                    View nextView = findViewById(R.id.updateModel); // Replace with the ID of your next view
                    nextView.requestFocus();
                    return true; // Consume the event
                }
                return false; // Allow default behavior for other actions
            }
        });
        updateBrand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        updateBrand.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                updateBrand.showDropDown();
            }
        });
        updateBrand.setOnClickListener(v -> updateBrand.showDropDown());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            updateName.setText(bundle.getString("Name"));
            updatePhone.setText(bundle.getString("Phone"));
            updateBrand.setText(bundle.getString("Brand"));
            updateModel.setText(bundle.getString("Model"));
            updateColour.setText(bundle.getString("Colour"));
            updatePassword.setText(bundle.getString("Password"));
            updateComplaint.setText(bundle.getString("Complaint"));
            updateEstimate.setText(bundle.getString("Estimate"));
            updateStatus.setText(bundle.getString("Status"));
            jobno=(bundle.getString("Job"));
            key = bundle.getString("Key");
            Expense = bundle.getString("Expense");
            Amount = bundle.getString("Amount");
            Payment = bundle.getString("Payment");
            Time = bundle.getString("Delivery");
            sparefrom = bundle.getString("Sparefrom");
            time = bundle.getString("Time");
            dateonly = bundle.getString("Date");
            imageUrl = bundle.getStringArrayList("Images");
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Test List").child(dateonly).child(jobno);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
            }
        });
    }
    private void modelcall(){
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdownstatus, Modellist);
        updateModel.setAdapter(arrayAdapter);
        updateModel.setThreshold(0);
        updateModel.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        updateModel.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // Move focus to the next view
                    View nextView = findViewById(R.id.updateColour); // Replace with the ID of your next view
                    nextView.requestFocus();
                    return true; // Consume the event
                }
                return false; // Allow default behavior for other actions
            }
        });
        updateModel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Model=adapterView.getItemAtPosition(position).toString();
            }
        });
        updateModel.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                updateModel.showDropDown();
            }
        });
        updateModel.setOnClickListener(v -> updateModel.showDropDown());
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
        String Estimate = updateEstimate.getText().toString();
        DataClass dataClass = new DataClass(Name, Phone, Brand,Model,Colour,Password,Complaint,Status,imageUrl,time,jobno,Estimate,Expense,Amount,Payment,Time,sparefrom);

        databaseReference.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    if (uri != null) {
                        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL.toString());
                        reference.delete();
                    }
                    Toast.makeText(UpdateActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(UpdateActivity.this,MainActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
