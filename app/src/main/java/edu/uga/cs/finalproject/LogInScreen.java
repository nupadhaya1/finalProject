package edu.uga.cs.finalproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LogInScreen extends AppCompatActivity {


    Button loginButton;
    Button registerButton;

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

        // initialize UI elements
        EditText usernameInput = findViewById(R.id.usernameEdit);
        EditText passwordInput = findViewById(R.id.passwordEdit);

        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);


        // Set up button listeners
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainScreen.class);
            startActivity(intent);
            finish();
        });
        // Set up button listeners
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterScreen.class);
            startActivity(intent);
            finish();
        });

    } // onCreate

}