package com.example.pokedex.PokedexFragment;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * This class is the foundation for this app
 * as of this moment it holds too much information
 * and will be broken down in future iterations
 */
public class DexEntry implements Parcelable {

    // pokemon endpoint variables
    private String number;
    private String name;
    private String image_url;
    private String cry_url;
    private String height;
    private String weight;
    private String[] types;
    private PokemonMove[] moves;
    private String[] forms;

    // pokemon-species endpoint variables
    private String flavour_text;
    private String[] varieties;

    // default constructor with default values
    public DexEntry() {
        number = "--";
        name = "???";
        image_url = "https://i.imgur.com/oDcJXID.png";
        cry_url = "";
        height = "Loading Data";
        weight = "Loading Data";
        types = new String[]{"type1, type2"};
        moves = new PokemonMove[]{};
        forms = new String[]{"form"};
        flavour_text = "Loading Data";
        varieties = new String[]{"variety"};
    }

    // parcelable constructor
    protected DexEntry(Parcel in) {
        number = in.readString();
        name = in.readString();
        image_url = in.readString();
        cry_url = in.readString();
        height = in.readString();
        weight = in.readString();
        types = in.createStringArray();
        moves = in.createTypedArray(PokemonMove.CREATOR);
        forms = in.createStringArray();
        flavour_text = in.readString();
        varieties = in.createStringArray();
    }


    public static final Creator<DexEntry> CREATOR = new Creator<DexEntry>() {
        @Override
        public DexEntry createFromParcel(Parcel in) {
            return new DexEntry(in);
        }

        @Override
        public DexEntry[] newArray(int size) {
            return new DexEntry[size];
        }
    };

    // getters and setters
    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getCry_url() { return cry_url; }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String[] getTypes() {
        return types;
    }

    public PokemonMove[] getMoves() {
        return moves;
    }

    public String getFlavour_text() {
        return flavour_text;
    }

    public String[] getVarieties() {
        return varieties;
    }

    public String[] getForms() {
        return forms;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setCry_url(String cry_url) {
        this.cry_url = cry_url;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public void setMoves(PokemonMove[] moves) {
        this.moves = moves;
    }

    public void setFlavour_text(String flavour_text) {
        this.flavour_text = flavour_text;
    }

    public void setVarieties(String[] varieties) {
        this.varieties = varieties;
    }

    public void setForms(String[] forms) {
        this.forms = forms;
    }


    // dont need this
    @Override
    public int describeContents() {
        return 0;
    }

    // for parcelable
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(number);
        dest.writeString(name);
        dest.writeString(image_url);
        dest.writeString(cry_url);
        dest.writeString(height);
        dest.writeString(weight);
        dest.writeStringArray(types);
        dest.writeTypedArray(moves, flags);
        dest.writeStringArray(forms);
        dest.writeString(flavour_text);
        dest.writeStringArray(varieties);
    }
}
