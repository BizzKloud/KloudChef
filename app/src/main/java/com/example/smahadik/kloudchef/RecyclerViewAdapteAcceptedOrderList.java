package com.example.smahadik.kloudchef;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

public class RecyclerViewAdapteAcceptedOrderList extends RecyclerView.Adapter<RecyclerViewAdapteAcceptedOrderList.MyViewHolderOrderItem> {


    public static Context context;
    LayoutInflater layoutInflater;
    View view;
    ArrayList<HashMap> acceptedMainOrderDetails;
    ArrayList<HashMap> acceptedVenOrderDetails;
    ArrayList<ArrayList<HashMap>> acceptedFoodItemsDetails;
    RecyclerView recyclerviewFoodItemList;
    Dialog orderCancelPopup;


    public RecyclerViewAdapteAcceptedOrderList(Context context, ArrayList<HashMap> acceptedMainOrderDetails , ArrayList<HashMap> acceptedVenOrderDetails , ArrayList<ArrayList <HashMap>> acceptedFoodItemsDetails) {
        RecyclerViewAdapteAcceptedOrderList.context = context;
        this.acceptedMainOrderDetails = acceptedMainOrderDetails;
        this.acceptedVenOrderDetails = acceptedVenOrderDetails;
        this.acceptedFoodItemsDetails = acceptedFoodItemsDetails;

    }

    @NonNull
    @Override
    public MyViewHolderOrderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.accepted_order_cardview, parent, false);
        return new MyViewHolderOrderItem(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolderOrderItem holder, final int position) {


        holder.textViewTableName.setText(acceptedMainOrderDetails.get(position).get("tableName").toString());
        holder.textViewOrderId.setText(acceptedVenOrderDetails.get(position).get("orderId").toString());
        holder.textViewVendorName.setText(acceptedVenOrderDetails.get(position).get("name").toString());
//        holder.textViewAdminName.setText(acceptedMainOrderDetails.get(position).get("displayName").toString());


        holder.acceptedOrderCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderDetailspopup(holder, position);
            }
        });


    }




    public void showOrderDetailspopup(final RecyclerViewAdapteAcceptedOrderList.MyViewHolderOrderItem holder, final int position) {

        holder.orderDetailsPopUp = new Dialog(context);
        holder.orderDetailsPopUp.setCanceledOnTouchOutside(true);
        holder.orderDetailsPopUp.setContentView(R.layout.accepted_order_details_popup_cardview);

        TextView textViewTableNameOD = holder.orderDetailsPopUp.findViewById(R.id.textViewTableNameOD);
        TextView textViewOrderIdOD = holder.orderDetailsPopUp.findViewById(R.id.textViewOrderIdOD);
        TextView textViewAdminNameOD = holder.orderDetailsPopUp.findViewById(R.id.textViewAdminNameOD);
        TextView textViewTaxAmountOD = holder.orderDetailsPopUp.findViewById(R.id.textViewTaxAmountOD);
        TextView textViewTotalAmountOD = holder.orderDetailsPopUp.findViewById(R.id.textViewTotalAmountOD);
        Button cancelButton = holder.orderDetailsPopUp.findViewById(R.id.cancelButton);
        Button readyButton = holder.orderDetailsPopUp.findViewById(R.id.readyButton);
        ImageButton closeButtonOD = holder.orderDetailsPopUp.findViewById(R.id.closeButtonOD);

        recyclerviewFoodItemList = holder.orderDetailsPopUp.findViewById(R.id.recyclerviewFoodItemList);
        RecyclerViewAdapteFoodItemList recyclerViewAdapteFoodItemList = new RecyclerViewAdapteFoodItemList(context, acceptedFoodItemsDetails.get(position));
        recyclerviewFoodItemList.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerviewFoodItemList.setAdapter(recyclerViewAdapteFoodItemList);

        textViewTableNameOD.setText(acceptedMainOrderDetails.get(position).get("tableName").toString());
        textViewOrderIdOD.setText(acceptedVenOrderDetails.get(position).get("orderId").toString());
        textViewAdminNameOD.setText(acceptedMainOrderDetails.get(position).get("displayName").toString());
        textViewTaxAmountOD.setText(Home.currencyFc +  Home.commaFormatter.format(Double.parseDouble(acceptedVenOrderDetails.get(position).get("totalTax").toString())) );
        textViewTotalAmountOD.setText(Home.currencyFc +  Home.commaFormatter.format(Double.parseDouble(acceptedVenOrderDetails.get(position).get("subTotalAmount").toString())) );

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                orderCancelPopup = new Dialog(context);
                orderCancelPopup.setCanceledOnTouchOutside(false);
                orderCancelPopup.setContentView(R.layout.cancel_confirmation_popup_card);
                Button logoutNoButton = orderCancelPopup.findViewById(R.id.logoutNoButton);
                Button logoutYesButton = orderCancelPopup.findViewById(R.id.logoutYesButton);

                logoutNoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderCancelPopup.dismiss();
                    }
                });

                logoutYesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderCancelPopup.dismiss();
                        holder.orderDetailsPopUp.dismiss();
                        Home.home.updateOrderStatus(position, "CANCELLED", "ACCEPTED");
                    }
                });

                orderCancelPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                orderCancelPopup.show();
            }
        });

        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.orderDetailsPopUp.dismiss();
                Home.home.updateOrderStatus(position, "CLOSED", "ACCEPTED");
            }
        });

        closeButtonOD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.orderDetailsPopUp.dismiss();
            }
        });

        holder.orderDetailsPopUp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        holder.orderDetailsPopUp.show();
    }





    @Override
    public int getItemCount() {
        return acceptedVenOrderDetails.size();
    }





    public static class MyViewHolderOrderItem extends RecyclerView.ViewHolder{

        CardView acceptedOrderCardView;
        TextView textViewTableName;
        TextView textViewOrderId;
        TextView textViewVendorName;
//        TextView textViewAdminName;
        TextView textViewTimer;
//        Timer timer;
        Dialog orderDetailsPopUp;

        MyViewHolderOrderItem(View itemView) {
            super(itemView);

            acceptedOrderCardView = itemView.findViewById(R.id.acceptedOrderCardView);
            textViewTableName = itemView.findViewById(R.id.textViewTableName);
            textViewOrderId = itemView.findViewById(R.id.textViewOrderId);
            textViewVendorName = itemView.findViewById(R.id.textViewVendorName);
//            textViewAdminName = itemView.findViewById(R.id.textViewAdminName);
            textViewTimer = itemView.findViewById(R.id.textViewTimer);


        }

    }
}
