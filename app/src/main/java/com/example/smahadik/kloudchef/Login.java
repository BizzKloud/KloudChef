package com.example.smahadik.kloudchef;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;



public class Login extends AppCompatActivity {

    // Firestore
    FirebaseFirestore firestore;
    CollectionReference db;

    Spinner foodcourtSpinner;
    Spinner usernameSpinner;
    EditText passEditText;
    Button loginButton;
//    Switch adminswitch;
    ProgressDialog progressDialog;


    // Variables
    ArrayList<HashMap> foodCourtArr = new ArrayList<>();
    ArrayList<HashMap> usersArr = new ArrayList<>();
    ArrayList<String> userNames = new ArrayList<>();
    ArrayList<String> fcNames = new ArrayList<>();
    ArrayAdapter<String> adapterfc;
    ArrayAdapter<String> adapterUser;
    String foodcourtsPath = "foodcourts";
    String usersPath;
    String venid;
    String passcode;
//    String fcid;
    HashMap fcDetails;
    int posfc;
    int posVen;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        FirebaseApp.initializeApp(this);


        // FireStore Settings
        firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build();
        firestore.setFirestoreSettings(settings);


        db = FirebaseFirestore.getInstance().collection("foodcourts");


        // Initializations
        passEditText = findViewById(R.id.passEditText);
        foodcourtSpinner = findViewById(R.id.foodcourtSpinner);
        usernameSpinner = findViewById(R.id.usernameSpinner);
        loginButton = findViewById(R.id.loginButton);
//        adminswitch = findViewById(R.id.adminswitch);
        progressDialog = new ProgressDialog(this);

        new AsysncTask().execute(foodcourtsPath);

        //disable fields
        passEditText.setEnabled(false);
        usernameSpinner.setEnabled(false);


//        //Setting a Switch
//        adminswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                initialRestart(isChecked);
//            }
//        });

        // Spinner
        fcNames.add("Food Court");
        adapterfc = new ArrayAdapter<String>(this, R.layout.spinner_item, fcNames);
        foodcourtSpinner.setAdapter(adapterfc);

        foodcourtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.i(" FC Position Sleected" , String.valueOf(position));
                foodcourtSpinner.setSelection(position);

                TextView tv = (TextView) view;
                if (position == 0) { tv.setTextColor(Color.GRAY);
                } else { tv.setTextColor(Color.BLACK); }

                if(position > 0) {
                    passEditText.setEnabled(false);
                    passEditText.setText("");
                    usernameSpinner.setEnabled(true);
                    userNames.clear();
                    userNames.add("User Name");
                    usersArr.clear();
                    posfc = position-1;
//                    fcid = foodCourtArr.get(position-1).get("fcid").toString();
                    fcDetails = foodCourtArr.get(position-1);
                    usersPath = foodcourtsPath + "/" + foodCourtArr.get(position-1).get("fcid").toString() +"/VendorM";
                    new AsysncTask().execute(usersPath);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        userNames.add("User Name");
        adapterUser = new ArrayAdapter<String>(this, R.layout.spinner_item, userNames);
        usernameSpinner.setAdapter(adapterUser);

        usernameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                usernameSpinner.setSelection(position);
                Log.i(" USername Position Sleected" , String.valueOf(position));

                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                if(position > 0) {
                    passEditText.setText("");
                    posVen = position-1;
                    venid = usersArr.get(position-1).get("venid").toString();
                    passcode = usersArr.get(position-1).get("pwd").toString();
                    passEditText.setEnabled(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }// OnCreate Done.






//    // Initial Restart For Switch
//    public void initialRestart (boolean flag) {
//
//        if(flag) {
//            adminswitch.setTextColor(getResources().getColor(R.color.bizzorange));
//        }else {
//            adminswitch.setTextColor(getResources().getColor(R.color.grey));
//        }
//
//        passEditText.setEnabled(false);
//        usernameSpinner.setEnabled(false);
//        usernameSpinner.setSelection(0);
//        passEditText.setText("");
//        foodcourtSpinner.setSelection(0);
//    }



    // Set FoodCourts/User Names
    public ArrayList<String> setnames(ArrayList<String> names, ArrayList<HashMap> hashmapArr) {
        String name = names.get(0);
        names.clear();
        names.add(name);
        for (int i = 0; i < hashmapArr.size(); i++) {
            names.add(hashmapArr.get(i).get("name").toString());
        }
        return (names);
    }


    // Spinner Initializer
    public ArrayAdapter<String> initSpinner(ArrayList<String> list) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, list) {

            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;

    }



    // AsyncTask FireStore
    private class AsysncTask extends AsyncTask<String , Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            if(strings[0].contains("VendorM")) {

                firestore.collection(strings[0]).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        usersArr.clear();
                        for(QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            usersArr.add((HashMap) document.getData() );
                        }
                        //Initiliaze
                        userNames = setnames(userNames, usersArr);
                        usernameSpinner.setAdapter(initSpinner(userNames));
                        adapterUser.notifyDataSetChanged();

                    }
                });

            }else {
                db.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        foodCourtArr.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            foodCourtArr.add((HashMap) document.getData());
                        }

                        //Initialize
                        fcNames = setnames(fcNames, foodCourtArr);
                        foodcourtSpinner.setAdapter(initSpinner(fcNames));
                        adapterfc.notifyDataSetChanged();
                        usernameSpinner.setAdapter(initSpinner(userNames));
                        adapterUser.notifyDataSetChanged();
                    }
                });
            }
            return null;
        }
    }



    //ONClick LOGIN
    public void loginAttempt (View view) {

        progressDialog.setMessage("Authenticating Credentials");
        progressDialog.show();

        loginButton.setEnabled(false);
        Log.i("Login" , "Clicked");
        String password = passEditText.getText().toString().trim();
        if(foodcourtSpinner.getSelectedItem() == "Food Court") {
            progressDialog.dismiss();
            loginButton.setEnabled(true);
            Toast.makeText(this, "Select 'FoodCourt' ", Toast.LENGTH_LONG).show();
        }
        else if (usernameSpinner.getSelectedItem() == "User Name") {
            progressDialog.dismiss();
            loginButton.setEnabled(true);
            Toast.makeText(this, "Select 'User Name' ", Toast.LENGTH_LONG).show();
        }
        else if(password.equals("")) {
            progressDialog.dismiss();
            loginButton.setEnabled(true);
            Toast.makeText(this, "Enter Password", Toast.LENGTH_LONG).show();
        }
        else {

            if(password.equals(passcode)) {
                Log.i("Login" , "Clicked");
                Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
                Intent Home = new Intent(this, Home.class);
//                Home.putExtra("fcid", fcid);
                Home.putExtra("fcDetails", fcDetails);
                Home.putExtra("vendorDetails" , usersArr.get(posVen));
                progressDialog.dismiss();
                loginButton.setEnabled(true);

                Log.i("Food Court" , foodcourtSpinner.getSelectedItem().toString() );
                Log.i("User Name" , usernameSpinner.getSelectedItem().toString() );
                Log.i("Passcode" , passcode);
                Log.i("Password" , passEditText.getText().toString() );


                startActivity(Home);
                finish();

            }else {
                progressDialog.dismiss();
                loginButton.setEnabled(true);
                Toast.makeText(this, "Invalid Login Credentials", Toast.LENGTH_LONG).show();
            }

//            Log.i("Food Court" , foodcourtSpinner.getSelectedItem().toString() );
//            Log.i("User Name" , usernameSpinner.getSelectedItem().toString() );
//            Log.i("Passcode" , passcode);
//            Log.i("Password" , passEditText.getText().toString() );

        }
    }


}
