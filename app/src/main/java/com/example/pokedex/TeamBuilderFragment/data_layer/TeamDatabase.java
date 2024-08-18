package com.example.pokedex.TeamBuilderFragment.data_layer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Database for the App
 * This has only one table as of now
 * to hold the teams table
 */
@Database(entities = {Team.class}, version = 1, exportSchema = false)
public abstract class TeamDatabase extends RoomDatabase {

    public abstract TeamDAO teamDAO();
    private static volatile TeamDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static TeamDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TeamDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    TeamDatabase.class, "team_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more teams, just add them.
                TeamDAO dao = INSTANCE.teamDAO();
                dao.deleteAll();

                Team team = new Team("pikachu", "pikachu", "pikachu", "pikachu", "pikachu", "pikachu");
                dao.addTeam(team);
                team = new Team("jigglypuff", "charmander", "lugia", "gible", "salamence", "???");
                dao.addTeam(team);
            });
        }
    };
}