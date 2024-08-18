package com.example.pokedex.TeamBuilderFragment;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.pokedex.R;
import com.example.pokedex.TeamBuilderFragment.ui_layer.TeamViewModel;

/**
 * Fragment for displaying the team lists that are pulled from
 * the teams database
 * This Fragment also lets got to the add fragment and
 * in the future will let you delete/update individual entries
 */
public class TeamListFragment extends Fragment {
    // constants
    private final int GRID_PORTRAIT = 1;
    private final int GRID_LANDSCAPE = 2;

    // variables needed so these can be called outside of the onViewCreated
    private TeamViewModel sharedViewModel;
    private GridLayoutManager layoutManager;
    private RecyclerView teamView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set up shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(TeamViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_team_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // set up image button
        ImageButton addTeam = view.findViewById(R.id.imageButton_team_list_add);
        Button deleteTeam = view.findViewById(R.id.button_delete_all);

        // set up recycler view
        teamView = view.findViewById(R.id.recycler_view_team_list);
        // set up the layout depending on the orientation
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            layoutManager=new GridLayoutManager(view.getContext(),GRID_LANDSCAPE);
        } else {
            layoutManager=new GridLayoutManager(view.getContext(),GRID_PORTRAIT);
        }
        teamView.setLayoutManager(layoutManager);
        final TeamAdapter adapter = new TeamAdapter(new TeamAdapter.TeamDiff());
        teamView.setAdapter(adapter);

        // start observing
        sharedViewModel.getAllTeams().observe(getViewLifecycleOwner(), adapter::submitList);

        addTeam.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_view, TeamBuilderFragment.class, null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit());

        deleteTeam.setOnClickListener(v -> {
            sharedViewModel.deleteAll();

            Toast.makeText(view.getContext(),R.string.all_deleted, Toast.LENGTH_LONG)
                    .show();
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // get the current position in the recycler view
        int position = layoutManager.findFirstVisibleItemPosition();
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(this.getContext(),GRID_LANDSCAPE);
        } else {
            layoutManager = new GridLayoutManager(this.getContext(),GRID_PORTRAIT);
        }
        layoutManager.scrollToPosition(position);
        teamView.setLayoutManager(layoutManager);
    }
}