package com.example.ifix;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class dealerAdapter extends RecyclerView.Adapter<dealerViewHolder> {
    private Context context;
    private List<DataClass> dataList;
    public dealerAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }
    @NonNull
    @Override
    public dealerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_dealer, parent, false);
        return new dealerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull dealerViewHolder holder, int position) {
        holder.recBrand.setText(dataList.get(position).getDataBrand());
        holder.recModel.setText(dataList.get(position).getDataModel());
        if (dataList.get(position).getDataTime().length()>4)
            holder.recDate.setText(dataList.get(position).getDataTime().substring(0,6));
        holder.recComplaint.setText(dataList.get(position).getDataComplaint());
        if (dataList.get(position).getDataAmount()!=null)
            holder.recAmount.setText("₹"+(dataList.get(position).getDataAmount()));
        else
            holder.recAmount.setText("₹");
        String Status=dataList.get(position).getDataStatus();
        if(Objects.equals(Status, "DELIVERED")){
            int color = context.getResources().getColor(R.color.ifixblue);
            holder.recColorbar.setBackgroundColor(color);
        } else if (Objects.equals(Status, "NOT OK")) {
            int color = context.getResources().getColor(R.color.green);
            holder.recColorbar.setBackgroundColor(color);
        }else if (Objects.equals(Status, "WAITING SPARE")) {
            int color = context.getResources().getColor(R.color.red);
            holder.recColorbar.setBackgroundColor(color);
        }else if (Objects.equals(Status, "STARTED")) {
            int color = context.getResources().getColor(R.color.red);
            holder.recColorbar.setBackgroundColor(color);
        }else if (Objects.equals(Status, "OUTSIDE")) {
            int color = context.getResources().getColor(R.color.darkyellow);
            holder.recColorbar.setBackgroundColor(color);
        }else if (Objects.equals(Status, "OK")) {
            int color = context.getResources().getColor(R.color.darkgreen);
            holder.recColorbar.setBackgroundColor(color);
        }else if (Objects.equals(Status, "NOT STARTED")) {
            int color = context.getResources().getColor(R.color.red);
            holder.recColorbar.setBackgroundColor(color);
        }


        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("Time", dataList.get(holder.getAdapterPosition()).getDataTime());
                intent.putStringArrayListExtra("Images", (ArrayList<String>) dataList.get(holder.getAdapterPosition()).getDataImage());
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
                intent.putExtra("Delivery", dataList.get(holder.getAdapterPosition()).getDataDeliveryTime());
                intent.putExtra("Job", dataList.get(holder.getAdapterPosition()).getDataJobNo());
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

class dealerViewHolder extends RecyclerView.ViewHolder{

    TextView recAmount, recBrand, recModel, recComplaint,recDate;
    LinearLayout recColorbar;
    CardView recCard;

    public dealerViewHolder(@NonNull View itemView) {
        super(itemView);

//        recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recCard);
        recBrand = itemView.findViewById(R.id.recBrand);
        recModel = itemView.findViewById(R.id.recModel);
        recAmount = itemView.findViewById(R.id.recAmount);
        recComplaint = itemView.findViewById(R.id.recComplaint);
        recDate=itemView.findViewById(R.id.date);
        recColorbar=itemView.findViewById(R.id.colourbar);
    }
}