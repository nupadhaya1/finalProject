/**
 * Model class representing a driver's ride offer.
 *
 * Contains the date, origin, destination, passenger count, and status of the offer.
 */
package edu.uga.cs.finalproject;

public class DriverOffer {

    /**
     * Date of the ride offer in MM/dd/yyyy format.
     */
    public String date;

    /**
     * Origin location of the ride.
     */
    public String from;

    /**
     * Destination location of the ride.
     */
    public String to;

    /**
     * Number of passengers as a string (negative for offers by driver).
     */
    public String passengers;

    /**
     * Status of the offer, e.g., "unaccepted", "accepted", or "canceled".
     */
    public String status;

    /**
     * Default constructor required for Firebase deserialization.
     */
    public DriverOffer() {
    }

    /**
     * Constructs a new DriverOffer with default status "unaccepted".
     *
     * @param date       Date of the ride offer.
     * @param from       Origin location.
     * @param to         Destination location.
     * @param passengers Number of passengers.
     */
    public DriverOffer(String date, String from, String to, String passengers) {
        this.date = date;
        this.from = from;
        this.to = to;
        this.passengers = passengers;
        this.status = "unaccepted";
    }

    /**
     * Constructs a new DriverOffer with a specified status.
     *
     * @param date       Date of the ride offer.
     * @param from       Origin location.
     * @param to         Destination location.
     * @param passengers Number of passengers.
     * @param status     Initial status of the offer.
     */
    public DriverOffer(String date, String from, String to, String passengers, String status) {
        this.date = date;
        this.from = from;
        this.to = to;
        this.passengers = passengers;
        this.status = status;
    }
}