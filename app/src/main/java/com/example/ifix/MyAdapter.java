package com.example.ifix;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private Context context;
    private List<Object> dataList;

    public MyAdapter(Context context, List<Object> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position) instanceof String) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerdate, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) {
            ((HeaderViewHolder) holder).bind((String) dataList.get(position));
        } else {
            ((ItemViewHolder) holder).bind((DataClass) dataList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerText;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerText = itemView.findViewById(R.id.dateTextView);
        }

        public void bind(String text) {
            headerText.setText(text);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView recImage;
        TextView recName, recPhone, recBrand, recModel, recComplaint, recJob;
        LinearLayout recColorbar;
        CardView recCard;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            recCard = itemView.findViewById(R.id.recCard);
            recPhone = itemView.findViewById(R.id.recPhone);
            recBrand = itemView.findViewById(R.id.recBrand);
            recModel = itemView.findViewById(R.id.recModel);
            recName = itemView.findViewById(R.id.recName);
            recComplaint = itemView.findViewById(R.id.recComplaint);
            recJob = itemView.findViewById(R.id.jobno);
            recColorbar = itemView.findViewById(R.id.colourbar);
        }

        public void bind(DataClass data) {
            recName.setText(data.getDataName());
            recPhone.setText(data.getDataPhone());
            recBrand.setText(data.getDataBrand());
            recModel.setText(data.getDataModel());
            recJob.setText(data.getDataJobNo());
            recComplaint.setText(data.getDataComplaint());
            String status = data.getDataStatus();
            if (Objects.equals(status, "DELIVERED")) {
                int color = itemView.getResources().getColor(R.color.ifixblue);
                recColorbar.setBackgroundColor(color);
            } else if (Objects.equals(status, "NOT OK")) {
                int color = itemView.getResources().getColor(R.color.green);
                recColorbar.setBackgroundColor(color);
            } else if (Objects.equals(status, "WAITING SPARE") || Objects.equals(status, "STARTED") || Objects.equals(status, "NOT STARTED")) {
                int color = itemView.getResources().getColor(R.color.red);
                recColorbar.setBackgroundColor(color);
            } else if (Objects.equals(status, "OUTSIDE")) {
                int color = itemView.getResources().getColor(R.color.darkyellow);
                recColorbar.setBackgroundColor(color);
            } else if (Objects.equals(status, "OK")) {
                int color = itemView.getResources().getColor(R.color.darkgreen);
                recColorbar.setBackgroundColor(color);
            }

            recCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
                    intent.putExtra("Time", data.getDataTime());
                    intent.putStringArrayListExtra("Images", (ArrayList<String>) data.getDataImage());
                    intent.putExtra("Phone", data.getDataPhone());
                    intent.putExtra("Name", data.getDataName());
                    intent.putExtra("Key", data.getKey());
                    intent.putExtra("Brand", data.getDataBrand());
                    intent.putExtra("Model", data.getDataModel());
                    intent.putExtra("Colour", data.getDataColour());
                    intent.putExtra("Password", data.getDataPassword());
                    intent.putExtra("Complaint", data.getDataComplaint());
                    intent.putExtra("Status", data.getDataStatus());
                    intent.putExtra("Expense", data.getDataExpense());
                    intent.putExtra("Amount", data.getDataAmount());
                    intent.putExtra("Payment", data.getDataPaymentVia());
                    intent.putExtra("Delivery", data.getDataDeliveryTime());
                    intent.putExtra("Job", data.getDataJobNo());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
