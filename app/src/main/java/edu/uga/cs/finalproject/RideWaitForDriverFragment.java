package edu.uga.cs.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Fragment that displays ride request details and waits for a driver to accept.
 * Shows live status updates from Firebase and allows the user to cancel or
 * update the request.
 */
public class RideWaitForDriverFragment extends Fragment {

    /** Bundle key for the ride date. */
    private static final String ARG_DATE = "date";
    /** Bundle key for the origin location. */
    private static final String ARG_FROM = "from";
    /** Bundle key for the destination location. */
    private static final String ARG_TO = "to";
    /** Bundle key for the number of passengers. */
    private static final String ARG_PASSENGERS = "passengers";
    /** Bundle key for the ride request ID. */
    private static final String ARG_RIDE_ID = "rideId";
    /** Bundle key for the ride status. */
    private static final String ARG_STATUS = "status";

    /** Reference to the specific ride request node in Firebase. */
    private DatabaseReference rideRef;
    /** Listener for status changes in Firebase. */
    private ValueEventListener statusListener;

    /**
     * Required empty public constructor for fragment instantiation.
     */
    public RideWaitForDriverFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment with ride details.
     *
     * @param rideId      Unique identifier for the ride request
     * @param rideRequest RideRequest object containing initial values
     * @return A new instance of RideWaitForDriverFragment
     */
    public static RideWaitForDriverFragment newInstance(String rideId, RideRequest rideRequest) {
        RideWaitForDriverFragment fragment = new RideWaitForDriverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, rideRequest.date);
        args.putString(ARG_FROM, rideRequest.from);
        args.putString(ARG_TO, rideRequest.to);
        args.putString(ARG_PASSENGERS, rideRequest.passengers);
        args.putString(ARG_RIDE_ID, rideId);
        args.putString(ARG_STATUS, rideRequest.status);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Inflates the view, initializes UI elements, sets initial values,
     * and registers a Firebase listener to observe status changes.
     * Also sets up cancel and update button functionality.
     *
     * @param inflater           LayoutInflater to inflate fragment views
     * @param container          Parent view that the fragment's UI should attach to
     * @param savedInstanceState Bundle containing saved state, if any
     * @return The root view for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_wait_for_driver, container, false);

        // Initialize UI references
        TextView dateText = view.findViewById(R.id.dateText);
        TextView fromText = view.findViewById(R.id.fromText);
        TextView toText = view.findViewById(R.id.toText);
        TextView passengersText = view.findViewById(R.id.passengersText);
        TextView rideIdText = view.findViewById(R.id.rideIdText);
        TextView statusText = view.findViewById(R.id.statusText);
        Button cancelButton = view.findViewById(R.id.cancelRideRequest);
        Button updateButton = view.findViewById(R.id.updateRideRequest);
        Bundle args = getArguments();

        if (args != null) {
            // Extract arguments
            String date = args.getString(ARG_DATE);
            String from = args.getString(ARG_FROM);
            String to = args.getString(ARG_TO);
            String passengers = args.getString(ARG_PASSENGERS);
            String rideId = args.getString(ARG_RIDE_ID);

            // Display initial ride data
            dateText.setText("Date: " + date);
            fromText.setText("From: " + from);
            toText.setText("To: " + to);
            passengersText.setText("Passengers: " + passengers);
            rideIdText.setText("Request ID: " + rideId);

            // Reference this ride in Firebase
            rideRef = FirebaseDatabase.getInstance()
                    .getReference("rideRequests").child(rideId);

            // Indicate we're in the waiting fragment
            MainScreen.isInWaitingFragment = true;
            MainScreen.currentWaitingRef = rideRef;

            // Set up status listener
            statusListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    RideRequest updatedRide = snapshot.getValue(RideRequest.class);
                    if (updatedRide != null) {
                        statusText.setText("Status: " + updatedRide.status);
                        if ("accepted".equalsIgnoreCase(updatedRide.status)) {
                            // Navigate to ActiveTripRider when accepted
                            Fragment activeTrip = new ActiveTripRider();
                            Bundle bundle = new Bundle();
                            bundle.putString("rideId", rideId);
                            activeTrip.setArguments(bundle);
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentContainerView, activeTrip)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    } else {
                        statusText.setText("Status: canceled or not found");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(getContext(), "Error loading ride status", Toast.LENGTH_SHORT).show();
                }
            };
            rideRef.addValueEventListener(statusListener);

            // Cancel button listener
            cancelButton.setOnClickListener(v -> {
                rideRef.removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Ride request canceled", Toast.LENGTH_SHORT).show();
                            // Return to RideFragment after cancel
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentContainerView, new RideFragment())
                                    .commit();
                        })
                        .addOnFailureListener(e -> Toast
                                .makeText(getContext(), "Failed to cancel request", Toast.LENGTH_SHORT).show());
            });

            // Update button listener
            updateButton.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("requestId", args.getString(ARG_RIDE_ID));
                ActualUpdateFragment fragment = new ActualUpdateFragment();
                fragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, fragment)
                        .addToBackStack(null)
                        .commit();
            });
        }
        return view;
    }

    /**
     * Cleans up the Firebase listener and resets flags when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        MainScreen.isInWaitingFragment = false;
        MainScreen.currentWaitingRef = null;
        super.onDestroyView();
        if (rideRef != null && statusListener != null) {
            rideRef.removeEventListener(statusListener);
        }
    }
}