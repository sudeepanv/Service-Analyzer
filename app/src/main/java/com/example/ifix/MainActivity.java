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

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<Object> dataList;
    FloatingActionButton fab;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    SearchView searchView;
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
        AlertDialog dialog = builder.create();
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
        return true;
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
        return data.getDataPhone().contains(query);
//                data.getDataPhone().toLowerCase().contains(query.toLowerCase()) ||
//                data.getDataBrand().toLowerCase().contains(query.toLowerCase()) ||
//                data.getDataModel().toLowerCase().contains(query.toLowerCase()) ||
//                data.getDataComplaint().toLowerCase().contains(query.toLowerCase());
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
