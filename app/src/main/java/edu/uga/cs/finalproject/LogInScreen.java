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

public class LogInScreen extends AppCompatActivity {

    public static final String TAG = "finalProject";
    private FirebaseAuth mAuth;
    Button loginButton;
    Button registerButton;
    EditText usernameInput;
    EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        usernameInput = findViewById(R.id.usernameEdit);
        passwordInput = findViewById(R.id.passwordEdit);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // Set up button listeners
        loginButton.setOnClickListener(v -> {
            String email = usernameInput.getText().toString().trim();
            String passwordText = passwordInput.getText().toString().trim();

            // COMMENTING OUT LOGIN FOR EASIER DEV/TESTING
             if (email.isEmpty() || passwordText.isEmpty()) {
             Toast.makeText(this, "Please enter both email and password",
             Toast.LENGTH_SHORT).show();
             return;
             }

            // // Sign in with email and password
             signIn(email, passwordText);

            // Navigate to MainScreen
//            Intent intent = new Intent(LogInScreen.this, MainScreen.class);
//            startActivity(intent);
//            finish();
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterScreen.class);
            startActivity(intent);
            finish();
        });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "signInWithEmail:success");
                            // FirebaseUser user = mAuth.getCurrentUser(); // You can access the user here
                            // if needed

                            // Navigate to MainScreen
                            Intent intent = new Intent(LogInScreen.this, MainScreen.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Sign in failed
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LogInScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
