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
 * Fragment that displays all unaccepted driver offers and allows the user
 * to select one to update. Navigates to ActualUpdateOfferFragment on selection.
 */
public class UpdateDriverOffer extends Fragment {

    /**
     * Required empty public constructor for fragment instantiation.
     */
    public UpdateDriverOffer() {
        // Required empty public constructor
    }

    /**
     * Called to create and return the view hierarchy associated with the fragment.
     * Inflates the driver offer update layout, queries Firebase for drive offers,
     * and populates only those with status "unaccepted". Sets click listeners
     * to navigate to ActualUpdateOfferFragment when an offer is selected.
     *
     * @param inflater           LayoutInflater to inflate views
     * @param container          Parent view that the fragment's UI should attach to
     * @param savedInstanceState Bundle containing saved state, if any
     * @return The root View for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_update_driver_offer, container, false);

        // Initialize the LinearLayout that will contain offer items
        LinearLayout requestListLayout = view.findViewById(R.id.driverOfferListLayout);

        // Reference to the "driveOffers" node in Firebase Realtime Database
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("driveOffers");

        // Attach a listener to read all drive offers
        dbRef.addValueEventListener(new ValueEventListener() {
            /**
             * Called when data at "driveOffers" changes.
             * Clears existing views and iterates through each offer,
             * displaying only those with status "unaccepted".
             *
             * @param snapshot DataSnapshot containing all child nodes under "driveOffers"
             */
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Clear previous offer views
                requestListLayout.removeAllViews();

                // Loop through each offer entry
                for (DataSnapshot rideSnap : snapshot.getChildren()) {
                    DriverOffer offer = rideSnap.getValue(DriverOffer.class);
                    String offerId = rideSnap.getKey();

                    // Display only unaccepted offers
                    if (offer != null && offer.status != null
                            && offer.status.equalsIgnoreCase("unaccepted")) {
                        // Create layout container for offer item
                        LinearLayout rideItemLayout = new LinearLayout(getContext());
                        rideItemLayout.setOrientation(LinearLayout.VERTICAL);
                        rideItemLayout.setPadding(16, 16, 16, 16);

                        // TextView showing offer details
                        TextView rideDetails = new TextView(getContext());
                        rideDetails.setTextColor(getResources().getColor(android.R.color.white));
                        rideDetails.setText(
                                "Offer ID: " + offerId +
                                        "\nDate: " + offer.date +
                                        "\nFrom: " + offer.from +
                                        "\nTo: " + offer.to +
                                        "\nPassengers: " + Math.abs(Integer.parseInt(offer.passengers)) +
                                        "\nStatus: " + offer.status);

                        rideItemLayout.addView(rideDetails);

                        // Navigate to ActualUpdateOfferFragment on click
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

                        // Divider between items
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

            /**
             * Called if the database read is cancelled or fails.
             * Displays an error message to the user.
             *
             * @param error DatabaseError containing details of the failure
             */
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load ride offers", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
