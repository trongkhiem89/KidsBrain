package com.kid.brain.view.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kid.brain.R;
import com.kid.brain.managers.enums.EItem;
import com.kid.brain.managers.enums.EStatus;
import com.kid.brain.managers.listeners.IOnItemClickListener;
import com.kid.brain.models.Item;

import java.util.List;

/**
 * Created by khiemnt on 4/17/17.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> mItems;
    private Item mItem;
    private LayoutInflater inflater;
    private IOnItemClickListener iOnItemClickListener;
    private Context context;

    public ItemAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setData(List<Item> items) {
        this.mItems = items;
        this.notifyDataSetChanged();
    }

    public void addData(List<Item> items) {
        if (this.mItems != null) {
            this.mItems = items;
            this.notifyDataSetChanged();
        } else
            setData(items);
    }


    public void setOnItemClickListener(IOnItemClickListener iOnItemClickListener) {
        this.iOnItemClickListener = iOnItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mItem = mItems.get(position);
        if (mItem == null) return;
        if (EStatus.Disable.name().equalsIgnoreCase(mItem.getStatus())) {
            holder.itemView.setVisibility(View.GONE);
        }

        holder.cateName.setText(mItem.getName());

        holder.itemView.setTag(mItem);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iOnItemClickListener.onItemClickListener(v.getTag());
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                iOnItemClickListener.onItemLongClickListener(v.getTag());
                return false;
            }
        });

        setCategoryIcon(holder.cateIcon, mItem.getId());
        holder.cardView.setBackgroundResource(R.drawable.selector_card_view_bound);


    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cateName;
        private ImageView cateIcon;
        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.cateName = (TextView) itemView.findViewById(R.id.cateName);
            this.cateIcon = (ImageView) itemView.findViewById(R.id.cateIcon);
            this.cardView = (CardView) itemView.findViewById(R.id.card_view);
        }
    }

    private void setCategoryIcon(ImageView cateIcon, String itemId) {
        if (EItem.TUTORIAL.getItemId().equalsIgnoreCase(itemId)) {
            cateIcon.setImageResource(R.drawable.icon_item_tutorial);
        } else if (EItem.SEARCH.getItemId().equalsIgnoreCase(itemId)) {
            cateIcon.setImageResource(R.drawable.icon_item_search);
        } else if (EItem.PROFILE.getItemId().equalsIgnoreCase(itemId)) {
            cateIcon.setImageResource(R.drawable.icon_item_user);
        } else if (EItem.KID.getItemId().equalsIgnoreCase(itemId)) {
            cateIcon.setImageResource(R.drawable.icon_item_kid);
        }
    }


}
