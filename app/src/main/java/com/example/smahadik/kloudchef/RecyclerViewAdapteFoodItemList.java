package com.example.smahadik.kloudchef;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

public class RecyclerViewAdapteFoodItemList extends RecyclerView.Adapter<RecyclerViewAdapteFoodItemList.MyViewHolderFoodItem> {

    Context context;
    LayoutInflater layoutInflater;
    View view;

    ArrayList<HashMap> foodItemsDetails;


    public RecyclerViewAdapteFoodItemList(Context context , ArrayList <HashMap> foodItemsDetails) {
        this.context = context;
        this.foodItemsDetails = foodItemsDetails;
    }

    @NonNull
    @Override
    public MyViewHolderFoodItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.fooditem_card, parent, false);
        return new MyViewHolderFoodItem(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolderFoodItem holder, int position) {

        holder.textViewfoodItemName.setText(foodItemsDetails.get(position).get("name").toString());
        holder.textViewfoodItemAmount.setText(Home.currencyFc + Home.commaFormatter.format( Double.parseDouble(foodItemsDetails.get(position).get("totalAmount").toString())) );
//        holder.textViewfoodItemAmount.setText(foodItemsDetails.get(position).get("totalAmount").toString());
        holder.textViewfoodItemQuantity.setText(foodItemsDetails.get(position).get("quantity").toString());

    }



    @Override
    public int getItemCount() {
        return foodItemsDetails.size();
    }



    public static class MyViewHolderFoodItem extends RecyclerView.ViewHolder{

        TextView textViewfoodItemName;
        TextView textViewfoodItemAmount;
        TextView textViewfoodItemQuantity;

        MyViewHolderFoodItem(View itemView) {
            super(itemView);

            textViewfoodItemName = itemView.findViewById(R.id.textViewfoodItemName);
            textViewfoodItemAmount = itemView.findViewById(R.id.textViewfoodItemAmount);
            textViewfoodItemQuantity = itemView.findViewById(R.id.textViewfoodItemQuantity);

        }
    }



}
