package com.bsecure.getlucky.adpters;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.models.StoreListModel;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bumptech.glide.Glide;
import com.loopeer.itemtouchhelperextension.Extension;
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;

public class StoreListOwnerAdapter extends RecyclerView.Adapter<StoreListOwnerAdapter.ContactViewHolder> {

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

    public StoreListOwnerAdapter(List<StoreListModel> list, Context context, StoreAdapterListener listener) {
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

            contactViewHolder.tv_address.setText(mycontactlist.getArea() + "," + mycontactlist.getCity() + "," + mycontactlist.getState());
            if (!TextUtils.isEmpty(mycontactlist.getStore_image()) && mycontactlist.getStore_image() != null) {
                Glide.with(context).load(Constants.PATH + "assets/upload/avatar/" + mycontactlist.getStore_image()).into(contactViewHolder.store_image);
            }
            applyClickEvents(contactViewHolder, matchesList, position);
            //applyEvents(contactViewHolder, matchesList, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void applyClickEvents(ContactViewHolder contactViewHolder, final List<StoreListModel> matchesList, final int position) {
        contactViewHolder.mViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onRowClicked(matchesList, position);
                } catch (Exception e) {

                }
            }
        });
        contactViewHolder.tv_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onRowClickedSpecialOffer(matchesList, position);
                } catch (Exception e) {

                }
            }
        });
        contactViewHolder.tv_offer_n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onRowClickedOffer(matchesList, position);
                } catch (Exception e) {

                }
            }
        });
        contactViewHolder.tv_operator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    listener.onRowClickedOperators(matchesList, position);
                } catch (Exception e) {

                }
            }
        });

    }

    private void applyEvents(ContactViewHolder contactViewHolder, final List<StoreListModel> classModelList, final int position) {

        if (contactViewHolder instanceof ItemSwipeWithActionWidthViewHolder) {
            ItemSwipeWithActionWidthViewHolder viewHolder = (ItemSwipeWithActionWidthViewHolder) contactViewHolder;
            viewHolder.mActionViewDelete.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.swipeToDelete(position, classModelList);
                        }
                    }

            );
            if (classModelList.get(position).getStatus().equalsIgnoreCase("0")) {
                ((TextView) viewHolder.mActionViewStatus.findViewById(R.id.view_list_repo_action_status)).setText("In-Active");
            } else {
                ((TextView) viewHolder.mActionViewStatus.findViewById(R.id.view_list_repo_action_status)).setText("Active");
            }
            viewHolder.mActionViewStatus.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.swipeToStatus(position, classModelList);

                        }
                    }

            );
            viewHolder.mActionViewEdit.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.swipeToEdit(position, classModelList);

                        }
                    }

            );

        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_item_row_view_1, parent, false);
        if (viewType == ITEM_TYPE_ACTION_WIDTH)
            return new ItemSwipeWithActionWidthViewHolder(itemView);
        return new ContactViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {

        return ITEM_TYPE_ACTION_WIDTH;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        public TextView store_name, tv_offer, tv_offer_n, tv_operator;
        protected TextView tv_address;
        public View mViewContent;
        ImageView store_image;


        public ContactViewHolder(View v) {
            super(v);

            store_name = (TextView) v.findViewById(R.id.store_name);
            tv_address = (TextView) v.findViewById(R.id.tv_address);
            tv_offer = (TextView) v.findViewById(R.id.tv_offer);
            tv_offer_n = (TextView) v.findViewById(R.id.tv_offer_n);
            tv_operator = (TextView) v.findViewById(R.id.tv_operator);
            store_image = (ImageView) v.findViewById(R.id.store_image);
            mViewContent = v.findViewById(R.id.ll_item);

        }
    }

    public interface StoreAdapterListener {

        void onRowClicked(List<StoreListModel> matchesList, int pos);

        void onRowClickedSpecialOffer(List<StoreListModel> matchesList, int pos);

        void onRowClickedOffer(List<StoreListModel> matchesList, int pos);

        void onRowClickedOperators(List<StoreListModel> matchesList, int pos);

        void swipeToEdit(int position, List<StoreListModel> classModelList);

        void swipeToDelete(int position, List<StoreListModel> classModelList);

        void swipeToStatus(int position, List<StoreListModel> classModelList);
    }

    class ItemSwipeWithActionWidthViewHolder extends ContactViewHolder implements Extension {

        public View mActionViewDelete;
        public View mActionViewStatus;
        public View mActionViewEdit;

        public View mActionContainer;

        public ItemSwipeWithActionWidthViewHolder(View itemView) {
            super(itemView);
            mActionViewDelete = itemView.findViewById(R.id.view_list_repo_action_delete);
            mActionViewStatus = itemView.findViewById(R.id.view_list_repo_action_status);
            mActionViewEdit = itemView.findViewById(R.id.view_list_edit_view);

            mActionContainer = itemView.findViewById(R.id.view_list_repo_action_container);
        }

        @Override
        public float getActionWidth() {
            return mActionContainer.getWidth();
        }
    }
}
