package com.example.pokedex.TeamBuilderFragment.ui_layer;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.pokedex.TeamBuilderFragment.data_layer.Team;
import com.example.pokedex.TeamBuilderFragment.data_layer.TeamRepository;

import java.util.List;

/**
 * ViewModel for the Team Database
 * As of right now the only usable commands are
 * add and deleteAll
 * Planning on adding delete individual, and update
 * if there is time
 */
public class TeamViewModel extends AndroidViewModel{

    public TeamRepository teamRepository;
    private final LiveData<List<Team>> mAllTeams;


    public TeamViewModel(@NonNull Application application) {
        super(application);
        teamRepository = new TeamRepository(application);
        mAllTeams = teamRepository.getAllTeams();
    }

    public LiveData<List<Team>> getAllTeams() { return mAllTeams; }

    public void addTeam(Team word) { teamRepository.addTeam(word); }

    // implement if there is time
    public void deleteAll(){teamRepository.deleteAll();}
}

