package com.example.zenithchance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
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

import com.example.zenithchance.AdminMainActivity;
import com.example.zenithchance.EntrantMainActivity;
import com.example.zenithchance.models.Admin;
import com.example.zenithchance.models.Entrant;
import com.example.zenithchance.models.Organizer;
import com.example.zenithchance.R;
import com.example.zenithchance.models.User;
import com.example.zenithchance.managers.UserManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

/**
 * This class is the view that helps users log in.
 * It checks if the device id is available for any users in the Firebase "users" collection.
 * If yes, it gets the type of user (admin, entrant, organizer) and directs them to the correct screen.
 * Otherwise, ask to create an account with the sign up button.
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
        String email = emailField.getText() == null ? "" : emailField.getText().toString().trim();
        String roleDisplay = (String) userRoles.getSelectedItem();
        String role = roleDisplay == null ? "" : roleDisplay.toLowerCase(Locale.US);

        if (name.isEmpty()) {
            nameField.setError("Name is required");
            nameField.requestFocus();
            signUpButton.setEnabled(true);
            return;
        }
        if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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

        // Check if a user already exists for this device. If yes, sign them in directly.
        FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("deviceId", deviceId)
                .limit(1)
                .get()
                .addOnSuccessListener(snap -> {
                    if (!snap.isEmpty()) {
                        Toast.makeText(this, "An account for this device already exists. Signing you in.", Toast.LENGTH_SHORT).show();
                        signInWithDeviceId();
                    } else {
                        createUser(name, email, deviceId, role);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Could not verify device: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        // Build the right subclass
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

        // persist to Firestore, then set current user and route
        FirebaseFirestore.getInstance()
                .collection("users")
                .add(user)
                .addOnSuccessListener(docRef -> {
                    user.setUserId(docRef.getId());
                    UserManager.getInstance().setCurrentUser(user);
                    Toast.makeText(this, "Account created: " + role, Toast.LENGTH_SHORT).show();
                    routeToHomeByType(role);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to create account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    signUpButton.setEnabled(true);
                });
    }

    /**
     * This gets the device id.
     * @return
     * Returns the device id.
     */
    private String getAndroidDeviceId() {
        try {
            return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            return null;
        }
    }

    private void signInWithDeviceId() {
        String deviceId = getAndroidDeviceId();
        if (deviceId == null || deviceId.isEmpty()) {
            Toast.makeText(this, "No device ID available.", Toast.LENGTH_SHORT).show();
            signUpButton.setEnabled(true);
            return;
        }

        // check for a user with the device id
        FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("deviceId", deviceId)
                .limit(1)
                .get()
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(snap -> {
                    if (!snap.isEmpty()) {
                        DocumentSnapshot doc = snap.getDocuments().get(0);
                        String type = doc.getString("type");

                        User user = null;
                        if ("entrant".equals(type)) user = doc.toObject(Entrant.class);
                        else if ("organizer".equals(type)) user = doc.toObject(Organizer.class);
                        else if ("admin".equals(type)) user = doc.toObject(Admin.class);

                        if (user != null) {
                            user.setUserId(doc.getId());
                            UserManager.getInstance().setCurrentUser(user);
                        }

                        String name = doc.getString("name");
                        Toast.makeText(this, "Welcome back" + (name != null ? ", " + name : "") + "!", Toast.LENGTH_SHORT).show();

                        routeToHomeByType(type);
                        finish();
                    } else {
                        // No account yet; stay on sign-up screen
                        Toast.makeText(this, "No account found for this device. Please sign up.", Toast.LENGTH_SHORT).show();
                        signUpButton.setEnabled(true);
                    }
                });
    }

    private void routeToHomeByType(String type) {
        if ("entrant".equalsIgnoreCase(type)) {
            startActivity(new Intent(this, EntrantMainActivity.class));
        } else if ("organizer".equalsIgnoreCase(type)) {
            startActivity(new Intent(this, OrganizerEventsActivity.class));
        } else if ("admin".equalsIgnoreCase(type)) {
            startActivity(new Intent(this, AdminMainActivity.class));
        } else {
            // if type is unknown
            startActivity(new Intent(this, EntrantMainActivity.class));
        }
    }

}
