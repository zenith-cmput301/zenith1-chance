package com.example.zenithchance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventVH> {
    private List<Event> eventList = new ArrayList<>();
    private final SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault());

    public void setItems(List<Event> items) {
        eventList.clear();
        eventList.addAll(items);
        notifyDataSetChanged();
    }

    public EventVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
        return new EventVH(v);
    }

    public void onBindViewHolder(EventVH h, int position) {
        Event e = eventList.get(position);
        h.name.setText(e.name);
        h.location.setText(e.location);
        h.status.setText(e.status);

        // date -> formatted string
        if (e.date != null) h.date.setText(fmt.format(e.date));
        else h.date.setText("");
    }

    @Override public int getItemCount() { return eventList.size(); }

    static class EventVH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView date, name, location, status;
        EventVH(View v) {
            super(v);
            date     = v.findViewById(R.id.event_date);
            name     = v.findViewById(R.id.event_name);
            location = v.findViewById(R.id.event_location);
            status   = v.findViewById(R.id.event_status);
        }
    }
}

