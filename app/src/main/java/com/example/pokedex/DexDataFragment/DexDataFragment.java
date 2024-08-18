package com.example.pokedex.DexDataFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pokedex.PokedexFragment.DexEntry;
import com.example.pokedex.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import java.io.IOException;

/**
 * Fragment for the DexEntry data for an individual pokemon
 * This fragment has a child fragment as well
 */
public class DexDataFragment extends Fragment {

    // number of entries constant
    private final int ENTRIES = 1024;

    // media player for the pokemons cry
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dex_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        int position;

        // set up bindings
        TextView dexNum = view.findViewById(R.id.textview_dex_data_number);
        TextView dexName = view.findViewById(R.id.textview_dex_data_name);

        ImageView dexImage = view.findViewById(R.id.imageview_dex_data_image);
        ImageButton cryButton = view.findViewById(R.id.imagebutton_dex_data_cry);

        ImageView type1 = view.findViewById(R.id.imageview_dex_data_type1);
        ImageView type2 = view.findViewById(R.id.imageview_dex_data_type2);

        Button aboutButton = view.findViewById(R.id.button_dex_data_about);
        Button movesButton = view.findViewById(R.id.button_dex_data_moves);
        Button varientButton = view.findViewById(R.id.button_dex_data_variants);

        // rename the buttons and switch to cards if there is time
        ImageView prevButton = view.findViewById(R.id.button_dex_data_prev);
        ImageView nextButton = view.findViewById(R.id.button_dex_data_next);
        TextView prevText = view.findViewById(R.id.textview_prev);
        TextView nextText = view.findViewById(R.id.textview_next);
        CardView prevCard = view.findViewById(R.id.cardview_prev);
        CardView nextCard = view.findViewById(R.id.cardview_next);

        aboutButton.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        // get data from bundle or make a new default values
        if(bundle != null){
            position = bundle.getInt("position");
        } else {
            position = 0;
        }
        Log.d("testttt", "test position: " + position);
        DexEntry entry = pullEntry(position);

        setupButtons(prevButton, nextButton, position, prevText, nextText, prevCard, nextCard);

        // set the default child fragment to the about fragment
        // in future updates have this open on the fragment that they were already on
        if(savedInstanceState == null){
            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_dex_data, AboutFragment.class, bundle)
                    .commit();
        }

        // set an image for the type(s) the pokemon has
        if(entry.getTypes().length == 1){
            type1.setImageResource(determineType(entry.getTypes()[0]));
            type2.setVisibility(View.GONE);
        } else {
            type1.setImageResource(determineType(entry.getTypes()[0]));
            type2.setImageResource(determineType(entry.getTypes()[1]));
        }

        // play cry on click
        cryButton.setOnClickListener(v -> playCry(entry));

        // switch to the about fragment
        aboutButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container_dex_data, AboutFragment.class, bundle)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
            aboutButton.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            movesButton.setPaintFlags(0);
            varientButton.setPaintFlags(0);
        });

        //switch to the varient fragment
        varientButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container_dex_data, VarietyFragment.class, bundle)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
            varientButton.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            aboutButton.setPaintFlags(0);
            movesButton.setPaintFlags(0);
        });

        // switch to the moves fragment
        movesButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container_dex_data, MoveFragment.class, bundle)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
            movesButton.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            aboutButton.setPaintFlags(0);
            varientButton.setPaintFlags(0);
        });

        // update the fragment to have the previous pokemon show
        prevCard.setOnClickListener(v -> {
            bundle.putInt("position",(position - 1));
            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_view, DexDataFragment.class, bundle)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        });

        // update the fragment to have the next pokemon show
        nextCard.setOnClickListener(v -> {
            bundle.putInt("position",(position + 1));
            getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_view, DexDataFragment.class, bundle)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        });

        // set the remaining textviews and the imageview
        dexName.setText(entry.getName());
        dexNum.setText(entry.getNumber());
        Picasso.get()
                .load(entry.getImage_url())
                .into(dexImage);
    }

    /**
     * Make the MediaPlayer play the given entries cry
     * @param entry - the given entry
     */
    private void playCry(DexEntry entry){
        // initializing media player
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(entry.getCry_url());
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determine what drawable to use depending on what type is provided
     * @param type - the type provided as a string
     * @return - the type as a drawable id
     */
    private int determineType(String type) {
        switch (type) {
            case "normal":
                return R.drawable.normal;
            case "fighting":
                return R.drawable.fighting;
            case "flying":
                return R.drawable.flying;
            case "poison":
                return R.drawable.poison;
            case "ground":
                return R.drawable.ground;
            case "rock":
                return R.drawable.rock;
            case "bug":
                return R.drawable.bug;
            case "ghost":
                return R.drawable.ghost;
            case "steel":
                return R.drawable.steel;
            case "fire":
                return R.drawable.fire;
            case "water":
                return R.drawable.water;
            case "grass":
                return R.drawable.grass;
            case "electric":
                return R.drawable.electric;
            case "psychic":
                return R.drawable.psychic;
            case "ice":
                return R.drawable.ice;
            case "dragon":
                return R.drawable.dragon;
            case "dark":
                return R.drawable.dark;
            case "fairy":
                return R.drawable.fairy;
            default:
                return R.drawable.ic_launcher_background;
        }
    }

    /**
     * Setup the prev and next buttons in the dex data fragment
     * @param prevButton - the prev Imageview being set up
     * @param nextButton - the nex Imageview being set up
     * @param position - the current position in the entries array
     * @param prevText - a text view for clarity with the prev button
     * @param nextText - a text view for clarity with the next button
     * @param  prevCard - the prev card view being set up
     * @param  nextCard - the next card view being set up
     */
    private void setupButtons(ImageView prevButton, ImageView nextButton, int position,
                              TextView prevText, TextView nextText,
                              CardView prevCard, CardView nextCard){
        // make sure the prev and next button only work if there is a prev or next
        if(position < 1){
            prevCard.setEnabled(false);
            prevButton.setBackground(getResources().getDrawable(R.drawable.null_sprite));
            prevButton.setEnabled(false);
            prevText.setVisibility(View.INVISIBLE);
        }
        if(position >= ENTRIES){
            nextCard.setEnabled(false);
            nextButton.setBackground(getResources().getDrawable(R.drawable.null_sprite));
            nextButton.setEnabled(false);
            nextText.setVisibility(View.INVISIBLE);
        }
        if(prevButton.isEnabled()){
            DexEntry prevEntry = pullEntry(position - 1);
            Picasso.get()
                    .load(prevEntry.getImage_url())
                    .into(prevButton);
        }
        if(nextButton.isEnabled()){
            DexEntry nextEntry = pullEntry(position + 1);
            Picasso.get()
                    .load(nextEntry.getImage_url())
                    .into(nextButton);
        }
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