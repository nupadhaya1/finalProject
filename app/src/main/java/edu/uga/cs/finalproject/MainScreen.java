package edu.uga.cs.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class MainScreen extends AppCompatActivity {

    public static boolean isInWaitingFragment = false;
    public static DatabaseReference currentWaitingRef = null; // Reference to the ride/offer to cancel

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_screen);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Only load default fragment when the Activity is created for the FIRST time
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, new RideFragment())
                    .commit();
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        bottomNav.setOnItemSelectedListener(item -> {
            if (MainScreen.isInWaitingFragment && MainScreen.currentWaitingRef != null) {
                // User is in a waiting state, confirm before navigating
                new AlertDialog.Builder(this)
                        .setTitle("Cancel current request?")
                        .setMessage("You have an ongoing request. Leaving now will cancel it. Are you sure?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Cancel the request
                            MainScreen.currentWaitingRef.removeValue();
                            MainScreen.isInWaitingFragment = false;
                            MainScreen.currentWaitingRef = null;

                            // Now proceed to navigate
                            navigateToFragment(item.getItemId());
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();

                return false; // Don't navigate yet
            } else {
                navigateToFragment(item.getItemId());
                return true;
            }
        });
    } // onCreate

    // Helper method for navigating between fragments
    private void navigateToFragment(int itemId) {
        Fragment selectedFragment = null;

        if (itemId == R.id.nav_driver) {
            selectedFragment = new RideFragment();
        } else if (itemId == R.id.nav_customer) {
            selectedFragment = new DriverFragment();
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
        } else if (itemId == R.id.nav_chart) {
            selectedFragment = new HistoryFragment();
        } else if (itemId == R.id.nav_about) {
            selectedFragment = new AboutFragment();
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, selectedFragment)
                    .commit();
        }
    }

    // Inflate the logout menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Handle logout
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut(); // Logs the user out of Firebase
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LogInScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

} // MainScreen
