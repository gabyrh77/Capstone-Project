package com.nanodegree.gaby.bakerylovers.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nanodegree.gaby.bakerylovers.R;

public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.ViewHolder>{
    private String[] mCursor;
    final private Activity mContext;
    final private MenuListAdapterOnClickHandler mClickHandler;
    private String mokaTorteLink = "https://drive.google.com/folderview?id=0B0edS0opR3fGQmpJbms0a3lVakE&usp=sharing";

    public MenuListAdapter(Activity context, int choiceMode) {
        super();
        mContext = context;
       // mEmptyView = emptyView;
        mClickHandler = (MenuListAdapterOnClickHandler) mContext;
        mCursor = new String[]{"Moka Torte", "Moka Torte","Moka Torte","Moka Torte","Moka Torte","Moka Torte","Moka Torte","Moka Torte","Moka Torte"};
       // mICM = new ItemChoiceManager(this);
       // mICM.setChoiceMode(choiceMode);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_list, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.productName.setText(mCursor[position]);
        if (true){
            holder.currentOrderButton.setTag(false);
            holder.currentOrderButton.setImageResource(R.drawable.ic_add_shopping_cart);
        }
        //Glide.with(mContext).load(mokaTorteLink).into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return mCursor.length;
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
            //mCursor.moveToPosition(adapterPosition);
            // String id = mCursor.getString(mCursor.getColumnIndex(AlexandriaContract.BookEntry._ID));
            //TODO: get product id
            if (view instanceof ImageButton) {
                mClickHandler.onToggleOrderItemClick(toggleOrderItem((ImageButton)view), 1);
            } else {
                mClickHandler.onProductItemClick(1);
                // mICM.onClick(this);
            }
        }
    }

    private boolean toggleOrderItem(ImageButton button){
        boolean toggle = (boolean) button.getTag();
        toggle = !toggle;
        if (toggle) {
            button.setImageResource(R.drawable.ic_remove_shopping_cart);
        } else {
            button.setImageResource(R.drawable.ic_add_shopping_cart);
        }
        button.setTag(toggle);
        return toggle;
    }

    public interface MenuListAdapterOnClickHandler {
        void onProductItemClick(long productId);
        void onToggleOrderItemClick(boolean add, long productId);
    }
}
