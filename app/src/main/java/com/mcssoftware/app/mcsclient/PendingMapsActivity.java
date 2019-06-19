package com.mcssoftware.app.mcsclient;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PendingMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST = 232 ;
    private GoogleMap mMap;
    SharedPreferences sharedPreferences;
    String carToRead = "NA";
    boolean man = false;
    DatabaseReference databaseReference, dbref1;
    ValueEventListener val;
    double oriLat, oriLng;
    SharedPreferences sp_tripInfo;
    String tripIDs;
    String carName, DriverName;
    TextView carNameDettails;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sharedPreferences = getSharedPreferences("CarPass", MODE_PRIVATE);

        carNameDettails = findViewById(R.id.carDetail);



        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //finish();
        }

        //Check whether this app has access to the location permission//


        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

//If the location permission has been granted, then start the TrackerService//

        if (permission == PackageManager.PERMISSION_GRANTED) {
            //startTrackerService();
        } else {

//If the app doesn’t currently have access to the user’s location, then request access//

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }



    }





    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {

//If the permission has been granted...// 3400139

        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            //...then start the GPS tracking service//

        } else {

//If the user denies the permission request, then display a toast with some more information//

            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Sydney and move the camera
        final LatLng sydney = new LatLng(-9.4438, 147.1803);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-9.4438, 147.1803), 15));


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

       // Log.d("LAT Origin", "onMapReady: "+ oriLat);


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReferencePick= FirebaseDatabase.getInstance().getReference("PickUpRead/"+user.getUid());

        dbref1 = FirebaseDatabase.getInstance().getReference("TestPendingTrips");

        dbref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot post: dataSnapshot.getChildren()){


                    if (post.child("showOriInClient").getValue()!= null &&
                            (boolean)post.child("showOriInClient").getValue() == true && post.child("userID").getValue().equals(user.getUid())){

                        Log.d("Pending trips", "onDataChange: "+ post.getKey());

                        carName = post.child("car").getValue().toString();
                        oriLat= (double) post.child("oriLatLng").child("latitude").getValue();
                        oriLng= (double) post.child("oriLatLng").child("longitude").getValue();

                        carNameDettails.setText("Vehicle Picking You Up: \n "+carName);

                        break;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReferencePick.child("Car").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                carToRead = dataSnapshot.getValue().toString();

                if (dataSnapshot.getValue().toString().equals("NA")){

                    databaseReference.child(dataSnapshot.getValue().toString()).addValueEventListener(val);

                    //databaseReference.child(dataSnapshot.getValue().toString()).addValueEventListener(val);
                    startActivity(new Intent(PendingMapsActivity.this, MainActivity.class));

                    man = false;
                }
                else {
                    Log.d("new Car", "onDataChange: " + dataSnapshot.getValue().toString());
                    man = true;
                  //  databaseReference.child(carToRead).removeEventListener(val)
                    databaseReference.child(dataSnapshot.getValue().toString()).addValueEventListener(val);

                }
//                if (man){
//                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



      databaseReference = FirebaseDatabase.getInstance()
                .getReference("Car Location");

         val = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(new LatLng(oriLat, oriLng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


                Log.d("CAR", "onDataChange: car to read on datachange "+ dataSnapshot.getKey());

                if (!carToRead.equals("NA")) {



                    double latCar = (Double) (dataSnapshot.child("latitude").getValue());
                    double lonCar = (Double) (dataSnapshot.child("longitude").getValue());

                    LatLng car = new LatLng(latCar, lonCar);


                    mMap.addMarker(new MarkerOptions()
                            .position(car)
                            .title(dataSnapshot.getKey()));

                    Log.d("Car Read", "onDataChange: "+ carToRead);

                } else {
                    Toast.makeText(PendingMapsActivity.this, "No Car: " + dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

                    databaseReference.child(dataSnapshot.getKey()).removeEventListener(this);
                    carToRead = "NA";


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

    }
}
