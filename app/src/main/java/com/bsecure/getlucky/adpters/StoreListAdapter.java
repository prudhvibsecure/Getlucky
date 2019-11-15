package com.bsecure.getlucky.adpters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.models.StoreListModel;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bumptech.glide.Glide;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.ContactViewHolder> {

    JSONArray array = new JSONArray();
    private Context context = null;
    private View.OnClickListener onClickListener;
    private StoreAdapterListener listener;
    private final LayoutInflater layoutInflater;
    private List<StoreListModel> matchesList = null;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;
    private HashMap<Integer, Boolean> isChecked = new HashMap<>();

    public StoreListAdapter(List<StoreListModel> list, Context context, StoreAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.matchesList = list;
        this.layoutInflater = LayoutInflater.from(context);
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void clear() {
        // matchesList=null;
        final int size = matchesList.size();
        matchesList.clear();
        notifyItemRangeRemoved(0, size);
    }
    public void addItems(List<StoreListModel> List) {
        matchesList.addAll(List);
        notifyDataSetChanged();
    }
    public void addLoading() {

        matchesList.add(new StoreListModel());
        notifyItemInserted(matchesList.size() - 1);
    }
    @Override
    public int getItemCount() {

        int arr = 0;

        try {
            if (matchesList.size() == 0) {
                arr = 0;

            } else {
                arr = matchesList.size();
            }

        } catch (Exception e) {
        }
        return arr;

    }

    @Override
    public void onBindViewHolder(final ContactViewHolder contactViewHolder, final int position) {

        try {
            final StoreListModel mycontactlist = matchesList.get(position);
            contactViewHolder.store_name.setText(mycontactlist.getStore_name());
            contactViewHolder.tv_address.setText(mycontactlist.getArea()+","+mycontactlist.getCity()+","+mycontactlist.getState());
            Glide.with(context).load(Constants.PATH+"assets/upload/avatar/" + mycontactlist.getStore_image()).into(contactViewHolder.store_image);
            applyClickEvents(contactViewHolder, matchesList, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    private void applyClickEvents(ContactViewHolder contactViewHolder, final List<StoreListModel> matchesList, final int position) {
        contactViewHolder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onRowClicked(matchesList, position);
                } catch (Exception e) {

                }
            }
        });

    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_item_row_view, parent, false);
        ContactViewHolder myHoder = new ContactViewHolder(view);
        return myHoder;

    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView store_name;
        protected TextView tv_address;
        LinearLayout ll_item;

        ImageView store_image;
        public View mViewContent;
        public View mActionContainer;

        public ContactViewHolder(View v) {
            super(v);

            store_name = (TextView) v.findViewById(R.id.store_name);
            tv_address = (TextView) v.findViewById(R.id.tv_address);
            store_image = (ImageView) v.findViewById(R.id.store_image);
            ll_item =  v.findViewById(R.id.ll_item);
            mActionContainer = itemView.findViewById(R.id.view_list_repo_action_container);

        }
    }

    public interface StoreAdapterListener {

        void onRowClicked(List<StoreListModel> matchesList, int pos);
    }
}
