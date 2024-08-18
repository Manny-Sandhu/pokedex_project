package com.example.pokedex;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pokedex.PokedexFragment.DexEntry;
import com.example.pokedex.PokedexFragment.PokedexFragment;
import com.example.pokedex.PokedexFragment.PokemonMove;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity {

    // Constants
    private final String DOMAIN_POKEMON = "https://pokeapi.co/api/v2/pokemon/";
    private final String DOMAIN_SPECIES = "https://pokeapi.co/api/v2/pokemon-species/";
    private final String ERROR_MESSAGE = "Something Went Wrong Sorry";
    private final int ENTRIES_NUM = 1025;
    // when at home use 4000 otherwise change to 6000 to be safe
    private final int WAIT_TIME = 6000;
    // when at home use 40 otherwise use 20 to be safe
    private final int INDEX_OFFSET = 20;

    // Array of requests and queues so there is no fighting over resources
    private RequestQueue[] mRequestQueue  = new RequestQueue[ENTRIES_NUM];
    private StringRequest[] mStringRequest = new StringRequest[ENTRIES_NUM];
    // array where all the data is going to be stored
    private DexEntry[] entries = new DexEntry[ENTRIES_NUM];

    // boolean for controlling the threads current state
    // not being used in this current build
    private boolean isRunning = true;
    // start and end index of the current calls being made to the api
    private int start_index = 0;
    private int end_index = INDEX_OFFSET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if(savedInstanceState == null){
            SharedPreferences sharedPreferences = getSharedPreferences("dexData", Context.MODE_PRIVATE);

            // this is for if you want to test the loading screen after data has been added
            /*SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();*/

            if(sharedPreferences.getAll().size() < ENTRIES_NUM){
                // fill the entries array with new blank DexEntries
                for(int i = 0; i < entries.length; i++){
                    entries[i] = new DexEntry();
                }
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.fragment_view, LoadingFragment.class, null)
                        .commit();

                fillPokedex();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.fragment_view, PokedexFragment.class, null)
                        .commit();
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.fragment_navigation, NavBarFragment.class, null)
                        .commit();
                Log.d("testtt", "skipped straight to pokedex");
            }
        }
    }

    /**
     * This Thread just start loading the data as long as the start index
     * is not greater than the length of the entries array
     * ******************************************************************
     * At this moment this function cannot be started more than once but
     * was left in in case that functionality gets added in a future update
     */
    private void fillPokedex(){
        if(start_index < entries.length){
            startloading();
        }
    }

    /**
     * Pull data from the /pokemon/{id} endpoint into the current entry
     * Entry is determined by the current loop index
     */
    private void pullDataPokemon(){
        for(int i = start_index; i < end_index; i++){
            // because the index is one behind the id number add 1 to the index
            String url = DOMAIN_POKEMON + (i + 1);
            mRequestQueue[i] = Volley.newRequestQueue(this);
            int finalI = i;
            mStringRequest[i] = new StringRequest(Request.Method.GET, url, response -> {
                try {
                    setNameAndForms(response, entries[finalI]);
                    setImageAndCryUrls(response, entries[finalI]);
                    setMoves(response, entries[finalI]);
                    setTypes(response,entries[finalI]);
                    setNumberHeightAndWeight(response, entries[finalI]);
                } catch (JSONException e) {
                    // the default values for these are already good in case of failure
                }
            }, error -> {
                // log Errors
                Log.d("Error", "API error" + error);
            });

            mRequestQueue[i].add(mStringRequest[i]);
        }
    }

    /**
     * Pull data from the /pokemon-species/{id} endpoint into the current entry
     * Entry is determined by the current loop index
     */
    private void pullDataSpecies(){
        for(int i = start_index; i < end_index; i++){
            // because the index is one behind the id number add 1 to the index
            String url = DOMAIN_SPECIES + (i + 1);
            mRequestQueue[i] = Volley.newRequestQueue(this);
            int finalI = i;
            mStringRequest[i] = new StringRequest(Request.Method.GET, url, response -> {
                try {
                    setSpeciesInfo(response, entries[finalI]);
                } catch (JSONException e) {
                    entries[finalI].setFlavour_text(ERROR_MESSAGE);
                }
            }, error -> {
                // log Errors
                Log.d("Error", "API error" + error);
            });
            mRequestQueue[i].add(mStringRequest[i]);
        }
    }

    /**
     * Start the thread that loads all the data from the api
     * Thread is paused in the onPause method and then continues when
     * the activity is restarted
     */
    private void startloading(){
       new Thread(() -> {
           while(start_index < entries.length){
               // make sure the end_index doesnt go over the array length
               if(end_index > entries.length){
                   end_index = entries.length;
               }
               updateEntries();
           }
           // after all the entries have been added upload the data and replace the fragment
           runOnUiThread(() -> {
               uploadData();
               getSupportFragmentManager().beginTransaction()
                       .setReorderingAllowed(true)
                       .replace(R.id.fragment_view, PokedexFragment.class, null)
                       .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                       .commit();
               getSupportFragmentManager().beginTransaction()
                       .setReorderingAllowed(true)
                       .add(R.id.fragment_navigation, NavBarFragment.class, null)
                       .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                       .commit();
           });
       }).start();
    }

    /**
     * Edit the given name to remove any un wanted information
     * @param name - the name given
     * @return a new name without any extra information
     */
    private String editName(String name) {
        switch (name) {
            case "deoxys-normal":
                return "deoxys";
            case "burmy-plant":
                return "burmy";
            case "wormadam-plant":
                return "wormadam";
            case "mothim-plant":
                return "mothim";
            case "cherrim-overcast":
                return "cherim";
            case "shellos-west":
                return "shellos";
            case "gastrodon-west":
                return "gastrodon";
            case "giratina-altered":
                return "giratina";
            case "shaymin-land":
                return "shaymin";
            case "arceus-normal":
                return "arceus";
            case "basculin-red-striped":
                return "basculin";
            case "darmanitan-standard":
                return "darmanitan";
            case "deerling-spring":
                return "deerling";
            case "sawsbuck-spring":
                return "sawsbuck";
            case "tornadus-incarnate":
                return "tornadus";
            case "thundurus-incarnate":
                return "thundurus";
            case "landorus-incarnate":
                return "landorus";
            case "keldeo-ordinary":
                return "keldeo";
            case "meoletta-aria":
                return "meoletta";
            case "scatterbug-icy-snow":
                return "scatterbug";
            case "spewpa-icy-snow":
                return "spewpa";
            case "vivillon-meadow":
                return "vivillion";
            case "flabebe-red":
                return "flabebe";
            case "floette-red":
                return "floette";
            case "florges-red":
                return "florges";
            case "furfrou-natural":
                return "furfrou";
            case "meowstic-male":
                return "meowstic";
            case "aegislash-shield":
                return "aegislash";
            case "pumpkaboo-average":
                return "pumpkaboo";
            case "gourgeist-average":
                return "gourgeist";
            case "xerneas-active":
                return "xerneas";
            case "zygarde-50":
                return "zygarde";
            case "lycanroc-midday":
                return "lycanroc";
            case "wishiwashi-solo":
                return "wishiwashi";
            case "silvally-normal":
                return "silvally";
            case "minior-red-meteor":
                return "minior";
            case "mimikyu-disguised":
                return "mimikyu";
            case "toxtricity-amped":
                return "toxtricity";
            case "sinistea-phony":
                return "sinistea";
            case "polteageist-phony":
                return "polteageist";
            case "alcremie-vanilla-cream":
                return "alcremie";
            case "indeedee-male":
                return "indeedee";
            case "morpeko-full-belly":
                return "morepeko";
            case "urshifu-single-strike":
                return "urshifu";
            case "basculegion-male":
                return "basculegion";
            case "enamorus-incarnate":
                return "enamorus";
            case "maushold-family-of-four":
                return "maushold";
            case "squawkabilly-green-plumage":
                return "squawkabilly";
            case "dudunsparce-two-segment":
                return "dudunsparce";
            case "gimmighoul-chest":
                return "gimmighoul";
            case "koraidon-apex-build":
                return "koraidon";
            case "miraidon-ultimate-mode":
                return "miraidon";
            case "poltchageist-counterfeit":
                return "poltchageist";
            case "sinistcha-unremarkable":
                return "sinistcha";
            case "unown-a":
                return "unown";
            default:
                return name;
        }
    }

    /**
     * Convert a height given in decimeters to ft and inches
     * @param height - the given height in decimeters
     * @return - a new height string in the form of "#ft, #in"
     */
    private String convertHeight(Number height){
        double height_full = height.floatValue() * 3.93;
        int height_in = (int) Math.floor(height_full % 12);
        int height_ft = (int) Math.floor(height_full/ 12);
        return height_ft + "ft, " + height_in + "in";
    }

    /**
     * Convert a weight given in hectograms to kilograms
     * @param weight - the given weight in hectograms
     * @return - a new string in the form of "#.##kg"
     */
    private String convertWeight(Number weight){
        double weight_full = weight.floatValue() / 10;
        DecimalFormat f = new DecimalFormat("##.00");
        return f.format(weight_full) + "kg";
    }

    /**
     * Go through the given response to find the english flavor text entry
     * @param response - a response from the pokemon-species/{id} endpoint
     * @return - a string representing the english flavor text entry
     * @throws JSONException - if there is a problem with the endpoint throw an exception
     */
    private String findFlavorText(String response) throws JSONException {
        JSONArray flavorText_json = new JSONObject(response).getJSONArray("flavor_text_entries");
        String language;
        for(int i = 0; i < flavorText_json.length(); i++){
            language = flavorText_json.getJSONObject(i).getJSONObject("language").getString("name");
            if(language.equals("en")){
                return flavorText_json.getJSONObject(i).getString("flavor_text");
            }
        }
        return ERROR_MESSAGE;
    }

    /**
     * Set the name and forms array for the given DexEntry
     * @param response - a response from the pokemon/{id} endpoint
     * @param entry - the given DexEntry that you are setting the values for
     * @throws JSONException - if the data doesnt exist throw an exception
     */
    private void setNameAndForms(String response, DexEntry entry) throws JSONException {
        String name = new JSONObject(response).getJSONArray("forms")
                .getJSONObject(0).getString("name");

        JSONArray forms_json = new JSONObject(response).getJSONArray("forms");
        String[] forms = new String[forms_json.length()];
        for(int j = 0; j < forms_json.length(); j++){
            forms[j] = forms_json.getJSONObject(j).getString("url");
        }
        // edit the name before setting it
        entry.setName(editName(name));
        entry.setForms(forms);
    }

    /**
     * Set the image and cry url for the given DexEntry
     * @param response - a response from the pokemon{id} endpoint
     * @param entry - the given DexEntry
     * @throws JSONException - throw an exception if the data doesnt exist
     */
    private void setImageAndCryUrls(String response, DexEntry entry) throws JSONException {
        String image_url =  new JSONObject(response).getJSONObject("sprites")
                .getJSONObject("other").getJSONObject("official-artwork")
                .getString("front_default");
        String cry_url = new JSONObject(response).getJSONObject("cries")
                .getString("latest");

        entry.setImage_url(image_url);
        entry.setCry_url(cry_url);
    }

    /**
     * Create a PokemonMoves array and set it as the given DexEntries move variable
     * @param response - a response from the pokemon/{id} endpoint
     * @param entry - the given DexEntry
     * @throws JSONException - throw an exception if the data doesnt exist
     */
    private void setMoves(String response, DexEntry entry) throws JSONException {
        JSONArray moves_json = new JSONObject(response).getJSONArray("moves");
        PokemonMove[] moves = new PokemonMove[moves_json.length()];
        // loop through moves_json to get every move available to the given DexEntry
        for(int j =0; j < moves_json.length(); j++ ){
            String moveName = moves_json.getJSONObject(j).getJSONObject("move").getString("name");
            String levelLearned = moves_json.getJSONObject(j).getJSONArray("version_group_details")
                    .getJSONObject(0).getString("level_learned_at");
            String methodLearned = moves_json.getJSONObject(j).getJSONArray("version_group_details")
                    .getJSONObject(0).getJSONObject("move_learn_method").getString("name");
            moves[j] = new PokemonMove(levelLearned, methodLearned, moveName);
        }

        entry.setMoves(moves);
    }

    /**
     * Set the types array on a given DexEntry
     * @param response - a response from the pokemon/{id} endpoint
     * @param entry - the given DexEntry
     * @throws JSONException - throw an exception if the data doesnt exist
     */
    private void setTypes(String response, DexEntry entry) throws JSONException {
        JSONArray types_json = new JSONObject(response).getJSONArray("types");
        String[] types = new String[types_json.length()];
        // because there are only 2 possible options if statement seemed easier to read
        if(types_json.length() == 1){
            types[0] = types_json.getJSONObject(0).getJSONObject("type").getString("name");
        } else {
            types[0] = types_json.getJSONObject(0).getJSONObject("type").getString("name");
            types[1] = types_json.getJSONObject(1).getJSONObject("type").getString("name");
        }

        entry.setTypes(types);
    }

    /**
     * Set the number, height, and weight variables for the given DexEntry
     * @param response - a response from the pokemon/{id} endpoint
     * @param entry - the given DexEntry
     * @throws JSONException - throw an exception if the data doesnt exist
     */
    private void setNumberHeightAndWeight(String response, DexEntry entry) throws JSONException {
        String number = new JSONObject(response).getString("id");
        Number height = (Number) new JSONObject(response).get("height");
        Number weight = (Number) new JSONObject(response).get("weight");

        // convert the height and weight values before setting them
        entry.setNumber(number);
        entry.setHeight(convertHeight(height));
        entry.setWeight(convertWeight(weight));
    }

    /**
     * Set the data from the pokemon-species/{id} endpoint to the given DexEntry
     * @param response - a response from the pokemon-species/{id}
     * @param entry - the given DexEntry
     * @throws JSONException - throw a JsonException if the data doesnt exist
     */
    private void setSpeciesInfo(String response, DexEntry entry) throws JSONException {
        String flavor_text = findFlavorText(response);
        JSONArray varieties_json = new JSONObject(response).getJSONArray("varieties");
        String[] varieties = new String[varieties_json.length()];

        for(int j = 0; j < varieties_json.length(); j++){
            varieties[j] = varieties_json.getJSONObject(j).getJSONObject("pokemon").getString("url");
        }
        entry.setFlavour_text(flavor_text);
        entry.setVarieties(varieties);
    }

    /**
     * Function for updating the entries array and the main_bundle
     * This will be placed in a loop and called every 4 seconds
     */
    private void updateEntries(){
        if(isRunning){
            try {
                pullDataPokemon();
                pullDataSpecies();
                // pull data every 4 seconds (or 6 on lab computers)
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // update the indexes before repeating the loop
            start_index = end_index;
            end_index = end_index + INDEX_OFFSET;
        }
    }

    /**
     * Upload the data pulled from the api calls into a shared preferences
     * file so it can be accessed again without making any calls
     */
    private void uploadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("dexData", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for(int i = 0; i < entries.length; i++){
            String jsonData = gson.toJson(entries[i]);
            editor.putString("entry_" + i, jsonData);
        }
        editor.apply();
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // need to do this to use the landscape qualifier
        // still creates problems for rotation on loading screen
        recreate();
    }

    // currently does nothing but may be used in future versions
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}