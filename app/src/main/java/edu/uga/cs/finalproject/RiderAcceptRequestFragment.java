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

public class RiderAcceptRequestFragment extends Fragment {

    public RiderAcceptRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // create a View
        View view = inflater.inflate(R.layout.fragment_rider_accept_request, container, false);

        // create and initialize a linear layout
        LinearLayout requestListLayout = view.findViewById(R.id.riderRequestListLayout);

        // create and initialize database reference
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("driveOffers");

        // add a database reference
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                requestListLayout.removeAllViews(); // Clear previous views

                List<Map.Entry<String, DriverOffer>> offerList = new ArrayList<>();

                // Loop through each ride offer snapshot retrieved from Firebase
                for (DataSnapshot rideSnap : snapshot.getChildren()) {
                    // Convert the snapshot into a DriverOffer object
                    DriverOffer offer = rideSnap.getValue(DriverOffer.class);
                    String offerId = rideSnap.getKey();

                    // Add the offer to the list only if its status is "unaccepted"
                    if (offer != null && "unaccepted".equalsIgnoreCase(offer.status)) {
                        offerList.add(new AbstractMap.SimpleEntry<>(offerId, offer));

                    } // if statement

                } // for loop

                // Sort the offers by date
                Collections.sort(offerList, (entry1, entry2) -> {

                    // try and catch
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                        Date date1 = sdf.parse(entry1.getValue().date);
                        Date date2 = sdf.parse(entry2.getValue().date);

                        // return
                        return date1.compareTo(date2);

                    } catch (Exception e) {

                        return 0;
                    } // try catch
                });

                // Display the sorted offers
                for (Map.Entry<String, DriverOffer> entry : offerList) {
                    String offerId = entry.getKey();
                    DriverOffer offer = entry.getValue();

                    // Create container for each offer
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

                    // Button to accept
                    Button acceptButton = new Button(getContext());
                    acceptButton.setText("Accept Offer");
                    acceptButton.setOnClickListener(v -> {
                        dbRef.child(offerId).child("status").setValue("accepted");
                        Toast.makeText(getContext(), "Ride offer accepted!", Toast.LENGTH_SHORT).show();

                        Fragment activeTripFragment = new ActiveTripRider();
                        Bundle bundle = new Bundle();
                        bundle.putString("rideId", offerId);
                        activeTripFragment.setArguments(bundle);

                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainerView, activeTripFragment)
                                .addToBackStack(null)
                                .commit();
                    });

                    // Divider
                    View divider = new View(getContext());
                    LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 1);
                    dividerParams.setMargins(0, 16, 0, 16);
                    divider.setLayoutParams(dividerParams);
                    divider.setBackgroundColor(getResources().getColor(android.R.color.white));

                    requestListLayout.addView(divider);
                    rideItemLayout.addView(rideDetails);
                    rideItemLayout.addView(acceptButton);
                    requestListLayout.addView(rideItemLayout);
                } // for loop
            } // onDataChange

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load ride offers", Toast.LENGTH_SHORT).show();
            } // onCancelled
        });

        // return view
        return view;

    } // onCreateView

} // RiderAcceptRequestFragment
