package com.example.zenithchance;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {

    private Button signInWithDeviceButton;
    private TextView goToSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sign_in), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // set the ids for the "redirect to sign up button" and the "sign in with device id button"
        signInWithDeviceButton = findViewById(R.id.redirectToSignUpButton);
        goToSignUpButton = findViewById(R.id.directToSignUpPage);

        signInWithDeviceButton.setOnClickListener(v -> signInWithDeviceId());
        goToSignUpButton.setOnClickListener(v -> goToSignUp());

    }

    private void signInWithDeviceId() {
        signInWithDeviceButton.setEnabled(false);
        String deviceId = getAndroidDeviceId();
        if (deviceId == null) { signInWithDeviceButton.setEnabled(true); return; }

        FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("deviceId", deviceId)
                .limit(1).get()
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    signInWithDeviceButton.setEnabled(true);
                })
                .addOnSuccessListener(snap -> {
                    if (!snap.isEmpty()) {
                        DocumentSnapshot doc = snap.getDocuments().get(0);
                        String type = doc.getString("type");
                        String name = doc.getString("name");
                        String email = doc.getString("email");

                        // success message, redirect to home page
                        Toast.makeText(this, "Welcome back" + (name != null ? ", " + name : "") + "!", Toast.LENGTH_SHORT).show();

                    } else {
                        // if there is no account in Firebase with the current device id, show error message and encourage to
                        // redirect to the sign up screen.
                        Toast.makeText(this, "No account found. Please sign up first.", Toast.LENGTH_SHORT).show();
                        goToSignUp();

                    }
                });

    }

    private void goToSignUp() {

        Intent signUpScreen = new Intent(this, SignUpActivity.class);
        startActivity(signUpScreen);
    }

    private String getAndroidDeviceId() {
        return "device_id";
        /*
        // get the current device id
        try {
            return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        } catch (Exception e) {
            return null;
        }
    }
    */
    }


}
