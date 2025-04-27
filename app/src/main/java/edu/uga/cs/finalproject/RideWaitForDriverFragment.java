package edu.uga.cs.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RideWaitForDriverFragment extends Fragment {

    private static final String ARG_DATE = "date";
    private static final String ARG_FROM = "from";
    private static final String ARG_TO = "to";
    private static final String ARG_PASSENGERS = "passengers";
    private static final String ARG_RIDE_ID = "rideId";
    private static final String ARG_STATUS = "status";

    private DatabaseReference rideRef;
    private ValueEventListener statusListener;

    public RideWaitForDriverFragment() {
        // Required empty public constructor
    }

    public static RideWaitForDriverFragment newInstance(String rideId, RideRequest rideRequest) {
        RideWaitForDriverFragment fragment = new RideWaitForDriverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, rideRequest.date);
        args.putString(ARG_FROM, rideRequest.from);
        args.putString(ARG_TO, rideRequest.to);
        args.putString(ARG_PASSENGERS, rideRequest.passengers);
        args.putString(ARG_RIDE_ID, rideId);
        args.putString(ARG_STATUS, rideRequest.status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_wait_for_driver, container, false);

        TextView dateText = view.findViewById(R.id.dateText);
        TextView fromText = view.findViewById(R.id.fromText);
        TextView toText = view.findViewById(R.id.toText);
        TextView passengersText = view.findViewById(R.id.passengersText);
        TextView rideIdText = view.findViewById(R.id.rideIdText);
        TextView statusText = view.findViewById(R.id.statusText);
        Button cancelButton = view.findViewById(R.id.cancelRideRequest);

        Bundle args = getArguments();
        if (args != null) {
            String date = args.getString(ARG_DATE);
            String from = args.getString(ARG_FROM);
            String to = args.getString(ARG_TO);
            String passengers = args.getString(ARG_PASSENGERS);
            String rideId = args.getString(ARG_RIDE_ID);

            // Set initial values
            dateText.setText("Date: " + date);
            fromText.setText("From: " + from);
            toText.setText("To: " + to);
            passengersText.setText("Passengers: " + passengers);
            rideIdText.setText("Request ID: " + rideId);

            // Get reference to this specific ride request
            rideRef = FirebaseDatabase.getInstance().getReference("rideRequests").child(rideId);

            // Listen for status changes
            statusListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    RideRequest updatedRide = snapshot.getValue(RideRequest.class);
                    if (updatedRide != null) {
                        statusText.setText("Status: " + updatedRide.status);

                        if ("accepted".equalsIgnoreCase(updatedRide.status)) {
                            Fragment activeTripRider = new ActiveTripRider();
                            Bundle bundle = new Bundle();
                            bundle.putString("rideId", rideId);  // <-- pass rideId
                            activeTripRider.setArguments(bundle);

                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentContainerView, activeTripRider)
                                    .addToBackStack(null)
                                    .commit();
                        }

                    } else {
                        statusText.setText("Status: canceled or not found");
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(getContext(), "Error loading ride status", Toast.LENGTH_SHORT).show();
                }
            };

            rideRef.addValueEventListener(statusListener);

            cancelButton.setOnClickListener(v -> {
                rideRef.removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Ride request canceled", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager().popBackStack();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), "Failed to cancel request", Toast.LENGTH_SHORT).show());
            });
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rideRef != null && statusListener != null) {
            rideRef.removeEventListener(statusListener);
        }
    }
}
