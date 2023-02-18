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

public class QuestionRVAdapter extends RecyclerView.Adapter<QuestionRVAdapter.MyViewHolder> {

    List<Question> questions;

    Context context;

    long quizId;
    int questionCount = 1;

    public QuestionRVAdapter(Context pC, List<Question> pQuestions, long pQuizId){
        this.questions = pQuestions;
        this.context = pC;
        this.quizId = pQuizId;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.question_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.currQuestion = questions.get(position);
        holder.questionTV.setText(holder.currQuestion.getQuestion());
        holder.questionNumTV.setText(String.valueOf(questionCount));
        questionCount++;
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView questionTV, questionNumTV;
        CardView questionCV;

        Question currQuestion;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTV = itemView.findViewById(R.id.questionRowQuestion);
            questionCV = itemView.findViewById(R.id.questionRowCardView);
            questionNumTV = itemView.findViewById(R.id.questionRowQuestionNum);

            questionCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ModifyQuestionActivity.class);
                    Bundle questionInfo = new Bundle();
                    questionInfo.putString("quizId", String.valueOf(quizId));
                    questionInfo.putString("id", currQuestion.getID());
                    questionInfo.putString("question", currQuestion.getQuestion());
                    questionInfo.putString("answer", currQuestion.getAnswer());
                    intent.putExtras(questionInfo);
                    context.startActivity(intent);
                }
            });


        }
    }
}