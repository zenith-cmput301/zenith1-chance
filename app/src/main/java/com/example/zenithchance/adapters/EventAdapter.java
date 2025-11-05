package com.example.zenithchance.adapters;

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
import com.example.zenithchance.models.Event;
import com.example.zenithchance.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context context;
    private List<com.example.zenithchance.models.Event> events;

    SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault());

    public EventAdapter(Context context, List<com.example.zenithchance.models.Event> events) {
        this.context = context;
        this.events = events;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, dateText, locationText, statusText;
        ImageView imageView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.event_name);
            dateText = itemView.findViewById(R.id.event_date);
            locationText = itemView.findViewById(R.id.event_location);
            statusText = itemView.findViewById(R.id.event_status);
            imageView = itemView.findViewById(R.id.event_image);
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_list_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.nameText.setText(event.getName());
        holder.dateText.setText(fmt.format(event.getDate()));
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
