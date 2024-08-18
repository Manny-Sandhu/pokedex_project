package com.example.pokedex.TeamBuilderFragment;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.pokedex.TeamBuilderFragment.data_layer.Team;

/**
 * Adapter for the Team list fragments Recycler view
 * used to display cards with data from the TeamDatabase
 */
public class TeamAdapter extends ListAdapter<Team, TeamViewHolder> {

    public TeamAdapter(@NonNull DiffUtil.ItemCallback<Team> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return TeamViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(TeamViewHolder holder, int position) {
        Team current = getItem(position);
        // holder method in the ViewHolder just binds the views
        holder.bind(current);
    }

    public static class TeamDiff extends DiffUtil.ItemCallback<Team> {

        @Override
        public boolean areItemsTheSame(@NonNull Team oldItem, @NonNull Team newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Team oldItem, @NonNull Team newItem) {
            return (oldItem.getSlot_1().equals(newItem.getSlot_1()) &&
                    oldItem.getSlot_2().equals(newItem.getSlot_2()) &&
                    oldItem.getSlot_3().equals(newItem.getSlot_3()) &&
                    oldItem.getSlot_4().equals(newItem.getSlot_4()) &&
                    oldItem.getSlot_5().equals(newItem.getSlot_5()) &&
                    oldItem.getSlot_6().equals(newItem.getSlot_6()));
        }
    }
}
