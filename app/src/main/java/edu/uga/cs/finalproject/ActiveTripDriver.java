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

public class ActiveTripDriver extends Fragment {

    private String rideId; // you need to pass this when opening this fragment
    private DatabaseReference rideRef;

    public ActiveTripDriver() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_trip_driver, container, false);

        // Get rideId from arguments
        if (getArguments() != null) {
            rideId = getArguments().getString("rideId");
        }

        // initialize firebase reference
        rideRef = FirebaseDatabase.getInstance().getReference("rideRequests").child(rideId);

        // create end drive button and listener
        Button endDriveButton = view.findViewById(R.id.endRideButton);
        endDriveButton.setOnClickListener(v -> {
            confirmEndOfTrip("driver");
        });

        // return to view
        return view;

    } // onCreateView

    private void confirmEndOfTrip(String role) {

        // if rideRef  is null return
        if (rideRef == null)
            return;

        // Mark the user's confirmation (either rider or driver) as true in the ride's confirmation section
        rideRef.child("confirmation").child(role).setValue(true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Waiting for rider to confirm ride end...", Toast.LENGTH_SHORT).show();

                    // Disable the button after driver confirms
                    View view = getView();
                    if (view != null) {
                        Button endDriveButton = view.findViewById(R.id.endRideButton);
                        endDriveButton.setEnabled(false);
                        endDriveButton.setText("Waiting for rider...");
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
                        e -> Toast.makeText(getContext(), "Failed to confirm drive end", Toast.LENGTH_SHORT).show());
    } // confirmEndOfTrip

} // ActiveTripDriver
