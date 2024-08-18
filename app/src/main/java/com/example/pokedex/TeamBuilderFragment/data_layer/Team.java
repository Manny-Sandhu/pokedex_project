package com.example.pokedex.TeamBuilderFragment.data_layer;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Outline for database entries
 * Each team has 6 slots filled with Strings
 * representing the name of the pokemon in that
 * slot
 */
@Entity(tableName = "team_table")
public class Team {

    @ColumnInfo(name = "team_id")
    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "slot_1")
    String slot_1;

    @ColumnInfo(name = "slot_2")
    String slot_2;

    @ColumnInfo(name = "slot_3")
    String slot_3;

    @ColumnInfo(name = "slot_4")
    String slot_4;

    @ColumnInfo(name = "slot_5")
    String slot_5;

    @ColumnInfo(name = "slot_6")
    String slot_6;


    public Team(String slot_1, String slot_2, String slot_3, String slot_4, String slot_5, String slot_6) {
        this.id = 0;
        this.slot_1 = slot_1;
        this.slot_2 = slot_2;
        this.slot_3 = slot_3;
        this.slot_4 = slot_4;
        this.slot_5 = slot_5;
        this.slot_6 = slot_6;
    }

    public int getId() {
        return id;
    }

    public String getSlot_1() {
        return slot_1;
    }

    public String getSlot_2() {
        return slot_2;
    }

    public String getSlot_3() {
        return slot_3;
    }

    public String getSlot_4() {
        return slot_4;
    }

    public String getSlot_5() {
        return slot_5;
    }

    public String getSlot_6() {
        return slot_6;
    }

    // for testing only
    @NonNull
    @Override
    public String toString() {
        return getSlot_1();
    }
}
