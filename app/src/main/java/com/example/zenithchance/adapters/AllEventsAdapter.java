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
 * For smooth display of All Event items fetched from Firebase
 *
 * @author Kiran
 * @version 1.0
 * @see com.example.zenithchance.fragments.AllEventsFragment
 */
public class AllEventsAdapter extends RecyclerView.Adapter<AllEventsAdapter.EventViewHolder> {

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    private Context context;
    private List<Event> events;
    private OnEventClickListener listener;

    SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault());

    public AllEventsAdapter(Context context, List<Event> events, OnEventClickListener listener) {
        this.context = context;
        this.events = events;
        this.listener = listener;
    }

    public void updateList(List<Event> newList) {
        this.events = newList;
        notifyDataSetChanged();
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
                .placeholder(R.drawable.ic_my_events)
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
