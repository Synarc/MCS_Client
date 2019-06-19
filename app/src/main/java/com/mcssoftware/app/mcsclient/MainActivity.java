package com.mcssoftware.app.mcsclient;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fangxu.allangleexpandablebutton.AllAngleExpandableButton;
import com.fangxu.allangleexpandablebutton.ButtonData;
import com.fangxu.allangleexpandablebutton.ButtonEventListener;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;
import com.mcssoftware.app.mcsclient.MAP_CLASSES.SignInActivity;
import com.mcssoftware.app.mcsclient.MAP_CLASSES.TrackingService;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 2020;
    private static final int PERMISSIONS_REQUEST =269 ;
    int i;

    private boolean existClient = false;
    String userName;
    SharedPreferences sharedPreferences;
    String carToRead = "NA";
    boolean man = false;
    DatabaseReference databaseReference1;
    ValueEventListener val;

    int index1;

    boolean carAssigned = false;
    CircleMenu circleMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
        }

        if (!connected){
            Toast.makeText(this, "Please Connect to the Interent", Toast.LENGTH_SHORT).show();
            finish();
        }



        if (isInternetAvailable() || isNetworkConnected()){
            Toast.makeText(this, "Connected Internet", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Please Connect to the Internet", Toast.LENGTH_SHORT).show();

        }


        sharedPreferences = getSharedPreferences(getString(R.string.tripInfo),MODE_PRIVATE);


        startTrackerService();


        final TextView signedInAs = findViewById(R.id.signedInAs);
//
//        final Button newTrip = findViewById(R.id.newTrip);
//        final Button reqTrip = findViewById(R.id.requestedTrip);
//        final Button pendTrip = findViewById(R.id.pendingTrips);
//        final Button comTrip = findViewById(R.id.completedTrips);


       // pendTrip.setEnabled(false);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.RegClient)+"/"+user.getUid()+"/fullName");



        DatabaseReference databaseReferencePick= FirebaseDatabase.getInstance().getReference("PickUpRead/"+user.getUid());
        databaseReferencePick.child("Car").setValue("NA");

        databaseReferencePick.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.child("Car").getValue().toString().equals("NA")){
//                    pendTrip.setEnabled(true);
                    carAssigned = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue()!= null) {
                    userName = dataSnapshot.getValue().toString();

                    signedInAs.setText("Signed In As " + userName);

                    sharedPreferences.edit().putString("userName", userName).apply();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        sharedPreferences.edit().putString("userId", user.getUid()).apply();

        sharedPreferences.edit().putString("phone", user.getPhoneNumber()).apply();

//        newTrip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (isInternetAvailable() || isNetworkConnected()){
//                    Toast.makeText(MainActivity.this, "New", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(MainActivity.this, NewActivity.class));
//                }else {
//                    Toast.makeText(MainActivity.this, "Please Connect to the Internet", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
//        reqTrip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (isInternetAvailable() || isNetworkConnected()){
//                    Toast.makeText(MainActivity.this, "Requested", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(MainActivity.this, RequestedTripActivity.class));
//                }
//                else
//                    {
//
//                        Toast.makeText(MainActivity.this, "Please Connect to the Internet", Toast.LENGTH_SHORT).show();
//
//                    }
//
//
//            }
//        });
//        pendTrip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                if (isInternetAvailable() || isNetworkConnected()){
//                    if (carAssigned) {
//                        Toast.makeText(MainActivity.this, "Pending", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(MainActivity.this, PendingMapsActivity.class));
//                    } else {
//                        Toast.makeText(MainActivity.this, "Vehicle Yet to be Assigned", Toast.LENGTH_SHORT).show();
//
//                    }
//                }
//                else {
//                    Toast.makeText(MainActivity.this, "Please Connect to the Internet", Toast.LENGTH_SHORT).show();
//
//                }
//
//
//            }
//        });
//        comTrip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (isInternetAvailable() || isNetworkConnected()){
//                    Toast.makeText(MainActivity.this, "Completed", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(MainActivity.this, CompletedTripActivity.class));
//
//                }
//                else {
//                    Toast.makeText(MainActivity.this, "Please Connect to the Internet", Toast.LENGTH_SHORT).show();
//
//                }
//
//            }
//        });

        TextView logout = findViewById(R.id.loggout);



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth auth = FirebaseAuth.getInstance();

                SharedPreferences sp = getSharedPreferences("login",MODE_PRIVATE);
                auth.signOut();
                sp.edit().putBoolean("logged",false).apply();
                FirebaseAuth.getInstance().signOut();


                startActivity(new Intent(MainActivity.this, SignInActivity.class));

//                 AuthUI.getInstance()
//                         .signOut(StartPageActivity.this)
//                         .addOnCompleteListener(new OnCompleteListener<Void>() {
//                             public void onComplete(@NonNull Task<Void> task) {
//                                 // ...
//
//                             }
//                         });

            }
        });


//        databaseReference1 = FirebaseDatabase.getInstance()
//                .getReference("Car Location");
//
//        databaseReference.child("NA").addValueEventListener(val);
//
//        val = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
////                mMap.clear();
//
//                Log.d("CAR", "onDataChange: car to read on datachange "+ dataSnapshot.getKey());
//
//                if (!carToRead.equals("NA")) {
//
////                    double latCar = (Double) (dataSnapshot.child("latitude").getValue());
////                    double lonCar = (Double) (dataSnapshot.child("longitude").getValue());
////
////                    LatLng car = new LatLng(latCar, lonCar);
////
////                    mMap.addMarker(new MarkerOptions()
////                            .position(car)
////                            .title(dataSnapshot.getKey()));
////
////                    Log.d("Car Read", "onDataChange: "+ carToRead);
//
//                } else {
//                    Toast.makeText(MainActivity.this, "No Car: " + dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
////                    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
////
////                    carToRead = "NA";
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };

       // cirmen();
        angleMenu(this);

    }

    private void startTrackerService() {


        startService(new Intent(this, TrackingService.class));


////Notify the user that tracking has been enabled//

        Toast.makeText(this, "Reading...", Toast.LENGTH_SHORT).show();

//Close MainActivity//

        // finish();
    }

    @Override
    public void onBackPressed() {
        i++;

        SharedPreferences sp_ = getSharedPreferences("FROM_MAIN", MODE_PRIVATE);

        sp_.edit().putBoolean("isFromMain", true).apply();

        if (i == 1) {
            Toast.makeText(MainActivity.this, "Press back once more to exit.",
                    Toast.LENGTH_SHORT).show();
        } else if (i > 1) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            super.onBackPressed();

            System.exit(0);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        i = 0;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    public  void cirmen(){
        circleMenu = (CircleMenu) findViewById(R.id.circle_menu);

        circleMenu.setMainMenu(Color.parseColor("#D4AF4F"), R.drawable.ic_directions_car_black_24dp, R.drawable.ic_cancel_black_24dp)
                .addSubMenu(Color.parseColor("#800694"),R.drawable.ic_add_black_24dp)
                .addSubMenu(Color.parseColor("#800694"), R.drawable.ic_arrow_forward_black_24dp)
                .addSubMenu(Color.parseColor("#800694"), R.drawable.ic_hourglass_empty_black_24dp)

                .addSubMenu(Color.parseColor("#800694"), R.drawable.ic_playlist_add_check_black_24dp)
//                .addSubMenu(Color.parseColor("#FF6A00"), R.mipmap.icon_gps)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {

                    @Override
                    public void onMenuSelected(int index) {
                        index1 = index;
                    }

                }).setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {

            @Override
            public void onMenuOpened() {}

            @Override
            public void onMenuClosed() {

            }

        });
    }

    public void angleMenu(Context context){
        AllAngleExpandableButton button = (AllAngleExpandableButton)findViewById(R.id.button_expandable);
        final List<ButtonData> buttonDatas = new ArrayList<>();

        int[] draw = {R.drawable.ic_directions_car_black_24dp, R.drawable.ic_add_black_24dp, R.drawable.ic_arrow_forward_black_24dp, R.drawable.ic_playlist_add_check_black_24dp, R.drawable.ic_hourglass_empty_black_24dp};


       // int[] drawable = {R.drawable.ic_notifications_black_24dp, R.drawable.ic_search_black_24dp, R.drawable.ic_menu_black_24dp, R.drawable.ic_home_black_24dp};
       // {"Trip Menu", "New Trips","Reques Trips", "Pending Trips", "Completed Trips"};//
       // buttonDatas.add( ButtonData.buildIconButton(this, draw[1], 5));

        for (int i = 0; i < draw.length; i++) {
            ButtonData buttonData = (ButtonData) ButtonData.buildIconButton(this,draw[i], 5);
            buttonDatas.add(buttonData);
        }
        button.setButtonDatas(buttonDatas);


        button.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(int index) {
                //do whatever you want,the param index is counted from startAngle to endAngle,
                //the value is from 1 to buttonCount - 1(buttonCount if aebIsSelectionMode=true)




                if (index == 1){

                    if (isInternetAvailable() || isNetworkConnected()){
                        Toast.makeText(MainActivity.this, "New", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, NewActivity.class));
                    }else {
                        Toast.makeText(MainActivity.this, "Please Connect to the Internet", Toast.LENGTH_SHORT).show();

                    }


                }
                if (index == 2){

                    if (isInternetAvailable() || isNetworkConnected()){
                        Toast.makeText(MainActivity.this, "Requested", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, RequestedTripActivity.class));
                    }
                    else
                    {

                        Toast.makeText(MainActivity.this, "Please Connect to the Internet", Toast.LENGTH_SHORT).show();

                    }


                }
                if (index == 4){

                    if (isInternetAvailable() || isNetworkConnected()){
                        if (carAssigned) {
                            Toast.makeText(MainActivity.this, "Pending", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, PendingMapsActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, "Vehicle Yet to be Assigned", Toast.LENGTH_SHORT).show();

                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Please Connect to the Internet", Toast.LENGTH_SHORT).show();

                    }
                }

                if (index == 3){

                    if (isInternetAvailable() || isNetworkConnected()){
                        Toast.makeText(MainActivity.this, "Completed", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, CompletedTripActivity.class));

                    }
                    else {
                        Toast.makeText(MainActivity.this, "Please Connect to the Internet", Toast.LENGTH_SHORT).show();

                    }
                }


            }

            @Override
            public void onExpand() {

            }

            @Override
            public void onCollapse() {

            }
        });
    }




}
