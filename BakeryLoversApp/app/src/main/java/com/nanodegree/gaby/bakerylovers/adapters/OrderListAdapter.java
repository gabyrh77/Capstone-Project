package com.nanodegree.gaby.bakerylovers.adapters;

import android.app.Activity;
import android.database.Cursor;
import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.nanodegree.gaby.bakerylovers.R;
import com.nanodegree.gaby.bakerylovers.data.DBContract;
import com.nanodegree.gaby.bakerylovers.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gaby_ on 19/6/2016.
 */

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {

    private List<OrderItem> mOrderItems;
    final private View mEmptyView;
    final private Activity mContext;
    final int MAX_DETAIL_VIEWS = 2;

    public OrderListAdapter(Activity context, View emptyView) {
        super();
        mContext = context;
        mEmptyView = emptyView;
        mOrderItems = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_list, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mOrderItems.size() > position) {
            OrderItem item = mOrderItems.get(position);
            holder.orderNumber.setText(mContext.getString(R.string.text_order_number, item.getOrderId()));
            holder.orderTotal.setText(mContext.getString(R.string.text_order_total,
                    Utils.getCurrencyFormatted(item.getTotalOrder())));
            if (item.getDeliveredDate() == null) {
                holder.orderState.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                holder.orderState.setText(mContext.getString(R.string.text_order_placed));
            } else {
                holder.orderState.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                holder.orderState.setText(mContext.getString(R.string.text_order_delivered));
            }
            holder.orderAddress.setText(mContext.getString(R.string.text_order_address, item.getAddress()));
            holder.orderPlaced.setText(item.placedDate);
            holder.toggleExpandButton.setTag(position);
            holder.detailView.removeAllViews();
            holder.expandView.removeAllViews();
            boolean needExpandView = false;
            if (item.getDetails() != null && item.getDetails().size() > 0) {
                List<OderDetailItem> details = item.getDetails();
                needExpandView = details.size() > MAX_DETAIL_VIEWS;
                int limitDetails = needExpandView ? MAX_DETAIL_VIEWS : details.size();
                for (int i = 0; i < limitDetails; i++) {
                    OderDetailItem detailItem = details.get(i);
                    View newChild = LayoutInflater.from(mContext)
                            .inflate(R.layout.item_confirm_order, holder.detailView, false);
                    TextView textAmount = (TextView)newChild.findViewById(R.id.item_amount_text);
                    textAmount.setText(String.valueOf(detailItem.getAmount()));
                    TextView textName = (TextView)newChild.findViewById(R.id.item_name_text);
                    textName.setText(detailItem.getProductName());
                    TextView textPrice = (TextView)newChild.findViewById(R.id.item_price_text);
                    textPrice.setText(Utils.getCurrencyFormatted(detailItem.getProductPrice()));
                    TextView textSubtotal = (TextView)newChild.findViewById(R.id.item_subtotal_text);
                    textSubtotal.setText(Utils.getCurrencyFormatted(detailItem.getTotalProduct()));
                    if (i + 1 == limitDetails && !item.isExpanded()) {
                        newChild.findViewById(R.id.item_divider_view).setVisibility(View.GONE);
                    }
                    holder.detailView.addView(newChild);
                }

                if (needExpandView) {
                    for (int i = limitDetails; i < details.size(); i++) {
                        OderDetailItem detailItem = details.get(i);
                        View newChild = LayoutInflater.from(mContext)
                                .inflate(R.layout.item_confirm_order, holder.expandView, false);
                        TextView textAmount = (TextView)newChild.findViewById(R.id.item_amount_text);
                        textAmount.setText(String.valueOf(detailItem.getAmount()));
                        TextView textName = (TextView)newChild.findViewById(R.id.item_name_text);
                        textName.setText(detailItem.getProductName());
                        TextView textPrice = (TextView)newChild.findViewById(R.id.item_price_text);
                        textPrice.setText(Utils.getCurrencyFormatted(detailItem.getProductPrice()));
                        TextView textSubtotal = (TextView)newChild.findViewById(R.id.item_subtotal_text);
                        textSubtotal.setText(Utils.getCurrencyFormatted(detailItem.getTotalProduct()));
                        if (i + 1 == details.size()) {
                            newChild.findViewById(R.id.item_divider_view).setVisibility(View.GONE);
                        }
                        holder.expandView.addView(newChild);
                    }
                }
            }

            if (needExpandView) {
                holder.toggleExpandButton.setVisibility(View.VISIBLE);
                if (item.isExpanded()) {
                    holder.toggleExpandButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_keyboard_arrow_up, 0, 0, 0);
                    holder.toggleExpandButton.setText(mContext.getString(R.string.action_view_less));
                    holder.expandView.setVisibility(View.VISIBLE);
                } else {
                    holder.toggleExpandButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_keyboard_arrow_down, 0, 0, 0);
                    holder.toggleExpandButton.setText(mContext.getString(R.string.action_view_all));
                    holder.expandView.setVisibility(View.GONE);
                }
            } else {
                holder.expandView.setVisibility(View.GONE);
                holder.toggleExpandButton.setVisibility(View.GONE);
            }

        }
    }

    public void swapCursor(Cursor cursor) {
        mOrderItems.clear();
        if (cursor != null && cursor.getCount() > 0) {
            long orderId = 0, previousOrderId;
            for (int i = 0; i < cursor.getCount(); i++) {
                if (i == 0) {
                    cursor.moveToPosition(i);
                    orderId = cursor.getLong(DBContract.OrderEntry.COLUMN_ORDER_ID_INDEX);
                }
                previousOrderId = orderId;
                OrderItem newItem = new OrderItem();
                newItem.setOrderId(orderId);
                newItem.setPlacedDate(Utils.getDateFormatted(cursor.getLong(DBContract.OrderEntry.COLUMN_PLACED_DATE_INDEX)));
                newItem.setDeliveredDate(Utils.getDateFormatted(cursor.getLong(DBContract.OrderEntry.COLUMN_DELIVERED_DATE_INDEX)));
                newItem.setTotalDelivery(cursor.getDouble(DBContract.OrderEntry.COLUMN_TOTAL_DELIVERY_INDEX));
                newItem.setTotalOrder(cursor.getDouble(DBContract.OrderEntry.COLUMN_TOTAL_PRICE_INDEX));
                newItem.setAddress(cursor.getString(DBContract.OrderEntry.COLUMN_ADDRESS_INDEX));
                newItem.setExpanded(false);
                List<OderDetailItem> details = new ArrayList<>();

                while (orderId == previousOrderId) {
                    OderDetailItem detail = new OderDetailItem();
                    detail.setProductId(cursor.getLong(DBContract.OrderEntry.COLUMN_PRODUCT_ID_INDEX));
                    detail.setProductName(cursor.getString(DBContract.OrderEntry.COLUMN_PRODUCT_NAME_INDEX));
                    detail.setProductPhoto(cursor.getString(DBContract.OrderEntry.COLUMN_PRODUCT_PHOTO_INDEX));
                    detail.setProductPrice(cursor.getDouble(DBContract.OrderEntry.COLUMN_PRODUCT_PRICE_INDEX));
                    detail.setTotalProduct(cursor.getDouble(DBContract.OrderEntry.COLUMN_PRODUCT_TOTAL_INDEX));
                    detail.setAmount(cursor.getInt(DBContract.OrderEntry.COLUMN_PRODUCT_AMOUNT_INDEX));
                    details.add(detail);

                    i++;
                    if (i < cursor.getCount()) {
                        cursor.moveToPosition(i);
                        orderId = cursor.getLong(DBContract.OrderEntry.COLUMN_ORDER_ID_INDEX);
                    } else {
                        break;
                    }
                }
                newItem.setDetails(details);
                mOrderItems.add(newItem);
                notifyItemChanged(mOrderItems.size() - 1);
            }
        } else {
            //TODO: SHOW EMPTY VIEW
        }
    }

    @Override
    public int getItemCount() {
        return mOrderItems.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView orderNumber;
        public final TextView orderTotal;
        public final TextView orderPlaced;
        public final TextView orderState;
        public final TextView orderAddress;
        public final GridLayout detailView;
        public final GridLayout expandView;
        public final Button toggleExpandButton;

        public ViewHolder(View view) {
            super(view);
            orderNumber = (TextView) view.findViewById(R.id.order_number);
            orderPlaced = (TextView) view.findViewById(R.id.order_placed);
            orderState = (TextView) view.findViewById(R.id.order_state);
            orderTotal = (TextView) view.findViewById(R.id.order_total);
            orderAddress = (TextView) view.findViewById(R.id.order_address);
            detailView = (GridLayout) view.findViewById(R.id.detail_view);
            expandView = (GridLayout) view.findViewById(R.id.expand_view);
            toggleExpandButton = (Button) view.findViewById(R.id.order_expand_button);
            toggleExpandButton.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int position = (int)v.getTag();
            mOrderItems.get(position).setExpanded(!mOrderItems.get(position).isExpanded());
            notifyItemChanged(position);
        }
    }

    protected class OderDetailItem {
        private long productId;
        private Double productPrice;
        private String productName;
        private String productPhoto;
        private double totalProduct;
        private int amount;

        public long getProductId() {
            return productId;
        }

        public void setProductId(long productId) {
            this.productId = productId;
        }

        public Double getProductPrice() {
            return productPrice;
        }

        public void setProductPrice(Double productPrice) {
            this.productPrice = productPrice;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductPhoto() {
            return productPhoto;
        }

        public void setProductPhoto(String productPhoto) {
            this.productPhoto = productPhoto;
        }

        public double getTotalProduct() {
            return totalProduct;
        }

        public void setTotalProduct(double totalProduct) {
            this.totalProduct = totalProduct;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }

    protected class OrderItem {
        private long orderId;
        private boolean expanded;
        private String address;
        private String placedDate;
        private String deliveredDate;
        private double totalOrder;
        private double totalDelivery;
        private List<OderDetailItem> details;

        public long getOrderId() {
            return orderId;
        }

        public void setOrderId(long orderId) {
            this.orderId = orderId;
        }

        public String getPlacedDate() {
            return placedDate;
        }

        public void setPlacedDate(String placedDate) {
            this.placedDate = placedDate;
        }

        public String getDeliveredDate() {
            return deliveredDate;
        }

        public void setDeliveredDate(String deliveredDate) {
            this.deliveredDate = deliveredDate;
        }

        public double getTotalOrder() {
            return totalOrder;
        }

        public void setTotalOrder(double totalOrder) {
            this.totalOrder = totalOrder;
        }

        public double getTotalDelivery() {
            return totalDelivery;
        }

        public void setTotalDelivery(double totalDelivery) {
            this.totalDelivery = totalDelivery;
        }

        public List<OderDetailItem> getDetails() {
            return details;
        }

        public void setDetails(List<OderDetailItem> details) {
            this.details = details;
        }

        public boolean isExpanded() {
            return expanded;
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
