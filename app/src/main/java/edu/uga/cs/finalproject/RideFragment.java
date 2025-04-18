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

        // Get current date
        String currentDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());

        Button requestRideButton = view.findViewById(R.id.requestRideButton);
        requestRideButton.setOnClickListener(v -> {
            String from = fromInput.getText().toString().trim();
            String to = toInput.getText().toString().trim();
            String passengers = passengerInput.getText().toString().trim();

            if (from.isEmpty() || to.isEmpty() || passengers.isEmpty()) {
                Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            RideRequest rideRequest = new RideRequest(currentDate, from, to, passengers);
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("rideRequests");

            dbRef.push().setValue(rideRequest)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Ride request posted!", Toast.LENGTH_SHORT).show();

                        // Navigate to waiting screen
                        getParentFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragmentContainerView, new RideWaitForDriverFragment())
                                .addToBackStack(null)
                                .commit();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to post request", Toast.LENGTH_SHORT).show());

        });

        // Optional: Navigation to available rides
        Button acceptOfferButton = view.findViewById(R.id.button2);
        acceptOfferButton.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, new AvailableRideFragments())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
