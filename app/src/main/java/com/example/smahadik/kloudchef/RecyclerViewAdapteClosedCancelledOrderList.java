package com.example.smahadik.kloudchef;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.Timestamp;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

public class RecyclerViewAdapteClosedCancelledOrderList extends RecyclerView.Adapter<RecyclerViewAdapteClosedCancelledOrderList.MyViewHolderOrderItem> {


    public static Context context;
    LayoutInflater layoutInflater;
    View view;
    ArrayList<HashMap> closedCancelledMainOrderDetails;
    ArrayList<HashMap> closedCancelledVenOrderDetails;
    ArrayList<ArrayList<HashMap>> closedCancelledFoodItemsDetails;
    RecyclerView recyclerviewFoodItemList;


    public RecyclerViewAdapteClosedCancelledOrderList(Context context, ArrayList<HashMap> closedCancelledMainOrderDetails , ArrayList<HashMap> closedCancelledVenOrderDetails , ArrayList<ArrayList <HashMap>> closedCancelledFoodItemsDetails) {
        RecyclerViewAdapteClosedCancelledOrderList.context = context;
        this.closedCancelledMainOrderDetails = closedCancelledMainOrderDetails;
        this.closedCancelledVenOrderDetails = closedCancelledVenOrderDetails;
        this.closedCancelledFoodItemsDetails = closedCancelledFoodItemsDetails;

    }

    @NonNull
    @Override
    public MyViewHolderOrderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.closed_cancelled_order_cardview, parent, false);
        return new MyViewHolderOrderItem(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolderOrderItem holder, final int position) {


        holder.textViewTableName.setText(closedCancelledMainOrderDetails.get(position).get("tableName").toString());
        holder.textViewOrderId.setText(closedCancelledVenOrderDetails.get(position).get("orderId").toString());
        holder.textViewVendorName.setText(closedCancelledVenOrderDetails.get(position).get("name").toString());
//        holder.textViewAdminName.setText(closedCancelledMainOrderDetails.get(position).get("displayName").toString());
        holder.textViewOrderStatus.setText(closedCancelledVenOrderDetails.get(position).get("orderStatus").toString());

        Timestamp startTime = (Timestamp) closedCancelledMainOrderDetails.get(position).get("time");
        Timestamp endTime = (Timestamp) closedCancelledVenOrderDetails.get(position).get("endTime");
        long timeElapsed = endTime.toDate().getTime() - startTime.toDate().getTime();
        long seconds = timeElapsed / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        String time = hours % 24 + ":" + minutes % 60 + ":" + seconds % 60;

        holder.textViewTimer.setText(time);

        holder.closedCancelledOrderCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderDetailspopup(holder, position);
            }
        });


    }




    public void showOrderDetailspopup(final RecyclerViewAdapteClosedCancelledOrderList.MyViewHolderOrderItem holder, final int position) {

        holder.orderDetailsPopUp = new Dialog(context);
        holder.orderDetailsPopUp.setCanceledOnTouchOutside(true);
        holder.orderDetailsPopUp.setContentView(R.layout.closed_cancelled_order_details_popup_cardview);

        TextView textViewTableNameOD = holder.orderDetailsPopUp.findViewById(R.id.textViewTableNameOD);
        TextView textViewOrderIdOD = holder.orderDetailsPopUp.findViewById(R.id.textViewOrderIdOD);
        TextView textViewAdminNameOD = holder.orderDetailsPopUp.findViewById(R.id.textViewAdminNameOD);
        TextView textViewTaxAmountOD = holder.orderDetailsPopUp.findViewById(R.id.textViewTaxAmountOD);
        TextView textViewTotalAmountOD = holder.orderDetailsPopUp.findViewById(R.id.textViewTotalAmountOD);
        ImageButton closeButtonOD = holder.orderDetailsPopUp.findViewById(R.id.closeButtonOD);

        recyclerviewFoodItemList = holder.orderDetailsPopUp.findViewById(R.id.recyclerviewFoodItemList);
        RecyclerViewAdapteFoodItemList recyclerViewAdapteFoodItemList = new RecyclerViewAdapteFoodItemList(context, closedCancelledFoodItemsDetails.get(position));
        recyclerviewFoodItemList.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerviewFoodItemList.setAdapter(recyclerViewAdapteFoodItemList);

        textViewTableNameOD.setText(closedCancelledMainOrderDetails.get(position).get("tableName").toString());
        textViewOrderIdOD.setText(closedCancelledVenOrderDetails.get(position).get("orderId").toString());
        textViewAdminNameOD.setText(closedCancelledMainOrderDetails.get(position).get("displayName").toString());
        textViewTaxAmountOD.setText(Home.currencyFc +  Home.commaFormatter.format( Double.parseDouble(closedCancelledVenOrderDetails.get(position).get("totalTax").toString())) );
        textViewTotalAmountOD.setText(Home.currencyFc +  Home.commaFormatter.format( Double.parseDouble(closedCancelledVenOrderDetails.get(position).get("subTotalAmount").toString())) );

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
        return closedCancelledVenOrderDetails.size();
    }





    public static class MyViewHolderOrderItem extends RecyclerView.ViewHolder{

        CardView closedCancelledOrderCardView;
        TextView textViewTableName;
        TextView textViewOrderId;
        TextView textViewVendorName;
//        TextView textViewAdminName;
        TextView textViewTimer;
        TextView textViewOrderStatus;
        Dialog orderDetailsPopUp;

        MyViewHolderOrderItem(View itemView) {
            super(itemView);

            closedCancelledOrderCardView = itemView.findViewById(R.id.closedCancelledOrderCardView);
            textViewTableName = itemView.findViewById(R.id.textViewTableName);
            textViewOrderId = itemView.findViewById(R.id.textViewOrderId);
            textViewVendorName = itemView.findViewById(R.id.textViewVendorName);
//            textViewAdminName = itemView.findViewById(R.id.textViewAdminName);
            textViewTimer = itemView.findViewById(R.id.textViewTimer);
            textViewOrderStatus = itemView.findViewById(R.id.textViewOrderStatus);


        }

    }
}
