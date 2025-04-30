/**
 * Fragment for drivers to wait for a rider to accept their offer.
 *
 * Displays offer details (date, from, to, passengers, request ID, status). 
 * Listens for status updates from Firebase and navigates to ActiveTripDriver 
 * when the offer is accepted. Allows canceling or updating the offer.
 */
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

    /**
     * Argument key for ride date.
     */
    private static final String ARG_DATE = "date";

    /**
     * Argument key for start location.
     */
    private static final String ARG_FROM = "from";

    /**
     * Argument key for destination.
     */
    private static final String ARG_TO = "to";

    /**
     * Argument key for passenger count.
     */
    private static final String ARG_PASSENGERS = "passengers";

    /**
     * Argument key for ride ID.
     */
    private static final String ARG_RIDE_ID = "rideId";

    /**
     * Argument key for status.
     */
    private static final String ARG_STATUS = "status";

    /**
     * Firebase reference for this offer.
     */
    private DatabaseReference rideRef;

    /**
     * Listener for status changes.
     */
    private ValueEventListener statusListener;

    /**
     * Required empty public constructor.
     */
    public DriveWaitforRiderFragment() {
    }

    /**
     * Factory method to create a new instance with offer details.
     *
     * @param offer       The unique offer ID.
     * @param driverOffer The DriverOffer object containing details.
     * @return A new instance of DriveWaitforRiderFragment.
     */
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

    /**
     * Inflates the fragment layout, displays offer details, and sets up listeners.
     *
     * @param inflater           LayoutInflater to inflate views.
     * @param container          Parent view to attach to.
     * @param savedInstanceState Previous state data, if any.
     * @return The root View of the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drive_wait_for_rider, container, false);

        TextView dateText = view.findViewById(R.id.dateText);
        TextView fromText = view.findViewById(R.id.fromText);
        TextView toText = view.findViewById(R.id.toText);
        TextView passengersText = view.findViewById(R.id.passengersText);
        TextView rideIdText = view.findViewById(R.id.rideIdText);
        TextView statusText = view.findViewById(R.id.statusText);
        Button cancelButton = view.findViewById(R.id.cancelRideRequest);
        Button updateButton = view.findViewById(R.id.updateRideRequest);

        Bundle args = getArguments();
        if (args != null) {
            String date = args.getString(ARG_DATE);
            String from = args.getString(ARG_FROM);
            String to = args.getString(ARG_TO);
            String passengers = args.getString(ARG_PASSENGERS);
            String rideId = args.getString(ARG_RIDE_ID);

            dateText.setText("Date: " + date);
            fromText.setText("From: " + from);
            toText.setText("To: " + to);
            passengersText.setText("Passengers: " + passengers);
            rideIdText.setText("Request ID: " + rideId);

            rideRef = FirebaseDatabase.getInstance().getReference("driveOffers").child(rideId);
            MainScreen.isInWaitingFragment = true;
            MainScreen.currentWaitingRef = rideRef;

            statusListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    RideRequest updatedRide = snapshot.getValue(RideRequest.class);
                    if (updatedRide != null) {
                        statusText.setText("Status: " + updatedRide.status);
                        if ("accepted".equalsIgnoreCase(updatedRide.status)) {
                            ActiveTripDriver activeTripFragment = new ActiveTripDriver();
                            Bundle bundle = new Bundle();
                            bundle.putString("rideId", rideId);
                            activeTripFragment.setArguments(bundle);
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentContainerView, activeTripFragment)
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

                // if ride ref is not null, add a listener and replace fragment
                if (rideRef != null) {
                    rideRef.removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Ride offer canceled", Toast.LENGTH_SHORT).show();
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragmentContainerView, new DriverFragment())
                                        .commit();
                            })
                            .addOnFailureListener(e -> Toast
                                    .makeText(getContext(), "Failed to cancel ride offer", Toast.LENGTH_SHORT).show());
                } // if statement
            });

            updateButton.setOnClickListener(v -> {
                // if update button is pressed pull up update fragment
                if (rideRef != null) {
                    ActualUpdateOfferFragment updateFragment = new ActualUpdateOfferFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("offerId", rideId);
                    updateFragment.setArguments(bundle);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView, updateFragment)
                            .addToBackStack(null)
                            .commit();
                } // if statement
            });
        }

        return view;
    }

    /**
     * Cleans up listeners and flags when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        MainScreen.isInWaitingFragment = false;
        MainScreen.currentWaitingRef = null;
        super.onDestroyView();

        if (rideRef != null && statusListener != null) {

            rideRef.removeEventListener(statusListener);

        }

    }
}