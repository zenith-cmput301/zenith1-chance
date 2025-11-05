package com.example.zenithchance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context context;
    private List<com.example.entrantsprofilepage.Event> events;

    public EventAdapter(Context context, List<com.example.entrantsprofilepage.Event> events) {
        this.context = context;
        this.events = events;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, dateText, timeText, locationText;
        ImageView imageView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.eventName);
            dateText = itemView.findViewById(R.id.eventDate);
            timeText = itemView.findViewById(R.id.eventTime);
            locationText = itemView.findViewById(R.id.eventLocation);
            imageView = itemView.findViewById(R.id.eventImage);
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.nameText.setText(event.getName());
        holder.dateText.setText(event.getDate());
        holder.timeText.setText(event.getTime());
        holder.locationText.setText(event.getLocation());

        Glide.with(context)
                .load(event.getImageUrl())
                .placeholder(R.drawable.ic_my_events)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v ->
                Toast.makeText(context, "Clicked: " + event.getName(), Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
