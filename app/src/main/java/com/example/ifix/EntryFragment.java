// MainFragment.java
package com.example.ifix;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class EntryFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<Object> dataList;
    private FloatingActionButton fab;
    private DatabaseReference databaseReference;
    private AlertDialog dialog;
    private ValueEventListener eventListener;
    private SearchView searchView;
    private boolean phone = true;
    private boolean name = true;
    private boolean model = true;
    private boolean brand = true;
    private boolean complaint = true;
    private boolean amount = true;
    private boolean status = true;
    private LinearLayout searchBox;
    ImageButton closesearch;
    private EditText editTextSearch;
    Toolbar tool;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        dataList = new ArrayList<>();
        fab = view.findViewById(R.id.fab);
        closesearch = view.findViewById(R.id.searchclose);
        searchView = view.findViewById(R.id.search);
        searchView.clearFocus();
        tool = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(tool);
        setHasOptionsMenu(true);
        adapter = new MyAdapter(getContext(), dataList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchBox = view.findViewById(R.id.searchBox);

        closesearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBox.setVisibility(View.GONE);
                tool.setVisibility(View.VISIBLE);
//        searchBox.setText(""); // Clear search text if needed

                // Optionally, you can hide the keyboard here
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                }
                searchView.setQuery("",false);
                int lastIndex = dataList.size() - 1;
                recyclerView.scrollToPosition(lastIndex);
//                dataList.clear();
//                loadInitialData(dialog);
//                adapter.notifyDataSetChanged();
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(),R.style.CustomAlertDialog);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("Test List");

        if (NetworkUtils.isNetworkAvailable(getContext())) {
            loadInitialData(dialog);
            dialog.show();
        } else {
            loadOfflineData(dialog);
            fab.setEnabled(false);
            fab.setBackgroundColor(getResources().getColor(R.color.grey));
            Toast.makeText(getContext(), "NO NETWORK", Toast.LENGTH_LONG).show();
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

        fab.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), UploadActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadInitialData(AlertDialog dialog) {
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
        Query query = databaseReference.orderByKey().limitToLast(50);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
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
                    adapter.notifyDataSetChanged();
                    int lastIndex = dataList.size() - 1;
                    recyclerView.scrollToPosition(lastIndex);
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.calendar) {
            openCalendarDialog();
            return true;
        }
        if (id == R.id.criteria) {
            showCriteriaPopup();
            return true;
        }
        if (id == R.id.search) {
            showSearchBox();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSearchBox() {
        tool.setVisibility(View.GONE);
        searchBox.setVisibility(View.VISIBLE);
        searchView.requestFocus();

        // Optionally, you can hide the keyboard here
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(searchView, InputMethodManager.SHOW_FORCED);
        }
    }
    private void showCriteriaPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.CustomAlertDialog);
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

    private void openCalendarDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),R.style.CustomAlertDialog, (view, year1, month1, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year1);
            calendar.set(Calendar.MONTH, month1);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String selectedDate = formatDate(calendar.getTimeInMillis());
            fetchEntriesForDate(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private String formatDate(long dateInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(dateInMillis);
    }

    private void fetchEntriesForDate(String date) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Test List").child(date);

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
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void searchList(String query) {
        List<Object> filteredList = new ArrayList<>();
        String header = "";
        boolean headeradded = false;
        for (Object obj : dataList) {
            if (obj instanceof String) {
                header = (String) obj;
                headeradded = false;
            } else if (obj instanceof DataClass) {
                DataClass data = (DataClass) obj;
                if (matchesSearchCriteria(data, query)) {
                    if (!headeradded)
                        filteredList.add(header);
                    headeradded = true;
                    filteredList.add(data);
                }
            }
        }

        adapter.updateDataList(filteredList);
    }

    private boolean matchesSearchCriteria(DataClass data, String query) {
        return (phone && data.getDataPhone() != null && data.getDataPhone().contains(query)) ||
                (name && data.getDataName() != null && data.getDataName().toUpperCase().contains(query.toUpperCase())) ||
                (brand && data.getDataBrand() != null && data.getDataBrand().toUpperCase().contains(query.toUpperCase())) ||
                (model && data.getDataModel() != null && data.getDataModel().toUpperCase().contains(query.toUpperCase())) ||
                (data.getDataBoughtfrom() != null && data.getDataBoughtfrom().toUpperCase().contains(query.toUpperCase())) ||
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

