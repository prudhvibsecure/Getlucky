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

public class SpecialOfferAdapter  extends RecyclerView.Adapter<SpecialOfferAdapter.ContactViewHolder> {

    JSONArray array = new JSONArray();
    private Context context = null;
    private View.OnClickListener onClickListener;
    private SpecialOfferListListener listener;
    private List<OfferModel> contactList = null;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;
    private HashMap<Integer, Boolean> isChecked = new HashMap<>();
    boolean isSelectedAll = false;
    private static String keyword = "";
    private List<KeyWords> my_select_list = new ArrayList<>();
    private int lastSelectedPosition = -1;
    private String code_status;
    public SpecialOfferAdapter(List<OfferModel> contactList, Context context, SpecialOfferListListener listener,String code_status) {
        this.context = context;
        this.listener = listener;
        this.contactList = contactList;
        this.code_status = code_status;
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
            contactViewHolder.offer_desc.setText(mycontactlist.getOffer_description());
            if (code_status.equalsIgnoreCase("2")) {
                if (mycontactlist.getDefault_status().equalsIgnoreCase("1")) {
                    contactViewHolder.offer_select.setChecked(true);

                } else {
                    contactViewHolder.offer_select.setChecked(false);
                }
            }else{
                contactViewHolder.offer_select.setVisibility(View.GONE);
            }
            //contactViewHolder.offer_select.setChecked(lastSelectedPosition == position);
            applyClickEvents(contactViewHolder, contactList, position,contactViewHolder.offer_select);

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

    private void applyClickEvents(ContactViewHolder contactViewHolder, final List<OfferModel> matchesList, final int position, final RadioButton offer_select) {
        contactViewHolder.offer_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onRowClickedPos(matchesList, position,offer_select);
                } catch (Exception e) {

                }
            }
        });
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_row_item, parent, false);
        ContactViewHolder myHoder = new ContactViewHolder(view);
        return myHoder;

    }
    public void clear() {
        // matchesList=null;
        final int size = contactList.size();
        contactList.clear();
        notifyItemRangeRemoved(0, size);
    }
    public class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView offer_desc;
        protected RadioButton offer_select;

        public ContactViewHolder(View v) {
            super(v);

            offer_desc = (TextView) v.findViewById(R.id.sp_off);
            offer_select = v.findViewById(R.id.offer_select);
           
        }
    }

    public interface SpecialOfferListListener {

        void onRowClicked(List<OfferModel> matchesList, int pos);

        void onRowClickedPos(List<OfferModel> matchesList, int position, RadioButton offer_select);
    }
}