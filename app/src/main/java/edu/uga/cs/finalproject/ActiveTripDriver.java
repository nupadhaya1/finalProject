package edu.uga.cs.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A Fragment displayed to the driver during an active trip.
 * 
 * Displays an "End Ride" button allowing the driver to confirm trip completion.
 * Updates the ride's confirmation flags in Firebase and waits for both
 * rider and driver confirmations. Once both have confirmed, marks the ride
 * as completed and navigates to {@link HistoryFragment}.
 */
public class ActiveTripDriver extends Fragment {

    /** The ID of the ride request; must be supplied via fragment arguments. */
    private String rideId;

    /** Reference to this ride's node in Firebase Realtime Database. */
    private DatabaseReference rideRef;

    /**
     * Required empty public constructor.
     */
    public ActiveTripDriver() {
        // Required empty public constructor
    }

    /**
     * Inflates the fragment layout, initializes Firebase references,
     * and sets up the "End Ride" button listener.
     *
     * @param inflater           The LayoutInflater object to inflate views.
     * @param container          The parent view that this fragment's UI should
     *                           attach to.
     * @param savedInstanceState If non-null, the fragment is being re-created from
     *                           a previous state.
     * @return The root View for this fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_active_trip_driver, container, false);

        // Retrieve rideId from arguments
        if (getArguments() != null) {
            rideId = getArguments().getString("rideId");
        }

        // Initialize Firebase reference for this ride
        rideRef = FirebaseDatabase.getInstance()
                .getReference("rideRequests")
                .child(rideId);

        // Set up "End Ride" button
        Button endDriveButton = view.findViewById(R.id.endRideButton);
        endDriveButton.setOnClickListener(v -> confirmEndOfTrip("driver"));

        return view;
    }

    /**
     * Records the end-of-trip confirmation for the given role ("driver" or
     * "rider"),
     * disables the button, and listens for the other party's confirmation. Once
     * both
     * have confirmed, marks the ride as completed and navigates to
     * {@link HistoryFragment}.
     *
     * @param role A string key representing the confirmer role ("driver" or
     *             "rider").
     */
    private void confirmEndOfTrip(String role) {
        if (rideRef == null) {
            return;
        }

        // Mark this user's confirmation in Firebase
        rideRef.child("confirmation")
                .child(role)
                .setValue(true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(),
                            "Waiting for rider to confirm ride end...",
                            Toast.LENGTH_SHORT).show();

                    // Disable the "End Ride" button
                    View root = getView();
                    if (root != null) {
                        Button endDriveButton = root.findViewById(R.id.endRideButton);
                        endDriveButton.setEnabled(false);
                        endDriveButton.setText("Waiting for rider...");
                    }

                    // Listen for both rider and driver confirmations
                    rideRef.child("confirmation")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    Boolean driverConfirmed = snapshot
                                            .child("driver")
                                            .getValue(Boolean.class);
                                    Boolean riderConfirmed = snapshot
                                            .child("rider")
                                            .getValue(Boolean.class);

                                    if (Boolean.TRUE.equals(driverConfirmed)
                                            && Boolean.TRUE.equals(riderConfirmed)) {
                                        // Both confirmed—complete the ride
                                        rideRef.child("status").setValue("completed");

                                        getParentFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.fragmentContainerView,
                                                        new HistoryFragment())
                                                .addToBackStack(null)
                                                .commit();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    Toast.makeText(getContext(),
                                            "Failed to check confirmation",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Failed to confirm drive end",
                        Toast.LENGTH_SHORT).show());
    }
}