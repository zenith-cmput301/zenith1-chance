package com.example.zenithchance.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.zenithchance.R;
import com.example.zenithchance.activities.NotificationsActivity;
import com.example.zenithchance.activities.SignInActivity;
import com.example.zenithchance.activities.SignUpActivity;
import com.example.zenithchance.managers.UserManager;
import com.example.zenithchance.models.User;

// TODO: Handle Exceptions: Invalid emails while editing
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
            usernameDisplay.setText(myUser.getName());
            emailDisplay.setText(myUser.getEmail());
        }

        // Edit profile
        editProfile.setOnClickListener(v -> {
            if (myUser != null) {
                editUsername.setText(myUser.getName());   // prefill with current name
                editEmail.setText(myUser.getEmail());     // prefill with current email
            }
            editInformation.setVisibility(VISIBLE);       // show the edit panel
        });


        // Confirm Edits
        confirmEdits.setOnClickListener(v -> {
            String newName = editUsername.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();

            // Check if user left both fields empty
            if (newName.isEmpty() && newEmail.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a name or email to update.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Only update name if not blank
            if (!newName.isEmpty()) {
                myUser.setName(newName);
                UserManager.getInstance().updateUserName(myUser);
            } else {
                editUsername.setText(myUser.getName());
            }

            // Only update email if not blank
            if (!newEmail.isEmpty()) {
                myUser.setEmail(newEmail);
                UserManager.getInstance().updateUserEmail(myUser);
            } else {
                editEmail.setText(myUser.getEmail());
            }

            // Refresh Display
            usernameDisplay.setText(myUser.getName());
            emailDisplay.setText(myUser.getEmail());

            //  Hide the edit panel
            editInformation.setVisibility(View.GONE);
        });

        // Delete profile
        deleteProfile.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Profile Warning!")
                    .setMessage("Are you sure you want to delete your profile?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Delete user and redirect to SignIn
                        UserManager.getInstance().deleteUserById(myUser.getUserId());
                        Intent intent = new Intent(getActivity(), SignUpActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // Dismiss dialog
                        dialog.dismiss();
                    })
                    .setCancelable(true)
                    .show();
        });


        // Notifications
        notificationPage.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotificationsActivity.class);
            startActivity(intent);
        });



        return view;
    }
}
