package com.bsecure.getlucky.adpters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bsecure.getlucky.R;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OfferPercentageAdapter extends RecyclerView.Adapter<OfferPercentageAdapter.ViewHolder> {
    JSONArray array;
    Context context;
    LayoutInflater inflater;

    public OfferPercentageAdapter(Context context, JSONArray array)
    {
        this.context = context;
        this.array = array;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public OfferPercentageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.percent_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferPercentageAdapter.ViewHolder holder, int position) {

        try {

            JSONObject obj = array.getJSONObject(position);
            String amount = obj.optString("balanace_cash");
            String per = obj.optString("offer_percentage");
            holder.text.setText("Rs\t" + amount + "\t or less for " + per + "% offer");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.text);
        }
    }
}
