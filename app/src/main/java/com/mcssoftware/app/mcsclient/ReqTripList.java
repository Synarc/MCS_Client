package com.mcssoftware.app.mcsclient;

public class ReqTripList {

    String date, time, fare, pick, drop, tripId;



    public ReqTripList(String date, String time, String fare, String pick, String drop, String tripId) {
        this.date = date;
        this.time = time;
        this.fare = fare;
        this.pick = pick;
        this.drop = drop;
        this.tripId = tripId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public String getPick() {
        return pick;
    }

    public void setPick(String pick) {
        this.pick = pick;
    }

    public String getDrop() {
        return drop;
    }

    public void setDrop(String drop) {
        this.drop = drop;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }
}
