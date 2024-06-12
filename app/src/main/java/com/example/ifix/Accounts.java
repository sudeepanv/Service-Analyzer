package com.example.ifix;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class Accounts extends MainActivity {
private int profit;
TextView Profit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        Profit=findViewById(R.id.accountProfit);
        Profit.setText(profit());
    }
    public int profit(){
        ArrayList<DataClass> searchList = new ArrayList<>();
        for (com.example.ifix.DataClass dataClass: dataList){
            int individualProfit=Integer.parseInt(dataClass.getDataAmount())-Integer.parseInt(dataClass.getDataExpense());
            profit=profit+individualProfit;
        }
        return profit;
    }
}