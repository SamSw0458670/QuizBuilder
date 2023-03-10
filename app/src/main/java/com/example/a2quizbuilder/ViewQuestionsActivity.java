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

    long quizId;

    Button backBtn, newQuestionBtn, quizPropsBtn;

    List<Question> questionList = new ArrayList<>();
    private RecyclerView recyclerView;

    private RecyclerView.Adapter recyclerAdapter;

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

        db = new DBAdapter(this);
        displayQuestions();
    }

    private View.OnClickListener onBackClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(ViewQuestionsActivity.this, ViewQuizzesActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener onNewClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(ViewQuestionsActivity.this, ModifyQuestionActivity.class);
            quizInfo.putBoolean("editing", false);
            intent.putExtras(quizInfo);
            startActivity(intent);
        }
    };

    private View.OnClickListener onQuizPropsClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(ViewQuestionsActivity.this, ModifyQuizActivity.class);
            quizInfo.putBoolean("editing", true);
            intent.putExtras(quizInfo);
            startActivity(intent);
        }
    };

    //function to get the quiz id
    private void fillQuizId() {

        quizId = Long.parseLong(quizInfo.getString("quizId"));
    }

    //function to load the questions from database into the array list
    private void loadQuestions() {

        fillQuizId();
        db.open();
        Cursor c = db.getAllQuestions(quizId);
        if (c.moveToFirst()) {
            do {
                Question question = new Question(c.getString(0), c.getString(1), c.getString(2));
                questionList.add(question);
            } while (c.moveToNext());
        }
        db.close();
    }

    //function to display the question array list in the recycler view
    private void displayQuestions() {

        loadQuestions();
        recyclerAdapter = new QuestionRVAdapter(this, questionList, quizId);

        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}