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

public class Accounts extends AppCompatActivity {

    private int profit = 0;
    private List<DataClass> dataList = new ArrayList<>();
    private TextView profitTextView;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        profitTextView = findViewById(R.id.accountProfit);

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
                    dataClass.setKey(itemSnapshot.getKey());
                    dataList.add(dataClass);
                }
                // Calculate profit after data is fetched
                int totalProfit = calculateProfit();
                profitTextView.setText(String.valueOf(totalProfit));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

    private int calculateProfit() {
        int totalProfit = 0;
        for (DataClass dataClass : dataList) {
            try {
                int individualProfit = Integer.parseInt(dataClass.getDataAmount()) - Integer.parseInt(dataClass.getDataExpense());
                totalProfit += individualProfit;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                // Handle the error, maybe log it or show a message
            }
        }
        return totalProfit;
    }
}
