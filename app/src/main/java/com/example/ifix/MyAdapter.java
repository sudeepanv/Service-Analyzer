package com.example.ifix;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<DataClass> dataList;

    public MyAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
        holder.recName.setText(dataList.get(position).getDataName());
        holder.recPhone.setText(dataList.get(position).getDataPhone());
        holder.recBrand.setText(dataList.get(position).getDataBrand());
        holder.recModel.setText(dataList.get(position).getDataModel());
        holder.recComplaint.setText(dataList.get(position).getDataComplaint());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getDataImage());
                intent.putExtra("Phone", dataList.get(holder.getAdapterPosition()).getDataPhone());
                intent.putExtra("Name", dataList.get(holder.getAdapterPosition()).getDataName());
                intent.putExtra("Key",dataList.get(holder.getAdapterPosition()).getKey());
                intent.putExtra("Brand", dataList.get(holder.getAdapterPosition()).getDataBrand());
                intent.putExtra("Model", dataList.get(holder.getAdapterPosition()).getDataModel());
                intent.putExtra("Colour", dataList.get(holder.getAdapterPosition()).getDataColour());
                intent.putExtra("Password", dataList.get(holder.getAdapterPosition()).getDataPassword());
                intent.putExtra("Complaint", dataList.get(holder.getAdapterPosition()).getDataComplaint());
                intent.putExtra("Status", dataList.get(holder.getAdapterPosition()).getDataStatus());
                intent.putExtra("Expense", dataList.get(holder.getAdapterPosition()).getDataExpense());
                intent.putExtra("Amount", dataList.get(holder.getAdapterPosition()).getDataAmount());
                intent.putExtra("Payment", dataList.get(holder.getAdapterPosition()).getDataPaymentVia());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<DataClass> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder{

    ImageView recImage;
    TextView recName, recPhone, recBrand, recModel, recComplaint;
    CardView recCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recCard);
        recPhone = itemView.findViewById(R.id.recPhone);
        recBrand = itemView.findViewById(R.id.recBrand);
        recModel = itemView.findViewById(R.id.recModel);
        recName = itemView.findViewById(R.id.recName);
        recComplaint = itemView.findViewById(R.id.recComplaint);
    }
}