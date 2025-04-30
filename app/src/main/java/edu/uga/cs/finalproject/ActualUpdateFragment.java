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

/**
 * A Fragment that allows the rider to update the details of an existing
 * {@link RideRequest}.
 * 
 * Retrieves the requestId from arguments, loads the current ride data from
 * Firebase Realtime Database, populates input fields, and writes updates back
 * to the database when the user confirms.
 * 
 */
public class ActualUpdateFragment extends Fragment {

    /** The key of the ride request to be updated in Firebase. */
    private String requestId;

    /** Input field for the ride date. */
    private EditText dateInput;

    /** Input field for the "from" location. */
    private EditText fromInput;

    /** Input field for the "to" location. */
    private EditText toInput;

    /** Input field for the number of passengers. */
    private EditText passengerInput;

    /** Reference to the specific rideRequests node in Firebase. */
    private DatabaseReference dbRef;

    /**
     * Required empty public constructor.
     */
    public ActualUpdateFragment() {
        // Required empty public constructor
    }

    /**
     * Called to do initial creation of the fragment.
     * 
     * Extracts the {@code requestId} from the fragment arguments Bundle.
     * 
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the request ID passed in arguments
        if (getArguments() != null) {
            requestId = getArguments().getString("requestId");
        }
    }

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     * 
     * - Inflates the layout
     * - Initializes input fields and the "Update" button
     * - Loads existing ride data from Firebase
     * - Sets up the update button to write changes back to Firebase
     * and navigate to {@link RideWaitForDriverFragment}.
     * 
     *
     * @param inflater           The LayoutInflater object that can be used to
     *                           inflate
     *                           any views in the fragment.
     * @param container          If non-null, this is the parent view that the
     *                           fragment's UI
     *                           should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state.
     * @return The root View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_actual_update, container, false);

        // Initialize UI elements
        dateInput = view.findViewById(R.id.dateInputRider);
        fromInput = view.findViewById(R.id.fromInputDriverUpdated);
        toInput = view.findViewById(R.id.toDriverInputUpdated);
        passengerInput = view.findViewById(R.id.passengerDriverInputUpdated);
        Button updateButton = view.findViewById(R.id.actualUpdateButton);

        // Ensure we have a request ID to work with
        if (requestId == null) {
            Toast.makeText(getContext(), "No request ID provided", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Reference the specific ride request in Firebase
        dbRef = FirebaseDatabase.getInstance()
                .getReference("rideRequests")
                .child(requestId);

        // Load existing ride data once
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            /**
             * Called when data at this location is updated.
             */
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

            /**
             * Called if the read is cancelled or fails.
             */
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load ride info", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle update button click
        updateButton.setOnClickListener(v -> {
            String updatedDate = dateInput.getText().toString().trim();
            String updatedFrom = fromInput.getText().toString().trim();
            String updatedTo = toInput.getText().toString().trim();
            String updatedPassengers = passengerInput.getText().toString().trim();

            // Validate inputs
            if (updatedDate.isEmpty() ||
                    updatedFrom.isEmpty() ||
                    updatedTo.isEmpty() ||
                    updatedPassengers.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Write updates to Firebase
            dbRef.child("date").setValue(updatedDate);
            dbRef.child("from").setValue(updatedFrom);
            dbRef.child("to").setValue(updatedTo);
            dbRef.child("passengers").setValue(updatedPassengers);

            Toast.makeText(getContext(), "Ride updated successfully!", Toast.LENGTH_SHORT).show();

            // Prepare updated RideRequest and navigate back
            RideRequest updatedRideRequest = new RideRequest(
                    updatedDate,
                    updatedFrom,
                    updatedTo,
                    updatedPassengers);
            updatedRideRequest.status = "unaccepted";

            RideWaitForDriverFragment rideWaitFragment = RideWaitForDriverFragment.newInstance(requestId,
                    updatedRideRequest);

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, rideWaitFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}