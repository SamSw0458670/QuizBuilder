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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_quizzes);

        backBtn = findViewById(R.id.VQuizBackBtn);
        newQuizBtn = findViewById(R.id.VQuizNewQuizBtn);
        recyclerView = findViewById(R.id.VQuizRecyclerView);

        backBtn.setOnClickListener(onBackClicked);
        newQuizBtn.setOnClickListener(onNewClicked);

        db = new DBAdapter(this);
        displayQuizzes();
    }

    private View.OnClickListener onBackClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(ViewQuizzesActivity.this, MainActivity.class);

            startActivity(intent);
        }
    };

    private View.OnClickListener onNewClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(ViewQuizzesActivity.this, ModifyQuizActivity.class);
            Bundle mode = new Bundle();
            mode.putBoolean("editing", false);
            intent.putExtras(mode);
            startActivity(intent);
        }
    };

    //function to load the quizzes from the database into the array list
    private void loadQuizzes(){

        db.open();
        Cursor c = db.getAllQuizzes();
        if(c.moveToFirst()){
            do{
                Quiz quiz = new Quiz(c.getString(0),c.getString(1), c.getString(2));
                quizList.add(quiz);
            }while(c.moveToNext());
        }
        db.close();
    }

    //function to display the quizzes from the array list in the recycler view
    private void displayQuizzes(){

        loadQuizzes();
        recyclerAdapter = new QuizRVAdapter(this, quizList);

        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}