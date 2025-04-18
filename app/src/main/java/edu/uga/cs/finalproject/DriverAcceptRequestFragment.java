package edu.uga.cs.finalproject;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverAcceptRequestFragment extends Fragment {

    public DriverAcceptRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_accept_request, container, false);

        TextView rideListTextView = view.findViewById(R.id.driverAcceptRequestList);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("rideRequests");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                StringBuilder rideList = new StringBuilder();

                for (DataSnapshot rideSnap : snapshot.getChildren()) {
                    RideRequest ride = rideSnap.getValue(RideRequest.class);
                    if (ride != null) {
                        rideList.append("Date: ").append(ride.date)
                                .append("\nFrom: ").append(ride.from)
                                .append("\nTo: ").append(ride.to)
                                .append("\nPassengers: ").append(ride.passengers)
                                .append("\n\n");
                    }
                }

                rideListTextView.setText(rideList.toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                rideListTextView.setText("Failed to load ride requests.");
            }
        });

        return view;
    }
}
