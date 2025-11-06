package com.example.zenithchance;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PeopleActivity extends AppCompatActivity {

    private Button btnWaiting, btnChosen, btnAccepted, btnDeclined, btnExport;
    private ListView listPeople;

    private List<Person> peopleList = new ArrayList<>();
    private List<Person> filteredList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private FirebaseFirestore db;
    private String eventId;
    private String currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        btnWaiting = findViewById(R.id.btnWaiting);
        btnChosen = findViewById(R.id.btnChosen);
        btnAccepted = findViewById(R.id.btnAccepted);
        btnDeclined = findViewById(R.id.btnDeclined);
        btnExport = findViewById(R.id.btnExport);
        listPeople = findViewById(R.id.listPeople);

        db = FirebaseFirestore.getInstance();

        eventId = getIntent().getStringExtra("eventId");
        if (eventId == null) {
            Toast.makeText(this, "No event ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPeopleFromFirestore();

        btnWaiting.setOnClickListener(v -> setFilter("Waiting"));
        btnChosen.setOnClickListener(v -> setFilter("Chosen"));
        btnAccepted.setOnClickListener(v -> setFilter("Accepted"));
        btnDeclined.setOnClickListener(v -> setFilter("Declined"));

        btnExport.setOnClickListener(v ->
                Toast.makeText(this, "Export feature coming soon!", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadPeopleFromFirestore() {
        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(eventDoc -> {
                    if (eventDoc.exists()) {
                        List<String> waitingList = (List<String>) eventDoc.get("waitingList");
                        if (waitingList == null || waitingList.isEmpty()) {
                            Toast.makeText(this, "No people in waiting list", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        peopleList.clear();
                        for (String userId : waitingList) {
                            db.collection("users").document(userId)
                                    .get()
                                    .addOnSuccessListener(userDoc -> {
                                        if (userDoc.exists()) {
                                            String name = userDoc.getString("name");
                                            String status = userDoc.getString("status");
                                            if (name == null) name = "Unknown User";
                                            if (status == null) status = "Waiting";

                                            peopleList.add(new Person(name, status));
                                            updateListView(peopleList);
                                        }
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Error loading user: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                        }
                    } else {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading event: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void setFilter(String status) {
        currentFilter = status;
        filterPeople(status);
    }

    private void filterPeople(String status) {
        if (status.equalsIgnoreCase("All")) {
            updateListView(peopleList);
            return;
        }

        filteredList.clear();
        for (Person p : peopleList) {
            if (p.getStatus().equalsIgnoreCase(status)) {
                filteredList.add(p);
            }
        }
        updateListView(filteredList);
    }

    private void updateListView(List<Person> list) {
        List<String> names = new ArrayList<>();
        for (Person p : list) {
            names.add(p.getName() + " (" + p.getStatus() + ")");
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        listPeople.setAdapter(adapter);
    }

    public static class Person {
        private final String name;
        private final String status;

        public Person(String name, String status) {
            this.name = name;
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public String getStatus() {
            return status;
        }
    }
}
