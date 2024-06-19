package com.example.ifix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Delivery extends AppCompatActivity {
TextView deliveryName,deliveryPhone,deliveryBrand,deliveryModel,deliveryColour,deliveryEstimate,deliveryComplaint,deliveryStatus,deliveryTime;
EditText deliveryExpense,deliveryAmount;
AutoCompleteTextView payment;
Button deliver;
String Payment,key,jobno,entrytime,dateOnly,Password;
List<String> oldImageURL;
ArrayAdapter<String> arrayAdapter;
DatabaseReference databaseReference;
StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deliveryscreen);
        deliveryName=findViewById(R.id.deliveryName);
        deliveryPhone=findViewById(R.id.deliveryPhone);
        deliveryBrand=findViewById(R.id.deliveryBrand);
        deliveryModel=findViewById(R.id.deliveryModel);
        deliveryColour=findViewById(R.id.deliveryColour);
        deliveryEstimate=findViewById(R.id.estimate);
        deliveryComplaint=findViewById(R.id.deliveryComplaint);
        deliveryStatus=findViewById(R.id.deliveryStatus);
        deliveryTime=findViewById(R.id.deliveryTime);
        deliveryExpense=findViewById(R.id.expense);
        deliveryAmount=findViewById(R.id.amount);
        payment=findViewById(R.id.payment);
        deliver=findViewById(R.id.deliver);

        String[] Paymenylist = getResources().getStringArray(R.array.Paymentlist);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdownstatus, Paymenylist);
        payment.setAdapter(arrayAdapter);

        payment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Payment=adapterView.getItemAtPosition(position).toString();
            }
        });
        payment.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                payment.showDropDown();
            }
        });

        // Show suggestions when the field is clicked
        payment.setOnClickListener(v -> payment.showDropDown());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            deliveryName.setText(bundle.getString("Name"));
            deliveryPhone.setText(bundle.getString("Phone"));
            deliveryBrand.setText(bundle.getString("Brand"));
            deliveryModel.setText(bundle.getString("Model"));
            deliveryColour.setText(bundle.getString("Colour"));
            deliveryEstimate.setText(bundle.getString("Estimate"));
            deliveryComplaint.setText(bundle.getString("Complaint"));
            deliveryStatus.setText(bundle.getString("Status"));
            Date deltimedate = Calendar.getInstance().getTime();
            // Define the desired format pattern
            // Create a SimpleDateFormat instance with the desired format pattern
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
            // Format the current date and time
            String deltime = sdf.format(deltimedate);
            deliveryTime.setText(deltime);
            deliveryExpense.setText(bundle.getString("Expense"));
            deliveryAmount.setText(bundle.getString("Amount"));
            payment.setText(bundle.getString("Payment"));
            jobno=(bundle.getString("Job"));
            key = bundle.getString("Key");
            Password = bundle.getString("Password");
            entrytime= bundle.getString("Time");
            dateOnly=bundle.getString("Date");
            oldImageURL = bundle.getStringArrayList("eImages");
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Entry List").child(dateOnly).child(jobno);

        deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });
    }
    public void updateData(){
        String Status = "DELIVERED";
        String Expense=deliveryExpense.getText().toString();
        String Amount=deliveryAmount.getText().toString();
        String Payment=payment.getText().toString();
        String Time= deliveryTime.getText().toString();
        DataClass dataClass = new DataClass(deliveryName.getText().toString(), deliveryPhone.getText().toString(), deliveryBrand.getText().toString(),
                deliveryModel.getText().toString(), deliveryColour.getText().toString(), Password, deliveryComplaint.getText().toString(),
                Status, oldImageURL, entrytime, jobno,deliveryEstimate.getText().toString(),Expense,Amount,Payment,Time);

        databaseReference.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Delivery.this, "Delivered", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(Delivery.this,MainActivity.class);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Delivery.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}