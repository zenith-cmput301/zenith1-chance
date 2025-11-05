package com.example.zenithchance.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.zenithchance.R;
import com.example.zenithchance.activities.NotificationsActivity;
import com.example.zenithchance.managers.UserManager;
import com.example.zenithchance.models.User;


// WORK IN PROGRESS :
// Implement Correct Delete functionality so that user is delted from firebase
// Fetch and Display username from firebase correctly
// Edit and update in firebase correctly
public class ProfileFragment extends Fragment {

    User myUser;

    TextView usernameDisplay, emailDisplay;
    Button editProfile, confirmEdits, deleteProfile;
    ConstraintLayout editInformation;
    EditText editUsername, editEmail;
    ImageButton notificationPage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Get current user
        myUser = UserManager.getInstance().getCurrentUser();

        // Initialize UI elements
        usernameDisplay = view.findViewById(R.id.usernameText);
        emailDisplay = view.findViewById(R.id.emailText);
        deleteProfile = view.findViewById(R.id.deleteProfileButton);
        editProfile = view.findViewById(R.id.editProfileButton);
        confirmEdits = view.findViewById(R.id.confirm_button);
        notificationPage = view.findViewById(R.id.notificationButton);

        editInformation = view.findViewById(R.id.editInformationConstraint);
        editUsername = view.findViewById(R.id.editTextUsername);
        editEmail = view.findViewById(R.id.editTextEmail);

        // Load profile info
        if (myUser != null) {
            usernameDisplay.setText(myUser.getUserId());
            emailDisplay.setText(myUser.getEmail());
        }

        // Edit profile
        editProfile.setOnClickListener(v -> editInformation.setVisibility(VISIBLE));

        confirmEdits.setOnClickListener(v -> {
            myUser.setUserId(editUsername.getText().toString());
            usernameDisplay.setText(myUser.getUserId());

            myUser.setEmail(editEmail.getText().toString());
            emailDisplay.setText(myUser.getEmail());

            editInformation.setVisibility(GONE);
        });

        // Confirm Edits
        confirmEdits.setOnClickListener(v -> {
            myUser.setUserId(editUsername.getText().toString());
            myUser.setEmail(editEmail.getText().toString());

            UserManager.getInstance().updateUserName(myUser);
            UserManager.getInstance().updateUserEmail(myUser);

            usernameDisplay.setText(myUser.getUserId());
            emailDisplay.setText(myUser.getEmail());

            editInformation.setVisibility(View.GONE);
        });

        // Delete profile
        deleteProfile.setOnClickListener(v -> {
            UserManager.getInstance().deleteUser(myUser);
            // TODO: redirect to sign-in screen
        });

        // Notifications
        notificationPage.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotificationsActivity.class);
            startActivity(intent);
        });



        return view;
    }
}
