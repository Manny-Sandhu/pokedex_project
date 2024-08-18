package com.example.pokedex.DexDataFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pokedex.PokedexFragment.DexEntry;
import com.example.pokedex.R;
import com.google.gson.Gson;

import java.util.Locale;


/**
 * About Fragment that has some simple details about the pokemon
 * Currently includes flavor text, height, and weight
 * Future updates planing on adding egg groups, genous, habitat
 */
public class AboutFragment extends Fragment {

    // constant strings
    private final String HEIGHT = "Height: ";
    private final String WEIGHT = "Weight: ";
    // TextToSpeech wont initialize unless there is an established class variable
    private TextToSpeech textToSpeech;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        int position;

        // set up the entries array and position
        if(bundle != null){
            position = bundle.getInt("position");
        } else {
            position = 0;
        }
        Log.d("testtt", "about position: " + position);
        DexEntry entry = pullEntry(position);

        TextView flavorText = view.findViewById(R.id.textview_about_flavor);
        TextView heightText = view.findViewById(R.id.textview_about_height);
        TextView weightText = view.findViewById(R.id.textview_about_weight);
        Button readAbout = view.findViewById(R.id.button_dex_text_to_speech);

        // create a textToSpeech object
        // if I separate this into a function it stops working
        textToSpeech = new TextToSpeech(view.getContext(), speech -> {

            // if No error is found then only it will run
            if(speech!=TextToSpeech.ERROR){
                // To Choose language of speech
                textToSpeech.setLanguage(Locale.CANADA);
            }
        }, "com.google.android.tts");


        String heightString = HEIGHT + entry.getHeight();
        String weightString = WEIGHT + entry.getWeight();
        // remove the line breaks from the flavor text
        String flavorString = entry.getFlavour_text().replace("\n", " ");
        flavorText.setText(flavorString);
        heightText.setText(heightString);
        weightText.setText(weightString);

        // play the text to speech
        readAbout.setOnClickListener(view1 -> textToSpeech.speak(flavorText.getText().toString(),
                TextToSpeech.QUEUE_FLUSH,null));
    }

    /**
     * Pull the given dexEntry from shared preferences instead of the entire array
     * for performance purposes
     * @param position - the position of the entry to pull from
     * @return the pulled DexEntry
     */
    private DexEntry pullEntry(int position){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("dexData", Context.MODE_PRIVATE);
        DexEntry entry;
        if(sharedPreferences != null){
            Gson gson = new Gson();
            String data = sharedPreferences.getString("entry_" + position, "");
            entry = gson.fromJson(data, DexEntry.class);
        } else {
            entry = new DexEntry();
        }
        return entry;
    }
}