package edu.uga.cs.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Toast;

// Firebase imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import androidx.annotation.NonNull;

// Import FirebaseUser and UserProfileChangeRequest
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterScreen extends AppCompatActivity {

    // Firebase instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // User input
        EditText firstName = findViewById(R.id.firstNameInput);
        EditText lastName = findViewById(R.id.lastNameInput);
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordFirst = findViewById(R.id.passwordInput);
        EditText passwordConfirm = findViewById(R.id.passwordConfirmInput);

        // button lister for register
        Button registerButton = findViewById(R.id.registerEscape);
        registerButton.setOnClickListener(v -> {

            // check the user input if it is valid Probably add firebase logic for passwords
            // HERE ****************************

            String first = firstName.getText().toString().trim();
            String last = lastName.getText().toString().trim();
            String emailString = emailInput.getText().toString().trim();
            String pass = passwordFirst.getText().toString().trim();
            String confirm = passwordConfirm.getText().toString().trim();

            // Check for empty fields
            if (first.isEmpty() || last.isEmpty() || emailString.isEmpty() ||
                    pass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            } // if statement

            // Check that first and last names contain only letters
            if (!first.matches("[a-zA-Z]+") || !last.matches("[a-zA-Z]+")) {
                Toast.makeText(this, "Names must only contain letters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check email format // (does not check legit just format)
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return;
            } // if statement

            // Check if passwords match
            if (!pass.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            } // if statement

            // Check password length
            if (pass.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            } // if statement

            // [START create user with email]
            mAuth.createUserWithEmailAndPassword(emailString, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(RegisterScreen.this, "Registration Successful.",
                                        Toast.LENGTH_SHORT).show();

                                // Get the current user
                                FirebaseUser user = mAuth.getCurrentUser();

                                // Create a UserProfileChangeRequest to update the user's display name
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(first + " " + last) // Combine first and last name
                                        .build();

                                // Update the user's profile
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Profile updated successfully
                                                    Toast.makeText(RegisterScreen.this, "Profile updated.",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Failed to update profile
                                                    Toast.makeText(RegisterScreen.this, "Failed to update profile.",
                                                            Toast.LENGTH_SHORT).show();
                                                }

                                                // Redirect to Login screen after profile update
                                                Intent intent = new Intent(RegisterScreen.this, LogInScreen.class);
                                                startActivity(intent);
                                                finish(); // Close the register activity
                                            }
                                        });

                            } else {
                                // If sign in fails, display a message to the user.
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthException e) {
                                    // Handle specific Firebase Authentication errors
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
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(RegisterScreen.this, "Registration failed: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
            // [END create user with email]

        }); // register listener

        // button lister for login back
        Button loginBack = findViewById(R.id.loginBackButton);
        loginBack.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterScreen.this, LogInScreen.class);
            startActivity(intent);
            Toast.makeText(this, "Login!", Toast.LENGTH_SHORT).show();
            finish();

        }); // register listener

    } // onCreate

} // RegisterScreen
