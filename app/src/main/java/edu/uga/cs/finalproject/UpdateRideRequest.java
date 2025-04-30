package edu.uga.cs.finalproject;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Fragment that displays all unaccepted ride requests and allows the user
 * to select one for updating. Transitions to ActualUpdateFragment on selection.
 */
public class UpdateRideRequest extends Fragment {

    /**
     * Required empty public constructor for fragment instantiation.
     */
    public UpdateRideRequest() {
        // Required empty public constructor
    }

    /**
     * Called to create the view hierarchy for this fragment.
     * Inflates the request layout, reads ride requests from Firebase,
     * and populates only those with status "unaccepted".
     * Sets up click listeners to navigate to ActualUpdateFragment.
     *
     * @param inflater           LayoutInflater to inflate fragment views
     * @param container          Parent ViewGroup for the fragment
     * @param savedInstanceState Bundle for saved state, if any
     * @return The root view of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_update_ride_request, container, false);

        // Find and initialize the LinearLayout for request items
        LinearLayout requestListLayout = view.findViewById(R.id.updateRequestListLayout);

        // Reference the "rideRequests" node in Firebase
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("rideRequests");

        // Listen for data changes on rideRequests
        dbRef.addValueEventListener(new ValueEventListener() {
            /**
             * Called when data at the reference changes.
             * Iterates through each ride request and displays unaccepted ones.
             *
             * @param snapshot DataSnapshot of rideRequests node
             */
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Clear previous views before updating
                requestListLayout.removeAllViews();

                // Loop through each ride entry
                for (DataSnapshot rideSnap : snapshot.getChildren()) {
                    RideRequest ride = rideSnap.getValue(RideRequest.class);
                    String requestId = rideSnap.getKey();

                    if (ride != null && ride.status != null
                            && ride.status.equalsIgnoreCase("unaccepted")) {
                        // Create container layout for a single ride item
                        LinearLayout rideItemLayout = new LinearLayout(getContext());
                        rideItemLayout.setOrientation(LinearLayout.VERTICAL);
                        rideItemLayout.setPadding(16, 16, 16, 16);

                        // TextView showing ride details
                        TextView rideDetails = new TextView(getContext());
                        rideDetails.setTextColor(getResources().getColor(android.R.color.white));
                        rideDetails.setText(
                                "Request ID: " + requestId +
                                        "\nDate: " + ride.date +
                                        "\nFrom: " + ride.from +
                                        "\nTo: " + ride.to +
                                        "\nPassengers: " + Math.abs(Integer.parseInt(ride.passengers)) +
                                        "\nStatus: " + ride.status);

                        // Add details view to layout
                        rideItemLayout.addView(rideDetails);

                        // Navigate to ActualUpdateFragment when clicked
                        rideItemLayout.setOnClickListener(v -> {
                            Bundle bundle = new Bundle();
                            bundle.putString("requestId", requestId);

                            ActualUpdateFragment fragment = new ActualUpdateFragment();
                            fragment.setArguments(bundle);

                            getParentFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragmentContainerView, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        });

                        // Add ride item layout to parent
                        requestListLayout.addView(rideItemLayout);

                        // Divider between items
                        View divider = new View(getContext());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 1);
                        params.setMargins(0, 16, 0, 16);
                        divider.setLayoutParams(params);
                        divider.setBackgroundColor(getResources().getColor(android.R.color.white));
                        requestListLayout.addView(divider);
                    }
                }
            }

            /**
             * Called if the database read is cancelled or fails.
             * Shows an error toast.
             *
             * @param error DatabaseError detailing the failure
             */
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load ride requests", Toast.LENGTH_SHORT).show();
            }
        });

        // Return the inflated and populated view
        return view;
    }
}
