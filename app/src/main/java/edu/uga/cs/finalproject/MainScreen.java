/**
 * Main activity that hosts the bottom navigation and manages fragment transactions.
 * 
 * This activity enables edge-to-edge rendering, configures the toolbar,
 * initializes the default fragment, and handles navigation across
 * Ride, Driver, Profile, History, and About sections. It also manages
 * cancellation of pending ride/offer requests and provides a logout option.
 * 
 */
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

    /**
     * Flag indicating if the user is in a waiting state for a ride/offer.
     */
    public static boolean isInWaitingFragment = false;

    /**
     * Reference to the current ride or offer in the database, used for
     * cancellation.
     */
    public static DatabaseReference currentWaitingRef = null;

    /**
     * Called when the activity is first created.
     * 
     * Enables edge-to-edge rendering, sets up the toolbar, loads the default
     * fragment
     * on first creation, and configures bottom navigation with cancellation
     * handling.
     * 
     *
     * @param savedInstanceState If non-null, the activity is being re-initialized
     *                           from a previous state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_screen);

        // Configure toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Load default fragment only on first creation
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, new RideFragment())
                    .commit();
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setOnItemSelectedListener(item -> {
            if (isInWaitingFragment && currentWaitingRef != null) {
                // Prompt before leaving a pending request
                new AlertDialog.Builder(this)
                        .setTitle("Cancel current request?")
                        .setMessage("You have an ongoing request. Leaving now will cancel it. Are you sure?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            currentWaitingRef.removeValue();
                            isInWaitingFragment = false;
                            currentWaitingRef = null;
                            navigateToFragment(item.getItemId());
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
                return false;
            } else {
                navigateToFragment(item.getItemId());
                return true;
            }
        });
    }

    /**
     * Replaces the container with the fragment corresponding to the selected menu
     * item.
     *
     * @param itemId The ID of the selected bottom navigation menu item.
     */
    private void navigateToFragment(int itemId) {
        Fragment selectedFragment = null;
        // check to see which fragment is the latest
        switch (itemId) {
            case R.id.nav_driver:
                selectedFragment = new RideFragment();
                break;
            case R.id.nav_customer:
                selectedFragment = new DriverFragment();
                break;
            case R.id.nav_profile:
                selectedFragment = new ProfileFragment();
                break;
            case R.id.nav_chart:
                selectedFragment = new HistoryFragment();
                break;
            case R.id.nav_about:
                selectedFragment = new AboutFragment();
                break;
        } // end of if else
          // if fragment is null
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, selectedFragment)
                    .commit();
        } // if statement
    } // navigateToFragment

    /**
     * Inflates the options menu, adding logout action.
     *
     * @param menu The options menu in which items are placed.
     * @return true for the menu to be displayed; false otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    } // onCreateOptionsMenu

    /**
     * Handles item selections from the options menu.
     * 
     * Specifically handles the logout action, signing out from Firebase
     * and redirecting to the login screen.
     * 
     *
     * @param item The selected menu item.
     * @return true if the event was handled; otherwise, calls superclass
     *         implementation.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // log out logic
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LogInScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        } // if statement

        // return
        return super.onOptionsItemSelected(item);
    } // onOptionsItemSelected
}