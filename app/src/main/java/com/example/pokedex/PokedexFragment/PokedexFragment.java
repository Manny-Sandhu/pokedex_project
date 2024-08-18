package com.example.pokedex.PokedexFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pokedex.DexDataFragment.DexDataFragment;
import com.example.pokedex.R;
import com.google.gson.Gson;

import java.util.Objects;

/**
 * The main fragment for this app
 * Displays a Recycler view with each pokemon entry
 */
public class PokedexFragment extends Fragment {

    private final int WAIT = 10000;
    private final int ENTRIES = 1025;
    private final int GRID_PORTRAIT = 3;
    private final int GRID_LANDSCAPE = 6;
    private final int CACHE_SIZE = 30;

    // variables needed so these can be called outside of the onViewCreated
    private GridLayoutManager layoutManager;
    private RecyclerView pokedex;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokedex, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DexEntry[] entries = pullEntries();
        Bundle bundle = new Bundle();

        // set up the view and adapter
        pokedex = view.findViewById(R.id.recycler_view_pokedex);
        PokedexAdapter adapter = new PokedexAdapter(entries);

        // set the layout on entry
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            layoutManager=new GridLayoutManager(view.getContext(),GRID_LANDSCAPE);
        } else {
            layoutManager=new GridLayoutManager(view.getContext(),GRID_PORTRAIT);
        }

        // manually adjust the cache size to 30 and make sure all views are the same size
        pokedex.setItemViewCacheSize(CACHE_SIZE);
        pokedex.setHasFixedSize(true);

        // set the layout manager and adapter
        pokedex.setLayoutManager(layoutManager);
        pokedex.setAdapter(adapter);

        adapter.setOnClickListener((position, model) -> {
            TextView model_textview = model.findViewById(R.id.textview_main_pokedex_number);
            String dex_number = (String)model_textview.getText();

            // only let the item open if it has data loaded
            if(!Objects.equals(dex_number, "--")){
                bundle.putInt("position", position);
                getParentFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_view, DexDataFragment.class, bundle)
                        .commit();
            }
        });
    }

    // new config function called on every rotation or screen size change
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // get the current position in the recycler view
        int position = layoutManager.findFirstVisibleItemPosition();
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(this.getContext(),GRID_LANDSCAPE);
        } else {
            layoutManager = new GridLayoutManager(this.getContext(),GRID_PORTRAIT);
        }
        //make sure to stay at the same place in the recycler view
        layoutManager.scrollToPosition(position);
        pokedex.setLayoutManager(layoutManager);
    }

    private DexEntry[] pullEntries(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("dexData", Context.MODE_PRIVATE);
        DexEntry[] entries = new DexEntry[sharedPreferences.getAll().size()];
        if(sharedPreferences != null){
            Gson gson = new Gson();
            for(int i = 0; i < sharedPreferences.getAll().size(); i++){
                String data = sharedPreferences.getString("entry_" + i, "");
                entries[i] = gson.fromJson(data, DexEntry.class);
            }
        } else {
            for(int i = 0; i < sharedPreferences.getAll().size(); i++) {
                entries[i] = new DexEntry();
            }
        }
        return entries;
    }
}

