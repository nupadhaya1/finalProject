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

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A Fragment that retrieves all unaccepted ride requests from Firebase Realtime
 * Database,
 * displays them in a sorted list by date, and allows the driver to accept a
 * ride.
 */
public class DriverAcceptRequestFragment extends Fragment {

    /**
     * Default public constructor. Required for Fragment subclass.
     */
    public DriverAcceptRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the fragment layout, sets up a listener on the "rideRequests" node
     * in Firebase,
     * and populates a LinearLayout with ride requests sorted by date.
     * Each request entry shows details and an "Accept Ride" button which, when
     * clicked,
     * updates the request status to "accepted" and navigates to the
     * ActiveTripDriver fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to
     *                           inflate any views in the fragment
     * @param container          If non-null, this is the parent view that the
     *                           fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state
     * @return The root View for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_accept_request, container, false);

        LinearLayout requestListLayout = view.findViewById(R.id.driverOfferListLayout);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("rideRequests");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                requestListLayout.removeAllViews(); // Clear previous views

                List<Map.Entry<String, RideRequest>> rideList = new ArrayList<>();

                for (DataSnapshot rideSnap : snapshot.getChildren()) {
                    RideRequest ride = rideSnap.getValue(RideRequest.class);
                    String requestId = rideSnap.getKey();

                    if (ride != null && "unaccepted".equalsIgnoreCase(ride.status)) {
                        rideList.add(new AbstractMap.SimpleEntry<>(requestId, ride));
                    }
                }

                // Sort the list by date
                Collections.sort(rideList, (entry1, entry2) -> {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                        Date date1 = sdf.parse(entry1.getValue().date);
                        Date date2 = sdf.parse(entry2.getValue().date);
                        return date1.compareTo(date2); // Ascending order
                    } catch (Exception e) {
                        return 0; // If parsing fails, keep original order
                    }
                });

                // Now display the sorted rides
                for (Map.Entry<String, RideRequest> entry : rideList) {
                    String requestId = entry.getKey();
                    RideRequest ride = entry.getValue();

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
                                    "\nPassengers: " + Math.abs(Integer.parseInt(ride.passengers)) +
                                    "\nStatus: " + ride.status);

                    // Button to accept
                    Button acceptButton = new Button(getContext());
                    acceptButton.setText("Accept Ride");
                    acceptButton.setOnClickListener(v -> {
                        dbRef.child(requestId).child("status").setValue("accepted")
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Ride offer accepted!", Toast.LENGTH_SHORT).show();

                                    Fragment activeTripFragment = new ActiveTripDriver();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("rideId", requestId); // pass rideId
                                    activeTripFragment.setArguments(bundle);

                                    getParentFragmentManager().beginTransaction()
                                            .replace(R.id.fragmentContainerView, activeTripFragment)
                                            .addToBackStack(null)
                                            .commit();
                                })
                                .addOnFailureListener(e -> Toast
                                        .makeText(getContext(), "Failed to accept ride offer", Toast.LENGTH_SHORT)
                                        .show());
                    });

                    // Divider
                    View divider = new View(getContext());
                    LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 1);
                    dividerParams.setMargins(0, 16, 0, 16);
                    divider.setLayoutParams(dividerParams);
                    divider.setBackgroundColor(getResources().getColor(android.R.color.white));

                    // Add views
                    requestListLayout.addView(divider);
                    rideItemLayout.addView(rideDetails);
                    rideItemLayout.addView(acceptButton);
                    requestListLayout.addView(rideItemLayout);
                } // for loop
            } // onDataChange

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load ride requests", Toast.LENGTH_SHORT).show();
            } // onCancelled
        });

        return view;
    } // onCreateView

} // DriverAcceptRequestFragment
