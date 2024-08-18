package com.example.pokedex.TeamBuilderFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pokedex.R;
import com.example.pokedex.TeamBuilderFragment.data_layer.Team;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * ViewHolder for the TeamList recycler that
 * holds imageviews that will be set using the
 * names given from the database
 */
public class TeamViewHolder extends RecyclerView.ViewHolder {

    private final String DOMAIN = "https://pokeapi.co/api/v2/pokemon/";
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private final ImageView teamItemView_slot1;
    private final ImageView teamItemView_slot2;
    private final ImageView teamItemView_slot3;
    private final ImageView teamItemView_slot4;
    private final ImageView teamItemView_slot5;
    private final ImageView teamItemView_slot6;

    private TeamViewHolder(View itemView) {
        super(itemView);
        teamItemView_slot1 = itemView.findViewById(R.id.textview_slot_1);
        teamItemView_slot2 = itemView.findViewById(R.id.textview_slot_2);
        teamItemView_slot3 = itemView.findViewById(R.id.textview_slot_3);
        teamItemView_slot4 = itemView.findViewById(R.id.textview_slot_4);
        teamItemView_slot5 = itemView.findViewById(R.id.textview_slot_5);
        teamItemView_slot6 = itemView.findViewById(R.id.textview_slot_6);
    }

    public void bind(Team team) {
        // create strings representing each team members url
        String url_1 = DOMAIN + team.getSlot_1();
        String url_2 = DOMAIN + team.getSlot_2();
        String url_3 = DOMAIN + team.getSlot_3();
        String url_4 = DOMAIN + team.getSlot_4();
        String url_5 = DOMAIN + team.getSlot_5();
        String url_6 = DOMAIN + team.getSlot_6();

        // load pictures from the urls
        loadPicture(url_1, teamItemView_slot1);
        loadPicture(url_2, teamItemView_slot2);
        loadPicture(url_3, teamItemView_slot3);
        loadPicture(url_4, teamItemView_slot4);
        loadPicture(url_5, teamItemView_slot5);
        loadPicture(url_6, teamItemView_slot6);
    }

    static TeamViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.team_list_item, parent, false);
        return new TeamViewHolder(view);
    }

    /**
     * Load pictures from a given url using the pokemon{name} endpoint
     * and load them into the given slot
     * @param url - String representing the given url
     * @param slot - Imageview representing the given slot
     */
    private void loadPicture(String url, ImageView slot){
        mRequestQueue = Volley.newRequestQueue(itemView.getContext());
        mStringRequest= new StringRequest(Request.Method.GET, url, response -> {
            try {
                String sprite = new JSONObject(response).getJSONObject("sprites")
                        .getString("front_default");
                Picasso.get()
                        .load(sprite)
                        .into(slot);
            } catch (JSONException e) {
                // if the sprite doesnt exist replace it with a question mark
                // this should only happen for the ??? name
                // some slots may still be null but they will keep the null pokeball picture
                slot.setImageResource(R.drawable.question);
            }
        }, error -> {
            // log Errors
            Log.d("Error", "API error" + error);
        });
        mRequestQueue.add(mStringRequest);
    }
}
