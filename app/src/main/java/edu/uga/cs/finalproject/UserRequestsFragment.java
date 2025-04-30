package edu.uga.cs.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Fragment that retrieves and displays a list of ride requests from Firebase
 * Realtime Database.
 * Allows users to cancel individual ride requests.
 */
public class UserRequestsFragment extends Fragment {

    /** Reference to the "rideRequests" node in Firebase Realtime Database. */
    private DatabaseReference rideRequestsRef;

    /**
     * Required empty public constructor for fragment instantiation.
     */
    public UserRequestsFragment() {
        // Required empty constructor
    }

    /**
     * Called to instantiate the fragment's view hierarchy.
     * Inflates the layout, initializes the Firebase reference, and populates the
     * list of ride requests.
     *
     * @param inflater           LayoutInflater to inflate views
     * @param container          Parent view that the fragment's UI should attach to
     * @param savedInstanceState Bundle for restoring state, if any
     * @return The root view of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_user_requests, container, false);

        // Find and initialize the LinearLayout container for list items
        LinearLayout listLayout = view.findViewById(R.id.rideRequestListLayout);

        // Initialize Firebase database reference
        rideRequestsRef = FirebaseDatabase.getInstance().getReference("rideRequests");

        // Read data once from the database
        rideRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            /**
             * Called when data is successfully read from the database.
             * Iterates through ride request entries and dynamically creates list items.
             *
             * @param snapshot DataSnapshot containing all child nodes under "rideRequests"
             */
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Clear any existing views
                listLayout.removeAllViews();

                // Loop through each ride request
                for (DataSnapshot rideSnap : snapshot.getChildren()) {
                    RideRequest request = rideSnap.getValue(RideRequest.class);
                    String rideId = rideSnap.getKey();

                    if (request != null) {
                        // Create container for a single request item
                        LinearLayout itemLayout = new LinearLayout(getContext());
                        itemLayout.setOrientation(LinearLayout.VERTICAL);
                        itemLayout.setPadding(20, 20, 20, 20);

                        // Display request details
                        TextView infoText = new TextView(getContext());
                        infoText.setText(
                                "ID: " + rideId + "\n" +
                                        "Date: " + request.date + "\n" +
                                        "From: " + request.from + "\n" +
                                        "To: " + request.to + "\n" +
                                        "Passengers: " + Math.abs(Integer.parseInt(request.passengers)) + "\n" +
                                        "Status: " + request.status);

                        // Create and configure the Cancel button
                        Button cancelButton = new Button(getContext());
                        cancelButton.setText("Cancel Ride");
                        cancelButton.setOnClickListener(v -> {
                            // Remove this ride request from database
                            rideRequestsRef.child(rideId).removeValue()
                                    .addOnSuccessListener(aVoid -> Toast
                                            .makeText(getContext(), "Ride canceled", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast
                                            .makeText(getContext(), "Failed to cancel ride", Toast.LENGTH_SHORT)
                                            .show());

                            // Hide this list item on success
                            itemLayout.setVisibility(View.GONE);
                        });

                        // Add views to item layout
                        itemLayout.addView(infoText);
                        itemLayout.addView(cancelButton);

                        // Add item and a divider to the parent layout
                        listLayout.addView(itemLayout);
                        View divider = new View(getContext());
                        divider.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 2));
                        divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                        listLayout.addView(divider);
                    }
                }
            }

            /**
             * Called if the database read is cancelled or fails.
             * Displays an error toast to the user.
             *
             * @param error DatabaseError containing details of the failure
             */
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load ride requests", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
