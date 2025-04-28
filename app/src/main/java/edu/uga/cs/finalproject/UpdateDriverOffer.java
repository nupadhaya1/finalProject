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

public class UpdateDriverOffer extends Fragment {

    public UpdateDriverOffer() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // create view
        View view = inflater.inflate(R.layout.fragment_update_driver_offer, container, false);

        // create and initialize linearlayout
        LinearLayout requestListLayout = view.findViewById(R.id.driverOfferListLayout);

        // initialize database reference
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("driveOffers");

        // add listener to database listener
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                requestListLayout.removeAllViews();

                // for loop for displaying
                for (DataSnapshot rideSnap : snapshot.getChildren()) {
                    DriverOffer offer = rideSnap.getValue(DriverOffer.class);
                    String offerId = rideSnap.getKey();

                    // check to see only unaccpeted is displayed
                    if (offer != null && offer.status != null && offer.status.equalsIgnoreCase("unaccepted")) {
                        LinearLayout rideItemLayout = new LinearLayout(getContext());
                        rideItemLayout.setOrientation(LinearLayout.VERTICAL);
                        rideItemLayout.setPadding(16, 16, 16, 16);

                        TextView rideDetails = new TextView(getContext());
                        rideDetails.setTextColor(getResources().getColor(android.R.color.white));
                        rideDetails.setText(
                                "Request ID: " + offerId +
                                        "\nDate: " + offer.date +
                                        "\nFrom: " + offer.from +
                                        "\nTo: " + offer.to +
                                        "\nPassengers: " + Math.abs(Integer.parseInt(offer.passengers)) +
                                        "\nStatus: " + offer.status);

                        rideItemLayout.addView(rideDetails);

                        rideItemLayout.setOnClickListener(v -> {
                            Bundle bundle = new Bundle();
                            bundle.putString("offerId", offerId);

                            ActualUpdateOfferFragment fragment = new ActualUpdateOfferFragment();
                            fragment.setArguments(bundle);

                            getParentFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragmentContainerView, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        });

                        requestListLayout.addView(rideItemLayout);

                        View divider = new View(getContext());
                        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 1);
                        dividerParams.setMargins(0, 16, 0, 16);
                        divider.setLayoutParams(dividerParams);
                        divider.setBackgroundColor(getResources().getColor(android.R.color.white));
                        requestListLayout.addView(divider);
                    } // if statement
                } // for loop
            } // onDataChange

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load ride offers", Toast.LENGTH_SHORT).show();
            } // onCancelled
        });

        // return the view
        return view;
    } // onCreateView

} // UpdateDriverOffer
