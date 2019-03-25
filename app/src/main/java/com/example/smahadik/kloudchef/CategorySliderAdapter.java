package com.example.smahadik.kloudchef;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CategorySliderAdapter extends PagerAdapter {

    Context context;
    View view;
    LayoutInflater layoutInflater;
    RecyclerView recyclerViewOrderList;
    RecyclerViewAdapteOpenOrderList recyclerViewAdapteOpenOrderList;
    RecyclerViewAdapteAcceptedOrderList recyclerViewAdapteAcceptedOrderList;
    RecyclerViewAdapteClosedCancelledOrderList recyclerViewAdapteClosedCancelledOrderList;


    public CategorySliderAdapter (Context context)  { this.context = context; }

    @Override
    public int getCount() { return 4; }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.category_order_list_fragment, container, false);

        recyclerViewOrderList = view.findViewById(R.id.recyclerviewOrderList);


        switch (position) {
            case 0:
                recyclerViewAdapteOpenOrderList = new RecyclerViewAdapteOpenOrderList(context ,Home.openMainOrderDetails, Home.openVenOrderDetails , Home.openFoodItemsDetails);
                recyclerViewOrderList.setLayoutManager(new GridLayoutManager(context , 1));
                recyclerViewOrderList.setAdapter(recyclerViewAdapteOpenOrderList);
                break;
            case 1:
                recyclerViewAdapteAcceptedOrderList = new RecyclerViewAdapteAcceptedOrderList(context, Home.acceptedMainOrderDetails , Home.acceptedVenOrderDetails, Home.acceptedFoodItemsDetails);
                recyclerViewOrderList.setLayoutManager(new GridLayoutManager(context , 1));
                recyclerViewOrderList.setAdapter(recyclerViewAdapteAcceptedOrderList);
                break;
            case 2:
                recyclerViewAdapteClosedCancelledOrderList = new RecyclerViewAdapteClosedCancelledOrderList(context,  Home.closedMainOrderDetails , Home.closedVenOrderDetails, Home.closedFoodItemsDetails);
                recyclerViewOrderList.setLayoutManager(new GridLayoutManager(context , 1));
                recyclerViewOrderList.setAdapter(recyclerViewAdapteClosedCancelledOrderList);
                break;
            case 3:
                recyclerViewAdapteClosedCancelledOrderList = new RecyclerViewAdapteClosedCancelledOrderList(context,  Home.cancelledMainOrderDetails , Home.cancelledVenOrderDetails, Home.cancelledFoodItemsDetails);
                recyclerViewOrderList.setLayoutManager(new GridLayoutManager(context , 1));
                recyclerViewOrderList.setAdapter(recyclerViewAdapteClosedCancelledOrderList);
                break;
        }




        container.addView(view);

        return view;
    }

//    public void onClick(int position){
//// Here you will get the position
//        Log.i("GOT POSITION" , String.valueOf(position) );
//    }


//    public void showOrderDetailspopup(final int position) {
//
//        Dialog orderDetailsPopUp = new Dialog(context);
//        orderDetailsPopUp.setCanceledOnTouchOutside(true);
//        orderDetailsPopUp.setContentView(R.layout.open_order_details_popup_cardview);
//
//        TextView textViewTableNameOD = orderDetailsPopUp.findViewById(R.id.textViewTableNameOD);
//        TextView textViewOrderIdOD = orderDetailsPopUp.findViewById(R.id.textViewOrderIdOD);
//        TextView textViewAdminNameOD = orderDetailsPopUp.findViewById(R.id.textViewAdminNameOD);
//        TextView textViewTaxAmountOD = orderDetailsPopUp.findViewById(R.id.textViewTaxAmountOD);
//        TextView textViewTotalAmountOD = orderDetailsPopUp.findViewById(R.id.textViewTotalAmountOD);
//        Button cancelButton = orderDetailsPopUp.findViewById(R.id.cancelButton);
//        Button acceptButton = orderDetailsPopUp.findViewById(R.id.acceptButton);
//
//        textViewTableNameOD.setText(Home.openMainOrderDetails.get(position).get("tableName").toString());
//        textViewOrderIdOD.setText(Home.openVenOrderDetails.get(position).get("orderId").toString());
//        textViewAdminNameOD.setText(Home.openMainOrderDetails.get(position).get("displayName").toString());
//        textViewTaxAmountOD.setText(Home.commaFormatter.format( Double.parseDouble(Home.openVenOrderDetails.get(position).get("totalTax").toString())) );
//        textViewTotalAmountOD.setText(Home.commaFormatter.format( Double.parseDouble(Home.openVenOrderDetails.get(position).get("subTotalAmount").toString())) );
//
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Home.dbTransaction.collection(openMainOrderDetails.get(position).get("orderId").toString())
////                        .document(openVenOrderDetails.get(position).get("orderId").toString()).update("orderStatus" , "CANCELLED");
//                ArrayList<String> oHStatus = (ArrayList<String>) Home.openVenOrderDetails.get(position).get("oHStatus");
//                oHStatus.add("CANCELLED");
//                Log.i("OHSTATUS" , oHStatus.toString());
//            }
//        });
//
//        orderDetailsPopUp.show();
//    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Home.vendorTabTitleListArr[position];
    }







}
