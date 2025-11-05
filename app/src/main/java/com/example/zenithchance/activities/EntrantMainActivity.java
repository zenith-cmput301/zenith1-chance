package com.example.zenithchance.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zenithchance.EntrantNavigationHelper;
import com.example.zenithchance.R;
import com.example.zenithchance.fragments.EntrantEventListFragment;
import com.example.zenithchance.models.Event;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
// SIGN-IN Page redirects to EntrantMainActivity

public class EntrantMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant_main);

        // Set up the bottom navigation using the helper
        EntrantNavigationHelper.setupBottomNav(this);
    }
}
