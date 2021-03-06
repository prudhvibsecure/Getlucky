package com.bsecure.getlucky.adpters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.models.KeyWords;
import com.bsecure.getlucky.models.OfferModel;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ContactViewHolder> {

    JSONArray array = new JSONArray();
    private Context context = null;
    private View.OnClickListener onClickListener;
    private OfferListListener listener;
    private List<OfferModel> contactList = null;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private  int currentSelectedIndex = -1;
    private HashMap<Integer, Boolean> isChecked = new HashMap<>();
    boolean isSelectedAll = false;
    private static String keyword = "";
    private List<KeyWords> my_select_list = new ArrayList<>();
    private String code_status;
    public OfferAdapter(List<OfferModel> contactList, Context context, OfferListListener listener,String code) {
        this.context = context;
        this.listener = listener;
        this.contactList = contactList;
        this.code_status = code;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {

        int arr = 0;

        try {
            if (contactList.size() == 0) {
                arr = 0;

            } else {
                arr = contactList.size();
            }

        } catch (Exception e) {
        }
        return arr;

    }

    @Override
    public void onBindViewHolder(final ContactViewHolder contactViewHolder, final int position) {

        try {
            final OfferModel mycontactlist = contactList.get(position);
            contactViewHolder.offer_desc.setText(mycontactlist.getOffer_percent_description());
            contactViewHolder.min.setText("Minimum Amount "+mycontactlist.getMin_amount());
            contactViewHolder.max.setText("Maximum Amount "+mycontactlist.getMax_amount());
            if (code_status.equalsIgnoreCase("2")) {
                if (mycontactlist.getDefault_status().equalsIgnoreCase("1")) {
                    contactViewHolder.selct_rd.setChecked(true);

                } else {
                    contactViewHolder.selct_rd.setChecked(false);
                }
            }else{
                contactViewHolder.selct_rd.setVisibility(View.GONE);
            }
           // contactViewHolder.selct_rd.setChecked(currentSelectedIndex == position);
            applyClickEvents(contactViewHolder, contactList, position,contactViewHolder.selct_rd);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void clear() {
        // matchesList=null;
        final int size = contactList.size();
        contactList.clear();
        notifyItemRangeRemoved(0, size);
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    private void applyClickEvents(ContactViewHolder contactViewHolder, final List<OfferModel> matchesList, final int position, final RadioButton selct_rd) {
        contactViewHolder.selct_rd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onRowClickedPos2(matchesList, position,selct_rd);
                } catch (Exception e) {

                }
            }
        });
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_row_item_n, parent, false);
        ContactViewHolder myHoder = new ContactViewHolder(view);
        return myHoder;

    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView offer_desc;
        protected TextView min;
        protected TextView max;
        protected RadioButton selct_rd;

        public ContactViewHolder(View v) {
            super(v);

            offer_desc = (TextView) v.findViewById(R.id.sp_off1);
            min = (TextView) v.findViewById(R.id.min);
            max = (TextView) v.findViewById(R.id.max);
            selct_rd =  v.findViewById(R.id.offer_select_n);
        }
    }

    public interface OfferListListener {

        void onRowClickedOffer(List<OfferModel> matchesList, int pos);
        void onRowClickedPos2(List<OfferModel> matchesList, int position, RadioButton selct_rd);
    }
}