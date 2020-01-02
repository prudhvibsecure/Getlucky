package com.bsecure.getlucky.adpters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.models.AccountModel;
import com.bsecure.getlucky.models.KeyWords;
import com.bsecure.getlucky.models.OfferModel;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BankAccountAdapter extends RecyclerView.Adapter<BankAccountAdapter.ContactViewHolder> {

    JSONArray array = new JSONArray();
    private Context context = null;
    private View.OnClickListener onClickListener;
    private BankAccountAdapterListListener listener;
    private List<AccountModel> contactList = null;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private static int currentSelectedIndex = -1;
    private HashMap<Integer, Boolean> isChecked = new HashMap<>();
    boolean isSelectedAll = false;
    private static String keyword = "";
    private List<KeyWords> my_select_list = new ArrayList<>();
    private int lastSelectedPosition = -1;
    private String code_status;
    public BankAccountAdapter(List<AccountModel> contactList, Context context, BankAccountAdapterListListener listener) {
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
            final AccountModel mycontactlist = contactList.get(position);
            contactViewHolder.account_name.setText(mycontactlist.getBank_acc_name());
            contactViewHolder.account_no.setText(mycontactlist.getBank_acc_no());
            applyClickEvents(contactViewHolder, contactList, position);

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

    private void applyClickEvents(ContactViewHolder contactViewHolder, final List<AccountModel> matchesList, final int position) {
        contactViewHolder.acc_ll.setOnClickListener(new View.OnClickListener() {
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

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bank_ac, parent, false);
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

        protected TextView account_name;
        protected ImageView bank_logo;
        protected TextView account_no;
        protected LinearLayout acc_ll;

        public ContactViewHolder(View v) {
            super(v);

            account_name =  v.findViewById(R.id.acc_name);
            account_no = v.findViewById(R.id.acc_no);
            bank_logo = v.findViewById(R.id.logo_bank);
            acc_ll = v.findViewById(R.id.acc_ll);

        }
    }

    public interface BankAccountAdapterListListener {

        void onRowClicked(List<AccountModel> matchesList, int pos);
    }
}