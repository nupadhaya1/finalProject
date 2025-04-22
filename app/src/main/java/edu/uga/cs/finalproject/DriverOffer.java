package edu.uga.cs.finalproject;

public class DriverOffer {
    public String date;
    public String from;
    public String to;
    public String passengers;
    public String status; // Default to "unaccepted"

    public DriverOffer() {

    }

    public DriverOffer(String date, String from, String to, String passengers) {
        this.date = date;
        this.from = from;
        this.to = to;
        this.passengers = passengers;
        this.status = "unaccepted";
    }

    public DriverOffer(String date, String from, String to, String passengers, String status) {
        this.date = date;
        this.from = from;
        this.to = to;
        this.passengers = passengers;
        this.status = status;
    }
}
