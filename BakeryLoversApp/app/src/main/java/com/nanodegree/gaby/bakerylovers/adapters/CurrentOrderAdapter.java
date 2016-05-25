package com.nanodegree.gaby.bakerylovers.adapters;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nanodegree.gaby.bakerylovers.R;
import com.nanodegree.gaby.bakerylovers.data.DBContract;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrentOrderAdapter extends RecyclerView.Adapter<CurrentOrderAdapter.ViewHolder>{
    private Cursor mCursor;
    final private View mEmptyView;
    final private Activity mContext;
    final private CurrentOrderAdapterOnClickHandler mClickHandler;
    final private ItemChoiceManager mICM;
    private NumberFormat mCurrencyFormat;

    public CurrentOrderAdapter(Activity context, View emptyView, int choiceMode) {
        super();
        mContext = context;
        mEmptyView = emptyView;
        mClickHandler = (CurrentOrderAdapterOnClickHandler) mContext;
        mICM = new ItemChoiceManager(this);
        mICM.setChoiceMode(choiceMode);
        mCurrencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CR"));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_current_order_list, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.productName.setText(mCursor.getString(DBContract.CurrentOrderEntry.COLUMN_PRODUCT_NAME_INDEX));
        holder.productPrice.setText(mCurrencyFormat.format(mCursor.getDouble(DBContract.CurrentOrderEntry.COLUMN_PRICE_UND_INDEX)));
        Log.d("adapter position: ", String.valueOf(position));

        if (!mCursor.isNull(DBContract.ProductEntry.COLUMN_PHOTO_URL_INDEX)){
            Glide.with(this.mContext).load(mCursor.getString(DBContract.CurrentOrderEntry.COLUMN_PRODUCT_PHOTO_URL_INDEX)).into(holder.productImage);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mICM.onRestoreInstanceState(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        mICM.onSaveInstanceState(outState);
    }

    public int getSelectedItemPosition() {
        return mICM.getSelectedItemPosition();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        // mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mCursor!=null?mCursor.getCount():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView productImage;
        public final TextView productName;
        public final TextView productPrice;
        public final EditText amountEdit;

        public ViewHolder(View view) {
            super(view);
            productImage = (ImageView) view.findViewById(R.id.item_current_image);
            productName = (TextView) view.findViewById(R.id.item_current_name_text);
            productPrice = (TextView) view.findViewById(R.id.item_current_price_text);
            amountEdit = (EditText) view.findViewById(R.id.item_current_amount_text);
            amountEdit.setClickable(true);
            amountEdit.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long id = mCursor.getLong(DBContract.CurrentOrderEntry.COLUMN_PRODUCT_ID_INDEX);
            Log.d("C. order adapter click", "product id " + String.valueOf(id));
            if (view instanceof EditText) {
                mClickHandler.onAmountItemClick(mCursor.getLong(0));
                //TODO: OPEN NUMBER PICKER
            } else {
                mClickHandler.onProductItemClick(id);
                mICM.onClick(this);
            }
        }
    }

    public interface CurrentOrderAdapterOnClickHandler {
        void onProductItemClick(long productId);
        void onAmountItemClick(long rowId);
    }
}
