package com.example.ifix;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<Object> dataList;
    FloatingActionButton fab;
    DatabaseReference databaseReference;
    AlertDialog dialog;
    ValueEventListener eventListener;
    SearchView searchView;
    private boolean phone = true;
    private boolean name = true;
    private boolean model = true;
    private boolean brand = true;
    private boolean complaint = true;
    private boolean amount = true;
    private boolean status = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        dataList = new ArrayList<>();
        fab = findViewById(R.id.fab);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();
        Toolbar tool=findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        adapter = new MyAdapter(this, dataList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();
        databaseReference = FirebaseDatabase.getInstance().getReference("Entry List");
        if (NetworkUtils.isNetworkAvailable(this)) {
            loadInitialData(dialog);
            dialog.show();
        } else {
            loadOfflineData(dialog);
            fab.setEnabled(false);
            fab.setBackgroundColor(getResources().getColor(R.color.grey));
            Toast.makeText(MainActivity.this, "NO NETWORK", Toast.LENGTH_LONG).show();
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });

    }
    private void loadInitialData(AlertDialog dialog){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                        String header = dateSnapshot.getKey();
                        dataList.add(header);
                        for (DataSnapshot jobSnapshot : dateSnapshot.getChildren()) {
                            DataClass dataClass = jobSnapshot.getValue(DataClass.class);
                            dataList.add(dataClass);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    int lastIndex = dataList.size() - 1;
                    recyclerView.scrollToPosition(lastIndex);
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });

    }
    private void loadOfflineData(AlertDialog dialog) {
        databaseReference.keepSynced(true);
        // Order by key and limit to the last 50 entries
        Query query = databaseReference.orderByKey().limitToLast(50);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                    Objects.requireNonNull(dataClass).setKey(itemSnapshot.getKey());
                    dataList.add(dataClass);
                }
                recyclerView.scrollToPosition(dataList.size() - 1);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.accountmenu) {
            Intent intent = new Intent(MainActivity.this, Accounts.class);
            startActivity(intent);
        }
        if (id == R.id.dealer) {
            Intent intent = new Intent(MainActivity.this, Dealer.class);
            startActivity(intent);
        }
        if (id == R.id.calendar) {
            openCalendarDialog();
        }
        if (id == R.id.showall) {
                dataList.clear();
                loadInitialData(dialog);
                adapter.notifyDataSetChanged();
        }
        if (id == R.id.criteria) {
            showCriteriaPopup();
        }
//        if (id == R.id.namecriteria) {
//            name=!name;
//            item.setChecked(name);
//            return true;
//        }
//        if (id == R.id.modelcriteria) {
//            model=!model;
//            item.setChecked(model);
//            return true;
//        }
//        if (id == R.id.amountcriteria) {
//            amount=!amount;
//            item.setChecked(amount);
//            return true;
//        }
//        if (id == R.id.complaintcriteria) {
//            complaint=!complaint;
//            item.setChecked(complaint);
//            return true;
//        }
//        if (id == R.id.brandcriteria) {
//            brand=!brand;
//            item.setChecked(brand);
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }
    private void showCriteriaPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.checkbox, null);
        builder.setView(dialogView);

        CheckBox phoneCheckBox = dialogView.findViewById(R.id.phoneCriteriaCheckbox);
        CheckBox nameCheckBox = dialogView.findViewById(R.id.nameCriteriaCheckbox);
        CheckBox modelCheckBox = dialogView.findViewById(R.id.modelCriteriaCheckbox);
        CheckBox amountCheckBox = dialogView.findViewById(R.id.amountCriteriaCheckbox);
        CheckBox complaintCheckBox = dialogView.findViewById(R.id.complaintCriteriaCheckbox);
        CheckBox brandCheckBox = dialogView.findViewById(R.id.brandCriteriaCheckbox);
        CheckBox statusCheckBox = dialogView.findViewById(R.id.statusCriteriaCheckbox);

        phoneCheckBox.setChecked(phone);
        nameCheckBox.setChecked(name);
        modelCheckBox.setChecked(model);
        amountCheckBox.setChecked(amount);
        complaintCheckBox.setChecked(complaint);
        brandCheckBox.setChecked(brand);
        brandCheckBox.setChecked(status);

        phoneCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> phone = isChecked);
        nameCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> name = isChecked);
        modelCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> model = isChecked);
        amountCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> amount = isChecked);
        complaintCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> complaint = isChecked);
        brandCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> brand = isChecked);
        statusCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> status = isChecked);

        builder.setPositiveButton("OK", (dialog, which) -> {
            // Handle "OK" action
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Handle "Cancel" action
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

//    public void onCheckboxClicked(View view) {
//        // Check which checkbox was clicked
//        CheckBox checkBox = (CheckBox) view;
//        int id = checkBox.getId();
//        if (id==R.id.phoneCriteriaCheckbox)
//                phone = checkBox.isChecked();
//        if (id==R.id.phoneCriteriaCheckbox)
//                name = checkBox.isChecked();
//        if (id==R.id.phoneCriteriaCheckbox)
//                model = checkBox.isChecked();
//        if (id==R.id.phoneCriteriaCheckbox)
//                amount = checkBox.isChecked();
//        if (id==R.id.phoneCriteriaCheckbox)
//                complaint = checkBox.isChecked();
//        if (id==R.id.phoneCriteriaCheckbox)
//                brand = checkBox.isChecked();
//    }

    private void openCalendarDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String selectedDate = formatDate(calendar.getTimeInMillis());
                fetchEntriesForDate(selectedDate);
            }
        }, year, month, day);

        datePickerDialog.show();
    }
    private String formatDate(long dateInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(dateInMillis);
    }

    private void fetchEntriesForDate(String date) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Entry List").child(date);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = jobSnapshot.getValue(DataClass.class);
                    if (dataClass != null) {
                        dataList.add(dataClass);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void searchList(String query) {
        List<Object> filteredList = new ArrayList<>();
        String header="";
        Boolean headeradded=false;
        for (int i = 0; i < dataList.size(); i++) {
            Object obj = dataList.get(i);
            if (obj instanceof String) {
                // Keep the header
                header=(String) obj;
                headeradded=false;
//                filteredList.add(obj);
            } else if (obj instanceof DataClass) {
                DataClass data = (DataClass) obj;
                if (matchesSearchCriteria(data, query)) {
                    // Check if previous item in filteredList is a header
//                    if (i > 0 && dataList.get(i - 1) instanceof String) {
//                        // If previous item was a header, add it before adding this data
//                        if (filteredList.isEmpty() || !filteredList.get(filteredList.size() - 1).equals(dataList.get(i - 1))) {
//                            filteredList.add(header);
//                            filteredList.add(dataList.get(i - 1));
//                        }
//                    }
                    if (!headeradded)
                        filteredList.add(header);
                    headeradded=true;
                    // Add the matching data item
                    filteredList.add(data);
                }
            }
        }

        adapter.updateDataList(filteredList);
    }

    private boolean matchesSearchCriteria(DataClass data, String query) {
        return  (phone && data.getDataPhone() != null && data.getDataPhone().contains(query)) ||
                (name && data.getDataName() != null && data.getDataName().toUpperCase().contains(query.toUpperCase())) ||
                (brand && data.getDataBrand() != null && data.getDataBrand().toUpperCase().contains(query.toUpperCase())) ||
                (model && data.getDataModel() != null && data.getDataModel().toUpperCase().contains(query.toUpperCase())) ||
                (status && data.getDataStatus() != null && data.getDataStatus().toUpperCase().contains(query.toUpperCase())) ||
                (data.getDataPaymentVia() != null && data.getDataPaymentVia().toUpperCase().contains(query.toUpperCase())) ||
                (amount && data.getDataAmount() != null && data.getDataAmount().contains(query)) ||
                (complaint && data.getDataComplaint() != null && data.getDataComplaint().toUpperCase().contains(query.toUpperCase()));
    }
}
class NetworkUtils {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}
