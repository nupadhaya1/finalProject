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

public class UpdateRideRequest extends Fragment {

    public UpdateRideRequest() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_ride_request, container, false); // reuse the layout
        LinearLayout requestListLayout = view.findViewById(R.id.updateRequestListLayout);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("rideRequests");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                requestListLayout.removeAllViews(); // Clear previous views

                for (DataSnapshot rideSnap : snapshot.getChildren()) {
                    RideRequest ride = rideSnap.getValue(RideRequest.class);
                    String requestId = rideSnap.getKey();

                    if (ride != null && ride.status != null && ride.status.equalsIgnoreCase("unaccepted")) {
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

                        rideItemLayout.addView(rideDetails);

                        // Set onClick to go to ActualUpdateOfferFragment
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

                        requestListLayout.addView(rideItemLayout);

                        // Optional: Divider
                        View divider = new View(getContext());
                        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 1);
                        dividerParams.setMargins(0, 16, 0, 16);
                        divider.setLayoutParams(dividerParams);
                        divider.setBackgroundColor(getResources().getColor(android.R.color.white));
                        requestListLayout.addView(divider);
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
