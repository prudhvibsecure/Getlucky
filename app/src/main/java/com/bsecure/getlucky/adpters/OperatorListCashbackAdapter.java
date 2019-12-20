package com.bsecure.getlucky.adpters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.models.OperatorModel;
import com.bsecure.getlucky.models.StoreListModel;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;

public class OperatorListCashbackAdapter extends RecyclerView.Adapter<OperatorListCashbackAdapter.ContactViewHolder> {

    JSONArray array = new JSONArray();
    private Context context = null;
    private View.OnClickListener onClickListener;
    private OperatorListAdapterListener listener;
    private final LayoutInflater layoutInflater;
    private List<OperatorModel> matchesList;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;
    private HashMap<Integer, Boolean> isChecked = new HashMap<>();

    public OperatorListCashbackAdapter(List<OperatorModel> list, Context context, OperatorListAdapterListener listener) {
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

    public void addItems(List<OperatorModel> List) {
        matchesList.addAll(List);
        notifyDataSetChanged();
    }

    public void addLoading() {

        matchesList.add(new OperatorModel());
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
            final OperatorModel mycontactlist = matchesList.get(position);
            contactViewHolder.op_name.setText(mycontactlist.getOperator_name());
            contactViewHolder.op_name_ur.setText(mycontactlist.getUsername());
            contactViewHolder.op_name_ps.setText(mycontactlist.getPassword());

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

    private void applyClickEvents(ContactViewHolder contactViewHolder, final List<OperatorModel> matchesList, final int position) {
        contactViewHolder.mViewContent.setOnClickListener(new View.OnClickListener() {
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

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.operator_item_row, parent, false);
        ContactViewHolder myHoder = new ContactViewHolder(view);
        return myHoder;

    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView op_name;
        protected TextView op_name_ur;
        protected TextView op_name_ps;

        LinearLayout mViewContent;
        public ContactViewHolder(View v) {
            super(v);

            op_name = (TextView) v.findViewById(R.id.op_name);
            op_name_ur = (TextView) v.findViewById(R.id.op_name_ur);
            op_name_ps = (TextView) v.findViewById(R.id.op_name_ps);
            mViewContent =  v.findViewById(R.id.ad_opclcik);


        }
    }

    public interface OperatorListAdapterListener {

        void onRowClicked(List<OperatorModel> matchesList, int pos);
    }
}