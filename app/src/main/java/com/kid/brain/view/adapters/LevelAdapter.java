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
import com.kid.brain.managers.listeners.IOnItemClickListener;
import com.kid.brain.models.Level;

import java.util.List;


/**
 * Created by khiemnt on 4/17/17.
 */
public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.ViewHolder> {

    private List<Level> levels;
    private Level level;
    private LayoutInflater inflater;
    private IOnItemClickListener iOnItemClickListener;
    private Context context;

    public LevelAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setData(List<Level> levels) {
        this.levels = levels;
        this.notifyDataSetChanged();
    }

    public void addData(List<Level> levels) {
        if (this.levels != null) {
            this.levels = levels;
            this.notifyDataSetChanged();
        } else
            setData(levels);
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
        level = levels.get(position);
        if (level == null) return;
//        if (EStatus.disable.name().equalsIgnoreCase(level.getStatus())){
//            holder.itemView.setVisibility(View.GONE);
//        }

        holder.cateName.setText(level.getName());

        holder.itemView.setTag(level);
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

//        setCategoryIcon(holder.cateIcon, holder.cateName, level.getId());
        holder.cardView.setBackgroundResource(R.drawable.selector_card_view_bound);
//        setBackgroundColor(holder.cardView, position);


    }

    @Override
    public int getItemCount() {
        return levels == null ? 0 : levels.size();
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

//    private void setCategoryIcon(ImageView cateIcon, TextView cateName, int cateId){
//        switch (cateId){
//            case 1: // Điều hoà
//                cateIcon.setImageResource(R.mipmap.cate_air_condition);
//                cateName.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
//                break;
//            case 2: // Máy giặt
//                cateIcon.setImageResource(R.mipmap.cate_washing_machine);
//                cateName.setTextColor(ContextCompat.getColor(context, R.color.colorDark));
//                break;
//            case 3: // Tủ lạnh
//                cateIcon.setImageResource(R.mipmap.cate_fridge);
//                cateName.setTextColor(ContextCompat.getColor(context, R.color.colorOrange));
//                break;
//            case 4: // Bình nóng lạnh
//                cateIcon.setImageResource(R.mipmap.cate_water_heater);
//                cateName.setTextColor(ContextCompat.getColor(context, R.color.colorBlue));
//                break;
//            case 5: // Bếp từ
//                cateIcon.setImageResource(R.mipmap.cate_cooker);
//                cateName.setTextColor(ContextCompat.getColor(context, R.color.colorBlueSky));
//                break;
//            case 6: // Máy rửa bát
//                cateIcon.setImageResource(R.mipmap.cate_dishwasher);
//                cateName.setTextColor(ContextCompat.getColor(context, R.color.colorOrange));
//                break;
//            case 7: // Máy hút mùi
//                cateIcon.setImageResource(R.mipmap.cate_cooker_hood);
//                cateName.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
//                break;
//            case 8: // Lò vi sóng
//                cateIcon.setImageResource(R.mipmap.cate_microwave);
//                cateName.setTextColor(ContextCompat.getColor(context, R.color.colorYellow));
//                break;
//            default:
////                cateIcon.setImageResource(R.mipmap.selector_card_view_bound);
//                break;
//
//
//        }
//    }


}
