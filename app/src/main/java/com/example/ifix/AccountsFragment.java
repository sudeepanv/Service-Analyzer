// AccountsFragment.java
package com.example.ifix;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    Toolbar tool;
    ImageView profileImage;
    private ImageView privacyEyeMenuItem;
    Drawable eyeclose,eyeopen;
    private boolean isTextVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        Profit = view.findViewById(R.id.accountProfit);
        Profit.setVisibility(View.INVISIBLE);
        Entry = view.findViewById(R.id.accountEntry);
        Expense = view.findViewById(R.id.accountExpense);
        Expense.setVisibility(View.INVISIBLE);
        Amount = view.findViewById(R.id.accountAmount);
        Amount.setVisibility(View.INVISIBLE);
        Delivered = view.findViewById(R.id.accountDelivered);
        returnedtxt = view.findViewById(R.id.accountReturn);
        Message = view.findViewById(R.id.alert);
        okay = view.findViewById(R.id.ok);
        notok = view.findViewById(R.id.notok);
        outside = view.findViewById(R.id.out);
        privacyEyeMenuItem = view.findViewById(R.id.privacyeye);
        eyeclose = getResources().getDrawable(R.drawable.visibility_off_24px);
        eyeopen = getResources().getDrawable(R.drawable.visibility_24px);
        tool = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(tool);
        setHasOptionsMenu(true);
        privacyEyeMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Test List");

        fetchDataAndCalculateProfit();
        profileImage = view.findViewById(R.id.profile_image);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Uri photoUrl = user.getPhotoUrl();
            if (photoUrl != null) {
                Glide.with(this)
                        .load(photoUrl)
                        .into(profileImage);
            }
        }
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.accounttool, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId()==R.id.action_view_info) {
                            showUserInfo(user);
                            return true;
                        } else if (item.getItemId()==R.id.action_sign_out) {
                            signOut();
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });


        return view;
    }
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.accounttool, menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_privacy_eye) {
//            togglePasswordVisibility();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
    @Override
    public void onPause() {
        super.onPause();
        // Set the TextView visibility to invisible when the fragment pauses
        Profit.setVisibility(View.INVISIBLE);
        Amount.setVisibility(View.INVISIBLE);
        Expense.setVisibility(View.INVISIBLE);
        isTextVisible = false;
        updatePrivacyEyeIcon();
    }
    private void togglePasswordVisibility() {
        if (isTextVisible) {
            Profit.setVisibility(View.INVISIBLE);
            Amount.setVisibility(View.INVISIBLE);
            Expense.setVisibility(View.INVISIBLE);
            isTextVisible = false;
            updatePrivacyEyeIcon();

        } else {
            Profit.setVisibility(View.VISIBLE);
            Amount.setVisibility(View.VISIBLE);
            Expense.setVisibility(View.VISIBLE);
            isTextVisible = true;
            updatePrivacyEyeIcon();
        }
    }
    private void updatePrivacyEyeIcon() {
        if (privacyEyeMenuItem != null) {
            if (isTextVisible) {
                privacyEyeMenuItem.setImageDrawable(eyeclose);
            } else {
                privacyEyeMenuItem.setImageDrawable(eyeopen);
            }
        }
    }
    private void showUserInfo(FirebaseUser user) {
        // You can display user info in a dialog or a new activity/fragment
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("User Info");
        builder.setMessage("Name: " + user.getDisplayName() + "\nEmail: " + user.getEmail());
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        // Redirect to login screen or handle sign-out logic
        Intent intent = new Intent(getContext(), AuthActivity.class);
        startActivity(intent);
        getActivity().finish();
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
