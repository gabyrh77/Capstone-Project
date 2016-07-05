package com.nanodegree.gaby.bakerylovers.adapters;

import android.app.Activity;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nanodegree.gaby.bakerylovers.R;
import com.nanodegree.gaby.bakerylovers.data.DBContract;
import com.nanodegree.gaby.bakerylovers.utils.Utils;

public class ConfirmOrderAdapter extends RecyclerView.Adapter<ConfirmOrderAdapter.ViewHolder>{
    private Cursor mCursor;

    public ConfirmOrderAdapter(Activity context) {
        super();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_confirm_order, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.productName.setText(mCursor.getString(DBContract.CurrentOrderEntry.COLUMN_PRODUCT_NAME_INDEX));
        double price = mCursor.getDouble(DBContract.CurrentOrderEntry.COLUMN_PRICE_UND_INDEX);
        holder.productPrice.setText(Utils.getCurrencyFormatted(price));
        int amount = mCursor.getInt(DBContract.CurrentOrderEntry.COLUMN_AMOUNT_INDEX);
        holder.amountItem.setText(String.valueOf(amount));
        holder.subtotalItem.setText(Utils.getCurrencyFormatted(price*amount));
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mCursor!=null?mCursor.getCount():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView productName;
        public final TextView productPrice;
        public final TextView amountItem;
        public final TextView subtotalItem;

        public ViewHolder(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.item_name_text);
            productPrice = (TextView) view.findViewById(R.id.item_price_text);
            amountItem = (TextView) view.findViewById(R.id.item_amount_text);
            subtotalItem = (TextView) view.findViewById(R.id.item_subtotal_text);
        }
    }
}
