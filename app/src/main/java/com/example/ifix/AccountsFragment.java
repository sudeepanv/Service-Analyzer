// AccountsFragment.java
package com.example.ifix;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccountsFragment extends Fragment {

    private int entry, delivered, noexpense,ok,nook,out,returned;
    private List<DataClass> dataList = new ArrayList<>();
    private TextView Amount, Entry, Delivered,returnedtxt,okay,outside,notok, Profit, Expense, Message;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        Profit = view.findViewById(R.id.accountProfit);
        Entry = view.findViewById(R.id.accountEntry);
        Expense = view.findViewById(R.id.accountExpense);
        Amount = view.findViewById(R.id.accountAmount);
        Delivered = view.findViewById(R.id.accountDelivered);
        returnedtxt = view.findViewById(R.id.accountReturn);
        Message = view.findViewById(R.id.alert);
        okay = view.findViewById(R.id.ok);
        notok = view.findViewById(R.id.notok);
        outside = view.findViewById(R.id.out);

        // Initialize Firebase DatabaseReference
        databaseReference = FirebaseDatabase.getInstance().getReference("Entry List");

        // Fetch data from Firebase and calculate profit
        fetchDataAndCalculateProfit();

        return view;
    }

    private void fetchDataAndCalculateProfit() {
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                entry = 0;
                delivered = 0;
                noexpense = 0;
                returned=0;
                ok=0;
                nook=0;
                out=0;

                if (snapshot.exists()) {
                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot jobSnapshot : dateSnapshot.getChildren()) {
                            DataClass dataClass = jobSnapshot.getValue(DataClass.class);
                            dataList.add(dataClass);
                            entry += 1;
                        }
                    }
                }

                // Calculate profit after data is fetched
                int totalProfit = 0;
                int totalExpense = 0;
                int totalAmount = 0;

                for (DataClass dataClass : dataList) {
                    try {
                        if (Objects.equals(dataClass.getDataStatus(), "DELIVERED")) {
                            delivered += 1;
                            Log.d("IntentData", "Key: " + dataClass.getKey());
                        } else if (Objects.equals(dataClass.getDataStatus(), "RETURNED")) {
                            returned+=1;
                        }else if (Objects.equals(dataClass.getDataStatus(), "OK")) {
                            ok+=1;
                        } else if (Objects.equals(dataClass.getDataStatus(), "NOT STARTED")) {
                            nook+=1;
                        } else if (Objects.equals(dataClass.getDataStatus(), "OUTSIDE")) {
                            out+=1;
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
                            noexpense += 1;
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
                okay.setText(String.valueOf(ok));
                notok.setText(String.valueOf(nook));
                outside.setText(String.valueOf(out));
                Entry.setText(String.valueOf(entry));
                Delivered.setText(String.valueOf(delivered));
                returnedtxt.setText(String.valueOf(returned));
                Message.setText(noexpense + " deliveries have zero expense");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }
}
