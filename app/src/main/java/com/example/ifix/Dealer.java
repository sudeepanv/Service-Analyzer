package com.example.ifix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Dealer extends AppCompatActivity {
    private List<DataClass> dataList = new ArrayList<>();
    private TextView Amount,Entry,Delivered,Profit,Expense;
    private AutoCompleteTextView dealerName;
    private DatabaseReference entryreference,dealerreference;
    private ValueEventListener entrylistener,dealerlistener;
    private String chosendealer,lastpaidtime,Balance;
    private EditText balance,amount;
    RecyclerView recyclerView;
    Button tick;
    dealerAdapter adapter;
    ArrayAdapter<String> arrayAdapter;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer);
        recyclerView = findViewById(R.id.recyclerView);
        dealerName=findViewById(R.id.dealername);
        dealerName.requestFocus();
        balance=findViewById(R.id.balance);
        amount=findViewById(R.id.amount);
        tick=findViewById(R.id.tickbutton);

        String[] Dealerlist = getResources().getStringArray(R.array.Dealerlist);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdownstatus, Dealerlist);
        dealerName.setAdapter(arrayAdapter);

        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newBalance=0;
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    // Find the currently focused view, so we can grab the correct window token from it.
                    View currentFocus = getCurrentFocus();
                    if (currentFocus != null) {
                        // Hide the keyboard
                        imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                    }
                }
                amount.clearFocus();
                try {
                    // Ensure that the EditText fields are not empty
                    if (!balance.getText().toString().isEmpty() && !amount.getText().toString().isEmpty()) {
                        // Parse the input to integers
                        int bal = Integer.parseInt(balance.getText().toString());
                        int amt = Integer.parseInt(amount.getText().toString());
                        // Subtract the amount from the balance
                        newBalance = bal - amt;
                        Balance= String.valueOf(newBalance);
                        // Display or use the new balance
                        // For example, set it to a TextView
                        balance.setText(String.valueOf(newBalance));
                        DealerClass dealerClass=new DealerClass(Integer.toString(newBalance), DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
                        dealerreference = FirebaseDatabase.getInstance().getReference("DealerAccounts").child(dealerName.getText().toString());
                        dealerreference.setValue(dealerClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(Dealer.this, "Updated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Dealer.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Handle empty input fields
                        Toast.makeText(Dealer.this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    // Handle the case where the input is not a valid integer
                    Toast.makeText(Dealer.this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                }

                amount.setText("");
            }
        });

        dealerName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                chosendealer=adapterView.getItemAtPosition(position).toString();
            }
        });
        dealerName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                dealerName.showDropDown();
            }
        });
        dealerName.setOnClickListener(v -> dealerName.showDropDown());


        GridLayoutManager gridLayoutManager = new GridLayoutManager(Dealer.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(Dealer.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dataList = new ArrayList<>();

        adapter = new dealerAdapter(Dealer.this, dataList);
        recyclerView.setAdapter(adapter);

        entryreference = FirebaseDatabase.getInstance().getReference("EntryList");
        dealerreference = FirebaseDatabase.getInstance().getReference("DealerAccounts");
        dealerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                datacall();
            }
        });
    }
    private void datacall(){
        chosendealer=dealerName.getText().toString();
        dealerreference.child(chosendealer).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DealerClass dealerClass = dataSnapshot.getValue(DealerClass.class);
                if (dealerClass != null) {
                    // Use the user object
                    Balance=dealerClass.getDataBalance();
                    lastpaidtime=dealerClass.getDatalastpaidtime();
                }
                entrylistener = entryreference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dataList.clear();
                        for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                            DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                            Objects.requireNonNull(dataClass).setKey(itemSnapshot.getKey());
                            dataList.add(dataClass);
                        }
                        if (chosendealer!=null){

                            ArrayList<DataClass> searchList = new ArrayList<>();
                            for (DataClass dataClass: dataList){
                                if (dataClass.getDataName().toUpperCase().equals(chosendealer)){
                                    searchList.add(dataClass);
                                    if (dataClass.getDataStatus().equals("DELIVERED")) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                                        try {
                                            String deliveryTime = dataClass.getDataDeliveryTime();
                                            if (deliveryTime != null && lastpaidtime != null) {
                                                Date deliverydate = sdf.parse(dataClass.getDataDeliveryTime());
                                                Date lastpay = sdf.parse(lastpaidtime);
                                                if (deliverydate != null && lastpay != null && deliverydate.after(lastpay)) {
                                                    int currentBalance = Balance != null ? Integer.parseInt(Balance) : 0;
                                                    Balance = String.valueOf(currentBalance + Integer.parseInt(dataClass.getDataAmount()));
                                                    balance.setText(Balance);
                                                }
                                            }
                                        } catch (ParseException e) {
                                            throw new RuntimeException(e);
                                        }

                                    }
                                }
                            }
                            balance.setText(Balance);
                            adapter.searchDataList(searchList);
                            adapter.notifyDataSetChanged();
                        }
                        dialog.dismiss();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dialog.dismiss();
                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(Dealer.this,"No Data Found",Toast.LENGTH_SHORT).show();
            }
        });
    }
}