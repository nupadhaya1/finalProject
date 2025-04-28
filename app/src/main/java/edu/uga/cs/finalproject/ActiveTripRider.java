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

public class ActiveTripRider extends Fragment {

    private String rideId; // you need to pass this when opening this fragment
    private DatabaseReference rideRef;

    public ActiveTripRider() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_trip_rider, container, false);

        // Get rideId from arguments
        if (getArguments() != null) {
            rideId = getArguments().getString("rideId");
        }

        // initialize rideRef for database
        rideRef = FirebaseDatabase.getInstance().getReference("rideRequests").child(rideId);

        // button listener for trip confirmation
        Button endRideButton = view.findViewById(R.id.endRideButton);
        endRideButton.setOnClickListener(v -> {
            confirmEndOfTrip("rider");
        });

        // return the view
        return view;
    } // onCreateView

    private void confirmEndOfTrip(String role) {

        // if rideref is null return
        if (rideRef == null)
            return;
        // Mark the user's confirmation (either rider or driver) as true in the ride's confirmation section
        rideRef.child("confirmation").child(role).setValue(true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Waiting for driver to confirm ride end...", Toast.LENGTH_SHORT)
                            .show();

                    // Disable the button after rider confirms
                    View view = getView();
                    if (view != null) {
                        Button endRideButton = view.findViewById(R.id.endRideButton);
                        endRideButton.setEnabled(false);
                        endRideButton.setText("Waiting for driver...");
                    }

                    // Now listen passively for both confirmations
                    rideRef.child("confirmation").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Boolean driverConfirmed = snapshot.child("driver").getValue(Boolean.class);
                            Boolean riderConfirmed = snapshot.child("rider").getValue(Boolean.class);

                            if (Boolean.TRUE.equals(driverConfirmed) && Boolean.TRUE.equals(riderConfirmed)) {
                                // Both confirmed: complete the ride
                                rideRef.child("status").setValue("completed");

                                // Move to HistoryFragment
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragmentContainerView, new HistoryFragment())
                                        .addToBackStack(null)
                                        .commit();
                            } // if statement
                        } // onDataChange

                        @Override
                        public void onCancelled(DatabaseError error) {
                            Toast.makeText(getContext(), "Failed to check confirmation", Toast.LENGTH_SHORT).show();
                        } // onCancelled
                    });
                })
                .addOnFailureListener(
                        e -> Toast.makeText(getContext(), "Failed to confirm ride end", Toast.LENGTH_SHORT).show());
    } // confirmEndOFTrip

} // ActiveTripRider
