package com.blackshadow.retailstoremanagementsystem;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public  class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.RecyclerViewHolder>{
    private Context mContext;
    private List<product> products;
    private OnItemClickListener mListener;

    public CartRecyclerAdapter(Context context, List<product> uploads) {
        mContext = context;
        products = uploads;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.product_cart_card, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        product currentProduct = products.get(position);
        holder.nameTextView.setText(currentProduct.getName());
        holder.descriptionTextView.setText(currentProduct.getPrice());
        holder.dateTextView.setText(getDateToday());
        final String link = currentProduct.getImageUrl().toString();
        Picasso.get()
                .load(link)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.placeholder)
                .fit()
                .centerCrop()
                .into(holder.productImageView);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public TextView nameTextView,descriptionTextView,dateTextView;
        public ImageView productImageView;
        public Button add_to_cart;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            nameTextView =itemView.findViewById ( R.id.nameTextViewcart );
            descriptionTextView = itemView.findViewById(R.id.descriptionTextViewcart);
            dateTextView = itemView.findViewById(R.id.dateTextViewcart);
            productImageView = itemView.findViewById(R.id.productImageViewcart);
            add_to_cart = itemView.findViewById(R.id.btn_remove_from_cart);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

            add_to_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos= getAdapterPosition();
                    mListener.onButtonClick(pos);
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem showItem = menu.add( Menu.NONE, 1, 1, "Show");
            MenuItem deleteItem = menu.add(Menu.NONE, 2, 2, "Delete");

            showItem.setOnMenuItemClickListener(this);
            deleteItem.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    switch (item.getItemId()) {
                        case 1:
                            mListener.onShowItemClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteItemClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onShowItemClick(int position);
        void onDeleteItemClick(int position);
        void onButtonClick(int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    private String getDateToday(){
        DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd");
        Date date=new Date();
        String today= dateFormat.format(date);
        return today;
    }
}
