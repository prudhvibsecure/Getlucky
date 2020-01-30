package com.bsecure.getlucky.adpters;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.models.StoreListModel;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bumptech.glide.Glide;
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;

public class ReferlistAdapter extends RecyclerView.Adapter<ReferlistAdapter.ContactViewHolder> {

    JSONArray array = new JSONArray();
    private Context context = null;
    private View.OnClickListener onClickListener;
    private StoreAdapterListener listener;
    private final LayoutInflater layoutInflater;
    private List<StoreListModel> matchesList;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;
    private HashMap<Integer, Boolean> isChecked = new HashMap<>();
    public static final int ITEM_TYPE_RECYCLER_WIDTH = 1000;
    public static final int ITEM_TYPE_ACTION_WIDTH = 1001;
    public static final int ITEM_TYPE_ACTION_WIDTH_NO_SPRING = 1002;
    public static final int ITEM_TYPE_NO_SWIPE = 1003;
    private ItemTouchHelperExtension mItemTouchHelperExtension;

    public ReferlistAdapter(List<StoreListModel> list, Context context, StoreAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.matchesList = list;
        this.layoutInflater = LayoutInflater.from(context);
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    public void setItemTouchHelperExtension(ItemTouchHelperExtension itemTouchHelperExtension) {
        mItemTouchHelperExtension = itemTouchHelperExtension;
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
            contactViewHolder.ss_location.setText(mycontactlist.getStore_referral_code());
            //applyEvents(contactViewHolder, matchesList, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void applyClickEvents(ContactViewHolder contactViewHolder, final List<StoreListModel> matchesList, final int position) {
        contactViewHolder.cashback.setOnClickListener(new View.OnClickListener() {
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

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cash_back_item_store, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        public TextView store_name, ss_location;
        public TextView cashback;
        ImageView store_image;
        CardView mViewContent;


        public ContactViewHolder(View v) {
            super(v);

            store_name = (TextView) v.findViewById(R.id.ss_name);
            ss_location = v.findViewById(R.id.ss_location);
            ss_location.setVisibility(View.VISIBLE);
            cashback = (TextView) v.findViewById(R.id.csh_bk);
            cashback.setVisibility(View.GONE);
            store_image = (ImageView) v.findViewById(R.id.ss_image);
            store_image.setVisibility(View.GONE);
            mViewContent = v.findViewById(R.id.ii_cs_tm);

        }
    }

    public interface StoreAdapterListener {

        void onRowClicked(List<StoreListModel> matchesList, int pos);
    }

}
