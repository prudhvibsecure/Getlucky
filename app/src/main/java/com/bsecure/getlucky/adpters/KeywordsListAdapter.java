package com.bsecure.getlucky.adpters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.models.KeyWords;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KeywordsListAdapter extends RecyclerView.Adapter<KeywordsListAdapter.ContactViewHolder> implements Filterable {

    JSONArray array = new JSONArray();
    private Context context = null;
    private View.OnClickListener onClickListener;
    private KeywordsListListener listener;
    private final LayoutInflater layoutInflater;
    private List<KeyWords> contactList = null;
    private List<KeyWords> contactListFiltered;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;
    private HashMap<Integer, Boolean> isChecked = new HashMap<>();
    boolean isSelectedAll=false;
    public KeywordsListAdapter(List<KeyWords> contactList, Context context, KeywordsListListener listener) {
        this.context = context;
        this.listener = listener;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
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

    @Override
    public int getItemCount() {

        int arr = 0;

        try {
            if (contactListFiltered.size() == 0) {
                arr = 0;

            } else {
                arr = contactListFiltered.size();
            }

        } catch (Exception e) {
        }
        return arr;

    }

    @Override
    public void onBindViewHolder(final ContactViewHolder contactViewHolder, final int position) {

        try {
            final KeyWords mycontactlist = contactListFiltered.get(position);
            if (!isSelectedAll){
                contactViewHolder.checkBox.setChecked(false);
            }
            else {
                contactViewHolder.checkBox.setChecked(true);
            }
            contactViewHolder.store_name.setText(mycontactlist.getKeyword());
            applyClickEvents(contactViewHolder, contactListFiltered, position,contactViewHolder.checkBox);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void selectAll(){
        isSelectedAll=true;
        notifyDataSetChanged();
    }
    public void unselectall(){
        isSelectedAll=false;
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    private void applyClickEvents(ContactViewHolder contactViewHolder, final List<KeyWords> matchesList, final int position, final CheckBox checkBox) {
        contactViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                listener.onRowClicked(matchesList, position,checkBox);
                if (compoundButton.isChecked()) {
                    isChecked.put(position, b);
                } else {
                    isChecked.remove(position);
                }

            }
        });
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_keywords, parent, false);
        ContactViewHolder myHoder = new ContactViewHolder(view);
        return myHoder;

    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView store_name;
        protected CheckBox checkBox;

        public ContactViewHolder(View v) {
            super(v);

            store_name = (TextView) v.findViewById(R.id.key_name);
            checkBox = (CheckBox) v.findViewById(R.id.key_id);
        }
    }

    public interface KeywordsListListener {

        void onRowClicked(List<KeyWords> matchesList, int pos, CheckBox checkBox);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = contactList;
                } else {
                    List<KeyWords> filteredList = new ArrayList<>();
                    for (KeyWords row : contactList) {

                        if (row.getKeyword().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<KeyWords>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
