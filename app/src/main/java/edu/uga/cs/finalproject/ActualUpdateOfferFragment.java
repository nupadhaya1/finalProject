package edu.uga.cs.finalproject;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActualUpdateOfferFragment extends Fragment {

    // create necessary UI variables
    private String offerId;
    private EditText fromInput, toInput, passengerInput;
    private DatabaseReference dbRef;

    public ActualUpdateOfferFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize offerId
        if (getArguments() != null) {
            offerId = getArguments().getString("offerId");
        } // if statement
    } // onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // create View
        View view = inflater.inflate(R.layout.fragment_actual_update_offer, container, false);

        // initialize UI elements
        fromInput = view.findViewById(R.id.fromInputDriver);
        toInput = view.findViewById(R.id.toDriverInput);
        passengerInput = view.findViewById(R.id.passengerDriverInput);
        Button updateButton = view.findViewById(R.id.updateOfferButtonFinal);

        // check offer id is provided
        if (offerId == null) {
            Toast.makeText(getContext(), "No offer ID provided", Toast.LENGTH_SHORT).show();
            return view;
        } // if statement

        // initialize firebase reference
        dbRef = FirebaseDatabase.getInstance().getReference("driveOffers").child(offerId);

        // Load existing ride data
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                 DriverOffer offer = snapshot.getValue(DriverOffer.class);
                if (offer != null) {
                    fromInput.setText(offer.from);
                    toInput.setText(offer.to);
                    passengerInput.setText(offer.passengers);
                } // if statement
            } // onDataChange

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load ride data", Toast.LENGTH_SHORT).show();
            } // onCancelled
        });

        // Update ride when button is pressed
        updateButton.setOnClickListener(v -> {
            String updatedFrom = fromInput.getText().toString().trim();
            String updatedTo = toInput.getText().toString().trim();
            String updatedPassengers = passengerInput.getText().toString().trim();

            if (updatedFrom.isEmpty() || updatedTo.isEmpty() || updatedPassengers.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            } // if statement

            // set values
            dbRef.child("from").setValue(updatedFrom);
            dbRef.child("to").setValue(updatedTo);
            dbRef.child("passengers").setValue(updatedPassengers);

            Toast.makeText(getContext(), "Ride updated successfully!", Toast.LENGTH_SHORT).show();

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView, new DriverFragment())
                        .addToBackStack(null)
                        .commit();


        });

        // return view
        return view;
    } // onCreateView
} //ActualUpdateOfferFragment
