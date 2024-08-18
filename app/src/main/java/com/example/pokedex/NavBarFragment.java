package com.example.pokedex;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pokedex.PokedexFragment.PokedexFragment;
import com.example.pokedex.TeamBuilderFragment.TeamListFragment;

/**
 * Fragment for the bottom navigation
 * This was separated to make the loading screen cleaner
 */
public class NavBarFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nav_bar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button pokemon_list = view.findViewById(R.id.button_pokedex);
        Button team_builder = view.findViewById(R.id.button_teams);

        pokemon_list.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_view, PokedexFragment.class, null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit());

        team_builder.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_view, TeamListFragment.class, null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit());
    }
}