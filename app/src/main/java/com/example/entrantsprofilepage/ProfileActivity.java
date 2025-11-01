package com.example.entrantsprofilepage;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * This is a class that uses the User class/subclasses, to display profile information.
 * TO DO: Connect to delete user function, connect with Sabrinas User class
 */
public class ProfileActivity extends AppCompatActivity {
    Button deleteProfile;
    Button editProfile;
    ImageButton notificationPage;
    TextView allEventsNavigationBar; // This text view is clickable
    TextView myEventsNavigationBar; // This text view is clickable
    TextView profileNavigationBar; // This text view is clickable
    TextView usernameDisplay;
    TextView emailDisplay;
    Button confirmEdits;
    ConstraintLayout editInformation;
    EditText editUsername;
    EditText editEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initializing Buttons:
        deleteProfile = findViewById(R.id.deleteProfileButton);
        editProfile = findViewById(R.id.editProfileButton);
        notificationPage = findViewById(R.id.notificationButton);
        allEventsNavigationBar = findViewById(R.id.allEventsBar);
        myEventsNavigationBar = findViewById(R.id.myEventsBar);
        profileNavigationBar = findViewById(R.id.profileBar);
        confirmEdits = findViewById(R.id.confirm_button);

        // Initializing Information Text:
        usernameDisplay = findViewById(R.id.usernameText);
        usernameDisplay.setText(User.getUserID());// broke here
        emailDisplay = findViewById(R.id.emailText);
        emailDisplay.setText(User.getEmail());

        // Initializing Edit Text Functions:
        editInformation = findViewById(R.id.editInformationConstraint);
        editUsername = findViewById(R.id.editTextUsername);
        editEmail = findViewById(R.id.editTextEmail);


        // Setting up the Edit Profile Button
        editProfile.setOnClickListener( new View.OnClickListener() {
            /**
             * This button pops up the edit text boxes to edit name and email
             * @param v
             * This is the view
             */
            @Override
            public void onClick(View v) {
                editInformation.setVisibility(VISIBLE);
                confirmEdits.setOnClickListener(new View.OnClickListener() {
                    /**
                     * This confirm button takes the edited text and updates it in the user class
                     * @param v
                     * This is the view
                     */
                    @Override
                    public void onClick(View v) {
                        User.setUserID(String.valueOf(editUsername.getText()));
                        usernameDisplay.setText(User.getUserID());
                        User.setEmail(String.valueOf(editEmail.getText()));
                        emailDisplay.setText(User.getEmail());
                        editInformation.setVisibility(GONE);
                    }
                });
            }

            }


        );





    }
}