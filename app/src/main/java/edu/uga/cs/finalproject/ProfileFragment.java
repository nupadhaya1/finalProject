package edu.uga.cs.finalproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int totalPoints = 0;


    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
            // create a view
            View view = inflater.inflate(R.layout.fragment_profile, container, false);

            // initialize the text
            TextView userNameText = view.findViewById(R.id.userNameText);
            TextView userEmailText = view.findViewById(R.id.userEmailText);
            TextView userPointsText = view.findViewById(R.id.userPoints);

            // get the user
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                // Set email
                userEmailText.setText("Email: " + user.getEmail());

                // Set display name (if available)
                String name = user.getDisplayName();
                if (name != null && !name.isEmpty()) {
                    userNameText.setText("Name: " + name);
                } else {
                    userNameText.setText("Name not set");
                }
            }

            // Initialize points
            int totalPoints = 0;

            // get the database
            DatabaseReference rideRequestsRef = FirebaseDatabase.getInstance().getReference("rideRequests");

            rideRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int points = 0;

                    // for loop for points system
                    for (DataSnapshot rideSnap : snapshot.getChildren()) {
                        RideRequest request = rideSnap.getValue(RideRequest.class);

                        Boolean driverConfirmed = rideSnap.child("confirmation").child("driver").getValue(Boolean.class);
                        Boolean riderConfirmed = rideSnap.child("confirmation").child("rider").getValue(Boolean.class);

                         // if the ride was both confirm increment points
                        if (request != null && Boolean.TRUE.equals(driverConfirmed) && Boolean.TRUE.equals(riderConfirmed)) {
                            try {
                                int passengers = Integer.parseInt(request.passengers);
                                points += passengers;
                            } catch (NumberFormatException e) {
                                // Ignore
                            } // try catch
                        } // if statement
                    } // for loop

                    // After calculation, update the text
                    userPointsText.setText("Total Points: " + points);
                } // onDataChange

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load points", Toast.LENGTH_SHORT).show();
                } // on cancelled
            });

            return view;
        } // onCreateView


    } // ProfileFragment