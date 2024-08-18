package com.example.pokedex.DexDataFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pokedex.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Adapter for the DexEntries Varieties
 * This is very similar to the Forms Adapter
 * If there is time condense these classes
 */
public class VarietyAdapter extends RecyclerView.Adapter<VarietyAdapter.ViewHolder>{

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;


    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView variety_name;
        ImageView variety_img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            variety_img = itemView.findViewById(R.id.imageview_variety_image);
            variety_name = itemView.findViewById(R.id.textview_variety_name);
        }
    }


    private String[] varieties;

    public VarietyAdapter(String[] varieties){this.varieties = varieties;}


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.variety_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        loadData(holder, position);
    }

    @Override
    public int getItemCount() {
        return varieties.length;
    }

    /**
     * Load data from the varieties endpoints into the recycler view
     * @param holder - the ViewHolder for this recycler view
     * @param position - the current position in the array
     */
    private void loadData(ViewHolder holder, int position){
        mRequestQueue = Volley.newRequestQueue(holder.itemView.getContext());
        mStringRequest = new StringRequest(Request.Method.GET, varieties[position], response -> {
            try {
                String name = new JSONObject(response).getJSONArray("forms")
                        .getJSONObject(0).getString("name");
                String image_url =  new JSONObject(response).getJSONObject("sprites")
                        .getJSONObject("other").getJSONObject("official-artwork")
                        .getString("front_default");

                holder.variety_name.setText(name);
                Picasso.get()
                        .load(image_url)
                        .into(holder.variety_img);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            // log Errors
            Log.d("Error", "API error" + error);
        });
        mRequestQueue.add(mStringRequest);
    }
}
