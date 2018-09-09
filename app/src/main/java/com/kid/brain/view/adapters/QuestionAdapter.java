package com.kid.brain.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.kid.brain.R;
import com.kid.brain.managers.enums.EQuestion;
import com.kid.brain.managers.listeners.IOnItemClickListener;
import com.kid.brain.models.Question;

import java.util.List;

/**
 * Created by khiemnt on 4/17/17.
 */
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private List<Question> questions;
    private Question question;
    private LayoutInflater inflater;
    private IOnItemClickListener iOnItemClickListener;
    private Context context;
    private EQuestion eQuestion;

    public QuestionAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;

        // Default display question from check list
        this.eQuestion = EQuestion.CHECK_LIST;
    }

    public void setEQuestion(EQuestion eQuestion) {
        this.eQuestion = eQuestion;
    }

    public void setData(List<Question> questions) {
        this.questions = questions;
        this.notifyDataSetChanged();
    }

    public void addData(List<Question> questions) {
        if (this.questions != null) {
            this.questions = questions;
            this.notifyDataSetChanged();
        } else
            setData(questions);
    }


    public void setOnItemClickListener(IOnItemClickListener iOnItemClickListener) {
        this.iOnItemClickListener = iOnItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        question = questions.get(position);
        if (question == null) return;

//        holder.itemView.setTag(question);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                iOnItemClickListener.onItemClickListener(v.getTag());
//            }
//        });
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                iOnItemClickListener.onItemLongClickListener(v.getTag());
//                return false;
//            }
//        });

        holder.checkBox.setText(question.getName());
        holder.checkBox.setChecked(question.isChecked());
        holder.checkBox.setTag(position);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer pos = (Integer) holder.checkBox.getTag();
                question = questions.get(pos);
                if (question.isChecked()) {
                    question.setChecked(false);
                } else {
                    question.setChecked(true);
                }
                iOnItemClickListener.onItemClickListener(question);
            }
        });

        if (eQuestion.equals(EQuestion.HISTORY)) {
            holder.checkBox.setClickable(false);
//            holder.checkBox.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return questions == null ? 0 : questions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            this.checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

}
