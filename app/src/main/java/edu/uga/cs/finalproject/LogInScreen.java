/**
 * Activity that handles user login using Firebase Authentication.
 *
 * This activity collects the user's email and password,
 * performs basic input validation, signs in via Firebase,
 * and navigates to the main screen upon successful authentication.
 * Edge-to-edge UI support is enabled for system bars.
 */
package edu.uga.cs.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Activity for user login.
 *
 * Validates email and password input, authenticates with FirebaseAuth,
 * and launches MainScreen on success or shows error messages on failure.
 */
public class LogInScreen extends AppCompatActivity {

    /**
     * Tag for logging.
     */
    public static final String TAG = "finalProject";

    /**
     * Firebase Authentication instance.
     */
    private FirebaseAuth mAuth;

    /**
     * Button to trigger login.
     */
    private Button loginButton;

    /**
     * Button to navigate to registration.
     */
    private Button registerButton;

    /**
     * EditText for email input.
     */
    private EditText usernameInput;

    /**
     * EditText for password input.
     */
    private EditText passwordInput;

    /**
     * Called when the activity is first created.
     *
     * Enables edge-to-edge rendering, initializes FirebaseAuth,
     * sets up input fields and button listeners for login and registration.
     *
     * @param savedInstanceState If non-null, the activity is being re-initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in_screen);

        // Apply window insets to root view for proper padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        usernameInput = findViewById(R.id.usernameEdit);
        passwordInput = findViewById(R.id.passwordEdit);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // Set up login button listener
        loginButton.setOnClickListener(v -> {
            String email = usernameInput.getText().toString().trim();
            String passwordText = passwordInput.getText().toString().trim();

            if (email.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            signIn(email, passwordText);
        });

        // Set up register button listener
        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterScreen.class));
            finish();
        });
    }

    /**
     * Signs in the user with the provided email and password.
     *
     * On success, logs a message and launches MainScreen.
     * On failure, shows a Toast and logs the exception.
     *
     * @param email    User's email address.
     * @param password User's password.
     */
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            startActivity(new Intent(LogInScreen.this, MainScreen.class));
                            finish();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LogInScreen.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    } // SignIn
} // LoginScreen
