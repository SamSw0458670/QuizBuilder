package com.example.a2quizbuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class ViewQuizzesActivity extends AppCompatActivity {

    DBAdapter db;

    Button backBtn, newQuizBtn;

    List<Quiz> quizList = new ArrayList<>();

    private RecyclerView recyclerView;

    private RecyclerView.Adapter recyclerAdapter;

    private RecyclerView.LayoutManager layoutManager;

    Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_quizzes);

        backBtn = findViewById(R.id.VQuizBackBtn);
        newQuizBtn = findViewById(R.id.VQuizNewQuizBtn);
        recyclerView = findViewById(R.id.VQuizRecyclerView);

        backBtn.setOnClickListener(onBackClicked);
        newQuizBtn.setOnClickListener(onNewClicked);

        displayQuizzes();

    }

    public View.OnClickListener onBackClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ViewQuizzesActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };

    public View.OnClickListener onNewClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ViewQuizzesActivity.this, ModifyQuizActivity.class);
            startActivity(intent);
        }
    };

    public void loadQuizzes(){
        Cursor c = db.getAllQuizzes();
        if(c.moveToFirst()){
            do{
                Quiz quiz = new Quiz(c.getString(0),c.getString(1), c.getString(2));
                quizList.add(quiz);
            }while(c.moveToNext());
        }
    }

    public void displayQuizzes(){
        //TODO - Populate quiz list
        for(int i = 0; i < 5; i++){
            quizList.add(new Quiz("1", "Quiz", "10"));
        }
        recyclerAdapter = new QuizRVAdapter(this, quizList);

        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}