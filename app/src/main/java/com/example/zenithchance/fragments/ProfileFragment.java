package com.example.zenithchance.fragments;

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
import com.example.zenithchance.activities.SignUpActivity;
import com.example.zenithchance.managers.UserManager;
import com.example.zenithchance.models.Event;
import com.example.zenithchance.models.User;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class for the UI used in Profile Page For ALl Users
 *
 * @author Lauren
 * @version 3.0
 */
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

        // Gets User Data
        if (myUser != null) {
            usernameDisplay.setText(myUser.getName());
            emailDisplay.setText(myUser.getEmail());
        }

        // Edit profile
        editProfile.setOnClickListener(v -> {
            if (myUser != null) {
                editUsername.setText(myUser.getName());   // current name
                editEmail.setText(myUser.getEmail());     // current email
            }
            editInformation.setVisibility(VISIBLE);       // Shows the edit panel
        });


        // Confirm Edits button, which will get the data the user typed in and pass it to the 2 functions
        confirmEdits.setOnClickListener(v -> {
            String newName = editUsername.getText().toString().trim();
            String newEmail = editEmail.getText().toString().trim();

            // Checks if user left both fields empty
            if (newName.isEmpty() && newEmail.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a name or email to update.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Calls changeName and changeEmail to do the work!
            changeName(newName);
            changeEmail(newEmail);
            editInformation.setVisibility(View.GONE);
        });

        // Delete profile button, will give an "are you sure message" first before deleting
        deleteProfile.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Profile Warning!")
                    .setMessage("Are you sure you want to delete your profile?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Deletes user and redirects to SignIn
                        UserManager.getInstance().deleteUserById(myUser.getUserId());
                        Intent intent = new Intent(getActivity(), SignUpActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // Dismisses dialog
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
    /**
     * Function for the User to change their name. Created to assist in testing.
     *
     * @author Lauren
     * @version 1.0
     * @param newName Is the string that will be the new name
     */
    public void changeName(String newName){

        // Only update name if not blank
        if (!newName.isEmpty()) {
            myUser.setName(newName);
            UserManager.getInstance().updateUserName(myUser);
        } else {
            editUsername.setText(myUser.getName());
        }
        usernameDisplay.setText(myUser.getName());
    }
    /**
     * Function for the User to change their email. Created to assist in testing.
     *
     * @author Lauren
     * @version 1.0
     * @param newEmail Is the string that will be the new email
     */
    public void changeEmail(String newEmail){
        if (!newEmail.isEmpty() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            // Checks if Email is valid, borrowed from SignUpActivity!
            myUser.setEmail(newEmail);
            UserManager.getInstance().updateUserEmail(myUser);
        } else if (!newEmail.isEmpty()) {
            editEmail.setError("Enter a valid email or leave blank");
            editEmail.requestFocus();
            confirmEdits.setEnabled(true);
            return;

        } else {
            editEmail.setText(myUser.getEmail());
        }
        emailDisplay.setText(myUser.getEmail());
    }
}
