/**
 * Fragment for drivers to create and post ride offers.
 *
 * Drivers can enter pickup and drop-off locations, number of passengers, and date.
 * After submission, the offer is stored in Firebase Realtime Database,
 * and the fragment navigates to a waiting screen for rider acceptance.
 */
package edu.uga.cs.finalproject;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

public class DriverFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    /**
     * Required empty public constructor.
     */
    public DriverFragment() {
    }

    /**
     * Factory method to create a new instance of DriverFragment.
     *
     * @param param1 Placeholder parameter.
     * @param param2 Placeholder parameter.
     * @return A new instance of fragment DriverFragment.
     */
    public static DriverFragment newInstance(String param1, String param2) {
        DriverFragment fragment = new DriverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        // return fragment
        return fragment;
    } // DriverFragment

    /**
     * Called when the fragment is first created.
     *
     * Retrieves and stores arguments if provided.
     *
     * @param savedInstanceState If non-null, fragment is being re-initialized.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * Inflates the fragment layout and initializes UI components.
     *
     * Autofills the current date, validates user inputs for from, to, passengers,
     * and date,
     * posts the offer to Firebase, and navigates to DriveWaitforRiderFragment.
     * Also sets up navigation to the accept ride requests screen.
     *
     * @param inflater           LayoutInflater to inflate views.
     * @param container          Parent view that the fragment attaches to.
     * @param savedInstanceState Previous saved state of the fragment.
     * @return The root View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver, container, false);

        EditText fromInput = view.findViewById(R.id.fromInputDriver);
        EditText toInput = view.findViewById(R.id.toDriverInput);
        EditText passengerInput = view.findViewById(R.id.passengerDriverInput);
        EditText dateInput = view.findViewById(R.id.dateInputDriver);

        String todayDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        dateInput.setText(todayDate);

        Button offerRideButton = view.findViewById(R.id.updateOfferButtonFinal);
        offerRideButton.setOnClickListener(v -> {
            String from = fromInput.getText().toString().trim();
            String to = toInput.getText().toString().trim();

            String passengersInput = passengerInput.getText().toString().trim();
            int passengersInt;
            try {
                passengersInt = Integer.parseInt(passengersInput);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Passengers must be a valid number!", Toast.LENGTH_SHORT).show();
                return;
            }
            String passengers = String.valueOf(passengersInt);
            String enteredDate = dateInput.getText().toString().trim();

            if (from.isEmpty() || to.isEmpty() || enteredDate.isEmpty()) {
                Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            DriverOffer offer = new DriverOffer(enteredDate, from, to, passengers);
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("driveOffers");

            String offerId = dbRef.push().getKey();
            if (offerId != null) {
                dbRef.child(offerId).setValue(offer)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Ride offer posted!", Toast.LENGTH_SHORT).show();
                            getParentFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragmentContainerView,
                                            DriveWaitforRiderFragment.newInstance(offerId, offer))
                                    .addToBackStack(null)
                                    .commit();
                        })
                        .addOnFailureListener(e -> Toast
                                .makeText(getContext(), "Failed to post ride offer", Toast.LENGTH_SHORT).show());
            }
        });

        Button acceptRideButton = view.findViewById(R.id.acceptRideButton);
        acceptRideButton.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, new DriverAcceptRequestFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
