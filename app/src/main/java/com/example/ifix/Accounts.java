package com.example.ifix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Accounts extends AppCompatActivity {

    private int entry,delivered,noexpense;
    private List<DataClass> dataList = new ArrayList<>();
    private TextView Amount,Entry,Delivered,Profit,Expense,Message;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        Profit = findViewById(R.id.accountProfit);
        Entry = findViewById(R.id.accountEntry);
        Expense = findViewById(R.id.accountExpense);
        Amount = findViewById(R.id.accountAmount);
        Delivered = findViewById(R.id.accountDelivered);
        Message = findViewById(R.id.alert);

        // Initialize Firebase DatabaseReference
        databaseReference = FirebaseDatabase.getInstance().getReference("Entry List");

        // Fetch data from Firebase and calculate profit
        fetchDataAndCalculateProfit();
    }

    private void fetchDataAndCalculateProfit() {
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot jobSnapshot : dateSnapshot.getChildren()) {
                            DataClass dataClass = jobSnapshot.getValue(DataClass.class);
                            dataList.add(dataClass);
                            entry+=1;
                        }
                    }
                }
                // Calculate profit after data is fetched
                int totalProfit=0;
                int totalExpense=0;
                int totalAmount = 0;
                for (DataClass dataClass : dataList) {
                    try {
                        if (Objects.equals(dataClass.getDataStatus(), "DELIVERED")){
                            delivered+=1;
                            Log.d("IntentData", "Key: " + dataClass.getKey());
                        }
                        if (dataClass.getDataExpense() != null && !dataClass.getDataExpense().isEmpty()) {
                            int individualExpense = Integer.parseInt(dataClass.getDataExpense());
                            totalExpense += individualExpense;
                        }
                        if (dataClass.getDataAmount() != null && !dataClass.getDataAmount().isEmpty() &&
                                dataClass.getDataExpense() != null && !dataClass.getDataExpense().isEmpty()) {
                            int individualProfit = Integer.parseInt(dataClass.getDataAmount()) - Integer.parseInt(dataClass.getDataExpense());
                            totalProfit += individualProfit;
                        }
                        if (dataClass.getDataAmount() != null && !dataClass.getDataAmount().isEmpty() && dataClass.getDataExpense().isEmpty()) {
                            totalProfit += Integer.parseInt(dataClass.getDataAmount());
                            noexpense+=1;
                        }
                        if (dataClass.getDataAmount() != null && !dataClass.getDataAmount().isEmpty()) {
                            int individualAmount = Integer.parseInt(dataClass.getDataAmount());
                            totalAmount += individualAmount;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        Log.d("IntentError", "Key: " + dataClass.getKey());
                        // Handle the error, maybe log it or show a message
                    }
                }
                Profit.setText(String.valueOf(totalProfit));
                Expense.setText(String.valueOf(totalExpense));
                Amount.setText(String.valueOf(totalAmount));
                Entry.setText(String.valueOf(entry));
                Delivered.setText(String.valueOf(delivered));
                Message.setText(noexpense+" deliveries have zero expense");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }
}
