package edu.uga.cs.finalproject;

import java.util.HashMap;
import java.util.Map;

public class RideRequest {
    public String date;
    public String from;
    public String to;
    public String passengers;
    public String status; // ride status (e.g., unaccepted, accepted, completed)
    public Map<String, Boolean> confirmation; // NEW: confirmation field

    public RideRequest() {
        // Default constructor required for DataSnapshot.getValue(RideRequest.class)
    }

    // Constructor for new ride requests
    public RideRequest(String date, String from, String to, String passengers) {
        this.date = date;
        this.from = from;
        this.to = to;
        this.passengers = passengers;
        this.status = "unaccepted"; // default when first created
        this.confirmation = new HashMap<>();
        this.confirmation.put("driver", false);
        this.confirmation.put("rider", false);
    }

    // Optional: Constructor that lets you specify everything
    public RideRequest(String date, String from, String to, String passengers, String status, Map<String, Boolean> confirmation) {
        this.date = date;
        this.from = from;
        this.to = to;
        this.passengers = passengers;
        this.status = status;
        this.confirmation = confirmation;
    }
}
