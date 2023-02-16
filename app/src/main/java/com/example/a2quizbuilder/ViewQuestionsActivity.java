package com.example.a2quizbuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class ViewQuestionsActivity extends AppCompatActivity {

    DBAdapter db;
    Intent quizIntent;
    Bundle quizInfo;

    long quizId = 0;

    Button backBtn, newQuestionBtn, quizPropsBtn;

    List<Question> questionList = new ArrayList<>();
    private RecyclerView recyclerView;

    private RecyclerView.Adapter recyclerAdapter;

    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_questions);

        quizIntent = getIntent();
        quizInfo = quizIntent.getExtras();

        backBtn = findViewById(R.id.VQuestionBackBtn);
        newQuestionBtn = findViewById(R.id.VQuestionNewQuestionBtn);
        quizPropsBtn = findViewById(R.id.VQuestionsQuizPropertiesBtn);
        recyclerView = findViewById(R.id.VQuestionsRecyclerView);


        backBtn.setOnClickListener(onBackClicked);
        newQuestionBtn.setOnClickListener(onNewClicked);
        quizPropsBtn.setOnClickListener(onQuizPropsClicked);

        displayQuestions();

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
            Bundle newQuestion = new Bundle();
            newQuestion.putBoolean("editing", false);
            newQuestion.putString("quizId", String.valueOf(quizId));
            intent.putExtras(newQuestion);
            startActivity(intent);
        }
    };

    public View.OnClickListener onQuizPropsClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ViewQuestionsActivity.this, ModifyQuizActivity.class);
            intent.putExtras(quizInfo);
            startActivity(intent);
        }
    };

    public void fillQuizId(){
        quizId = Long.parseLong(quizInfo.getString("quizId"));
    }

    public void loadQuestions(){
        //TODO: get quiz id
        Cursor c = db.getAllQuestions(quizId);
        if(c.moveToFirst()){
            do{
                Question question = new Question(c.getString(0), c.getString(1), c.getString(2));
                questionList.add(question);
            }while(c.moveToNext());
        }
    }

    public void displayQuestions(){
        //TODO - Populate question list
        for(int i = 0; i < 5; i++){
            questionList.add(new Question("1","Question", "answer"));
        }
        recyclerAdapter = new QuestionRVAdapter(this, questionList, quizId);

        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}