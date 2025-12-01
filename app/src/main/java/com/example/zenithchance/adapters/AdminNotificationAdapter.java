package com.example.zenithchance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zenithchance.R;
import com.example.zenithchance.models.Event;
import com.example.zenithchance.models.Notification;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminNotificationAdapter extends RecyclerView.Adapter<AdminNotificationAdapter.LogViewHolder> {

    private List<Notification> logs;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AdminNotificationAdapter(List<Notification> logs) {
        this.logs = logs;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_notification_log, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        Notification log = logs.get(position);

        holder.logMessage.setText(log.getToDisplay());
        holder.eventName.setText("Event: " + log.getEventName());
        holder.status.setText("Status: " + log.getStatus());
        holder.uid.setText("User: " + log.getEntrant());

        holder.organizer.setText("Organizer: Loading...");

        db.collection("Events")
                .whereEqualTo("name", log.getEventName())
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        Event event = query.getDocuments().get(0).toObject(Event.class);
                        if (event != null && event.getOrganizer() != null) {
                            holder.organizer.setText("Organizer: " + event.getOrganizer());
                        } else {
                            holder.organizer.setText("Organizer: Unknown");
                        }
                    } else {
                        holder.organizer.setText("Organizer: Not found");
                    }
                })
                .addOnFailureListener(e -> {
                    holder.organizer.setText("Organizer: Error");
                });
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView logMessage, eventName, status, uid, organizer;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            logMessage = itemView.findViewById(R.id.logMessage);
            eventName  = itemView.findViewById(R.id.logEventName);
            status     = itemView.findViewById(R.id.logStatus);
            uid        = itemView.findViewById(R.id.logUid);
            organizer  = itemView.findViewById(R.id.logOrganizer);
        }
    }
}
