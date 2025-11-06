package com.example.zenithchance.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.zenithchance.R;
import com.example.zenithchance.managers.UserManager;
import com.example.zenithchance.models.Entrant;
import com.example.zenithchance.models.Organizer;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;


public class AdminBrowseProfilesFragment extends Fragment {

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.admin_browse_profiles, container, false);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);

        LinearLayout listContainer = view.findViewById(R.id.listContainer);
        LayoutInflater li = LayoutInflater.from(requireContext());
        UserManager um = UserManager.getInstance();

        listContainer.removeAllViews();

        Task<List<Entrant>> entrantsTask = um.fetchEntrants();
        Task<List<Organizer>> organizersTask = um.fetchOrganizers();

        Tasks.whenAllSuccess(entrantsTask, organizersTask)
                .addOnSuccessListener(results -> {
                    @SuppressWarnings("unchecked") List<Entrant> entrants = (List<Entrant>) results.get(0);
                    @SuppressWarnings("unchecked") List<Organizer> organizers = (List<Organizer>) results.get(1);

                    class Row {
                        String id, name, email, type;
                        Row(String i, String n, String e, String t) { id=i; name=n; email=e; type=t; }
                    }

                    List<Row> rows = new ArrayList<>();
                    if (entrants != null) {
                        for (Entrant e : entrants) {
                            rows.add(new Row(e.getUserId(), e.getName(), e.getEmail(), e.getType()));
                        }
                    }
                    if (organizers != null) {
                        for (Organizer o : organizers) {
                            rows.add(new Row(o.getUserId(), o.getName(), o.getEmail(), o.getType()));
                        }
                    }

                    rows.sort((a, b) -> String.valueOf(a.name).compareToIgnoreCase(String.valueOf(b.name)));

                    for (Row r : rows) {
                        View card = li.inflate(R.layout.browse_profile_card_fragment, listContainer, false);
                        bindCard(card, r.id, r.name, r.email, r.type);
                        listContainer.addView(card);
                    }
                })
                .addOnFailureListener(e -> {
                    // Log or show a small message; avoid crashing
                    System.err.println("Failed to fetch profiles: " + e.getMessage());
                });


        return view;
    }

    private void bindCard(View card, String userId, String name, String email, String type) {
        TextView nameTv  = card.findViewById(R.id.browse_profile_name);
        TextView emailTv = card.findViewById(R.id.browse_profile_email);
        TextView typeTv  = card.findViewById(R.id.browse_profile_user_type);
        MaterialButton deleteButton = card.findViewById(R.id.buttonDelete);

        if (nameTv != null)  nameTv.setText(name);
        if (emailTv != null) emailTv.setText(email);

        if (typeTv != null) {
            if (!TextUtils.isEmpty(type)) {
                String t = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
                typeTv.setText(t);

                int color = android.R.color.darker_gray;
                if ("organizer".equalsIgnoreCase(type))      color = android.R.color.holo_green_dark;
                else if ("entrant".equalsIgnoreCase(type))   color = android.R.color.holo_blue_dark;

                typeTv.setTextColor(ContextCompat.getColor(requireContext(), color));
            } else {
                typeTv.setText("");
            }
        }

        if (deleteButton != null) {
            deleteButton.setOnClickListener(v -> {
                UserManager.getInstance().deleteUserById(userId);
                ViewGroup parent = (ViewGroup) card.getParent();
                if (parent != null) parent.removeView(card);
            });
        }
    }
}