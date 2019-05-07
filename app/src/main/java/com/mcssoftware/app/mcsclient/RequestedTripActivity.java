package com.mcssoftware.app.mcsclient;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestedTripActivity extends AppCompatActivity {


    FirebaseAuth auth;
    DatabaseReference mDb;

    List <ReqTripList> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_trip);


        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        mList = new ArrayList<>();

        mDb = FirebaseDatabase.getInstance().getReference(getString(R.string.reqTrip)+"/"+user.getUid()+"/Trips");

        mDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("Trips", "onCreate: " +  dataSnapshot);

                int i =0;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){

                mList.add(new ReqTripList(
                        postSnapshot.child("Date").getValue().toString(),
                        postSnapshot.child("Time").getValue().toString(),
                        postSnapshot.child("Fare").getValue().toString(),
                        postSnapshot.child("Origin").getValue().toString(),
                        postSnapshot.child("Destination").getValue().toString(),
                        postSnapshot.getKey()


                ));

                    Log.d("Trips", "onCreate: " +  mList.get(i));


                }


                /*
                *
                * TODO the list mList is to be put in a recyclerView
                *
                * */
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
}
