package com.example.zenithchance;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

/**
 * This is a class that uses the User class/subclasses, to display profile information.
 * TO DO: Connect to delete user function, connect with Sabrina's User class
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

    User myUser;
    ArrayList notificationList;


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
        myUser.setUserId("123");
        myUser.setDeviceId("456");
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
        usernameDisplay.setText(myUser.getUserId());// broke here
        emailDisplay = findViewById(R.id.emailText);
        emailDisplay.setText(myUser.getEmail());

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
                                                        myUser.setUserId(String.valueOf(editUsername.getText()));
                                                        usernameDisplay.setText(myUser.getUserId());
                                                        myUser.setEmail(String.valueOf(editEmail.getText()));
                                                        emailDisplay.setText(myUser.getEmail());
                                                        editInformation.setVisibility(GONE);
                                                    }
                                                });
                                            }

                                        }


        );


        deleteProfile.setOnClickListener(new View.OnClickListener() {
            /**
             * This delete button deletes the User from the database using UserManager.
             * @param v
             * This is the view
             */
            @Override
            public void onClick(View v) {
                UserManager.getInstance().deleteUser(myUser); // This might break things, need to test
            }
        });

        // This sends you to the NotificationActivity page!

        // Initialize notification list if null
        if (notificationList == null) {
            notificationList = new ArrayList<>();
        }

// Launcher for result
        ActivityResultLauncher<Intent> notificationActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> updatedList = result.getData().getStringArrayListExtra("notificationList");
                        if (updatedList != null) {
                            notificationList = updatedList;
                        }
                    }
                }
        );
        // Correct listener type here!
        notificationPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, NotificationsActivity.class);
                intent.putStringArrayListExtra("notificationList", notificationList);
                notificationActivityLauncher.launch(intent);
            }
        });

    }}