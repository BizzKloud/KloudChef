package com.example.smahadik.kloudchef;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.Timestamp;

import java.security.PrivateKey;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class RecyclerViewAdapteOpenOrderList extends RecyclerView.Adapter<RecyclerViewAdapteOpenOrderList.MyViewHolderOrderItem> {

    Context context;
    LayoutInflater layoutInflater;
    View view;
    ArrayList<HashMap> openMainOrderDetails;
    ArrayList<HashMap> openVenOrderDetails;
    ArrayList<ArrayList<HashMap>> openFoodItemsDetails;
//    RecyclerViewClickListener listener;
    Dialog orderCancelPopup;
    Dialog orderDetailsPopUp;



//    public RecyclerViewAdapteOpenOrderList (Context context, ArrayList<HashMap> openMainOrderDetails , ArrayList<HashMap> openVenOrderDetails ,  ArrayList<ArrayList <HashMap>> openFoodItemsDetails, RecyclerViewClickListener listener) {
    public RecyclerViewAdapteOpenOrderList (Context context, ArrayList<HashMap> openMainOrderDetails , ArrayList<HashMap> openVenOrderDetails ,  ArrayList<ArrayList <HashMap>> openFoodItemsDetails) {
        this.context = context;
        this.openMainOrderDetails = openMainOrderDetails;
        this.openVenOrderDetails = openVenOrderDetails;
        this.openFoodItemsDetails = openFoodItemsDetails;
//        this.listener = listener;

    }

    @NonNull
    @Override
    public MyViewHolderOrderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.open_order_cardview, parent, false);
//        return new MyViewHolderOrderItem(view, listener);
        return new MyViewHolderOrderItem(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolderOrderItem holder, final int position) {

        holder.textViewTableName.setText(openMainOrderDetails.get(position).get("tableName").toString());
        holder.textViewOrderId.setText(openVenOrderDetails.get(position).get("orderId").toString());
        holder.textViewVendorName.setText(openVenOrderDetails.get(position).get("name").toString());
//        holder.textViewAdminName.setText(openMainOrderDetails.get(position).get("displayName").toString());

//        Timestamp timestamp = (Timestamp) openMainOrderDetails.get(position).get("time");
//        Date today = new Date();
//        long currentTime = today.getTime();
//        timeElapsed =  Calendar.getInstance().getTime().getTime() - timestamp.toDate().getTime() ;
////        Log.i("TIMER ORDER: " , timestamp.toDate().toString() );
////        Log.i("TIMER CURRENT: " , String.valueOf(currentTime) );
////        Log.i("DIFF : " , String.valueOf(timeElapsed) );
//
//        holder.timer = new Timer();
//        holder.timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                long seconds = timeElapsed / 1000;
//                long minutes = seconds / 60;
//                long hours = minutes / 60;
//                String time = hours % 24 + ":" + minutes % 60 + ":" + seconds % 60;
//                holder.textViewTimer.setText(time);
//                timeElapsed++;
//            }
//        },1, 1);

        holder.openOrderCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrderDetailspopup(holder, position);
            }
        });


    }




    public void showOrderDetailspopup(final RecyclerViewAdapteOpenOrderList.MyViewHolderOrderItem holder, final int position) {

        orderDetailsPopUp = new Dialog(context);
        orderDetailsPopUp.setCanceledOnTouchOutside(false);
        orderDetailsPopUp.setContentView(R.layout.open_order_details_popup_cardview);

        TextView textViewTableNameOD = orderDetailsPopUp.findViewById(R.id.textViewTableNameOD);
        TextView textViewOrderIdOD = orderDetailsPopUp.findViewById(R.id.textViewOrderIdOD);
        TextView textViewAdminNameOD = orderDetailsPopUp.findViewById(R.id.textViewAdminNameOD);
        TextView textViewTaxAmountOD = orderDetailsPopUp.findViewById(R.id.textViewTaxAmountOD);
        TextView textViewTotalAmountOD = orderDetailsPopUp.findViewById(R.id.textViewTotalAmountOD);
        Button cancelButton = orderDetailsPopUp.findViewById(R.id.cancelButton);
        Button acceptButton = orderDetailsPopUp.findViewById(R.id.acceptButton);
        ImageButton closeButtonOD = orderDetailsPopUp.findViewById(R.id.closeButtonOD);

        RecyclerView recyclerviewFoodItemList = orderDetailsPopUp.findViewById(R.id.recyclerviewFoodItemList);
        RecyclerViewAdapteFoodItemList recyclerViewAdapteFoodItemList = new RecyclerViewAdapteFoodItemList(context, openFoodItemsDetails.get(position));
        recyclerviewFoodItemList.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerviewFoodItemList.setAdapter(recyclerViewAdapteFoodItemList);

        textViewTableNameOD.setText(Home.openMainOrderDetails.get(position).get("tableName").toString());
        textViewOrderIdOD.setText(Home.openVenOrderDetails.get(position).get("orderId").toString());
        textViewAdminNameOD.setText(Home.openMainOrderDetails.get(position).get("displayName").toString());
        textViewTaxAmountOD.setText(Home.currencyFc + Home.commaFormatter.format(Double.parseDouble(Home.openVenOrderDetails.get(position).get("totalTax").toString())) );
        textViewTotalAmountOD.setText(Home.currencyFc + Home.commaFormatter.format( Double.parseDouble(Home.openVenOrderDetails.get(position).get("subTotalAmount").toString())) );

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
                        orderDetailsPopUp.dismiss();
                        Home.home.updateOrderStatus(position, "CANCELLED", "OPEN");
                    }
                });

                orderCancelPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                orderCancelPopup.show();

            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderDetailsPopUp.dismiss();
                Home.home.updateOrderStatus(position, "ACCEPTED" , "OPEN");
            }
        });

        closeButtonOD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderDetailsPopUp.dismiss();
            }
        });

        orderDetailsPopUp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        orderDetailsPopUp.show();
    }



    @Override
    public int getItemCount() {
        return openVenOrderDetails.size();
    }





    public static class MyViewHolderOrderItem extends RecyclerView.ViewHolder{

        CardView openOrderCardView;
        TextView textViewTableName;
        TextView textViewOrderId;
        TextView textViewVendorName;
//        TextView textViewAdminName;
        TextView textViewTimer;
//        Timer timer;
//        Dialog orderDetailsPopUp;
//        recyclerviewFoodItemList;


//        private RecyclerViewClickListener mListener;

//        MyViewHolderOrderItem(View itemView, RecyclerViewClickListener listener) {
        MyViewHolderOrderItem(View itemView) {
            super(itemView);

            openOrderCardView = itemView.findViewById(R.id.openOrderCardView);
            textViewTableName = itemView.findViewById(R.id.textViewTableName);
            textViewOrderId = itemView.findViewById(R.id.textViewOrderId);
            textViewVendorName = itemView.findViewById(R.id.textViewVendorName);
//            textViewAdminName = itemView.findViewById(R.id.textViewAdminName);
            textViewTimer = itemView.findViewById(R.id.textViewTimer);

//            mListener = listener;
//            itemView.setOnClickListener(MyViewHolderOrderItem.this);

        }

//        @Override
//        public void onClick(View view) {
//
//            int pos = getAdapterPosition();
//            if (pos != RecyclerView.NO_POSITION){
//                mListener.onClick(view, pos);
//            }
////            mListener.onClick(view, getAdapterPosition());
//        }
    }
}
