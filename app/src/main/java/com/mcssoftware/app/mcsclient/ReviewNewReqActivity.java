package com.mcssoftware.app.mcsclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;

public class ReviewNewReqActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    DatabaseReference RequestedTrips;

    TextView ori, des, time, date, fare;
    Button confirm, back;
    String clientName;
     FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_new_req);

        FirebaseApp.initializeApp(getApplicationContext());
        user = FirebaseAuth.getInstance().getCurrentUser();



       //RequestedTrips = FirebaseDatabase.getInstance().getReference(getString(R.string.reqTrip));
        RequestedTrips = FirebaseDatabase.getInstance().getReference(getString(R.string.testReqTrip));

        sharedPreferences = getSharedPreferences(getString(R.string.tripInfo),MODE_PRIVATE);

        ori = findViewById(R.id.ori);
        des = findViewById(R.id.des);
        time = findViewById(R.id.timerev);
        date = findViewById(R.id.dateRev);
        fare = findViewById(R.id.fareRev);

        confirm = findViewById(R.id.confirmSubmit);
        back = findViewById(R.id.backsubmit);




        ori.setText( sharedPreferences.getString(getString(R.string.ori),"date") );
        des.setText(sharedPreferences.getString(getString(R.string.des),"date"));
        time.setText(sharedPreferences.getString(getString(R.string.time),"time"));
        date.setText(sharedPreferences.getString(getString(R.string.date),"date"));
        fare.setText("K "+(String.valueOf(sharedPreferences.getLong(getString(R.string.fare),56))) );

        final LatLng oriLatLng = new LatLng(sharedPreferences.getFloat(getString(R.string.oriLat), 2),
                sharedPreferences.getFloat(getString(R.string.oriLong), 2));

        final LatLng desLatLng = new LatLng(sharedPreferences.getFloat(getString(R.string.desLat), 2),
                sharedPreferences.getFloat(getString(R.string.desLong), 2));






        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isInternetAvailable() || isNetworkConnected()) {
                    TripInfo tripInfo = new TripInfo(sharedPreferences.getString(getString(R.string.ori), "date"),
                            sharedPreferences.getString(getString(R.string.des), "date"),
                            (String.valueOf(sharedPreferences.getLong(getString(R.string.fare), 56))),
                            sharedPreferences.getString(getString(R.string.time), "time"),
                            sharedPreferences.getString(getString(R.string.date), "date"),
                            oriLatLng,
                            desLatLng,
                            sharedPreferences.getString("userName", "userName"),
                            sharedPreferences.getString("userId", "userId"),
                            sharedPreferences.getString("phone", "phone")


                    );

                    String uploadId = RequestedTrips.push().getKey();

                    SharedPreferences sp_tripInfo = getSharedPreferences(getString(R.string.tripInfo), MODE_PRIVATE);

                    sp_tripInfo.edit().putString("tripIDs", uploadId).apply();


                    RequestedTrips.child(uploadId).setValue(tripInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ReviewNewReqActivity.this, "Fail To Register Trips", Toast.LENGTH_SHORT).show();
                        }
                    });

                    startActivity(new Intent(ReviewNewReqActivity.this, MainActivity.class));

                }
                else{
                    Toast.makeText(ReviewNewReqActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReviewNewReqActivity.this, PickDateActivity.class));
            }
        });



        Log.d("INFO", "onCreate: "+sharedPreferences.getLong(getString(R.string.fare),56)
                +sharedPreferences.getString(getString(R.string.time),"time")
                +sharedPreferences.getString(getString(R.string.date),"date")
                +sharedPreferences.getString(getString(R.string.des),"date")
               );
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        SharedPreferences sp_ = getSharedPreferences("FROM_MAIN", MODE_PRIVATE);


        boolean isFromMain = sp_.getBoolean("isFromMain", false);

        if (isFromMain) {
            startActivity(new Intent(ReviewNewReqActivity.this, MainActivity.class));
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
}
