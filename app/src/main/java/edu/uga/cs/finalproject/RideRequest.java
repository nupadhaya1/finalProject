package edu.uga.cs.finalproject;

import java.util.HashMap;
import java.util.Map;

/**
 * Data model representing a ride request in the application.
 * 
 * Fields include the request date, origin, destination, number of passengers,
 * current status, and confirmation flags for driver and rider.
 * This class provides constructors for default instantiation by Firebase
 * and for creating new ride requests programmatically.
 */
public class RideRequest {

    /**
     * Date of the ride request in string format (e.g., YYYY-MM-DD).
     */
    public String date;

    /**
     * Origin location of the requested ride.
     */
    public String from;

    /**
     * Destination location of the requested ride.
     */
    public String to;

    /**
     * Number of passengers for the ride, stored as a string.
     */
    public String passengers;

    /**
     * Current status of the ride (e.g., "unaccepted", "accepted", "completed").
     */
    public String status;

    /**
     * Confirmation flags for both driver and rider.
     * <ul>
     * <li>"driver": true if the driver has confirmed</li>
     * <li>"rider": true if the rider has confirmed</li>
     * </ul>
     */
    public Map<String, Boolean> confirmation;

    /**
     * Default constructor required for Firebase
     * DataSnapshot.getValue(RideRequest.class).
     */
    public RideRequest() {
        // Default constructor required for DataSnapshot.getValue
    }

    /**
     * Constructs a new ride request with the given parameters.
     * Sets the default status to "unaccepted" and initializes confirmation flags to
     * false.
     *
     * @param date       The ride date (format: YYYY-MM-DD)
     * @param from       The origin location
     * @param to         The destination location
     * @param passengers Number of passengers as a string
     */
    public RideRequest(String date, String from, String to, String passengers) {
        this.date = date;
        this.from = from;
        this.to = to;
        this.passengers = passengers;
        this.status = "unaccepted";
        this.confirmation = new HashMap<>();
        this.confirmation.put("driver", false);
        this.confirmation.put("rider", false);
    }

    /**
     * Constructs a ride request with full specification, including status and
     * confirmation map.
     *
     * @param date         The ride date
     * @param from         The origin location
     * @param to           The destination location
     * @param passengers   Number of passengers as a string
     * @param status       Current status of the ride
     * @param confirmation Map of confirmation flags for driver and rider
     */
    public RideRequest(String date, String from, String to, String passengers, String status,
            Map<String, Boolean> confirmation) {
        this.date = date;
        this.from = from;
        this.to = to;
        this.passengers = passengers;
        this.status = status;
        this.confirmation = confirmation;
    }
}
