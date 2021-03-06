package com.nanodegree.gaby.bakerylovers.adapters;

import android.app.Activity;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nanodegree.gaby.bakerylovers.R;
import com.nanodegree.gaby.bakerylovers.data.DBContract;
import com.nanodegree.gaby.bakerylovers.utils.Utils;

public class CurrentOrderAdapter extends RecyclerView.Adapter<CurrentOrderAdapter.ViewHolder>{
    private Cursor mCursor;
    final private View mEmptyView;
    final private Activity mContext;
    final private CurrentOrderAdapterOnClickHandler mClickHandler;

    public CurrentOrderAdapter(Activity context, View emptyView) {
        super();
        mContext = context;
        mEmptyView = emptyView;
        mClickHandler = (CurrentOrderAdapterOnClickHandler) mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_current_order_list, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.productName.setText(mCursor.getString(DBContract.CurrentOrderEntry.COLUMN_PRODUCT_NAME_INDEX));
        holder.productPrice.setText(Utils.getCurrencyFormatted(mCursor.getDouble(DBContract.CurrentOrderEntry.COLUMN_PRICE_UND_INDEX)));
        holder.amountEdit.setText(String.valueOf(mCursor.getInt(DBContract.CurrentOrderEntry.COLUMN_AMOUNT_INDEX)));

        if (!mCursor.isNull(DBContract.ProductEntry.COLUMN_PHOTO_URL_INDEX)){
            Glide.with(this.mContext)
                    .load(mCursor.getString(DBContract.CurrentOrderEntry.COLUMN_PRODUCT_PHOTO_URL_INDEX))
                    .placeholder(R.drawable.no_image)
                    .into(holder.productImage);
        }
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        if (mEmptyView != null) {
            mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
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
        public final Button removeButton;

        public ViewHolder(View view) {
            super(view);
            productImage = (ImageView) view.findViewById(R.id.item_current_image);
            productName = (TextView) view.findViewById(R.id.item_current_name_text);
            productPrice = (TextView) view.findViewById(R.id.item_current_price_text);
            amountEdit = (EditText) view.findViewById(R.id.item_current_amount_text);
            removeButton = (Button) view.findViewById(R.id.remove_button);
            amountEdit.setClickable(true);
            amountEdit.setOnClickListener(this);
            removeButton.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long id = mCursor.getLong(DBContract.CurrentOrderEntry.COLUMN_PRODUCT_ID_INDEX);
            if (view.getId() == R.id.item_current_amount_text) {
                int amount = mCursor.getInt(DBContract.CurrentOrderEntry.COLUMN_AMOUNT_INDEX);
                mClickHandler.onAmountItemClick(id, amount);
            } else if (view.getId() == R.id.remove_button) {
                mClickHandler.onDeleteProductClick(id);
            }else {
                mClickHandler.onProductItemClick(id);
            }
        }
    }

    public interface CurrentOrderAdapterOnClickHandler {
        void onProductItemClick(long productId);
        void onAmountItemClick(long productId, int amount);
        void onDeleteProductClick(long productId);
    }
}
