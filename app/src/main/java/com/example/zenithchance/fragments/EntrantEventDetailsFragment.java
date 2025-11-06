package com.example.zenithchance.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.zenithchance.R;

public class EntrantEventDetailsFragment extends Fragment {

    public EntrantEventDetailsFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.entrant_event_details, container, false);

        TextView name = view.findViewById(R.id.event_name);
        TextView location = view.findViewById(R.id.location);
        TextView organizer = view.findViewById(R.id.organizer_name);
        TextView time = view.findViewById(R.id.time);
        TextView desc = view.findViewById(R.id.description);
        ImageView image = view.findViewById(R.id.header_image);

        Bundle args = getArguments();
        if (args != null) {
            name.setText(args.getString("event_name"));
            location.setText(args.getString("event_location"));
            organizer.setText(args.getString("event_organizer"));
            time.setText(args.getString("event_time"));
            desc.setText(args.getString("event_description"));

            String imageUrl = args.getString("event_image_url");
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_my_events)
                    .into(image);
        }

        return view;
    }
}
