package com.example.zenithchance;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class PeopleActivity extends AppCompatActivity {

    private Button btnWaiting, btnChosen, btnAccepted, btnDeclined, btnExport;
    private ListView listPeople;

    private List<Person> peopleList = new ArrayList<>();
    private List<Person> filteredList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

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

        // Load mock data
        loadPeopleData();

        // Initially show all people
        updateListView(peopleList);

        // --- Set up filter button clicks ---
        btnWaiting.setOnClickListener(v -> setFilter("Waiting"));
        btnChosen.setOnClickListener(v -> setFilter("Chosen"));
        btnAccepted.setOnClickListener(v -> setFilter("Accepted"));
        btnDeclined.setOnClickListener(v -> setFilter("Declined"));

        // Export button (placeholder)
        btnExport.setOnClickListener(v ->
                Toast.makeText(this, "Export feature coming soon!", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadPeopleData() {
        peopleList.clear();
        peopleList.add(new Person("Alice Johnson", "Waiting"));
        peopleList.add(new Person("Bob Smith", "Chosen"));
        peopleList.add(new Person("Catherine Lee", "Accepted"));
        peopleList.add(new Person("David Brown", "Declined"));
        peopleList.add(new Person("Emily Davis", "Accepted"));
        peopleList.add(new Person("Frank Wilson", "Waiting"));
        peopleList.add(new Person("Grace Miller", "Chosen"));
    }

    private void setFilter(String status) {
        currentFilter = status;
        Toast.makeText(this, "Filtering: " + status, Toast.LENGTH_SHORT).show();
        filterPeople(status);
    }

    private void filterPeople(String status) {
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


