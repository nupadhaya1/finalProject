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

public class UserRequestsFragment extends Fragment {

    private DatabaseReference rideRequestsRef;

    public UserRequestsFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // create View
        View view = inflater.inflate(R.layout.fragment_user_requests, container, false);

        // create and initialize linearlayout
        LinearLayout listLayout = view.findViewById(R.id.rideRequestListLayout);

        // initialize database reference
        rideRequestsRef = FirebaseDatabase.getInstance().getReference("rideRequests");

        // add listener to the database reference
        rideRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listLayout.removeAllViews();

                // display
                for (DataSnapshot rideSnap : snapshot.getChildren()) {
                    RideRequest request = rideSnap.getValue(RideRequest.class);
                    String rideId = rideSnap.getKey();

                    // check requests
                    if (request != null) {
                        LinearLayout itemLayout = new LinearLayout(getContext());
                        itemLayout.setOrientation(LinearLayout.VERTICAL);
                        itemLayout.setPadding(20, 20, 20, 20);

                        TextView infoText = new TextView(getContext());
                        infoText.setText(
                                "ID: " + rideId + "\n" +
                                        "Date: " + request.date + "\n" +
                                        "From: " + request.from + "\n" +
                                        "To: " + request.to + "\n" +
                                        "Passengers: " + request.passengers + "\n" +
                                        "Status: " + request.status
                        );

                        // create cancel button and functionality
                        Button cancelButton = new Button(getContext());
                        cancelButton.setText("Cancel Ride");
                        cancelButton.setOnClickListener(v -> {
                            rideRequestsRef.child(rideId).removeValue()
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(getContext(), "Ride canceled", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(getContext(), "Failed to cancel ride", Toast.LENGTH_SHORT).show());

                            itemLayout.setVisibility(View.GONE); // hide after cancel
                        });

                        itemLayout.addView(infoText);
                        itemLayout.addView(cancelButton);

                        listLayout.addView(itemLayout);

                        // Optional divider
                        View divider = new View(getContext());
                        divider.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 2));
                        divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                        listLayout.addView(divider);
                    } // if statement
                } // for loop
            } // onDataChange

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load ride requests", Toast.LENGTH_SHORT).show();
            } // onCancelled
        });

        // return view
        return view;
    } // onCreateView
} //UserRequestsFragment
