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
 * A Fragment that allows the driver to update an existing {@link DriverOffer}.
 * 
 * Retrieves the offer ID from arguments, loads the current offer data from
 * Firebase Realtime Database, populates input fields, and writes updates back
 * to the database when the user confirms.
 * 
 */
public class ActualUpdateOfferFragment extends Fragment {

    /** The key of the offer to be updated in Firebase. */
    private String offerId;

    /** Input field for the "from" location. */
    private EditText fromInput;

    /** Input field for the "to" location. */
    private EditText toInput;

    /** Input field for the number of passengers. */
    private EditText passengerInput;

    /** Input field for the date of the offer. */
    private EditText dateInput;

    /** Reference to the specific driveOffers node in Firebase. */
    private DatabaseReference dbRef;

    /**
     * Required empty public constructor.
     */
    public ActualUpdateOfferFragment() {
        // Required empty public constructor
    }

    /**
     * Called to do initial creation of the fragment.
     * 
     * Extracts the {@code offerId} from the fragment arguments Bundle.
     * 
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the offer ID passed in arguments
        if (getArguments() != null) {
            offerId = getArguments().getString("offerId");
        }
    }

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     * 
     * - Inflates the layout
     * - Initializes input fields and the "Update" button
     * - Loads existing offer data from Firebase
     * - Sets up the update button to write changes back to Firebase
     * and navigate back to {@link DriveWaitforRiderFragment}.
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
        View view = inflater.inflate(R.layout.fragment_actual_update_offer, container, false);

        // Initialize UI elements
        fromInput = view.findViewById(R.id.fromInputDriver);
        toInput = view.findViewById(R.id.toDriverInput);
        passengerInput = view.findViewById(R.id.passengerDriverInput);
        dateInput = view.findViewById(R.id.dateInputDriver);
        Button updateButton = view.findViewById(R.id.updateOfferButtonFinal);

        // Ensure we have an offer ID to work with
        if (offerId == null) {
            Toast.makeText(getContext(), "No offer ID provided", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Reference the specific offer in Firebase
        dbRef = FirebaseDatabase.getInstance()
                .getReference("driveOffers")
                .child(offerId);

        // Load existing offer data once
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            /**
             * Called when data at this location is updated.
             */
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DriverOffer offer = snapshot.getValue(DriverOffer.class);
                if (offer != null) {
                    fromInput.setText(offer.from);
                    toInput.setText(offer.to);
                    passengerInput.setText(offer.passengers);
                    dateInput.setText(offer.date);
                }
            }

            /**
             * Called if the read is cancelled or fails.
             */
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load ride data", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle update button click
        updateButton.setOnClickListener(v -> {
            String updatedFrom = fromInput.getText().toString().trim();
            String updatedTo = toInput.getText().toString().trim();
            String updatedPassengers = passengerInput.getText().toString().trim();
            String updatedDate = dateInput.getText().toString().trim();

            // Validate inputs
            if (updatedFrom.isEmpty() ||
                    updatedTo.isEmpty() ||
                    updatedPassengers.isEmpty() ||
                    updatedDate.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Write updates to Firebase
            dbRef.child("from").setValue(updatedFrom);
            dbRef.child("to").setValue(updatedTo);
            dbRef.child("passengers").setValue(updatedPassengers);
            dbRef.child("date").setValue(updatedDate);

            Toast.makeText(getContext(), "Ride updated successfully!", Toast.LENGTH_SHORT).show();

            // Navigate back to the waiting fragment with updated data
            DriveWaitforRiderFragment driveWaitFragment = DriveWaitforRiderFragment.newInstance(
                    offerId,
                    new DriverOffer(
                            updatedDate,
                            updatedFrom,
                            updatedTo,
                            updatedPassengers,
                            "unaccepted"));

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, driveWaitFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}