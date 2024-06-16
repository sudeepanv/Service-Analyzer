package com.example.ifix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dealer extends AppCompatActivity {
    private List<DataClass> dataList = new ArrayList<>();
    private TextView Amount,Entry,Delivered,Profit,Expense;
    private AutoCompleteTextView dealerName;
    private DatabaseReference entryreference,dealerreference;
    private ValueEventListener eventListener;
    private String chosendealer;
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
        balance=findViewById(R.id.balance);
        amount=findViewById(R.id.amount);
        tick=findViewById(R.id.tickbutton);

        String[] Dealerlist = getResources().getStringArray(R.array.Dealerlist);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdownstatus, Dealerlist);
        dealerName.setAdapter(arrayAdapter);
        dealerName.setText(Dealerlist[0], false);

        tick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newBalance=0;
                try {
                    // Ensure that the EditText fields are not empty
                    if (!balance.getText().toString().isEmpty() && !amount.getText().toString().isEmpty()) {
                        // Parse the input to integers
                        int bal = Integer.parseInt(balance.getText().toString());
                        int amt = Integer.parseInt(amount.getText().toString());
                        // Subtract the amount from the balance
                        newBalance = bal - amt;
                        // Display or use the new balance
                        // For example, set it to a TextView
                        balance.setText(String.valueOf(newBalance));
                        DealerClass dealerClass=new DealerClass(Integer.toString(newBalance));
                        dealerreference = FirebaseDatabase.getInstance().getReference("DealerAccounts").child(dealerName.getText().toString());
                        dealerreference.setValue(dealerClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(Dealer.this, "Updated", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(Dealer.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
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
        eventListener = entryreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                    Objects.requireNonNull(dataClass).setKey(itemSnapshot.getKey());
                    dataList.add(dataClass);
                }
                chosendealer=dealerName.getText().toString();
                if (chosendealer!=null){
                    ArrayList<DataClass> searchList = new ArrayList<>();
                    for (DataClass dataClass: dataList){
                        if (dataClass.getDataName().toUpperCase().equals(chosendealer)){
                            searchList.add(dataClass);
                        }
                    }
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
}