package edu.uga.cs.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RideFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public RideFragment() {
        // Required empty public constructor
    }

    public static RideFragment newInstance(String param1, String param2) {
        RideFragment fragment = new RideFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride, container, false);

        // Get references to inputs
        EditText fromInput = view.findViewById(R.id.fromInputDriver);
        EditText toInput = view.findViewById(R.id.toDriverInput);
        EditText passengerInput = view.findViewById(R.id.passengerDriverInput);
        EditText dateInput = view.findViewById(R.id.dateInputRider);

        // 1. Autofill today's date initially
        String todayDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        dateInput.setText(todayDate);

        Button requestRideButton = view.findViewById(R.id.requestRideButton);
        requestRideButton.setOnClickListener(v -> {
            String from = fromInput.getText().toString().trim();
            String to = toInput.getText().toString().trim();

            // Passengers Int Valid
            String passengersInput = passengerInput.getText().toString().trim();
            int passengersInt;
            try {
                passengersInt = Integer.parseInt(passengersInput) * -1;
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Passengers must be a valid number!", Toast.LENGTH_SHORT).show();
                return; // Stop submitting if not a number
            }
            String passengers = String.valueOf(passengersInt);

            String enteredDate = dateInput.getText().toString().trim(); // <-- Get live value

            if (from.isEmpty() || to.isEmpty() || passengers.isEmpty() || enteredDate.isEmpty()) {
                Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }


            // 2. Use whatever is entered into the date field
            RideRequest rideRequest = new RideRequest(enteredDate, from, to, passengers);
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("rideRequests");

            String rideId = dbRef.push().getKey(); // generate unique ID
            if (rideId != null) {
                dbRef.child(rideId).setValue(rideRequest)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Ride request posted!", Toast.LENGTH_SHORT).show();

                            getParentFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragmentContainerView, RideWaitForDriverFragment.newInstance(rideId, rideRequest))
                                    .addToBackStack(null)
                                    .commit();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), "Failed to post request", Toast.LENGTH_SHORT).show());
            }
        });

        // Navigation to your requests
        Button yourRequests= view.findViewById(R.id.yourRequestButton);
        yourRequests.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, new UserRequestsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Navigation to accept offers
        Button acceptOfferButton = view.findViewById(R.id.button2);
        acceptOfferButton.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, new RiderAcceptRequestFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Update an unaccepted ride request
        Button updateButton = view.findViewById(R.id.updateRequestButton);
        updateButton.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, new UpdateRideRequest())
                    .addToBackStack(null)
                    .commit();
            Toast.makeText(getContext(), "Update Ride Request Page!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
