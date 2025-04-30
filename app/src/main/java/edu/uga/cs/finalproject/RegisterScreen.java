/**
 * Activity that handles user registration using Firebase Authentication.
 * 
 * This activity collects the user's first name, last name, email, and password,
 * performs validation on input fields, creates a new Firebase Auth user,
 * updates the user's display name, and redirects to the login screen.
 * Edge-to-edge UI support is enabled for system bars.
 * 
 */
package edu.uga.cs.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import androidx.annotation.NonNull;

public class RegisterScreen extends AppCompatActivity {

    /**
     * Firebase Authentication instance for user registration.
     */
    private FirebaseAuth mAuth;

    /**
     * Called when the activity is first created.
     * Sets up the UI, enables edge-to-edge rendering, and initializes
     * FirebaseAuth. Handles registration and login-back button clicks.
     *
     * @param savedInstanceState If non-null, this activity is being re-initialized
     *                           from a previous saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_screen);

        // Apply window insets to root view for proper padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // User input fields
        EditText firstName = findViewById(R.id.firstNameInput);
        EditText lastName = findViewById(R.id.lastNameInput);
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordFirst = findViewById(R.id.passwordInput);
        EditText passwordConfirm = findViewById(R.id.passwordConfirmInput);

        // Registration button listener
        Button registerButton = findViewById(R.id.registerEscape);
        registerButton.setOnClickListener(v -> {
            // Retrieve and validate input
            String first = firstName.getText().toString().trim();
            String last = lastName.getText().toString().trim();
            String emailString = emailInput.getText().toString().trim();
            String pass = passwordFirst.getText().toString().trim();
            String confirm = passwordConfirm.getText().toString().trim();

            if (first.isEmpty() || last.isEmpty() || emailString.isEmpty() ||
                    pass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!first.matches("[a-zA-Z]+") || !last.matches("[a-zA-Z]+")) {
                Toast.makeText(this, "Names must only contain letters", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pass.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create new user with FirebaseAuth
            mAuth.createUserWithEmailAndPassword(emailString, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterScreen.this, "Registration Successful.",
                                        Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();

                                // Update user's display name
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(first + " " + last)
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(RegisterScreen.this, "Profile updated.",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(RegisterScreen.this, "Failed to update profile.",
                                                            Toast.LENGTH_SHORT).show();
                                                }

                                                // Redirect to login screen
                                                Intent intent = new Intent(RegisterScreen.this, LogInScreen.class);
                                                startActivity(intent);
                                                finish();
                                            } // onComplete
                                        });
                            } else {
                                // Handle registration failure
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthException e) {
                                    String errorCode = e.getErrorCode();
                                    if ("ERROR_EMAIL_ALREADY_IN_USE".equals(errorCode)) {
                                        Toast.makeText(RegisterScreen.this, "Email address is already in use.",
                                                Toast.LENGTH_SHORT).show();
                                    } else if ("ERROR_WEAK_PASSWORD".equals(errorCode)) {
                                        Toast.makeText(RegisterScreen.this, "The password is too weak.",
                                                Toast.LENGTH_SHORT).show();
                                    } else if ("ERROR_INVALID_EMAIL".equals(errorCode)) {
                                        Toast.makeText(RegisterScreen.this, "The email address is invalid.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(RegisterScreen.this, "Registration failed: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    } // end of if else
                                } catch (Exception e) {
                                    Toast.makeText(RegisterScreen.this, "Registration failed: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                } // end of try catch
                            } // end of if else
                        } // end of on Complete
                    });
        });

        // Login-back button listener
        Button loginBack = findViewById(R.id.loginBackButton);
        loginBack.setOnClickListener(v -> {
            startActivity(new Intent(RegisterScreen.this, LogInScreen.class));
            Toast.makeText(this, "Login!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
