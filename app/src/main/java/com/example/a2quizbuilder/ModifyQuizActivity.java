package com.example.a2quizbuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
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

    boolean editing = true;

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
            goToPreviousView();
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
        checkEditing();
    }

    private void getQuizInfo(){
        quizId = quizIntent.getStringExtra("quizId");
        if(quizId != null){
            db.open();
            Cursor c = db.getSingleQuiz(Long.parseLong(quizId));
            if(c.moveToFirst()){
                quizName = c.getString(0);
                quizSeconds = c.getString(1);
            }
            db.close();
        }
        else{
            quizId = defaultId;
            quizName = defaultQuizName;
            quizSeconds = defaultSeconds;
        }
    }

    private void checkEditing(){
        editing = quizIntent.getBooleanExtra("editing", true);
        if(!editing){
            deleteBtn.setVisibility(View.INVISIBLE);
        }
    }


    private void increaseSeconds(){
        int sec = Integer.parseInt(numSecondsTV.getText().toString());
        if(sec < 99){
            sec++;
        } else {
            Toast.makeText(ModifyQuizActivity.this,
                    "Maximum time is 99 seconds", Toast.LENGTH_LONG).show();
        }
        numSecondsTV.setText(String.valueOf(sec));
    }

    private void decreaseSeconds(){
        int sec = Integer.parseInt(numSecondsTV.getText().toString());
        if(sec > 1){
            sec--;
        } else {
            Toast.makeText(ModifyQuizActivity.this,
                    "Minimum time is 1 second", Toast.LENGTH_LONG).show();
        }
        numSecondsTV.setText(String.valueOf(sec));
    }

    private void saveChanges(){
        quizName = quizNameET.getText().toString();
        quizSeconds = numSecondsTV.getText().toString();

        if(editing){
            updateQuiz();
        }
        else{
            createQuiz();
        }
    }

    private void updateQuiz(){
        if(validateQuizName()) {
            db.open();
            if (db.updateQuiz(Long.parseLong(quizId), quizName, quizSeconds)) {
                Toast.makeText(ModifyQuizActivity.this,
                        "Quiz Updated", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ModifyQuizActivity.this,
                        "Error updating quiz, please try again", Toast.LENGTH_LONG).show();
            }
            db.close();
            goToViewQuestions();
        }
    }

    private void createQuiz(){
        if(validateQuizName()) {
            try {
                db.open();
                long id = db.addNewQuiz(quizName, quizSeconds);
                db.close();
                Toast.makeText(ModifyQuizActivity.this,
                        "Quiz Added", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ModifyQuizActivity.this,
                        "Error adding quiz, please try again", Toast.LENGTH_LONG).show();
            }
            ;
            goToViewQuizzes();
        }
    }

    private void deleteQuiz() {
        db.open();
        if (db.deleteQuiz(Long.parseLong(quizId))) {
            Toast.makeText(ModifyQuizActivity.this,
                    "Quiz Deleted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ModifyQuizActivity.this,
                    "Error deleting quiz, please try again", Toast.LENGTH_LONG).show();
        }
        db.close();
        goToViewQuizzes();
    }

    private void goToPreviousView(){
        if(editing){
            goToViewQuestions();
        }
        else{
            goToViewQuizzes();
        }
    }

    private void goToViewQuestions(){
        Intent intent = new Intent(ModifyQuizActivity.this, ViewQuestionsActivity.class);
        intent.putExtras(quizInfo);
        startActivity(intent);
    }

    private void goToViewQuizzes(){
        Intent intent = new Intent(ModifyQuizActivity.this, ViewQuizzesActivity.class);
        startActivity(intent);
    }

    private boolean validateQuizName(){
        if (!quizName.isEmpty()){
            return true;
        } else {
            Toast.makeText(ModifyQuizActivity.this,
                    "There must be text in the name field", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}