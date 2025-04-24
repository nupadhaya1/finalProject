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

public class UserOffersFragment extends Fragment {

    private DatabaseReference driveOffersRef;

    public UserOffersFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_offers, container, false);
        LinearLayout listLayout = view.findViewById(R.id.rideOffersListLayout);

        driveOffersRef = FirebaseDatabase.getInstance().getReference("driveOffers");

        driveOffersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listLayout.removeAllViews();

                for (DataSnapshot rideSnap : snapshot.getChildren()) {
                    DriverOffer offer = rideSnap.getValue(DriverOffer.class);
                    String offerId = rideSnap.getKey();

                    if (offer != null) {
                        LinearLayout itemLayout = new LinearLayout(getContext());
                        itemLayout.setOrientation(LinearLayout.VERTICAL);
                        itemLayout.setPadding(20, 20, 20, 20);

                        TextView infoText = new TextView(getContext());
                        infoText.setText(
                                "ID: " + offerId + "\n" +
                                        "Date: " + offer.date + "\n" +
                                        "From: " + offer.from + "\n" +
                                        "To: " + offer.to + "\n" +
                                        "Passengers: " + offer.passengers + "\n" +
                                        "Status: " + offer.status
                        );

                        Button cancelButton = new Button(getContext());
                        cancelButton.setText("Cancel Offer");
                        cancelButton.setOnClickListener(v -> {
                            driveOffersRef.child(offerId).removeValue()
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(getContext(), "Offer Canceled", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(getContext(), "Failed to cancel offer", Toast.LENGTH_SHORT).show());

                            itemLayout.setVisibility(View.GONE);
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
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load drive offers", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
