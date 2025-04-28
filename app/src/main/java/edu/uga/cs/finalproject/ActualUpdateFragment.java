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
    private EditText fromInput, toInput, passengerInput;
    private DatabaseReference dbRef;

    // empty required constructor
    public ActualUpdateFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize requestId
        if (getArguments() != null) {
            requestId = getArguments().getString("requestId");
        } // if statement
    } // onCreate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // create view
        View view = inflater.inflate(R.layout.fragment_actual_update, container, false);

        // initialize UI elements
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
                    fromInput.setText(ride.from);
                    toInput.setText(ride.to);
                    passengerInput.setText(ride.passengers);
                }
            }


            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load ride info", Toast.LENGTH_SHORT).show();
            } // onCancelled
        });

        // create button lister to update user entry

        updateButton.setOnClickListener(v -> {
            dbRef.child("from").setValue(fromInput.getText().toString());
            dbRef.child("to").setValue(toInput.getText().toString());
            dbRef.child("passengers").setValue(passengerInput.getText().toString());

            Toast.makeText(getContext(), "Ride updated successfully!", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, new RideFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // return the view
        return view;

    } // onCreateView

} // ActualUpdateFragment
