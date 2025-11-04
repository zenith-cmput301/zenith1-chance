package com.example.entrantsprofilepage;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.zenithchance.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        Fragment AllEvents = new AllEventsFragment();
        Fragment MyEvents = new MyEventsFragment();
        Fragment Profile = new ProfileFragment();

        setCurrentFragment(AllEvents);

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_all_events) {
                setCurrentFragment(AllEvents);
            } else if (item.getItemId() == R.id.nav_my_events) {
                setCurrentFragment(MyEvents);
            } else {
                setCurrentFragment(Profile);
            }
            return true;
        });
    }
        private void setCurrentFragment(Fragment fragment) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();

    }
}