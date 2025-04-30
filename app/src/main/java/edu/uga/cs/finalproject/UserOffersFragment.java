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

/**
 * Fragment that retrieves and displays a list of driver offers from Firebase
 * Realtime Database.
 * Allows users to cancel individual driving offers.
 */
public class UserOffersFragment extends Fragment {

    /**
     * Reference to the "driveOffers" node in Firebase Realtime Database.
     */
    private DatabaseReference driveOffersRef;

    /**
     * Required empty public constructor for fragment instantiation.
     */
    public UserOffersFragment() {
        // Required empty constructor
    }

    /**
     * Called to create the fragment's view hierarchy.
     * Inflates the layout, initializes the Firebase reference, and populates the
     * list of drive offers.
     *
     * @param inflater           LayoutInflater used to inflate views in the
     *                           fragment
     * @param container          The parent view to attach the fragment UI to
     * @param savedInstanceState Bundle containing saved state, if any
     * @return The root view of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the fragment's layout
        View view = inflater.inflate(R.layout.fragment_user_offers, container, false);

        // Find and initialize the LinearLayout container for offers
        LinearLayout listLayout = view.findViewById(R.id.rideOffersListLayout);

        // Initialize Firebase database reference to the "driveOffers" node
        driveOffersRef = FirebaseDatabase.getInstance().getReference("driveOffers");

        // Read data once from the database
        driveOffersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            /**
             * Called when data is successfully read from the database.
             * Iterates through driver offer entries and dynamically creates list items.
             *
             * @param snapshot DataSnapshot containing all child nodes under "driveOffers"
             */
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Clear existing views
                listLayout.removeAllViews();

                // Loop through each driver offer
                for (DataSnapshot rideSnap : snapshot.getChildren()) {
                    DriverOffer offer = rideSnap.getValue(DriverOffer.class);
                    String offerId = rideSnap.getKey();

                    if (offer != null) {
                        // Create layout for a single offer item
                        LinearLayout itemLayout = new LinearLayout(getContext());
                        itemLayout.setOrientation(LinearLayout.VERTICAL);
                        itemLayout.setPadding(20, 20, 20, 20);

                        // Display offer details
                        TextView infoText = new TextView(getContext());
                        infoText.setText(
                                "ID: " + offerId + "\n" +
                                        "Date: " + offer.date + "\n" +
                                        "From: " + offer.from + "\n" +
                                        "To: " + offer.to + "\n" +
                                        "Passengers: " + Math.abs(Integer.parseInt(offer.passengers)) + "\n" +
                                        "Status: " + offer.status);

                        // Create Cancel Offer button
                        Button cancelButton = new Button(getContext());
                        cancelButton.setText("Cancel Offer");
                        cancelButton.setOnClickListener(v -> {
                            // Remove this offer from database
                            driveOffersRef.child(offerId).removeValue()
                                    .addOnSuccessListener(aVoid -> Toast
                                            .makeText(getContext(), "Offer canceled", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast
                                            .makeText(getContext(), "Failed to cancel offer", Toast.LENGTH_SHORT)
                                            .show());

                            // Hide item on success
                            itemLayout.setVisibility(View.GONE);
                        });

                        // Add views to item layout
                        itemLayout.addView(infoText);
                        itemLayout.addView(cancelButton);

                        // Add item and divider to parent layout
                        listLayout.addView(itemLayout);
                        View divider = new View(getContext());
                        divider.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 2));
                        divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                        listLayout.addView(divider);
                    }
                }
            }

            /**
             * Called if the database read is cancelled or fails.
             * Displays an error toast to the user.
             *
             * @param error DatabaseError containing details of the failure
             */
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load drive offers", Toast.LENGTH_SHORT).show();
            }
        });

        // Return the fully constructed view
        return view;
    }
}
