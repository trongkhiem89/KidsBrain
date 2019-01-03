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
import com.kid.brain.managers.application.KidApplication;
import com.kid.brain.managers.listeners.IOnItemClickListener;
import com.kid.brain.models.Category;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Random;


/**
 * Created by khiemnt on 4/17/17.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories;
    private Category category;
    private LayoutInflater inflater;
    private IOnItemClickListener iOnItemClickListener;
    private Context context;

    private int resIds[] = new int[]{R.drawable.gradient_1, R.drawable.gradient_2, R.drawable.gradient_3, R.drawable.gradient_4, R.drawable.gradient_5, R.drawable.gradient_1, R.drawable.gradient_2, R.drawable.gradient_3, R.drawable.gradient_4, R.drawable.gradient_5};

    public CategoryAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setData(List<Category> categories) {
        this.categories = categories;
        this.notifyDataSetChanged();
    }

    public void addData(List<Category> categories) {
        if (this.categories != null) {
            this.categories = categories;
            this.notifyDataSetChanged();
        } else
            setData(categories);
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
        category = categories.get(position);
        if (category == null) return;
//        if (EStatus.disable.name().equalsIgnoreCase(category.getStatus())){
//            holder.itemView.setVisibility(View.GONE);
//        }

        holder.cateName.setText(category.getName());

        holder.itemView.setTag(category);
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

//        setCategoryIcon(holder.cateIcon, category.getId());
        ImageLoader.getInstance().displayImage(category.getAvatar(), holder.cateIcon, KidApplication.getInstance().getOptions());

        holder.cardView.setBackgroundResource(R.drawable.selector_card_view_bound);
//        holder.cardView.setBackgroundResource(resIds[position]);

    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
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

    private void setCategoryIcon(ImageView cateIcon, int cateId){
        switch (cateId){
            case 1: // Vận động
                cateIcon.setImageResource(R.drawable.icon_van_dong);
                break;
            case 4: // Nhận thức
                cateIcon.setImageResource(R.drawable.icon_nhan_thuc);
                break;
            case 5: // Ngôn ngữ
                cateIcon.setImageResource(R.drawable.icon_ngon_ngu);
                break;
            case 7: // Cảm xúc / Tình cảm xã hội
                cateIcon.setImageResource(R.drawable.icon_cam_xuc);
                break;
            case 8: // Dấu hiệu nghi ngại
                cateIcon.setImageResource(R.drawable.icon_dau_hieu_nghi_ngai);
                break;
            default:
                cateIcon.setImageResource(R.drawable.icon_van_dong);
                break;
        }
    }


}
