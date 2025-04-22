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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DriverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DriverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DriverFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DriverFragment newInstance(String param1, String param2) {
        DriverFragment fragment = new DriverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver, container, false);

        EditText fromInput = view.findViewById(R.id.fromInputDriver);
        EditText toInput = view.findViewById(R.id.toDriverInput);
        EditText passengerInput = view.findViewById(R.id.passengerDriverInput);

        String currentDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());

        Button offerRideButton = view.findViewById(R.id.updateOfferButtonFinal);
        offerRideButton.setOnClickListener(v -> {
            String from = fromInput.getText().toString().trim();
            String to = toInput.getText().toString().trim();
            String passengers = passengerInput.getText().toString().trim();

            if (from.isEmpty() || to.isEmpty() || passengers.isEmpty()) {
                Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            RideRequest rideRequest = new RideRequest(currentDate, from, to, passengers);
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("driveOffers"); // <- Use different node if needed

            String rideId = dbRef.push().getKey();
            if (rideId != null) {
                dbRef.child(rideId).setValue(rideRequest)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Ride offer posted!", Toast.LENGTH_SHORT).show();

                            // Navigate to waiting screen with rideId
                            getParentFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragmentContainerView, DriveWaitforRiderFragment.newInstance(rideId, rideRequest))
                                    .addToBackStack(null)
                                    .commit();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), "Failed to post ride offer", Toast.LENGTH_SHORT).show());
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

        Button updateoffer = view.findViewById(R.id.updateOfferButton);
        updateoffer.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView, new UpdateDriverOffer())
                    .addToBackStack(null)
                    .commit();
        });


        return view;
    }

}