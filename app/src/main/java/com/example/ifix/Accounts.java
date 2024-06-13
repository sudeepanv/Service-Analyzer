package com.example.ifix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Accounts extends AppCompatActivity {

    private int entry,delivered;
    private List<DataClass> dataList = new ArrayList<>();
    private TextView Amount,Entry,Delivered,Profit,Expense;
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
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                    Objects.requireNonNull(dataClass).setKey(itemSnapshot.getKey());
                    dataList.add(dataClass);
                    entry+=1;
                }

                // Calculate profit after data is fetched
                int totalProfit=0;
                int totalExpense=0;
                int totalAmount = 0;
                for (DataClass dataClass : dataList) {
                    try {
                        int individualExpense = Integer.parseInt(dataClass.getDataExpense());
                        totalExpense += individualExpense;
                        int individualProfit = Integer.parseInt(dataClass.getDataAmount()) - Integer.parseInt(dataClass.getDataExpense());
                        totalProfit += individualProfit;
                        int individualAmount = Integer.parseInt(dataClass.getDataAmount());
                        totalAmount += individualAmount;
                        if (Objects.equals(dataClass.getDataStatus(), "DELIVERED"))
                            delivered+=1;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        // Handle the error, maybe log it or show a message
                    }
                }
                Profit.setText(String.valueOf(totalProfit));
                Expense.setText(String.valueOf(totalExpense));
                Amount.setText(String.valueOf(totalAmount));
                Entry.setText(String.valueOf(entry));
                Delivered.setText(String.valueOf(delivered));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }
}
