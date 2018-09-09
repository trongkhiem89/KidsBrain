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
import com.kid.brain.models.Kid;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by khiemnt on 4/17/17.
 */
public class KidAdapter extends RecyclerView.Adapter<KidAdapter.ViewHolder> {

    private List<Kid> mAccounts;
    private Kid mAccount;
    private LayoutInflater inflater;
    private IOnItemClickListener iOnItemClickListener;
    private Context context;

    public KidAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setData(List<Kid> accounts) {
        this.mAccounts = accounts;
        this.notifyDataSetChanged();
    }

    public void addData(List<Kid> accounts) {
        if (this.mAccounts != null) {
            this.mAccounts = accounts;
            this.notifyDataSetChanged();
        } else
            setData(accounts);
    }

    public void setOnItemClickListener(IOnItemClickListener iOnItemClickListener) {
        this.iOnItemClickListener = iOnItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_kid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mAccount = mAccounts.get(position);
        if (mAccount == null) return;

        if (position == mAccounts.size() - 1) {
            holder.kidPhoto.setImageResource(R.drawable.icon_cate_add);
            holder.kidName.setText(context.getString(R.string.btn_add_kid));
        } else {
//            String strOld = mAccount.getAge(mAccount.getBirthDay(), context);
//            if (!TextUtils.isEmpty(strOld)) {
//                holder.kidOld.setText(strOld);
//            }
            holder.kidName.setText(mAccount.getFullNameOrEmail());
            holder.kidName.setText(mAccount.getFullNameOrEmail());
            ImageLoader.getInstance().displayImage(mAccount.getPhoto(), holder.kidPhoto, KidApplication.getInstance().getOptions());
        }

        holder.itemView.setTag(mAccount);
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

        holder.cardView.setBackgroundResource(R.drawable.selector_card_view_bound);
    }

    @Override
    public int getItemCount() {
        return mAccounts == null ? 0 : mAccounts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView kidName;
//        private TextView kidOld;
        private ImageView kidPhoto;
        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.kidPhoto = (ImageView) itemView.findViewById(R.id.kidPhoto);
//            this.kidOld = (TextView) itemView.findViewById(R.id.kidOld);
            this.kidName = (TextView) itemView.findViewById(R.id.kidName);
            this.cardView = (CardView) itemView.findViewById(R.id.card_view);
        }
    }


}
