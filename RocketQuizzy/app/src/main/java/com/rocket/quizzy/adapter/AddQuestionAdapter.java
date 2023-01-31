package com.rocket.quizzy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rocket.quizzy.R;
import com.rocket.quizzy.model.Question;
import com.rocket.quizzy.service.DayNight;

import java.util.List;

public class AddQuestionAdapter extends RecyclerView.Adapter<AddQuestionAdapter.Holder> {

    List<Question> list;
    Context context;

    public AddQuestionAdapter(List<Question> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item_add_question, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        Question question = list.get(position);

        DayNight dayNight = new DayNight(context);
        dayNight.checkContentView(holder.itemView);
        dayNight.checkTextView(holder.tvQuestion,R.color.grey);
        dayNight.checkCardView(holder.cardView);

        if (question.getQuestionType().equals(Question.QUESTION_TEXT)){
            holder.ivType.setImageResource(R.drawable.text_type);
        }else if (question.getQuestionType().equals(Question.QUESTION_IMAGE)){
            holder.ivType.setImageResource(R.drawable.image_type);
        }else if (question.getQuestionType().equals(Question.QUESTION_AUDIO)){
            holder.ivType.setImageResource(R.drawable.audio_type);
        }

        holder.tvQuestion.setText(question.getQuestionText());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        ImageView ivType;
        TextView tvQuestion;
        CardView cardView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ivType = itemView.findViewById(R.id.iv_type);
            tvQuestion = itemView.findViewById(R.id.tv_question);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
