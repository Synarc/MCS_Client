package com.mcssoftware.app.mcsclient;

import com.google.android.gms.maps.model.LatLng;

public class TripInfo {

    String Origin, Destination, Fare, Time, Date;

    LatLng OriginLatLng, DestinLatLng;



    public TripInfo( String origin, String destination, String fare, String time, String date, LatLng originLatLng, LatLng destinLatLng) {



        Origin = origin;
        Destination = destination;
        Fare = fare;
        Time = time;
        Date = date;
        OriginLatLng = originLatLng;
        DestinLatLng = destinLatLng;

    }


}
