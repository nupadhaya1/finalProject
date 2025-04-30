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
 * Fragment that displays all unaccepted driver offers to the rider, sorted by
 * date,
 * and allows the rider to accept an offer. Upon acceptance, the fragment
 * transitions
 * to ActiveTripRider to show the active trip details.
 */
public class RiderAcceptRequestFragment extends Fragment {

    /**
     * Required empty public constructor for fragment instantiation.
     */
    public RiderAcceptRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the layout, retrieves drive offers from Firebase, filters and sorts
     * them,
     * and dynamically populates UI elements for each offer. Sets up listeners for
     * accepting offers.
     *
     * @param inflater           LayoutInflater to inflate fragment views
     * @param container          Parent view that the fragment's UI should attach to
     * @param savedInstanceState Bundle containing saved state, if any
     * @return The root View for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_rider_accept_request, container, false);

        // LinearLayout container for listing ride offers
        LinearLayout requestListLayout = view.findViewById(R.id.riderRequestListLayout);
        // Reference to the "driveOffers" node in Firebase Realtime Database
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("driveOffers");

        // Attach a listener to read drive offers
        dbRef.addValueEventListener(new ValueEventListener() {
            /**
             * Called when driveOffers data changes. Clears existing views, collects
             * unaccepted offers,
             * sorts them by date, and creates UI entries with accept buttons.
             *
             * @param snapshot DataSnapshot containing all child nodes under "driveOffers"
             */
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Clear previous views
                requestListLayout.removeAllViews();

                // List to hold offer entries
                List<Map.Entry<String, DriverOffer>> offerList = new ArrayList<>();

                // Filter unaccepted offers
                for (DataSnapshot rideSnap : snapshot.getChildren()) {
                    // Convert the snapshot into a DriverOffer object
                    DriverOffer offer = rideSnap.getValue(DriverOffer.class);
                    String offerId = rideSnap.getKey();
                    if (offer != null && "unaccepted".equalsIgnoreCase(offer.status)) {
                        offerList.add(new AbstractMap.SimpleEntry<>(offerId, offer));

                    } // if statement

                } // for loop

                // Sort offers by date (format: MM/dd/yyyy)
                Collections.sort(offerList, (e1, e2) -> {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                        Date d1 = sdf.parse(e1.getValue().date);
                        Date d2 = sdf.parse(e2.getValue().date);
                        return d1.compareTo(d2);
                    } catch (Exception ex) {
                        return 0;
                    }
                });

                // Create UI elements for each sorted offer
                for (Map.Entry<String, DriverOffer> entry : offerList) {
                    String offerId = entry.getKey();
                    DriverOffer offer = entry.getValue();

                    // Container for a single offer
                    LinearLayout rideItemLayout = new LinearLayout(getContext());
                    rideItemLayout.setOrientation(LinearLayout.VERTICAL);
                    rideItemLayout.setPadding(16, 16, 16, 16);

                    // TextView for offer details
                    TextView rideDetails = new TextView(getContext());
                    rideDetails.setTextColor(getResources().getColor(android.R.color.white));
                    rideDetails.setText(
                            "Offer ID: " + offerId +
                                    "\nDate: " + offer.date +
                                    "\nFrom: " + offer.from +
                                    "\nTo: " + offer.to +
                                    "\nPassengers: " + offer.passengers +
                                    "\nStatus: " + offer.status);

                    // Button to accept the offer
                    Button acceptButton = new Button(getContext());
                    acceptButton.setText("Accept Offer");
                    acceptButton.setOnClickListener(v -> {
                        // Update status in Firebase
                        dbRef.child(offerId).child("status").setValue("accepted");
                        Toast.makeText(getContext(), "Ride offer accepted!", Toast.LENGTH_SHORT).show();

                        // Navigate to ActiveTripRider
                        Fragment activeTrip = new ActiveTripRider();
                        Bundle bundle = new Bundle();
                        bundle.putString("rideId", offerId);
                        activeTrip.setArguments(bundle);
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainerView, activeTrip)
                                .addToBackStack(null)
                                .commit();
                    });

                    // Divider view
                    View divider = new View(getContext());
                    LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 1);
                    dividerParams.setMargins(0, 16, 0, 16);
                    divider.setLayoutParams(dividerParams);
                    divider.setBackgroundColor(getResources().getColor(android.R.color.white));

                    // Add views in order
                    requestListLayout.addView(divider);
                    rideItemLayout.addView(rideDetails);
                    rideItemLayout.addView(acceptButton);
                    requestListLayout.addView(rideItemLayout);
                } // for loop
            } // onDataChange

            /**
             * Called if reading driveOffers is cancelled or fails. Shows an error toast.
             *
             * @param error DatabaseError with details of the failure
             */
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load ride offers", Toast.LENGTH_SHORT).show();
            } // onCancelled
        });

        // return view
        return view;

    } // onCreateView

} // RiderAcceptRequestFragment
