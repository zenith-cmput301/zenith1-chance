package com.example.zenithchance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zenithchance.R;
import com.example.zenithchance.models.Event;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for displaying a list of events in the AllEventsFragment.
 * <p>
 * Handles binding event data (name, date, location, image) to the list item layout.
 * Provides click listener support for each event item.
 * </p>
 *
 * @author Kiran
 * @version 1.0
 * @see com.example.zenithchance.fragments.AllEventsFragment
 */
public class AllEventsAdapter extends RecyclerView.Adapter<AllEventsAdapter.EventViewHolder> {

    /**
     * Interface for handling clicks on individual event items.
     */
    public interface OnEventClickListener {
        /**
         * Called when an event item is clicked.
         *
         * @param event The clicked Event object.
         */
        void onEventClick(Event event);
    }

    private Context context;
    private List<Event> events;
    private OnEventClickListener listener;

    private final SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault());

    /**
     * Constructor for the AllEventsAdapter.
     *
     * @param context  The context in which the adapter is used.
     * @param events   The list of Event objects to display.
     * @param listener Listener for handling clicks on events.
     */
    public AllEventsAdapter(Context context, List<Event> events, OnEventClickListener listener) {
        this.context = context;
        this.events = events;
        this.listener = listener;
    }

    /**
     * Updates the adapter's data with a new list of events.
     *
     * @param newList The new list of Event objects.
     */
    public void updateList(List<Event> newList) {
        this.events = newList;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for an individual event item.
     * Holds references to the UI elements for reuse.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, dateText, locationText, statusText;
        ImageView imageView;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView The view representing a single event item.
         */
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
        View view = LayoutInflater.from(context).inflate(R.layout.all_events_list_item, parent, false);
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
                .placeholder(R.drawable.celebration_placeholder)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onEventClick(event);
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
