package com.example.smahadik.kloudchef;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.annotation.Nullable;

public class Home extends Activity {

    // Initilizations
    public static Home home= new Home();
//    public static String fcid;
    public static HashMap vendorDetails;
    public static TabLayout orderCategoryTabs;
    public static ViewPager orderListViewPager;
    public static CategorySliderAdapter categorySliderAdapter;
    public static ArrayList<HashMap> mainOrderDetails = new ArrayList<>();
    public static ArrayList<HashMap> venOrderDetails = new ArrayList<>();
    public static ArrayList< ArrayList<HashMap>> foodItemsDetails = new ArrayList<>();
    public static String currencyFc;
    HashMap fcDetails;
    TextView textViewOrderCount;
    HashMap orderIDRange = new HashMap();
    String [] zeros = {"00000000" , "0000000" , "000000" , "00000" , "0000" , "000" , "00" , "0"};
    String vendorIdCode;

    // Basic
    String newOrderId;
    Dialog logoutConfirmationPopup;
    public static ProgressDialog progressBar;
    Boolean statusForUpdate = true;


    //Firebase
    public static FirebaseFirestore firestore;
    public static DocumentReference dbTransaction;

    // Basics
    public static DecimalFormat commaFormatter = new DecimalFormat("##,##,##,##0.00");
//    public static DecimalFormat decimalFormatter = new DecimalFormat("####0.00");
//    public static DecimalFormat removedecimalFormatter = new DecimalFormat("#####");
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    public static String todaysDate = simpleDateFormat.format(Calendar.getInstance().getTime());
    public static String [] vendorTabTitleListArr = {"OPEN" , "ACCEPTED" , "CLOSED" , "CANCELLED"};

    // OPEN
    public static ArrayList<HashMap> openMainOrderDetails = new ArrayList<>();
    public static ArrayList<HashMap> openVenOrderDetails = new ArrayList<>();
    public static ArrayList< ArrayList<HashMap>> openFoodItemsDetails = new ArrayList<>();
//    public static ArrayList< ArrayList<HashMap>> openVenTaxDetails = new ArrayList<>();

    // ACCEPTED
    public static ArrayList<HashMap> acceptedMainOrderDetails = new ArrayList<>();
    public static ArrayList<HashMap> acceptedVenOrderDetails = new ArrayList<>();
    public static ArrayList< ArrayList<HashMap>> acceptedFoodItemsDetails = new ArrayList<>();
//    public static ArrayList< ArrayList<HashMap>> acceptedVenTaxDetails = new ArrayList<>();

    // COMPLETED
    public static ArrayList<HashMap> closedMainOrderDetails = new ArrayList<>();
    public static ArrayList<HashMap> closedVenOrderDetails = new ArrayList<>();
    public static ArrayList< ArrayList<HashMap>> closedFoodItemsDetails = new ArrayList<>();

    // CANCELLED
    public static ArrayList<HashMap> cancelledMainOrderDetails = new ArrayList<>();
    public static ArrayList<HashMap> cancelledVenOrderDetails = new ArrayList<>();
    public static ArrayList< ArrayList<HashMap>> cancelledFoodItemsDetails = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Intent
        Intent login = getIntent();
        logoutConfirmationPopup = new Dialog(this);
        progressBar = new ProgressDialog(this);
        progressBar.setCanceledOnTouchOutside(false);
//        fcid = login.getStringExtra("fcid");
        fcDetails = (HashMap) login.getSerializableExtra("fcDetails");
        vendorDetails = (HashMap) login.getSerializableExtra("vendorDetails");
        vendorIdCode = vendorDetails.get("venid").toString().substring(vendorDetails.get("venid").toString().length()-3);


        // Initializations
        orderListViewPager = findViewById(R.id.orderListViewPager);
        textViewOrderCount = findViewById(R.id.textViewOrderCount);

        currencyFc = fcDetails.get("curky").toString();
        if(currencyFc.equals("INR")) {
            currencyFc = "\u20B9" + " ";
        }else { currencyFc = "$ "; }


        // FireBase
        firestore = FirebaseFirestore.getInstance();
        dbTransaction = firestore.document( "transactions/" + fcDetails.get("fcid") + "/orders/" + todaysDate);
        getOrderIds();


//        Log.i("1 FCID" , fcid);
        Log.i("1 Vendor Details" , vendorDetails.toString());

//        updateTableLayout(4000);




    } // OnCreate Done


    public void updateTableLayout(int seconds) {

        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()){
            new CountDownTimer(seconds, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }
                @Override
                public void onFinish() {
                    Log.i("MAIN ORDER DETAILS" , mainOrderDetails.toString());
                    Log.i("MAIN ORDER SIZE" , String.valueOf(mainOrderDetails.size()) );
                    Log.i("VENDOR ORDER DETAILS" , venOrderDetails.toString());
                    Log.i("VENDOR SIZE" , String.valueOf(venOrderDetails.size()) );
                    Log.i("FOODITEM ORDER DETAILS" , foodItemsDetails.toString());
                    Log.i("FOODITEM SIZE" , String.valueOf(foodItemsDetails.size()) );
                    setupDatabaseForTabs();
                }
            }.start();
        } else {
            Toast.makeText(this, "Check INTERNET Connection", Toast.LENGTH_LONG).show();
        }

    }


    public void setupDatabaseForTabs() {

        //OPEN Orders
        openMainOrderDetails.clear();
        openVenOrderDetails.clear();
        openFoodItemsDetails.clear();
        //ACCEPTED Orders
        acceptedMainOrderDetails.clear();
        acceptedVenOrderDetails.clear();
        acceptedFoodItemsDetails.clear();
        // CLOSED Order
        closedMainOrderDetails.clear();
        closedVenOrderDetails.clear();
        closedFoodItemsDetails.clear();
        // CANCELLED Order
        cancelledMainOrderDetails.clear();
        cancelledVenOrderDetails.clear();
        cancelledFoodItemsDetails.clear();

        for (int i=0; i<foodItemsDetails.size() ; i++) {

            Log.i("MAIN ORDER DETAILS" , mainOrderDetails.get(i).toString());
            Log.i("VENDOR ORDER DETAILS" , venOrderDetails.get(i).toString());
            Log.i("FOODITEM ORDER DETAILS" , foodItemsDetails.get(i).toString());

            switch (venOrderDetails.get(i).get("orderStatus").toString()) {

                case "OPEN" :
                    openMainOrderDetails.add(mainOrderDetails.get(i));
                    openVenOrderDetails.add(venOrderDetails.get(i));
                    openFoodItemsDetails.add(foodItemsDetails.get(i));
//                    Log.i("OPEN ORDER DETAILS" , openMainOrderDetails.toString());
//                    Log.i(" OPEN VENDOR ORDER DETAILS" , openVenOrderDetails.toString());
//                    Log.i("OPEN FOoD ITEM" ,openFoodItemsDetails.toString() );
//                    Log.i("OPEN FOoD ITEM -1 main" ,foodItemsDetails.get(i).toString() );
                    break;

                case "ACCEPTED" :
                    acceptedMainOrderDetails.add(mainOrderDetails.get(i));
                    acceptedVenOrderDetails.add(venOrderDetails.get(i));
                    acceptedFoodItemsDetails.add(foodItemsDetails.get(i));

//                    Log.i("ACCEPTED ORDER DETAILS" , acceptedMainOrderDetails.toString());
//                    Log.i(" ACCEPETD VENDOR ORDER DETAILS" , acceptedVenOrderDetails.toString());
//                    Log.i("ACCEPTED FOoD ITEM" ,acceptedFoodItemsDetails.toString() );
//                    Log.i("ACCEPTED FOoD ITEM -1 main" ,foodItemsDetails.get(i).toString() );
                    break;

                case "CLOSED" :
                    closedMainOrderDetails.add(mainOrderDetails.get(i));
                    closedVenOrderDetails.add(venOrderDetails.get(i));
                    closedFoodItemsDetails.add(foodItemsDetails.get(i));
                    break;

                case "CANCELLED" :
                    cancelledMainOrderDetails.add(mainOrderDetails.get(i));
                    cancelledVenOrderDetails.add(venOrderDetails.get(i));
                    cancelledFoodItemsDetails.add(foodItemsDetails.get(i));
                    break;

            }
        }

        sortOrders(closedMainOrderDetails, closedVenOrderDetails, closedFoodItemsDetails );
        sortOrders(cancelledMainOrderDetails, cancelledVenOrderDetails, cancelledFoodItemsDetails );
        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() { setupViewPager(); }
        }.start();

    }


    public void setupViewPager() {
        categorySliderAdapter = new CategorySliderAdapter(this);
        orderListViewPager.setAdapter(categorySliderAdapter);
        orderCategoryTabs = findViewById(R.id.orderCategoryTabs);
        orderCategoryTabs.setupWithViewPager(orderListViewPager);

        for(int i=0; i<vendorTabTitleListArr.length; i++) {

            View view;

            if(i < 2) {
                view = LayoutInflater.from(this).inflate(R.layout.custom_tab_with_counter, null);
                TextView textViewTabTitle = view.findViewById(R.id.textViewTabTitle);
                textViewTabTitle.setText(vendorTabTitleListArr[i]);

                TextView textViewTabCount = view.findViewById(R.id.textViewTabCount);
                textViewTabCount.setVisibility(View.VISIBLE);
                if(i == 0) {
                    textViewOrderCount.setText(String.valueOf(openMainOrderDetails.size()));
                    textViewTabCount.setText(String.valueOf(openMainOrderDetails.size()));
                } else {
                    textViewTabCount.setText(String.valueOf(acceptedMainOrderDetails.size()));
                }
            } else {
                view = LayoutInflater.from(this).inflate(R.layout.custom_tab_without_counter, null);
                TextView textViewTabTitle = view.findViewById(R.id.textViewTabTitle);
                textViewTabTitle.setText(vendorTabTitleListArr[i]);
            }
            orderCategoryTabs.getTabAt(i).setCustomView(view);
        }

        progressBar.dismiss();

    }


    public void sortOrders(ArrayList<HashMap> SMainOrderDetails , ArrayList<HashMap> SVenOrderDetails , ArrayList< ArrayList<HashMap>> SFoodItemsDetails) {

        for (int i = 0; i < SVenOrderDetails.size(); i++) {

            for (int j = SVenOrderDetails.size() - 1; j > i; j--) {

                Timestamp one = (Timestamp) SVenOrderDetails.get(i).get("endTime");
                Timestamp two = (Timestamp) SVenOrderDetails.get(j).get("endTime");

                if ( one.toDate().getTime() < two.toDate().getTime() ) {

                    HashMap tmpOrderMain = SMainOrderDetails.get(i);
                    HashMap tmpOrderVendor = SVenOrderDetails.get(i);
                    ArrayList<HashMap> tmpOrderFoodItem = SFoodItemsDetails.get(i);

                    SMainOrderDetails.set(i, SMainOrderDetails.get(j));
                    SMainOrderDetails.set(j,tmpOrderMain);
                    SVenOrderDetails.set(i, SVenOrderDetails.get(j));
                    SVenOrderDetails.set(j,tmpOrderVendor);
                    SFoodItemsDetails.set(i, SFoodItemsDetails.get(j));
                    SFoodItemsDetails.set(j,tmpOrderFoodItem);
                }

            }
        }

        if(SMainOrderDetails.size() > 0) {
            if(SVenOrderDetails.get(0).get("orderStatus").toString().equals("CLOSED")) {
                closedMainOrderDetails = SMainOrderDetails;
                closedVenOrderDetails = SVenOrderDetails;
                closedFoodItemsDetails = SFoodItemsDetails;
            } else {
                cancelledMainOrderDetails = SMainOrderDetails;
                cancelledVenOrderDetails = SVenOrderDetails;
                cancelledFoodItemsDetails = SFoodItemsDetails;
            }
        }
//        Log.i("SORTED ORDER ID ARRAY" , Home.orderIdArr.toString());
    }




    // UPADTING DATABASE => ACCEPT, CANCEL, READY ORDER ============================================
    public void updateOrderStatus (int position, String status, String tabName) {

        if(tabName.equals("OPEN")) {
            progressBar.setMessage("Updating Order Details");
            progressBar.show();

            Home.dbTransaction
                    .collection(openMainOrderDetails.get(position).get("orderId").toString())
                    .document(openVenOrderDetails.get(position).get("orderId").toString()).update("orderStatus" , status);

            ArrayList<String> oHStatus = (ArrayList<String>) Home.openVenOrderDetails.get(position).get("oHStatus");
            oHStatus.add(status);

            Home.dbTransaction
                    .collection(openMainOrderDetails.get(position).get("orderId").toString())
                    .document(openVenOrderDetails.get(position).get("orderId").toString()).update("oHStatus" , oHStatus);

            if(status.equals("CANCELLED")) {
                Home.dbTransaction
                        .collection(openMainOrderDetails.get(position).get("orderId").toString())
                        .document(openVenOrderDetails.get(position).get("orderId").toString()).update("endTime" , Calendar.getInstance().getTime());
                updateFinalOrderStatus(openMainOrderDetails.get(position).get("orderId").toString());
            }

        } else if(tabName.equals("ACCEPTED")) {
            progressBar.setMessage("Updating Order Details");
            progressBar.show();

            Home.dbTransaction
                    .collection(acceptedMainOrderDetails.get(position).get("orderId").toString())
                    .document(acceptedVenOrderDetails.get(position).get("orderId").toString()).update("orderStatus" , status);

            ArrayList<String> oHStatus = (ArrayList<String>) acceptedVenOrderDetails.get(position).get("oHStatus");
            oHStatus.add(status);

            Home.dbTransaction
                    .collection(acceptedMainOrderDetails.get(position).get("orderId").toString())
                    .document(acceptedVenOrderDetails.get(position).get("orderId").toString()).update("oHStatus" , oHStatus);

            Home.dbTransaction
                    .collection(acceptedMainOrderDetails.get(position).get("orderId").toString())
                    .document(acceptedVenOrderDetails.get(position).get("orderId").toString()).update("endTime" , Calendar.getInstance().getTime());

            updateFinalOrderStatus(acceptedMainOrderDetails.get(position).get("orderId").toString());
        }


    }


    public void updateFinalOrderStatus(final String orderId) {

        Log.i("Updating FINAL Order Details for :" , orderId);

        dbTransaction.collection(orderId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                statusForUpdate = true;
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                    String id = document.getId();
                    if(id.equals("Total") || id.equals("Freebie") || id.equals("Tax_Fcm") ) {
                        continue;
                    }else {
                        if(document.get("orderStatus").toString().equals("OPEN") || document.get("orderStatus").toString().equals("ACCEPTED")) {
                            statusForUpdate = false;
                            break;
                        }
                    }
                }

                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) { }

                    @Override
                    public void onFinish() {
                        Log.i("statusForUpdate : " , String.valueOf(statusForUpdate));
                        if(statusForUpdate) {
                            Home.dbTransaction
                                    .collection(orderId)
                                    .document("Total").update("finalOrderStatus" , "CLOSED");
                        }
                    }
                }.start();


            }
        });


    }







    // GETTING DATABASE ============================================================================
    public void getOrderIds() {

        // ORDER ID RANGE
        dbTransaction.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                orderIDRange = (HashMap) documentSnapshot.getData();
//                Log.i("1 ORDER ID RANGE" , orderIDRange.toString());
                progressBar.setMessage("Getting Order Details");
                progressBar.show();
                if(orderIDRange != null) {
                    new CountDownTimer(5000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }
                        @Override
                        public void onFinish() {
                            getVenOrderDetails();
                        }
                    }.start();
                } else {
                    Toast.makeText(Home.this, "Orders NOT Found !", Toast.LENGTH_SHORT).show();
                    updateTableLayout(2000);
                }
//                updateTableLayout(10000);

            }
        });
    }


    public void getVenOrderDetails() {

        int size = Integer.parseInt(orderIDRange.get("eOrderId").toString()) - Integer.parseInt(orderIDRange.get("sOrderId").toString());
        for(int i=0; i<=size ; i++) {

            String odid = orderIDRange.get("sOrderId").toString();
            newOrderId = String.valueOf(Integer.parseInt(odid) + i );
            newOrderId = zeros[newOrderId.length()-1] + newOrderId;

//            Log.i("1 CHECKING FOR ORDER ID" , newOrderId);

            int id = getVendorID(newOrderId+vendorIdCode);
            if(id == -1) {

                dbTransaction.collection(newOrderId).document(newOrderId+vendorIdCode).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot.exists()) {
                            HashMap mainDoc = (HashMap) documentSnapshot.getData();
//                            Log.i("1 DOC EXIST for ORderID :" , mainDoc.get("orderId").toString());
//                            Log.i("1 DOC  :" , mainDoc.toString());

                            int id1 = getVendorID(mainDoc.get("orderId").toString());
                            if(id1 != -1) {
                                // ADD to to particular field
                                venOrderDetails.set(id1, mainDoc);
//                                Log.i("1 UPDATED VENDOR ORDER DETAILS" , venOrderDetails.toString());
                                // SETORDER LIST ADAPTER ==> CALL UPDATES()
                                updateTableLayout(4000);
                            } else {
//                                Log.i("1 DOC ADDED for ORderID :" , newOrderId);
//                                Log.i("1 DOC  :" , mainDoc.toString());
                                venOrderDetails.add(mainDoc);
                            }
                        }
                    }
                });
            }

            if(size == i) {
                new CountDownTimer(6000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }
                    @Override
                    public void onFinish() {
                        getMainOrderDetails();
                    }
                }.start();
            }


        }

    }


    public void getMainOrderDetails() {

//        Log.i("CALLED MAIN ORDER DETAILS" , "TRUE");
//        Log.i("VENDOR ORDER DETAILS" , venOrderDetails.toString());

        progressBar.setMessage("Getting Order Details");
        progressBar.show();

        int startIndex = 0;
        if(mainOrderDetails.size() != 0) {
            startIndex = mainOrderDetails.size();
        }

        for(int i=startIndex; i<venOrderDetails.size() ; i++) {

            String mainOrderid = venOrderDetails.get(i).get("orderId").toString();
            mainOrderid = mainOrderid.substring(0, mainOrderid.length()-3);
//            Log.i("1 CHECKING FOR MAIN ORDER ID" , mainOrderid);

            dbTransaction.collection(mainOrderid).document("Total").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if(documentSnapshot.exists()) {
                        HashMap mainTotalDoc = (HashMap) documentSnapshot.getData();
//                        Log.i("1 DOC EXIST for ORderID :" , mainTotalDoc.get("orderId").toString());
//                        Log.i("1 DOC  :" , mainTotalDoc.toString());

                        int id = getMainID(mainTotalDoc.get("orderId").toString());
                        if(id != -1) {
                            // ADD to to particular field
                            mainOrderDetails.set(id, mainTotalDoc);
//                            Log.i("1 UPDATED VENDOR ORDER DETAILS" , mainOrderDetails.toString());
                            // SETORDER LIST ADAPTER ==> CALL UPDATES()

                        } else {
//                            Log.i("1 DOC ADDED for ORderID :" ,  mainTotalDoc.get("orderId").toString());
//                            Log.i("1 DOC  :" , mainTotalDoc.toString());
                            mainOrderDetails.add(mainTotalDoc);
                            // SETORDER LIST ADAPTER ==> CALL UPDATES()
                        }

                        if(mainOrderDetails.size() == venOrderDetails.size()) {
                            new CountDownTimer(3000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }
                                @Override
                                public void onFinish() {
                                    getFddDetails();
                                }
                            }.start();
                        }

                        // SETORDER LIST ADAPTER ==> CALL UPDATES()
                    }
                }
            });

        }
    }


    public void getFddDetails () {

//        Log.i("CALLED getFddAndTaxDetails DETAILS" , "TRUE");
//        Log.i("VENDOR ORDER DETAILS" , venOrderDetails.toString());
//        Log.i("MAIN ORDER DETAILS" , mainOrderDetails.toString());

        int startIndex = 0;
        if(foodItemsDetails.size() != 0) {
            startIndex = foodItemsDetails.size();
        }

        for(int i=startIndex; i<venOrderDetails.size() ; i++) {

//            Log.i("1 CHECKING FOR MAIN ORDER ID" , mainOrderDetails.get(i).get("orderId").toString());
//            Log.i("1 CHECKING FOR VENODR ORDER ID" , venOrderDetails.get(i).get("orderId").toString());

            String pathFdd = mainOrderDetails.get(i).get("orderId").toString()+ "/" + venOrderDetails.get(i).get("orderId").toString() + "/FoodItems";
//            String pathVenTax = mainOrderDetails.get(i).get("orderId").toString()+ "/" + venOrderDetails.get(i).get("orderId").toString() + "/VendorTax";

            dbTransaction.collection(pathFdd).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    ArrayList<HashMap> foodItemList = new ArrayList<>();
                    for(QueryDocumentSnapshot foodItemDoc : queryDocumentSnapshots) {
                        foodItemList.add((HashMap) foodItemDoc.getData());
                    }

//                    Log.i("FOODITEM ADDED" , foodItemList.toString());
                    foodItemsDetails.add(foodItemList);
//                    Log.i("FOOD ITEM SIZE" , String.valueOf(foodItemsDetails.size()));

                    if(foodItemsDetails.size() == mainOrderDetails.size()) {
                        progressBar.setMessage("Getting Order Details");
                        progressBar.show();
                        updateTableLayout(5000);
                    }

                }
            });

//            dbTransaction.collection(pathVenTax).addSnapshotListener(new EventListener<QuerySnapshot>() {
//                @Override
//                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
//
//                    ArrayList<HashMap> venTaxList = new ArrayList<>();
////                    Log.i("CURRENT VEN", mainOrderDetails.get(i).get("orderID").toString() );
//                    for(QueryDocumentSnapshot foodItemDoc : queryDocumentSnapshots) {
//                        venTaxList.add((HashMap) foodItemDoc.getData());
////                            Log.i("INSIDE FOODITEM-1 DETAILS" , foodItemList.toString());
////                            Log.i("INSIDE VEN-FOODITEM-1 DETAILS" , venfoodItemList.toString());
////                            Log.i("INSIDE FOODITEM DETAILS" , Home.orderFoodItemArr.toString());
//                    }
//                    venTaxDetails.add(venTaxList);
//
//                }
//            });
        }

    }


    public int getVendorID (String venid) {

        for(int i=0; i<venOrderDetails.size(); i++) {
            // vendor Found in orderVendor
            if (venOrderDetails.get(i).get("orderId").toString().equals(venid)) {
//                Log.i("1 FOUND AT ID : i = " , String.valueOf(i));
                return i;
            }
        }
//        Log.i("Food Item in menu " , "NOT FOUND");
        return -1;

    }


    public int getMainID (String mainid) {

        for(int i=0; i<mainOrderDetails.size(); i++) {
            // Order Found in Mainorder
            if (mainOrderDetails.get(i).get("orderId").toString().equals(mainid)) {
//                Log.i("1 FOUND AT ID : i = " , String.valueOf(i));
                return i;
            }
        }
//        Log.i("Food Item in menu " , "NOT FOUND");
        return -1;

    }




    // LOGOUT ======================================================================================
    public void logout(View view) {

        logoutConfirmationPopup.setCanceledOnTouchOutside(false);
        logoutConfirmationPopup.setContentView(R.layout.logout_confirmation_popup_card);
        Button logoutCancelButton = logoutConfirmationPopup.findViewById(R.id.logoutCancelButton);
        Button logoutYesButton = logoutConfirmationPopup.findViewById(R.id.logoutYesButton);
        logoutCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutConfirmationPopup.dismiss();
            }
        });
        logoutYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutConfirmationPopup.dismiss();
                Intent logout = new Intent(Home.this, Login.class);
                startActivity(logout);
                System.exit(0);
                finish();
            }
        });
        logoutConfirmationPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        logoutConfirmationPopup.show();

    }





}










