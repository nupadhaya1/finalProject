package edu.uga.cs.finalproject;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverAcceptRequestFragment extends Fragment {

    public DriverAcceptRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_accept_request, container, false);

        LinearLayout requestListLayout = view.findViewById(R.id.driverRequestListLayout);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("rideRequests");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                requestListLayout.removeAllViews(); // Clear previous views

                for (DataSnapshot rideSnap : snapshot.getChildren()) {
                    RideRequest ride = rideSnap.getValue(RideRequest.class);
                    String requestId = rideSnap.getKey();

                    if (ride != null && "unaccepted".equalsIgnoreCase(ride.status)) {
                        // Create container for each request
                        LinearLayout rideItemLayout = new LinearLayout(getContext());
                        rideItemLayout.setOrientation(LinearLayout.VERTICAL);
                        rideItemLayout.setPadding(16, 16, 16, 16);

                        // TextView for ride details
                        TextView rideDetails = new TextView(getContext());
                        rideDetails.setTextColor(getResources().getColor(android.R.color.white));
                        rideDetails.setText(
                                "Request ID: " + requestId +
                                        "\nDate: " + ride.date +
                                        "\nFrom: " + ride.from +
                                        "\nTo: " + ride.to +
                                        "\nPassengers: " + ride.passengers +
                                        "\nStatus: " + ride.status);

                        // Button to accept
                        Button acceptButton = new Button(getContext());
                        acceptButton.setText("Accept Ride");
                        acceptButton.setOnClickListener(v -> {
                            dbRef.child(requestId).child("status").setValue("accepted");
                            Toast.makeText(getContext(), "Ride accepted!", Toast.LENGTH_SHORT).show();
                        });

                        // Add views
                        rideItemLayout.addView(rideDetails);
                        rideItemLayout.addView(acceptButton);
                        requestListLayout.addView(rideItemLayout);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load ride requests", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

}
