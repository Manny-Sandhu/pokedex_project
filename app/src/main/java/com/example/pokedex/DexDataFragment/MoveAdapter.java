package com.example.pokedex.DexDataFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedex.PokedexFragment.PokemonMove;
import com.example.pokedex.R;

/**
 * Adapter for the Move Recycler view
 * Each DexEntry has multiple moves and this needs its own adapter to display
 * Very simple adapter that just sets text at the moment
 */
public class MoveAdapter extends RecyclerView.Adapter<MoveAdapter.ViewHolder> {


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView move_name;
        TextView move_method;
        TextView move_level;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            move_name = itemView.findViewById(R.id.textview_moves_name);
            move_method = itemView.findViewById(R.id.textview_moves_method);
            move_level = itemView.findViewById(R.id.textview_moves_learned_at);
        }
    }

    private final PokemonMove[] moves;

    public MoveAdapter(PokemonMove[] moves) {
        this.moves = moves;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.move_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.move_name.setText(moves[position].getMove());
        holder.move_method.setText(moves[position].getMethod());
        holder.move_level.setText(moves[position].getLevel());
    }

    @Override
    public int getItemCount() {
        return moves.length;
    }

}
