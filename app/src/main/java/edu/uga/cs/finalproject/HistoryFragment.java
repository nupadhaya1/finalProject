/**
 * Fragment to display the user's ride history and calculate total points.
 *
 * This fragment retrieves all ride requests from Firebase Realtime Database,
 * filters for rides confirmed by both driver and rider, and displays each
 * completed ride's details. It also sums passengers as points and shows the total.
 */
package edu.uga.cs.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A {@link Fragment} that shows ride history items with completed status.
 */
public class HistoryFragment extends Fragment {

    /**
     * Reference to the rideRequests node in Firebase Database.
     */
    private DatabaseReference rideRequestsRef;

    /**
     * Counter for total points accumulated.
     */
    private int totalPoints = 0;

    /**
     * Required empty public constructor.
     */
    public HistoryFragment() {
    }

    /**
     * Inflates the layout, queries rideRequests, and displays history items.
     *
     * @param inflater           LayoutInflater to inflate views.
     * @param container          Parent view for the fragment UI.
     * @param savedInstanceState If non-null, fragment is being re-created.
     * @return The root View of the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        LinearLayout listLayout = view.findViewById(R.id.historyListLayout);

        rideRequestsRef = FirebaseDatabase.getInstance()
                .getReference("rideRequests");

        rideRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listLayout.removeAllViews();
                totalPoints = 0;

                for (DataSnapshot rideSnap : snapshot.getChildren()) {
                    RideRequest request = rideSnap.getValue(RideRequest.class);
                    String rideId = rideSnap.getKey();

                    Boolean driverConfirmed = rideSnap.child("confirmation")
                            .child("driver").getValue(Boolean.class);
                    Boolean riderConfirmed = rideSnap.child("confirmation")
                            .child("rider").getValue(Boolean.class);

                    if (request != null && Boolean.TRUE.equals(driverConfirmed)
                            && Boolean.TRUE.equals(riderConfirmed)) {
                        displayRideItem(listLayout, rideId, request);
                    }
                }

                showTotalPoints(listLayout);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load history", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    /**
     * Creates and adds a view for a completed ride entry.
     * Updates the totalPoints counter.
     *
     * @param parent  The LinearLayout container.
     * @param rideId  Unique key of the ride.
     * @param request The RideRequest object.
     */
    private void displayRideItem(LinearLayout parent, String rideId, RideRequest request) {
        LinearLayout itemLayout = new LinearLayout(getContext());
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(20, 20, 20, 20);

        try {
            int passengers = Integer.parseInt(request.passengers);
            String rideType = passengers < 0 ? "Ride Taken" : "Ride Offered";

            TextView infoText = new TextView(getContext());
            infoText.setText(
                    rideType + "\n" +
                            "ID: " + rideId + "\n" +
                            "Date: " + request.date + "\n" +
                            "From: " + request.from + "\n" +
                            "To: " + request.to + "\n" +
                            "Passengers: " + Math.abs(passengers) + "\n" +
                            "Status: Completed");

            parent.addView(itemLayout);
            parent.addView(infoText);
            addDivider(parent);

            totalPoints += passengers;
        } catch (NumberFormatException e) {
            // skip invalid passenger entries
        }
    }

    /**
     * Adds a divider view to the parent layout.
     *
     * @param parent The LinearLayout to add the divider to.
     */
    private void addDivider(LinearLayout parent) {
        View divider = new View(getContext());
        divider.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 2));
        divider.setBackgroundColor(
                getResources().getColor(android.R.color.darker_gray));
        parent.addView(divider);
    }

    /**
     * Displays the total accumulated points at the end of the list.
     *
     * @param parent The LinearLayout container.
     */
    private void showTotalPoints(LinearLayout parent) {
        TextView pointsSummary = new TextView(getContext());
        pointsSummary.setText("Total Points: " + totalPoints);
        pointsSummary.setPadding(0, 40, 0, 0);
        pointsSummary.setTextSize(20);
        parent.addView(pointsSummary);
    }
}
