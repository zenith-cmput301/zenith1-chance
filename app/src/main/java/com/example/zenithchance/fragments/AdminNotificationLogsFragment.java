package com.example.zenithchance.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zenithchance.R;
import com.example.zenithchance.adapters.AdminNotificationAdapter;
import com.example.zenithchance.models.Notification;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminNotificationLogsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView noLogsMessage;
    private AdminNotificationAdapter adapter;
    private List<Notification> logsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_notification_logs, container, false);

        recyclerView = view.findViewById(R.id.logsRecycler);
        noLogsMessage = view.findViewById(R.id.noLogsMessage);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AdminNotificationAdapter(logsList);
        recyclerView.setAdapter(adapter);

        loadNotificationLogs();

        return view;
    }

    private void loadNotificationLogs() {
        FirebaseFirestore.getInstance()
                .collection("notifications")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    logsList.clear();

                    for (var doc : querySnapshot.getDocuments()) {
                        Notification log = doc.toObject(Notification.class);
                        if (log != null) logsList.add(log);
                    }

                    adapter.notifyDataSetChanged();

                    if (logsList.isEmpty()) {
                        noLogsMessage.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        noLogsMessage.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }
}
