package com.kid.brain.view.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kid.brain.R;
import com.kid.brain.managers.listeners.IOnItemClickListener;
import com.kid.brain.models.Kid;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;

/**
 * Created by khiemnt on 4/17/17.
 */
public class SlideMenuAdapter extends RecyclerView.Adapter<SlideMenuAdapter.ViewHolder> {

    private List<Kid> mLevels;
    private Kid mLevel;
    private LayoutInflater inflater;
    private IOnItemClickListener iOnItemClickListener;
    private Context context;
    private int mRowIndex;

    public SlideMenuAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setData(List<Kid> levels) {
        this.mLevels = levels;
        this.notifyDataSetChanged();
    }

    public void addData(List<Kid> levels) {
        if (this.mLevels != null) {
            this.mLevels = levels;
            this.notifyDataSetChanged();
        } else
            setData(levels);
    }


    public void setOnItemClickListener(IOnItemClickListener iOnItemClickListener) {
        this.iOnItemClickListener = iOnItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_slide_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        mLevel = mLevels.get(position);
        if (mLevel == null) return;

        ImageLoader.getInstance().displayImage(mLevel.getPhoto(), holder.icon, this.getOptions());
//        holder.icon.setImageResource(R.drawable.icon_setting);
        holder.name.setText(mLevel.getFullName());

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRowIndex = position;
                notifyDataSetChanged();
                Integer pos = (Integer) holder.itemView.getTag();
                iOnItemClickListener.onItemClickListener(mLevels.get(pos));
            }
        });

//        if (mRowIndex == position) {
//            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.slideMenuSelected));
//            //holder.name.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
//        } else {
//            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.slideMenuUnSelected));
//            holder.name.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
//        }
    }


    @Override
    public int getItemCount() {
        return mLevels == null ? 0 : mLevels.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout relRoot;
        private ImageView icon;
        private TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            this.relRoot = itemView.findViewById(R.id.relRoot);
            this.icon = itemView.findViewById(R.id.icon);
            this.name = itemView.findViewById(R.id.name);
        }
    }

    private DisplayImageOptions getOptions() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.icon_item_user)
                .showImageForEmptyUri(R.drawable.icon_item_user)
                .showImageOnFail(R.drawable.icon_item_user)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
    }

}
