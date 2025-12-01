package com.example.zenithchance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zenithchance.R;
import com.example.zenithchance.models.Notification;

import java.util.List;

public class AdminNotificationAdapter extends RecyclerView.Adapter<AdminNotificationAdapter.LogViewHolder> {

    private List<Notification> logs;

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

        // Display the message already prepared in your model
        holder.logMessage.setText(log.getToDisplay());

        holder.eventName.setText("Event: " + log.getEventName());
        holder.status.setText("Status: " + log.getStatus());
        holder.uid.setText("User: " + log.getEntrant());
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView logMessage, eventName, status, uid;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            logMessage = itemView.findViewById(R.id.logMessage);
            eventName  = itemView.findViewById(R.id.logEventName);
            status     = itemView.findViewById(R.id.logStatus);
            uid        = itemView.findViewById(R.id.logUid);
        }
    }
}
