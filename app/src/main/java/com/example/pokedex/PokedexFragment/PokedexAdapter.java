package com.example.pokedex.PokedexFragment;

import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import androidx.annotation.NonNull;

import com.example.pokedex.R;

import com.squareup.picasso.Picasso;

/**
 * Adapter for the PokedexFragment recycler view
 * This adapter is creates clickable views
 */
public class PokedexAdapter extends RecyclerView.Adapter<PokedexAdapter.ViewHolder>{

    // OnClickListener object to store the onCLickListener Instance
    private static OnClickListener onClickListener;
    // interface for the OnClickListener
    public interface OnClickListener { void onClick(int position, View model);}
    // function to set the OnClickListener for the adapter
    public void setOnClickListener(OnClickListener onClickListener) {this.onClickListener = onClickListener;}

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView dex_number;
        TextView dex_name;
        ImageView dex_img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dex_img = itemView.findViewById(R.id.imageview_main_pokedex_image);
            dex_number = itemView.findViewById(R.id.textview_main_pokedex_number);
            dex_name = itemView.findViewById(R.id.textview_main_pokedex_name);

            itemView.setOnClickListener(view -> {
                if (onClickListener != null) {
                    onClickListener.onClick(getAdapterPosition(), itemView);
                }
            });
        }
    }


    private final DexEntry[] entries;

    public PokedexAdapter(DexEntry[] entries){this.entries = entries;}


    @NonNull
    @Override
    public PokedexAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokedex_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokedexAdapter.ViewHolder holder, int position) {
        holder.dex_number.setText(entries[position].getNumber());
        holder.dex_name.setText(entries[position].getName());
        // load image from the web with picasso
        Picasso.get()
                .load(entries[position].getImage_url())
                .into(holder.dex_img);
    }

    @Override
    public int getItemCount() {
        return entries.length;
    }
}


