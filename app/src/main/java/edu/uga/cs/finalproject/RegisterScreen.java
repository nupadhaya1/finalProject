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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterScreen extends AppCompatActivity {




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

        // User input
        EditText firstName = findViewById(R.id.firstNameInput);
        EditText lastName = findViewById(R.id.lastNameInput);
        EditText dateOfBirth = findViewById(R.id.dateOfBirthInput);
        EditText phone = findViewById(R.id.phoneNumberInput);
        EditText ugaID = findViewById(R.id.ugaIDInput);
        EditText major = findViewById(R.id.MajorInput);
        EditText emailInput = findViewById(R.id.emailInput);
        EditText usernameInput = findViewById(R.id.usernameInput);
        EditText passwordFirst = findViewById(R.id.passwordInput);
        EditText passwordConfirm = findViewById(R.id.passwordConfirmInput);


        // button lister for register
        Button registerButton = findViewById(R.id.registerEscape);
        registerButton.setOnClickListener(v -> {

            // check the user input if it is valid Probably add firebase logic for passwords HERE ****************************

            String first = firstName.getText().toString().trim();
            String last = lastName.getText().toString().trim();
            String dob = dateOfBirth.getText().toString().trim();
            String phoneNumber = phone.getText().toString().trim();
            String ugaIDNum = ugaID.getText().toString().trim();
            String majorInput = major.getText().toString().trim();
            String emailString = emailInput.getText().toString().trim();
            String user = usernameInput.getText().toString().trim();
            String pass = passwordFirst.getText().toString().trim();
            String confirm = passwordConfirm.getText().toString().trim();


            // Check that birthdate is valid and not in the future
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            sdf.setLenient(false); // Strict format checking

            // check date of birth
            try {
                Date birthDate = sdf.parse(dob);
                Date today = new Date();

                if (birthDate.after(today)) {
                    Toast.makeText(this, "Birthdate cannot be in the future", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (ParseException e) {
                Toast.makeText(this, "Please enter a valid birthdate (MM/DD/YY)", Toast.LENGTH_SHORT).show();
                return;
            } // checl date of birth


            // Check for empty fields
            if (first.isEmpty() || last.isEmpty() || dob.isEmpty() || phoneNumber.isEmpty() ||
                    ugaIDNum.isEmpty() || majorInput.isEmpty() ||
                    emailString.isEmpty() || user.isEmpty() ||
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
            }  // if statement

            // Check password length
            if (pass.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            } // if statement

            // check the date format




            Intent intent = new Intent(RegisterScreen.this, LogInScreen.class);
            startActivity(intent);
            Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show();
            finish();

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
