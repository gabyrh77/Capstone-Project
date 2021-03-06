package com.nanodegree.gaby.bakerylovers.adapters;

import android.app.Activity;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nanodegree.gaby.bakerylovers.R;
import com.nanodegree.gaby.bakerylovers.data.DBContract;
import com.nanodegree.gaby.bakerylovers.utils.Utils;

public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.ViewHolder>{
    private Cursor mCursor;
    final private View mEmptyView;
    final private Activity mContext;
    final private MenuListAdapterOnClickHandler mClickHandler;

    public MenuListAdapter(Activity context, View emptyView) {
        super();
        mContext = context;
        mEmptyView = emptyView;
        mClickHandler = (MenuListAdapterOnClickHandler) mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_list, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.productName.setText(mCursor.getString(DBContract.ProductEntry.COLUMN_NAME_INDEX));
        holder.productPrice.setText(Utils.getCurrencyFormatted(mCursor.getDouble(DBContract.ProductEntry.COLUMN_PRICE_INDEX)));
        if (mCursor.getInt(DBContract.ProductEntry.COLUMN_AVAILABLE_INDEX) > 0) {
            holder.currentOrderButton.setEnabled(true);
            if (mCursor.isNull(DBContract.ProductEntry.COLUMN_CURRENT_AMOUNT_INDEX)) {
                holder.currentOrderButton.setContentDescription(mContext.getString(R.string.product_add_to_cart));
                holder.currentOrderButton.setTag(false);
                holder.currentOrderButton.setImageResource(R.drawable.ic_add_shopping_cart);
            } else {
                holder.currentOrderButton.setContentDescription(mContext.getString(R.string.product_added_to_cart));
                holder.currentOrderButton.setTag(true);
                holder.currentOrderButton.setImageResource(R.drawable.ic_add_shopping_cart);
            }
        } else {
            holder.currentOrderButton.setContentDescription(mContext.getString(R.string.product_not_available));
            holder.currentOrderButton.setEnabled(false);
            holder.currentOrderButton.setImageResource(R.drawable.ic_remove_shopping_cart);
        }
        if (!mCursor.isNull(DBContract.ProductEntry.COLUMN_PHOTO_URL_INDEX)){
            Glide.with(this.mContext)
                    .load(mCursor.getString(DBContract.ProductEntry.COLUMN_PHOTO_URL_INDEX))
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView productImage;
        public final TextView productName;
        public final TextView productPrice;
        public final ImageButton currentOrderButton;

        public ViewHolder(View view) {
            super(view);
            productImage = (ImageView) view.findViewById(R.id.item_product_image);
            productName = (TextView) view.findViewById(R.id.item_product_name_text);
            productPrice = (TextView) view.findViewById(R.id.item_product_price_text);
            currentOrderButton = (ImageButton) view.findViewById(R.id.current_order_button);
            currentOrderButton.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long id = mCursor.getLong(DBContract.ProductEntry.COLUMN_PRODUCT_ID_INDEX);
            if (view instanceof ImageButton) {
                Double price = mCursor.getDouble(mCursor.getColumnIndex((DBContract.ProductEntry.COLUMN_PRICE)));
                String name = mCursor.getString(mCursor.getColumnIndex(DBContract.ProductEntry.COLUMN_NAME));
                mClickHandler.onToggleOrderItemClick((Boolean) view.getTag(), id, price, name);
            } else {
                mClickHandler.onProductItemClick(id);
            }
        }
    }

    public interface MenuListAdapterOnClickHandler {
        void onProductItemClick(long productId);
        void onToggleOrderItemClick(boolean added, long productId, double price, String name);
    }
}
