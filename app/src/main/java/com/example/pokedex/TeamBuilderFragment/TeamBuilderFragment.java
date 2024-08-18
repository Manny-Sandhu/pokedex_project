package com.example.pokedex.TeamBuilderFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView;

import com.example.pokedex.PokedexFragment.DexEntry;
import com.example.pokedex.R;
import com.example.pokedex.TeamBuilderFragment.data_layer.Team;
import com.example.pokedex.TeamBuilderFragment.ui_layer.TeamViewModel;
import com.google.gson.Gson;


/**
 * Fragment for the team builder that allows users to add teams
 * to the teams database
 * once the team is added they are returned to the team list fragment
 */
public class TeamBuilderFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    // view model for the database data
    private TeamViewModel sharedViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sharedViewModel = new ViewModelProvider(requireActivity()).get(TeamViewModel.class);
        return inflater.inflate(R.layout.fragment_team_builder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        DexEntry[] entries = pullData();

        // create a string array for the spinner and set the first result to ???
        String[] names = new String[entries.length+1];
        names[0] = "???";
        for(int i = 1; i < names.length; i++ ){
            // need to subtract 1 from entries because of the offset
            names[i] = entries[i-1].getName();
        }

        // set up the spinner views
        Spinner slot_1 = view.findViewById(R.id.spinner_slot_1);
        Spinner slot_2 = view.findViewById(R.id.spinner_slot_2);
        Spinner slot_3 = view.findViewById(R.id.spinner_slot_3);
        Spinner slot_4 = view.findViewById(R.id.spinner_slot_4);
        Spinner slot_5 = view.findViewById(R.id.spinner_slot_5);
        Spinner slot_6 = view.findViewById(R.id.spinner_slot_6);
        Button saveButton = view.findViewById(R.id.button_team_builder_save);

        // set up and add the adapter to each spinner
        ArrayAdapter ad = new ArrayAdapter(
                view.getContext(),
                R.layout.spinner_layout,
                names);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        slot_1.setAdapter(ad);
        slot_2.setAdapter(ad);
        slot_3.setAdapter(ad);
        slot_4.setAdapter(ad);
        slot_5.setAdapter(ad);
        slot_6.setAdapter(ad);

        // add data to the database when clicking save
        saveButton.setOnClickListener(v -> {
            // get team data
            String pokemon1 = (String)slot_1.getSelectedItem();
            String pokemon2 = (String)slot_2.getSelectedItem();
            String pokemon3 = (String)slot_3.getSelectedItem();
            String pokemon4 = (String)slot_4.getSelectedItem();
            String pokemon5 = (String)slot_5.getSelectedItem();
            String pokemon6 = (String)slot_6.getSelectedItem();
            // add Team to database
            Team team = new Team(pokemon1, pokemon2, pokemon3, pokemon4, pokemon5, pokemon6);
            sharedViewModel.addTeam(team);
            // switch fragments
            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_view, TeamListFragment.class, bundle)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // dont need to do anything yet but might use this in the future
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // dont need to do anything yet but might use this in the future
    }

    /**
     * Pull the data for all the entries into a DexEntry array
     * to use when needed
     * @return - an array of DexEntries that was just pulled
     */
    private DexEntry[] pullData(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("dexData", Context.MODE_PRIVATE);
        DexEntry[] entries = new DexEntry[sharedPreferences.getAll().size()];
        Gson gson = new Gson();
        for(int i = 0; i < sharedPreferences.getAll().size(); i++){
            String data = sharedPreferences.getString("entry_" + i, "");
            entries[i] = gson.fromJson(data, DexEntry.class);
        }
        return entries;
    }
}