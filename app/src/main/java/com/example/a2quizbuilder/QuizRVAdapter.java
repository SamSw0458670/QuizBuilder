package com.example.a2quizbuilder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuizRVAdapter extends RecyclerView.Adapter<QuizRVAdapter.MyViewHolder> {

    List<Quiz> quizzes;

    Context context;

    public QuizRVAdapter(Context pC, List<Quiz> pQuizzes) {
        this.quizzes = pQuizzes;
        this.context = pC;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.quiz_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //set current quiz and text view
        holder.currentQuiz = quizzes.get(position);
        holder.quizNameTV.setText(holder.currentQuiz.getM_name());
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView quizNameTV;
        CardView rowCV;

        Quiz currentQuiz;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            quizNameTV = itemView.findViewById(R.id.quizRowQuizName);
            rowCV = itemView.findViewById(R.id.quizRowCardView);

            rowCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewQuestionsActivity.class);
                    Bundle quizInfo = new Bundle();
                    quizInfo.putString("quizId", currentQuiz.getID());
                    intent.putExtras(quizInfo);
                    context.startActivity(intent);
                }
            });
        }
    }
}
