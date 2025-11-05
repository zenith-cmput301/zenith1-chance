package com.example.zenithchance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.zenithchance.models.Admin;
import com.example.zenithchance.models.Entrant;
import com.example.zenithchance.models.Organizer;
import com.example.zenithchance.R;
import com.example.zenithchance.models.User;
import com.example.zenithchance.managers.UserManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

/**
 * This class is the view that helps users log in.
 */
public class SignUpActivity extends AppCompatActivity {

    private Spinner userRoles;
    private EditText nameField, emailField;
    private Button signUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sign_up), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        userRoles = findViewById(R.id.roles);
        nameField = findViewById(R.id.nameTextField);
        emailField = findViewById(R.id.emailTextField);
        signUpButton = findViewById(R.id.signUpButton);

        // array of roles (Admin, entrant, organizer)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Entrant", "Organizer", "Admin"}
        );
        userRoles.setAdapter(adapter);

        signUpButton.setOnClickListener(v -> onSignUpButtonTap());

    }

    /**
     * This validates the fields that the user entered for signing in and prevents duplicate accounts for the same device id.
     */
    // The following function is from OpenAI, ChatGPT, "How to validate fields for User class?", 2025-11-2
    private void onSignUpButtonTap() {
        signUpButton.setEnabled(false);
        String name = nameField.getText() == null ? "" : nameField.getText().toString().trim();
        String email =  emailField.getText() == null ? "" : emailField.getText().toString().trim();
        String roleDisplay = (String) userRoles.getSelectedItem();
        String role = roleDisplay == null ? "" : roleDisplay.toLowerCase(Locale.US);

        if (name.isEmpty()) {
            nameField.setError("Name is required");
            nameField.requestFocus();
            signUpButton.setEnabled(true);
            return;
        }
        if (!email.isEmpty() &&
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Enter a valid email or leave blank");
            emailField.requestFocus();
            signUpButton.setEnabled(true);
            return;
        }
        if (!(role.equals("entrant") || role.equals("organizer") || role.equals("admin"))) {
            Toast.makeText(this, "Please pick a role", Toast.LENGTH_SHORT).show();
            signUpButton.setEnabled(true);
            return;
        }

        String deviceId = getAndroidDeviceId();
        if (deviceId == null || deviceId.isEmpty()) {
            Toast.makeText(this, "Unable to read device ID", Toast.LENGTH_SHORT).show();
            signUpButton.setEnabled(true);
            return;
        }

        // Prevent duplicate accounts for this device
        // Query Firestore to check for this device id, if it does not exist then create user
        FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("deviceId", deviceId)
                .limit(1)
                .get()
                .addOnSuccessListener(snap -> {
                    if (!snap.isEmpty()) {
                        Toast.makeText(this,
                                "An account for this device already exists. Signing you in.",
                                Toast.LENGTH_SHORT).show();
                        // Go back to sign-in (or straight to home if you prefer)
                        startActivity(new Intent(this, SignInActivity.class));
                        finish();
                    } else {
                        createUser(name, email, deviceId, role);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Could not verify device: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    signUpButton.setEnabled(true);
                });
    }

    /**
     * This creates a User object and adds to Firebase.
     * @param name
     * This is the name of the user
     * @param email
     * This is the email of the user
     * @param deviceId
     * This is the device id of the user
     * @param role
     * This is the role of the user (entrant, organizer, admin)
     */
    // The following function is from OpenAI, ChatGPT, "How to create a new user?", 2025-11-2
    private void createUser(String name, String email, String deviceId, String role) {
        User user;
        switch (role) {
            case "organizer": user = new Organizer(); break;
            case "admin": user = new Admin(); break;
            default: user = new Entrant();
        }
        user.setName(name);
        user.setEmail(email);
        user.setDeviceId(deviceId);
        user.setType(role);

        UserManager.getInstance().addUser(user);

        Toast.makeText(this, "Account created: " + role, Toast.LENGTH_SHORT).show();

        // After account is created, direct to default screen
    }

    /**
     * This gets the device id.
     * @return
     * Returns the device id.
     */
    private String getAndroidDeviceId() {

        return "device_id";

        // get the current device id
        // REFERENCE: https://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id
        /* try {
            String android_id = Settings.Secure.getString(getContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);



        } catch (Exception e) {
            return null;
        }
    } */
    }
}
