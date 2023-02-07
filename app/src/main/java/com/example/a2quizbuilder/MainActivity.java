package com.example.a2quizbuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button startBtn, viewQuizzesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.startBtn);
        viewQuizzesBtn = findViewById(R.id.viewQuizzesBtn);

        startBtn.setOnClickListener(onStartClicked);
        viewQuizzesBtn.setOnClickListener(onViewQuizzesClicked);



    }

    public View.OnClickListener onStartClicked = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), QuestionActivity.class);
            startActivity(i); //Go to the first question of the quiz
        }
    };

    public View.OnClickListener onViewQuizzesClicked = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), ViewQuizzesActivity.class);
            startActivity(i); //Go to the first question of the quiz
        }
    };

}