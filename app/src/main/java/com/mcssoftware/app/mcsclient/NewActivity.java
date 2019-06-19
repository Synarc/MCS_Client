package com.mcssoftware.app.mcsclient;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.compat.GeoDataClient;
import com.google.android.libraries.places.compat.PlaceDetectionClient;
import com.google.android.libraries.places.compat.Places;
import com.google.firebase.database.DatabaseReference;
import com.mcssoftware.app.mcsclient.MAP_CLASSES.DirectionFinder;
import com.mcssoftware.app.mcsclient.MAP_CLASSES.DirectionFinderListener;
import com.mcssoftware.app.mcsclient.MAP_CLASSES.Route;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener, GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveListener {

    private static final int PERMISSIONS_REQUEST = 258;
    private GoogleMap mMap;
    ArrayList<LatLng> listpoints;
    private LatLng Base;
    double latOrigin;
    double lonOrigin;
    double latDesti;
    double lonDesti;
    TextView desName;
    TextView oriName;
    Button confirmPlace;
    Button calc, back;

    SharedPreferences sp_tripInfo;

    private ProgressBar progressBar;



    MarkerOptions marker;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();

    private List<Polyline> polylinePaths = new ArrayList<>();
    LatLng port_Moresby;

    private ProgressDialog progressDialog;


    private LatLng testLatlng;
    private LatLng testLatlngDes;
    private TextView textView;
    private boolean isTripReady = false;
    ImageView imageMarker;
    ArrayList<Double> distancesToTotal;
    int roundedfare = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        initialise();

        onClickButtons();


    }

    private void onClickButtons() {

        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!desName.getText().toString().equals("Destination") && !oriName.getText().toString().equals("Origin")) {


                    startActivity(new Intent(NewActivity.this, FareActivity.class));
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewActivity.this, MainActivity.class));

            }
        });

        oriName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Snackbar snackbar = (Snackbar) Snackbar
                        .make(v, "Choose pick up location by moving map", Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        });

        desName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = (Snackbar) Snackbar
                        .make(v, "Choose drop off location by moving map", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        permissionLocation();
    }

    private void initialise() {

        sp_tripInfo = getSharedPreferences(getString(R.string.tripInfo), MODE_PRIVATE);


        confirmPlace = findViewById(R.id.confirmPLace);


        imageMarker = findViewById(R.id.centerMark);
        listpoints = new ArrayList<>();
        distancesToTotal = new ArrayList<>();

        oriName = findViewById(R.id.oriName);
        progressBar = findViewById(R.id.indeterminateBar);

        Base = new LatLng(-9.419426, 147.182256);
        desName = findViewById(R.id.desName);


        desName.setText("Destination");
        oriName.setText("Origin");


        calc = findViewById(R.id.calulateTrip);
        back = findViewById(R.id.backNew);

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
        port_Moresby = new LatLng(-9.4438, 147.1803);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //mMap.addMarker(new MarkerOptions().position(port_Moresby).title("Marker in Sydney"));


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        else {
            mMap.setMyLocationEnabled(true);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(port_Moresby));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(port_Moresby, 13));


            confirmPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    longPressMap(mMap.getCameraPosition().target);
                }
            });


            mMap.setOnCameraIdleListener(this);
            mMap.setOnCameraMoveListener(this);
        }


    }

    public void longPressMap (LatLng latLng){


        /*
        *
        * checks if there are two markers on the screen
        * */

        if (listpoints.size() == 0 || listpoints.size() == 2){

            listpoints.clear();
            mMap.clear();

            desName.setText("Destination");
            oriName.setText("Origin");
        }

        listpoints.add(latLng);
        String str_origin = listpoints.get(0).latitude+ ","+ listpoints.get(0).longitude;
        String str_Base = Base.latitude + "," + Base.longitude;

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);

        if (listpoints.size() == 1){
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(listpoints.get(0).latitude, listpoints.get(0).longitude, 1);

               // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(port_Moresby, 13));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(port_Moresby, 13));
                Log.d("ADDRESS", "longPressMap: " + addresses.get(0).getAddressLine(0));


                sp_tripInfo.edit().putString(getString(R.string.ori),addresses.get(0).getAddressLine(0)).apply();
                sp_tripInfo.edit().putFloat(getString(R.string.oriLat), (float) listpoints.get(0).latitude).apply();
                sp_tripInfo.edit().putFloat(getString(R.string.oriLong), (float) listpoints.get(0).longitude).apply();


                oriName.setText(addresses.get(0).getAddressLine(0));
            } catch (IOException e) {
                e.printStackTrace();
            }

           // sendRequest(str_Base,str_origin);

        }
        else{

           // selectTimeButton.setVisibility(View.VISIBLE);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }

        mMap.addMarker(markerOptions);

        if (listpoints.size() ==2){



            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(listpoints.get(1).latitude, listpoints.get(1).longitude, 1);

                Log.d("ADDRESS", "longPressMap: " + addresses.get(0).getAddressLine(0));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(port_Moresby, 13));

                desName.setText(addresses.get(0).getAddressLine(0));

                sp_tripInfo.edit().putString(getString(R.string.des),addresses.get(0).getAddressLine(0)).apply();
                sp_tripInfo.edit().putFloat(getString(R.string.desLat), (float) listpoints.get(1).latitude).apply();
                sp_tripInfo.edit().putFloat(getString(R.string.desLong), (float) listpoints.get(1).longitude).apply();

            } catch (IOException e) {
                e.printStackTrace();
            }
            latOrigin = listpoints.get(0).latitude;
            lonOrigin = listpoints.get(0).longitude;
            latDesti =listpoints.get(1).latitude;
            lonDesti = listpoints.get(1).longitude;

            String str_dest = listpoints.get(1).latitude + "," + listpoints.get(1).longitude;

            //sendRequest(str_origin,str_dest);
            sendRequest(str_origin, str_dest);

        }
    }

    private void sendRequest(String ori, String des) {
        String origin = ori;
        String destination = des;

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart() {

        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        progressBar.setIndeterminate(true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
       progressDialog.dismiss();


        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        double priceRide = 0.0;
        double roundTripDistance = 0;




        for (Route route : routes) {

            distancesToTotal.add((double) route.distance.value);

            //


            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.RED).
                    width(7);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            //polylinePaths.add(mMap.addPolyline(polylineOptions));
        }


        if (isTripReady){

            String str_Base = Base.latitude + "," + Base.longitude;
            listpoints.clear();
            isTripReady = false;

        }
//        if (distancesToTotal.size() == 2){
//            roundTripDistance = distancesToTotal.get(0)+ distancesToTotal.get(1)+ distancesToTotal.get(2);
//            priceRide = 2.0*roundTripDistance *3.83*20/100000.0;
//
//            Log.d("ROUNDTRIP", "onDirectionFinderSuccess: "+roundTripDistance);
//
//            roundedfare = roundup(priceRide);
//            ((TextView) findViewById(R.id.price)).setText("K "+Double.toString(roundedfare));
//
//            distancesToTotal.clear();
//
//        }

        roundTripDistance = distancesToTotal.get(0);//+ distancesToTotal.get(1)+ distancesToTotal.get(2);
        priceRide = 2.0*roundTripDistance *3.83*20*1.66/100000.0;


        Log.d("ROUNDTRIP", "onDirectionFinderSuccess: "+roundTripDistance);

        roundedfare = roundup(priceRide);
        sp_tripInfo.edit().putLong(getString(R.string.fare), (long) roundedfare).apply();

       // ((TextView) findViewById(R.id.price)).setText("K "+Double.toString(roundedfare));

        distancesToTotal.clear();

    }
    public static int roundup(double value){

        int rounded = 0;
        int divided;
        if (value % 5 != 0){
            divided = (int) (value/5);
            rounded = divided * 5 + 5;
        }else {
            rounded = (int) value;
        }

        return rounded;
    }

    @Override
    public void onCameraIdle() {

        //imageMarker.setVisibility(View.GONE);
        Log.d("CAMERA_POS", "onCameraIdle: "+ mMap.getCameraPosition().target);

        confirmPlace.setVisibility(View.VISIBLE);

    }

    @Override
    public void onCameraMove() {


        confirmPlace.setVisibility(View.GONE);
        imageMarker.setVisibility(View.VISIBLE);

    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.mid_lollipop_icon);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(5, 5, vectorDrawable.getIntrinsicWidth() + 25, vectorDrawable.getIntrinsicHeight() + 25);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    private void permissionLocation(){

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //finish();
        }

        //Check whether this app has access to the location permission//
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        //If the location permission has been granted, then start the TrackerService//

        if (permission == PackageManager.PERMISSION_GRANTED) {
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


}
