package com.bsecure.getlucky.adpters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.interfaces.ClickListener;

import org.json.JSONArray;
import org.json.JSONException;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    Context context;
    JSONArray array;
    LayoutInflater inflater;
    ClickListener listener;

    public LocationAdapter(Context context, JSONArray array, ClickListener listener)
    {
        this.context = context;
        this.array = array;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }
    @NonNull
    @Override
    public LocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.location_item,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationAdapter.ViewHolder holder, final int position) {

        try {
            holder.area.setText(array.getJSONObject(position).optString("area"));
            holder.city.setText(array.getJSONObject(position).optString("city") + ", " + array.getJSONObject(position).optString("state"));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(position);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView area, city;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            area = itemView.findViewById(R.id.area);
            city = itemView.findViewById(R.id.city);
        }
    }


}
