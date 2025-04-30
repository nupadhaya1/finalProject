/**
 * Fragment to display the user's profile information and accumulated ride points.
 * This fragment shows the current user's display name and email retrieved
 * from FirebaseAuth, and calculates total points based on completed ride
 * confirmations stored in Firebase Realtime Database.
 */
package edu.uga.cs.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass that displays user profile details.
 */
public class ProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    /**
     * Required empty public constructor.
     */
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of ProfileFragment.
     *
     * @param param1 First parameter (unused placeholder).
     * @param param2 Second parameter (unused placeholder).
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called to do initial creation of the fragment.
     * Retrieves any passed arguments.
     *
     * @param savedInstanceState If non-null, fragment is being re-constructed.
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
     * Called to have the fragment instantiate its user interface view.
     * Inflates the layout, displays user name and email, and computes total points
     * based on confirmed rides from Firebase Realtime Database.
     *
     * @param inflater           LayoutInflater to inflate views.
     * @param container          Parent view that the fragment's UI should attach
     *                           to.
     * @param savedInstanceState If non-null, fragment is being re-created.
     * @return The root View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // TextViews for displaying user info and points
        TextView userNameText = view.findViewById(R.id.userNameText);
        TextView userEmailText = view.findViewById(R.id.userEmailText);
        TextView userPointsText = view.findViewById(R.id.userPoints);

        // Get current Firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmailText.setText("Email: " + user.getEmail());
            String name = user.getDisplayName();
            userNameText.setText(name != null && !name.isEmpty()
                    ? "Name: " + name
                    : "Name not set");
        }

        // Reference to rideRequests in Firebase Database
        DatabaseReference rideRequestsRef = FirebaseDatabase
                .getInstance().getReference("rideRequests");

        // Listen once to compute total points
        rideRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int points = 0;
                for (DataSnapshot rideSnap : snapshot.getChildren()) {
                    RideRequest request = rideSnap.getValue(RideRequest.class);
                    Boolean driverConfirmed = rideSnap.child("confirmation")
                            .child("driver").getValue(Boolean.class);
                    Boolean riderConfirmed = rideSnap.child("confirmation")
                            .child("rider").getValue(Boolean.class);
                    if (request != null
                            && Boolean.TRUE.equals(driverConfirmed)
                            && Boolean.TRUE.equals(riderConfirmed)) {
                        try {
                            points += Integer.parseInt(request.passengers);
                        } catch (NumberFormatException e) {
                            // Ignore invalid number format
                        }
                    }
                }
                userPointsText.setText("Total Points: " + points);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load points", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
