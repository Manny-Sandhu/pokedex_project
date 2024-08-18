package com.example.pokedex.TeamBuilderFragment.data_layer;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;


public class TeamRepository {
    private TeamDAO mTeamDao;
    private LiveData<List<Team>> mAllTeams;

    public TeamRepository(Application application) {
        TeamDatabase db = TeamDatabase.getDatabase(application);
        mTeamDao = db.teamDAO();
        mAllTeams = mTeamDao.getTeamList();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Team>> getAllTeams() {
        return mAllTeams;
    }

    // add a team to the database
    public void addTeam(Team team) {
        TeamDatabase.databaseWriteExecutor.execute(() -> mTeamDao.addTeam(team));
    }

    // Implement this later if there is time
    public void updateTeam(Team team){
        TeamDatabase.databaseWriteExecutor.execute(() -> mTeamDao.updateTeam(team));
    }

    // Implement this later if there is time
    public void deleteTeam(Team team){
        TeamDatabase.databaseWriteExecutor.execute(() -> mTeamDao.deleteTeam(team));
    }

    // delete every entry in the database
    public void deleteAll(){
        TeamDatabase.databaseWriteExecutor.execute(() -> mTeamDao.deleteAll());
    }
}
