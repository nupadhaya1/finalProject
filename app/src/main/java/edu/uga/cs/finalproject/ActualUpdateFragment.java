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

public class ActualUpdateFragment extends Fragment {

    // create necessary UI variables
    private String requestId;
    private EditText dateInput, fromInput, toInput, passengerInput;
    private DatabaseReference dbRef;

    // empty required constructor
    public ActualUpdateFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize requestId
        if (getArguments() != null) {
            requestId = getArguments().getString("requestId");
        }
    } // onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // create view
        View view = inflater.inflate(R.layout.fragment_actual_update, container, false);

        // initialize UI elements
        dateInput = view.findViewById(R.id.dateInputRider);
        fromInput = view.findViewById(R.id.fromInputDriverUpdated);
        toInput = view.findViewById(R.id.toDriverInputUpdated);
        passengerInput = view.findViewById(R.id.passengerDriverInputUpdated);
        Button updateButton = view.findViewById(R.id.actualUpdateButton);

        // initialize database reference
        dbRef = FirebaseDatabase.getInstance().getReference("rideRequests").child(requestId);

        // Prefill fields from Firebase
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                RideRequest ride = snapshot.getValue(RideRequest.class);
                if (ride != null) {
                    dateInput.setText(ride.date);
                    fromInput.setText(ride.from);
                    toInput.setText(ride.to);
                    passengerInput.setText(ride.passengers);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load ride info", Toast.LENGTH_SHORT).show();
            }
        });

        // Update button logic
        updateButton.setOnClickListener(v -> {
            String updatedDate = dateInput.getText().toString().trim();
            String updatedFrom = fromInput.getText().toString().trim();
            String updatedTo = toInput.getText().toString().trim();
            String updatedPassengers = passengerInput.getText().toString().trim();

            if (updatedDate.isEmpty() || updatedFrom.isEmpty() || updatedTo.isEmpty() || updatedPassengers.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update Firebase
            dbRef.child("date").setValue(updatedDate);
            dbRef.child("from").setValue(updatedFrom);
            dbRef.child("to").setValue(updatedTo);
            dbRef.child("passengers").setValue(updatedPassengers);

            Toast.makeText(getContext(), "Ride updated successfully!", Toast.LENGTH_SHORT).show();

            // Create RideRequest correctly
            RideRequest updatedRideRequest = new RideRequest(
                    updatedDate,
                    updatedFrom,
                    updatedTo,
                    updatedPassengers
            );
            updatedRideRequest.status = "unaccepted";  // manually set the status field

            RideWaitForDriverFragment rideWaitFragment = RideWaitForDriverFragment.newInstance(requestId, updatedRideRequest);

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, rideWaitFragment)
                    .addToBackStack(null)
                    .commit();
        });


        // return the view
        return view;
    } // onCreateView

} // ActualUpdateFragment
