package edu.uga.cs.finalproject;

public class RideRequest {
    public String date;
    public String from;
    public String to;
    public String passengers;
    public String status; // NEW: status field

    public RideRequest() {
        // Default constructor required for calls to DataSnapshot.getValue(RideRequest.class)
    }

    public RideRequest(String date, String from, String to, String passengers) {
        this.date = date;
        this.from = from;
        this.to = to;
        this.passengers = passengers;
        this.status = "unaccepted"; // default status when created
    }

    public RideRequest(String date, String from, String to, String passengers, String status) {
        this.date = date;
        this.from = from;
        this.to = to;
        this.passengers = passengers;
        this.status = status;
    }
}
