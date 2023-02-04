package com.example.a2quizbuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class ViewQuestionsActivity extends AppCompatActivity {

    DBAdapter db;

    Button backBtn, newQuestionBtn, quizPropsBtn;

    List<Question> questionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_questions);

        backBtn.findViewById(R.id.VQuestionBackBtn);
        newQuestionBtn.findViewById(R.id.VQuestionNewQuestionBtn);
        quizPropsBtn.findViewById(R.id.VQuestionsQuizPropertiesBtn);

        backBtn.setOnClickListener(onBackClicked);
        newQuestionBtn.setOnClickListener(onNewClicked);
        quizPropsBtn.setOnClickListener(onQuizPropsClicked);

    }

    public View.OnClickListener onBackClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ViewQuestionsActivity.this, ViewQuizzesActivity.class);
            startActivity(intent);
        }
    };

    public View.OnClickListener onNewClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ViewQuestionsActivity.this, ModifyQuestionActivity.class);
            startActivity(intent);
        }
    };

    public View.OnClickListener onQuizPropsClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ViewQuestionsActivity.this, ModifyQuizActivity.class);
            startActivity(intent);
        }
    };

    public void loadQuestions(){
        //TODO: get quiz id
        long quizId = 0;
        Cursor c = db.getAllQuestions(quizId);
        if(c.moveToFirst()){
            do{
                Question question = new Question(c.getString(1), c.getString(2));
                questionList.add(question);
            }while(c.moveToNext());
        }
    }

    public void displayQuestions(){
        //TODO
    }
}