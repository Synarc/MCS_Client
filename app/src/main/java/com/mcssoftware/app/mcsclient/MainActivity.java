package com.mcssoftware.app.mcsclient;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mcssoftware.app.mcsclient.MAP_CLASSES.SignInActivity;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 2020;

    private boolean existClient = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        final TextView signedInAs = findViewById(R.id.signedInAs);

        final Button newTrip = findViewById(R.id.newTrip);
        final Button reqTrip = findViewById(R.id.requestedTrip);
        final Button pendTrip = findViewById(R.id.pendingTrips);
        final Button comTrip = findViewById(R.id.completedTrips);


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.RegClient)+"/"+user.getUid()+"/fullName");



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String userName = dataSnapshot.getValue().toString();

                    signedInAs.setText("Signed In As "+ userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        newTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "New", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, NewActivity.class));
            }
        });
        reqTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Requested", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, RequestedTripActivity.class));

            }
        });
        pendTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Pending", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, PendingMapsActivity.class));

            }
        });
        comTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Completed", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, CompletedTripActivity.class));

            }
        });

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


    }

}
