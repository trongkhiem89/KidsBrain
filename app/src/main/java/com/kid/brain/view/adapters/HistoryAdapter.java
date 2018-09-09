package com.kid.brain.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kid.brain.R;
import com.kid.brain.managers.listeners.IOnItemClickListener;
import com.kid.brain.models.History;
import com.kid.brain.view.dialog.DateTimeUtils;

import java.util.List;

/**
 * Created by khiemnt on 4/17/17.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<History> mHistories;
    private History mHistory;
    private LayoutInflater inflater;
    private IOnItemClickListener iOnItemClickListener;
    private Context context;

    public HistoryAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setData(List<History> histories) {
        this.mHistories = histories;
        this.notifyDataSetChanged();
    }

    public void addData(List<History> histories) {
        if (this.mHistories != null) {
            this.mHistories = histories;
            this.notifyDataSetChanged();
        } else
            setData(histories);
    }


    public void setOnItemClickListener(IOnItemClickListener iOnItemClickListener) {
        this.iOnItemClickListener = iOnItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        mHistory = mHistories.get(position);
        if (mHistory == null) return;

        holder.tvHistory.setText(DateTimeUtils.convertUTCToLocalDateTime(mHistory.getDate()));
        holder.itemView.setTag(mHistory);
        holder.itemView.setBackgroundResource(R.drawable.selector_white);
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
    }

    @Override
    public int getItemCount() {
        return mHistories == null ? 0 : mHistories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvHistory;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvHistory = itemView.findViewById(R.id.tvHistory);
        }
    }

}
