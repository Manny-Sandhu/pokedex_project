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
 * This Fragment holds the forms and varieties recycler views
 * These are very similar to each other and may be updated in the future
 */
public class VarietyFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_variety, container, false);
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

        // find the recycler views
        RecyclerView varietyView = view.findViewById(R.id.recycler_view_variety);
        RecyclerView formsView = view.findViewById(R.id.recycler_view_forms);
        // allow smooth scrolling
        varietyView.setNestedScrollingEnabled(false);
        formsView.setNestedScrollingEnabled(false);
        // set up adapters
        VarietyAdapter vAdapter = new VarietyAdapter(entry.getVarieties());
        FormAdapter fAdapter = new FormAdapter((entry.getForms()));

        varietyView.setAdapter(vAdapter);
        formsView.setAdapter(fAdapter);
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