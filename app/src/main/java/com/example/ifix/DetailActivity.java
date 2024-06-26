package com.example.ifix;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import java.util.Calendar;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    TextView detailJobNo,detailFrom,detailEstimate, detailDelivery, detailTime, detailPhone, detailName, detailBrand, detailModel, detailColour, detailPassword, detailComplaint, detailStatus, detailExpense, detailAmount, detailPayment;
    LinearLayout imagesContainer;
    TextView deliveryEstimate,deliveryTime;
    MultiAutoCompleteTextView sparefrom;
    EditText deliveryExpense,deliveryAmount;
    Button deliver,returnbtn;
String name,num,brand,model,color,comp,jobno,status,Password,entrytime;
ArrayList oldImageURL;
    AlertDialog dialog;
    AutoCompleteTextView payment;
    Button deleteButton, editButton, deliveredButton;
    ImageButton phone;
    String key,dateOnly;
    DatabaseReference databaseReference;
    ArrayList<String> imageUrls;
    Toolbar tool;
    private MenuItem privacyEyeMenuItem;
    private boolean isTextVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailTime = findViewById(R.id.detailTime);
        detailFrom = findViewById(R.id.detailFrom);
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
        detailExpense.setVisibility(View.INVISIBLE);
        detailAmount = findViewById(R.id.detailAmount);
        detailAmount.setVisibility(View.INVISIBLE);
        detailPayment = findViewById(R.id.detailPayment);
        detailDelivery = findViewById(R.id.detailDelivery);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        phone = findViewById(R.id.phonebutton);
        deliveredButton = findViewById(R.id.deliveredButton);
        detailJobNo = findViewById(R.id.detailJobNo);
        tool = findViewById(R.id.toolbar);
        setSupportActionBar(tool);

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = detailPhone.getText().toString(); // Replace with the phone number you want to dial
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phoneNumber));
                startActivity(intent);
            }
        });

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
            detailFrom.setText(bundle.getString("Sparefrom", ""));
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
            }else {
                LinearLayout lay = findViewById(R.id.imagelayout);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imagesContainer.getLayoutParams();
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                lay.setLayoutParams(layoutParams);
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
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Test List").child(dateOnly).child(detailJobNo.getText().toString());
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
                        detailEstimate.getText().toString(),detailExpense.getText().toString(),detailAmount.getText().toString(),detailPayment.getText().toString(),detailDelivery.getText().toString(),detailFrom.getText().toString());
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
                                        finish();
                                        startActivity(intent);
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
                        .putExtra("Expense", detailExpense.getText().toString())
                        .putExtra("Amount", detailAmount.getText().toString())
                        .putExtra("Payment", detailPayment.getText().toString())
                        .putExtra("Delivery", detailDelivery.getText().toString())
                        .putExtra("Images", imageUrls)
                        .putExtra("Time",detailTime.getText().toString())
                        .putExtra("Estimate",detailEstimate.getText().toString())
                        .putExtra("Key", key)
                        .putExtra("Sparefrom", detailFrom.getText().toString())
                        .putExtra("Date", dateOnly);
                startActivity(intent);
            }
        });

        deliveredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                        name= detailName.getText().toString())
//                        num= detailPhone.getText().toString())
//                        brand= detailBrand.getText().toString())
//                        model= detailModel.getText().toString())
//                        .putExtra("Colour", detailColour.getText().toString())
//                        .putExtra("Password", detailPassword.getText().toString())
//                        .putExtra("Complaint", detailComplaint.getText().toString())
//                        .putExtra("Status", detailStatus.getText().toString())
//                        .putExtra("Expense", detailExpense.getText().toString())
//                        .putExtra("Amount", detailAmount.getText().toString())
//                        .putExtra("Payment", detailPayment.getText().toString())
//                        .putExtra("Estimate",detailEstimate.getText().toString())
//                        .putExtra("Images", imageUrls)
//                        .putExtra("Job", detailJobNo.getText().toString())
//                        .putExtra("Time",detailTime.getText().toString())
//                        .putExtra("Delivery",detailDelivery.getText().toString())
//                        .putExtra("Sparefrom",detailFrom.getText().toString())
//                        .putExtra("Key", key)
//                        .putExtra("Date", dateOnly);
//                startActivity(intent);
                deliver();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailtool, menu);
        privacyEyeMenuItem = menu.findItem(R.id.action_privacy_eye);
        updatePrivacyEyeIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_privacy_eye) {
            togglePasswordVisibility();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void togglePasswordVisibility() {
        if (isTextVisible) {
            detailAmount.setVisibility(View.INVISIBLE);
            detailExpense.setVisibility(View.INVISIBLE);
            isTextVisible = false;
            updatePrivacyEyeIcon();

        } else {
            detailAmount.setVisibility(View.VISIBLE);
            detailExpense.setVisibility(View.VISIBLE);
            isTextVisible = true;
            updatePrivacyEyeIcon();
        }
    }
    private void updatePrivacyEyeIcon() {
        if (privacyEyeMenuItem != null) {
            if (isTextVisible) {
                privacyEyeMenuItem.setIcon(R.drawable.visibility_off_24px);
            } else {
                privacyEyeMenuItem.setIcon(R.drawable.visibility_24px);
            }
        }
    }
    private void deliver(){
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        CustomDialogFragment customDialogFragment = new CustomDialogFragment();
//        customDialogFragment.show(fragmentManager, "CustomDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CustomAlertDialog);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.deliverypopup, null);
        builder.setView(dialogView);
        deliveryEstimate=dialogView.findViewById(R.id.estimate);
        deliveryTime=dialogView.findViewById(R.id.deliveryTime);
        deliveryExpense=dialogView.findViewById(R.id.expense);
        deliveryAmount=dialogView.findViewById(R.id.amount);
        sparefrom=dialogView.findViewById(R.id.sparefrom);
        payment=dialogView.findViewById(R.id.payment);
        deliver=dialogView.findViewById(R.id.deliver);
        returnbtn=dialogView.findViewById(R.id.returnbtn);
        String[] Paymenylist = getResources().getStringArray(R.array.Paymentlist);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdownstatus, Paymenylist);
        payment.setAdapter(arrayAdapter);

        payment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String Payment=adapterView.getItemAtPosition(position).toString();
            }
        });
        payment.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                payment.showDropDown();
            }
        });
        // Show suggestions when the field is clicked
        payment.setOnClickListener(v -> payment.showDropDown());
        String[] Shoplist = getResources().getStringArray(R.array.Shoplist);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdownstatus, Shoplist);
        sparefrom.setAdapter(arrayAdapter);
        sparefrom.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        sparefrom.setThreshold(0);
        sparefrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String shop=adapterView.getItemAtPosition(position).toString();
            }
        });
        sparefrom.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                sparefrom.showDropDown();
            }
        });

        // Show suggestions when the field is clicked
        sparefrom.setOnClickListener(v -> sparefrom.showDropDown());

            deliveryEstimate.setText(detailEstimate.getText().toString());
            sparefrom.setText(detailFrom.getText().toString());
            deliveryTime.setText(detailDelivery.getText().toString());
            if (deliveryTime.getText().toString().length()<5){
                Date deltimedate = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                String deltime = sdf.format(deltimedate);
                deliveryTime.setText(deltime);
            }
            deliveryExpense.setText(detailExpense.getText().toString());
            deliveryAmount.setText(detailAmount.getText().toString());
            payment.setText(detailPayment.getText().toString());
            jobno=(detailJobNo.getText().toString());
            Password = detailPassword.getText().toString();
            entrytime= detailTime.getText().toString();

        databaseReference = FirebaseDatabase.getInstance().getReference("Test List").child(dateOnly).child(jobno);

        deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData("DELIVERED");
            }
        });
        returnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData("RETURNED");
            }
        });
        dialog = builder.create();
        dialog.show();
    }
    public void updateData(String status){
        String Expense=deliveryExpense.getText().toString();
        String Amount=deliveryAmount.getText().toString();
        String Payment=payment.getText().toString();
        String Time= deliveryTime.getText().toString();
        DataClass dataClass = new DataClass(detailName.getText().toString(), detailPhone.getText().toString(), detailBrand.getText().toString(),
                detailModel.getText().toString(), detailColour.getText().toString(), Password, detailComplaint.getText().toString(),
                status, imageUrls, entrytime, jobno,deliveryEstimate.getText().toString(),Expense,Amount,Payment,Time,sparefrom.getText().toString());

        databaseReference.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(DetailActivity.this, "Delivered", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailActivity.this, "Can't Deliver", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
}
