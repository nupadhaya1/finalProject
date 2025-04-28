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

public class DriveWaitforRiderFragment extends Fragment {

    // create info variables
    private static final String ARG_DATE = "date";
    private static final String ARG_FROM = "from";
    private static final String ARG_TO = "to";
    private static final String ARG_PASSENGERS = "passengers";
    private static final String ARG_RIDE_ID = "rideId";
    private static final String ARG_STATUS = "status";

    // listener and database
    private DatabaseReference rideRef;
    private ValueEventListener statusListener;

    public DriveWaitforRiderFragment() {
    }

    public static DriveWaitforRiderFragment newInstance(String offer, DriverOffer driverOffer) {
        DriveWaitforRiderFragment fragment = new DriveWaitforRiderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, driverOffer.date);
        args.putString(ARG_FROM, driverOffer.from);
        args.putString(ARG_TO, driverOffer.to);
        args.putString(ARG_PASSENGERS, driverOffer.passengers);
        args.putString(ARG_RIDE_ID, offer);
        args.putString(ARG_STATUS, driverOffer.status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // create a view
        View view = inflater.inflate(R.layout.fragment_drive_wait_for_rider, container, false);

        // create and intialize UI elements
        TextView dateText = view.findViewById(R.id.dateText);
        TextView fromText = view.findViewById(R.id.fromText);
        TextView toText = view.findViewById(R.id.toText);
        TextView passengersText = view.findViewById(R.id.passengersText);
        TextView rideIdText = view.findViewById(R.id.rideIdText);
        TextView statusText = view.findViewById(R.id.statusText);
        Button cancelButton = view.findViewById(R.id.cancelRideRequest);
        Button updateButton = view.findViewById(R.id.updateRideRequest);

        Bundle args = getArguments();

        // check arguements
        if (args != null) {

            // initialize date,from,to, passengers, rideId
            String date = args.getString(ARG_DATE);
            String from = args.getString(ARG_FROM);
            String to = args.getString(ARG_TO);
            String passengers = args.getString(ARG_PASSENGERS);
            String rideId = args.getString(ARG_RIDE_ID);

            // set the textboxes
            dateText.setText("Date: " + date);
            fromText.setText("From: " + from);
            toText.setText("To: " + to);
            passengersText.setText("Passengers: " + passengers);
            rideIdText.setText("Request ID: " + rideId);

            // initialze the ride database
            rideRef = FirebaseDatabase.getInstance().getReference("driveOffers").child(rideId);

            MainScreen.isInWaitingFragment = true;
            MainScreen.currentWaitingRef = rideRef;

            // create a listener
            statusListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    RideRequest updatedRide = snapshot.getValue(RideRequest.class);
                    // check to see if updateRide isnt null
                    if (updatedRide != null) {
                        statusText.setText("Status: " + updatedRide.status);

                        // check if accept
                        if ("accepted".equalsIgnoreCase(updatedRide.status)) {
                            Fragment activeTripFragment = new ActiveTripDriver();
                            Bundle bundle = new Bundle();
                            bundle.putString("rideId", rideId); // Pass rideId so the next fragment knows
                            activeTripFragment.setArguments(bundle);

                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentContainerView, activeTripFragment)
                                    .addToBackStack(null)
                                    .commit();
                        } // if statement

                    } else {
                        statusText.setText("Status: canceled or not found");
                    } // end of if else statement

                } // onDataChange

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(getContext(), "Error loading ride status", Toast.LENGTH_SHORT).show();
                } // onCancelled
            };

            // adda listener
            rideRef.addValueEventListener(statusListener);

            // add listener to  button
            cancelButton.setOnClickListener(v -> {
                if (rideRef != null) {
                    rideRef.removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Ride offer canceled", Toast.LENGTH_SHORT).show();

                                // Instead of popBackStack(), manually replace with DriverFragment
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragmentContainerView, new DriverFragment())
                                        .commit();
                            })
                            .addOnFailureListener(e -> Toast
                                    .makeText(getContext(), "Failed to cancel ride offer", Toast.LENGTH_SHORT).show());
                }
            });


            updateButton.setOnClickListener(v -> {
                if (rideRef != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("offerId", args.getString(ARG_RIDE_ID)); // Pass offerId safely

                    ActualUpdateOfferFragment actualUpdateOfferFragment = new ActualUpdateOfferFragment();
                    actualUpdateOfferFragment.setArguments(bundle);

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView, actualUpdateOfferFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });

        } // if statement

        // return view
        return view;

    } // onCreateView

    @Override
    public void onDestroyView() {
        MainScreen.isInWaitingFragment = false;
        MainScreen.currentWaitingRef = null;

        super.onDestroyView();
        // if rider is null and status listener is null
        if (rideRef != null && statusListener != null) {
            rideRef.removeEventListener(statusListener);
        } // if statement
    } // onDestroyView

} // DriverWaitForRideFragment
