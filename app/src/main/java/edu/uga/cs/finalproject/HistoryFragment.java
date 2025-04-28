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

public class HistoryFragment extends Fragment {

    private DatabaseReference rideRequestsRef;
    private int totalPoints = 0; // Points counter

    public HistoryFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        LinearLayout listLayout = view.findViewById(R.id.historyListLayout);

        rideRequestsRef = FirebaseDatabase.getInstance().getReference("rideRequests");

        rideRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listLayout.removeAllViews();
                totalPoints = 0; // Reset points

                for (DataSnapshot rideSnap : snapshot.getChildren()) {
                    RideRequest request = rideSnap.getValue(RideRequest.class);
                    String rideId = rideSnap.getKey();

                    // Check both confirmations
                    Boolean driverConfirmed = rideSnap.child("confirmation").child("driver").getValue(Boolean.class);
                    Boolean riderConfirmed = rideSnap.child("confirmation").child("rider").getValue(Boolean.class);

                    if (request != null && Boolean.TRUE.equals(driverConfirmed) && Boolean.TRUE.equals(riderConfirmed)) {
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
                                            "Passengers: " + Math.abs(passengers)+ "\n" +
                                            "Status: Completed"
                            );

                            itemLayout.addView(infoText);
                            listLayout.addView(itemLayout);

                            // Divider
                            View divider = new View(getContext());
                            divider.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, 2));
                            divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                            listLayout.addView(divider);

                            // Add to points
                            totalPoints += passengers;

                        } catch (NumberFormatException e) {
                            // Ignore if passengers is not a number
                        }
                    }
                }

                // Points Summary at the end
                TextView pointsSummary = new TextView(getContext());
                pointsSummary.setText("Total Points: " + totalPoints);
                pointsSummary.setPadding(0, 40, 0, 0);
                pointsSummary.setTextSize(20);
                listLayout.addView(pointsSummary);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load history", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
