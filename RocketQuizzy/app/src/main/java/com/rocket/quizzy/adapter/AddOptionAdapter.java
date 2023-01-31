package com.rocket.quizzy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rocket.quizzy.R;
import com.rocket.quizzy.model.AddOption;
import com.rocket.quizzy.service.DayNight;
import com.rocket.quizzy.view.AddQuizActivity;

import java.util.List;

public class AddOptionAdapter extends RecyclerView.Adapter<AddOptionAdapter.Holder> {
    List<AddOption> list;
    Context context;
    AddQuizActivity activity;

    public AddOptionAdapter(List<AddOption> list, Context context, AddQuizActivity activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AddOptionAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item_add_option, parent, false));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull AddOptionAdapter.Holder holder, @SuppressLint("RecyclerView") int position) {
        AddOption addOption = list.get(position);

        DayNight dayNight = new DayNight(context);
        dayNight.checkCardView(holder.cardView);
        dayNight.checkEditText(holder.tvOption);
        dayNight.checkContentView(holder.itemView);
        dayNight.checkCardView(holder.contentView,R.color.darkColor,R.color.light_sky);

        if (list.size() > 0) {
            holder.radioButton.setChecked(activity.selectedOption == position);
        }
        if (position == 0 || position == 1){
            holder.removeBtn.setVisibility(View.GONE);
        }else {
            holder.removeBtn.setVisibility(View.VISIBLE);
        }

        if (list.size() > 0) {
            holder.tvOption.setHint(context.getString(R.string.opt_no) + addOption.getOptionNo());
        }
        holder.removeBtn.setOnClickListener(v -> {
            if (position != 0){
                if (position != 1){
                    list.remove(position);
                    notifyDataSetChanged();
                }
            }
        });

        holder.tvOption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (list.size() > 0){
                    list.get(position).setOptionName(holder.tvOption.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (list.size() > 0) {
                if (isChecked) {
                    activity.selectOption(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder{

        EditText tvOption;
        RadioButton radioButton;
        ImageView removeBtn;
        CardView cardView,contentView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvOption = itemView.findViewById(R.id.edtv_quiz_title);
            radioButton = itemView.findViewById(R.id.radioBtn);
            removeBtn = itemView.findViewById(R.id.remove_btn);
            cardView = itemView.findViewById(R.id.cardView);
            contentView = itemView.findViewById(R.id.cardView_2);
        }
    }
}
