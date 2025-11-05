package com.example.zenithchance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zenithchance.R;

public class OrganizerEventDetailsActivity extends AppCompatActivity {

    private Button btnPeople;
    private Button btnEdit;
    private Button btnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons
        btnPeople = findViewById(R.id.btnPeople);
        btnEdit = findViewById(R.id.btnEdit);
        btnMap = findViewById(R.id.btnMap);

        btnPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrganizerEventDetailsActivity.this, PeopleActivity.class);
                startActivity(intent);
            }
        });

    }
}
