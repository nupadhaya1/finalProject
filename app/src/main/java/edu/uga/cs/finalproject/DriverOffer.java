package edu.uga.cs.finalproject;

public class DriverOffer {
    public String date;
    public String from;
    public String to;
    public String passengers;
    public String status;

    public DriverOffer() {
        // Default constructor required for calls to DataSnapshot.getValue(RideRequest.class)

    }

    // Constructor for new driver offers with default status "unaccepted"
    public DriverOffer(String date, String from, String to, String passengers) {
        this.date = date;
        this.from = from;
        this.to = to;
        this.passengers = passengers;
        this.status = "unaccepted";
    } // DriverOffer

    public DriverOffer(String date, String from, String to, String passengers, String status) {
        this.date = date;
        this.from = from;
        this.to = to;
        this.passengers = passengers;
        this.status = status;
    } // DriverOffer
} // DriverOffer
