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
 * Adapter for the Form Recycler view
 * Forms are very similar to Variants honestly they seem interchangeable
 * Used the Variety Adapter as a base for this one
 */
public class FormAdapter extends RecyclerView.Adapter<FormAdapter.ViewHolder> {

    // request variables
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView variety_name;
        ImageView variety_img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            variety_img = itemView.findViewById(R.id.imageview_variety_image);
            variety_name = itemView.findViewById(R.id.textview_variety_name);
        }
    }


    private final String[] forms;

    public FormAdapter(String[] forms) {
        this.forms = forms;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokedex_form_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        loadData(holder, position);
    }

    @Override
    public int getItemCount() {
        return forms.length;
    }

    /**
     * Load data from the form endpoints into the recycler view
     * @param holder - the ViewHolder for this recycler view
     * @param position - the current position in the array
     */
    private void loadData(ViewHolder holder, int position) {
        mRequestQueue = Volley.newRequestQueue(holder.itemView.getContext());
        mStringRequest = new StringRequest(Request.Method.GET, forms[position], response -> {
            try {
                String name = new JSONObject(response).getString("name");
                String image_url = new JSONObject(response).getJSONObject("sprites")
                        .getString("front_default");
                if(image_url.equals("null")){
                    holder.variety_img.setImageResource(R.drawable.null_sprite);
                } else {
                    Picasso.get()
                            .load(image_url)
                            .into(holder.variety_img);
                }
                holder.variety_name.setText(name);
            } catch (JSONException e) {
                holder.variety_img.setImageResource(R.drawable.null_sprite);
            }
        }, error -> {
            // log Errors
            Log.d("Error", "API error" + error);
        });
        mRequestQueue.add(mStringRequest);
    }
}
