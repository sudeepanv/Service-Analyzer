// DealerFragment.java
package com.example.ifix;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DealerFragment extends Fragment {
    private List<Object> dataList = new ArrayList<>();
    private AutoCompleteTextView dealerName;
    private DatabaseReference entryReference, dealerReference;
    private ValueEventListener entryListener, dealerListener;
    private String chosenDealer, lastPaidTime, Balance,phone;
    private TextView balance,oldbalance,lasttime;
    RecyclerView recyclerView;
    Button tick;
    List<String> dealernames=new ArrayList<>();
    dealerAdapter adapter;
    FloatingActionButton fab;
    ArrayAdapter<String> arrayAdapter;
    AlertDialog dialog;
    Toolbar tool;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dealer, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        dealerName = view.findViewById(R.id.dealername);
        balance = view.findViewById(R.id.balance);
        oldbalance = view.findViewById(R.id.oldbalance);
        lasttime = view.findViewById(R.id.lasttime);

//        String[] DealerList = getResources().getStringArray(R.array.Dealerlist);
        arrayAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdownstatus, dealernames);
        dealerName.setAdapter(arrayAdapter);
        tool = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(tool);
        setHasOptionsMenu(true);

        fab = view.findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dealerName.getText().toString().isEmpty()||dealerName.getText().toString().equals(null)){
                    Toast.makeText(requireContext(),"Enter Dealer Name",Toast.LENGTH_SHORT).show();
                } else{
                    Intent intent=new Intent(getContext(),UploadActivity.class);
                    intent.putExtra("Name",chosenDealer);
                    intent.putExtra("Phone", phone);
                    startActivity(intent);

                }
            }
        });


//        tick.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int newBalance = 0;
//                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (imm != null) {
//                    View currentFocus = requireActivity().getCurrentFocus();
//                    if (currentFocus != null) {
//                        imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
//                    }
//                }
//                amount.clearFocus();
//                try {
//                    if (!balance.getText().toString().isEmpty() && !amount.getText().toString().isEmpty()) {
//                        int bal = Integer.parseInt(balance.getText().toString());
//                        int amt = Integer.parseInt(amount.getText().toString());
//                        newBalance = bal - amt;
//                        Balance = String.valueOf(newBalance);
//                        balance.setText(String.valueOf(newBalance));
//                        Date payTime = Calendar.getInstance().getTime();
//                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
//                        String paidTime = sdf.format(payTime);
//                        DealerClass dealerClass = new DealerClass(chosenDealer,phone,Integer.toString(newBalance), paidTime);
//                        dealerReference = FirebaseDatabase.getInstance().getReference("DealerAccounts").child(dealerName.getText().toString());
//                        dealerReference.setValue(dealerClass).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    } else {
//                        Toast.makeText(requireContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (NumberFormatException e) {
//                    Toast.makeText(requireContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
//                }
//
//                amount.setText("");
//            }
//        });
        FirebaseDatabase.getInstance().getReference("DealerAccounts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dealernames.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                        String header = dateSnapshot.getKey();
                        dealernames.add(header);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });


        dealerName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                chosenDealer = adapterView.getItemAtPosition(position).toString();
            }
        });
        dealerName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                dealerName.showDropDown();
            }
        });
        dealerName.setOnClickListener(v -> dealerName.showDropDown());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(),R.style.CustomAlertDialog);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dataList = new ArrayList<>();

        adapter = new dealerAdapter(requireContext(), dataList);
        recyclerView.setAdapter(adapter);

        entryReference = FirebaseDatabase.getInstance().getReference("Entry List");
        dealerReference = FirebaseDatabase.getInstance().getReference("DealerAccounts");
        dealerName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    v.clearFocus();
                    return true;
                }
                return false;
            }
        });

        dealerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                dataCall();
            }
        });

        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dealertool, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.adddealer) {
            adddealerbox();
            return true;
        }
        if (id == R.id.updateaccount) {
            showaccounts();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showaccounts(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.CustomAlertDialog);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dealerbalancepopup, null);
        builder.setView(dialogView);
        EditText balan = dialogView.findViewById(R.id.dealerbalance);
        EditText amount = dialogView.findViewById(R.id.dealerpaidadd);
        balan.setText(balance.getText().toString());
        builder.setPositiveButton("OK", (dialog, which) -> {
            int newBalance = 0;

//            amount.clearFocus();
            try {
                if (!balance.getText().toString().isEmpty() && !amount.getText().toString().isEmpty()) {
                    int bal = Integer.parseInt(balance.getText().toString());
                    int amt = Integer.parseInt(amount.getText().toString());
                    newBalance = bal - amt;
                    Balance = String.valueOf(newBalance);
                    balance.setText(String.valueOf(newBalance));
                    Date payTime = Calendar.getInstance().getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                    String paidTime = sdf.format(payTime);
                    DealerClass dealerClass = new DealerClass(chosenDealer,phone,Integer.toString(newBalance), paidTime);
                    dealerReference = FirebaseDatabase.getInstance().getReference("DealerAccounts").child(dealerName.getText().toString());
                    dealerReference.setValue(dealerClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(requireContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            }

            amount.setText("");
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Handle "Cancel" action
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void adddealerbox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.CustomAlertDialog);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.adddealerpopup, null);
        builder.setView(dialogView);
        EditText addname = dialogView.findViewById(R.id.dealernameadd);
        EditText addphone = dialogView.findViewById(R.id.dealerphoneadd);
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
        String fordate = sdf.format(date);
        builder.setPositiveButton("OK", (dialog, which) -> {
            DealerClass dealerClass = new DealerClass(addname.getText().toString(),addphone.getText().toString(),"0",fordate);
            dealerReference = FirebaseDatabase.getInstance().getReference("DealerAccounts").child(addname.getText().toString());
            dealerReference.setValue(dealerClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Handle "Cancel" action
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void dataCall() {
        chosenDealer = dealerName.getText().toString();
        dealerReference.child(chosenDealer).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DealerClass dealerClass = dataSnapshot.getValue(DealerClass.class);
                if (dealerClass != null) {
                    phone=dealerClass.getPhone();
                    Balance = dealerClass.getDataBalance();
                    oldbalance.setText(Balance);
                    lastPaidTime = dealerClass.getDatalastpaidtime();
                    lasttime.setText(lastPaidTime);
                }
                entryListener = entryReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dataList.clear();
                        if (snapshot.exists()) {
                            for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                                String header = dateSnapshot.getKey();
                                dataList.add(header);
                                for (DataSnapshot jobSnapshot : dateSnapshot.getChildren()) {
                                    DataClass dataClass = jobSnapshot.getValue(DataClass.class);
                                    dataList.add(dataClass);
                                }
                            }
                        }
                        int currentBalance;
                        ArrayList<Object> searchList = new ArrayList<>();
                        if (chosenDealer != null) {
                            searchList.clear();
                            for (Object data : dataList) {
                                if (data instanceof DataClass) {
                                    DataClass dataClass = (DataClass) data;
                                    if (dataClass.getDataName().toUpperCase().equals(chosenDealer)) {
                                        if (dataClass.getDataPaymentVia().equals("CREDIT")) {
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
                                            SimpleDateFormat fdf = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
                                            try {
                                                String deliveryTime = dataClass.getDataDeliveryTime();
                                                if (deliveryTime != null && lastPaidTime != null) {
                                                    Date deliveryDate = sdf.parse(dataClass.getDataDeliveryTime());
                                                    Date lastPay = sdf.parse(lastPaidTime);
                                                    if (deliveryDate != null && lastPay != null && deliveryDate.after(lastPay)) {
                                                        searchList.add(dataClass);
                                                        currentBalance= Balance != null ? Integer.parseInt(Balance) : 0;
                                                        Balance = String.valueOf(currentBalance + Integer.parseInt(dataClass.getDataAmount()));
                                                        balance.setText(Balance);
                                                    }
                                                }
                                            } catch (ParseException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    }
                                }else if (data instanceof String) {
                                    String header = (String) data;
                                    searchList.add(header);
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
                Toast.makeText(requireContext(), "No Data Found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
