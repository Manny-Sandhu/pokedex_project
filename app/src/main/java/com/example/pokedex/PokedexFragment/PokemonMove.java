package com.example.pokedex.PokedexFragment;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Move class for the DexEntry class to hold Move Objects
 * currently only using level, method, and move name but will add
 * description, type, damage/effect in future iterations
 */
public class PokemonMove implements Parcelable {
    private final String level;
    private final String method;
    private final String move;

    public PokemonMove(String level, String method, String move) {
        this.level = level;
        this.method = method;
        this.move = move;
    }

    protected PokemonMove(Parcel in) {
        level = in.readString();
        method = in.readString();
        move = in.readString();
    }

    public static final Creator<PokemonMove> CREATOR = new Creator<PokemonMove>() {
        @Override
        public PokemonMove createFromParcel(Parcel in) {
            return new PokemonMove(in);
        }

        @Override
        public PokemonMove[] newArray(int size) {
            return new PokemonMove[size];
        }
    };

    // getters but no setters needed in this version
    public String getLevel() {
        return "learned at: " + level;
    }

    public String getMethod() {
        return "learned by: " + method;
    }

    public String getMove() {
        return move;
    }

    // not using this
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(level);
        dest.writeString(method);
        dest.writeString(move);
    }

    // for testing dont need to use anywhere else
    @NonNull
    @Override
    public String toString() {
        return getMove() + getLevel() + getMethod();
    }
}
