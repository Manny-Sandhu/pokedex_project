package com.example.pokedex.DexDataFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pokedex.PokedexFragment.DexEntry;
import com.example.pokedex.R;
import com.google.gson.Gson;

/**
 * Move Fragment for the Pokemons moves
 * These will be set into a recycler view using cards to
 * display all the different moves a pokemon can learn
 */
public class MoveFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_move, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        int position;

        // check if the bundle is null
        if(bundle != null){
            position = bundle.getInt("position");
        } else {
            position = 0;
        }
        DexEntry entry = pullEntry(position);

        // set the Recyclerview and adapter up
        RecyclerView moveView = view.findViewById(R.id.recycler_view_moves);
        MoveAdapter adapter = new MoveAdapter(entry.getMoves());
        moveView.setAdapter(adapter);
    }

    /**
     * Pull the given dexEntry from shared preferences instead of the entire array
     * for performance purposes
     * @param position - the position of the entry to pull from
     * @return the pulled DexEntry
     */
    private DexEntry pullEntry(int position){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("dexData", Context.MODE_PRIVATE);
        DexEntry entry;
        if(sharedPreferences != null){
            Gson gson = new Gson();
            String data = sharedPreferences.getString("entry_" + position, "");
            entry = gson.fromJson(data, DexEntry.class);
        } else {
            entry = new DexEntry();
        }
        return entry;
    }
}