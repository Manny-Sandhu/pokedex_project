package com.example.pokedex.TeamBuilderFragment.data_layer;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TeamDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addTeam(Team team);

    // maybe change this to a query so its easier to use
    @Update
    void updateTeam(Team team);

    // maybe change this to a query so its easier to use
    @Delete
    void deleteTeam(Team team);

    @Query("DELETE FROM team_table")
    void deleteAll();

    @Query("SELECT * FROM team_table ORDER BY team_id ASC")
    LiveData<List<Team>> getTeamList();
}
