package com.example.zenithchance;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Class to bridge backend data (list of events) & List UI.
 * The choice to make it RecyclerView is for experience smoothness.
 *
 * @author Percy
 * @version 1.0
 * @see Event
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventVH> {
    private List<Event> eventList = new ArrayList<>();
    private final SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault());
    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public EventsAdapter(List<Event> events, OnEventClickListener listener) {
        if (events != null) eventList.addAll(events);
        this.listener = listener;
    }

    /**
     * Method to refreshes the UI with updated list.
     *
     * @param items Updated items to be displayed.
     */
    public void setItems(List<Event> items) {
        eventList.clear();
        if (items != null) eventList.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * Method to inflates each event item in the list.
     * Inflates the layout for an event, then wrap it in a view holder for reusability.
     *
     * @param parent   The ViewGroup into which the new View will be added
     *                 after it is bound to an adapter position
     *                 (should be the event list fragment).
     * @param viewType The view type of the new View.
     * @return         An event holder.
     */
    @NonNull
    public EventVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
        return new EventVH(v);
    }

    /**
     * Once we have an event holder container, we fill them in with this method.
     *
     * @param h        The ViewHolder to be filled in.
     * @param position The position of the item within the adapter's data set.
     */
    public void onBindViewHolder(EventVH h, int position) {
        Event e = eventList.get(position);
        h.name.setText(e.getName());
        h.location.setText(e.getLocation());
        h.status.setText(e.getStatus());
        h.date.setText(fmt.format(e.getDate())); // formatting date

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onEventClick(e);
        });
    }

    /**
     * Gets list of events' size.
     *
     * @return Item count.
     */
    @Override public int getItemCount() { return eventList.size(); }

    /**
     * Class for holding event in a RecycleView.
     */
    static class EventVH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView date, name, location, status;

        /**
         *
         * @param v The list item view.
         */
        EventVH(View v) {
            super(v);
            date     = v.findViewById(R.id.event_date);
            name     = v.findViewById(R.id.event_name);
            location = v.findViewById(R.id.event_location);
            status   = v.findViewById(R.id.event_status);
        }
    }
}

