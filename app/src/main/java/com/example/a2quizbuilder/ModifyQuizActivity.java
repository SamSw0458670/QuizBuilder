package com.example.a2quizbuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class ModifyQuizActivity extends AppCompatActivity {

    Intent quizIntent;
    Bundle quizInfo;

    DBAdapter db;
    Button backBtn, deleteBtn, plusBtn, minusBtn, saveBtn, cancelBtn;

    TextView numSecondsTV;
    EditText quizNameET;

    String quizId, quizName, quizSeconds;
    String defaultId = "-1", defaultQuizName = "Enter Quiz Name", defaultSeconds = "10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_quiz);

        quizIntent = getIntent();
        quizInfo = quizIntent.getExtras();
        db = new DBAdapter(this);

        backBtn = findViewById(R.id.EQuizBackBtn);
        deleteBtn = findViewById(R.id.EQuizDeleteBtn);
        plusBtn = findViewById(R.id.EQuizPlusBtn);
        minusBtn = findViewById(R.id.EQuizMinusBtn);
        saveBtn = findViewById(R.id.EQuizSaveBtn);
        cancelBtn = findViewById(R.id.EQuizCancelBtn);
        numSecondsTV = findViewById(R.id.EQuizSecondsTextView);
        quizNameET = findViewById(R.id.EQuizQuizNameEditText);

        backBtn.setOnClickListener(onBackClicked);
        deleteBtn.setOnClickListener(onDeleteClicked);
        plusBtn.setOnClickListener(onPlusClicked);
        minusBtn.setOnClickListener(onMinusClicked);
        saveBtn.setOnClickListener(onSaveClicked);
        cancelBtn.setOnClickListener(onBackClicked);

        populateInfo();
    }

    public View.OnClickListener onBackClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goToViewQuizzes();
        }
    };

    public View.OnClickListener onDeleteClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            deleteQuiz();
        }
    };

    public View.OnClickListener onPlusClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            increaseSeconds();
        }
    };

    public View.OnClickListener onMinusClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           decreaseSeconds();
        }
    };

    public View.OnClickListener onSaveClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveChanges();
        }
    };

    private void populateInfo(){
        getQuizInfo();
        quizNameET.setText(quizName);
        numSecondsTV.setText(quizSeconds);
    }

    private void getQuizInfo(){
        quizId = quizIntent.getStringExtra("id");
        if(quizId != null){
           quizName = quizIntent.getStringExtra("name");
           quizSeconds = quizIntent.getStringExtra("seconds");
        }
        else{
            quizId = defaultId;
            quizName = defaultQuizName;
            quizSeconds = defaultSeconds;
        }
    }


    private void increaseSeconds(){
        int sec = Integer.parseInt(numSecondsTV.getText().toString());
        sec++;
        numSecondsTV.setText(String.valueOf(sec));
    }

    private void decreaseSeconds(){
        int sec = Integer.parseInt(numSecondsTV.getText().toString());
        sec--;
        numSecondsTV.setText(String.valueOf(sec));
    }

    private void saveChanges(){
        quizName = quizNameET.getText().toString();
        quizSeconds = numSecondsTV.getText().toString();

        if(!Objects.equals(quizId, defaultId)){
            updateQuiz();
        }
        else{
            createQuiz();
        }
        goToViewQuizzes();
    }

    private void updateQuiz(){
        if(db.updateQuiz(Long.parseLong(quizId), quizName, quizSeconds)){
            Toast.makeText(ModifyQuizActivity.this,
                    "Quiz Updated",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(ModifyQuizActivity.this,
                    "Something Went Wrong",Toast.LENGTH_LONG).show();
        }
    }

    private void createQuiz(){
        db.addNewQuiz(quizName, quizSeconds);
        Toast.makeText(ModifyQuizActivity.this,
                "Question Added",Toast.LENGTH_LONG).show();
    }

    private void deleteQuiz() {
        if (db.deleteQuiz(Long.parseLong(quizId))) {
            Toast.makeText(ModifyQuizActivity.this,
                    "Quiz Deleted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ModifyQuizActivity.this,
                    "Something Went Wrong", Toast.LENGTH_LONG).show();
        }
        goToViewQuizzes();
    }

    private void goToViewQuizzes(){
        Intent intent = new Intent(ModifyQuizActivity.this, ViewQuestionsActivity.class);
        //intent.putExtras(quizInfo);
        startActivity(intent);
    }
}