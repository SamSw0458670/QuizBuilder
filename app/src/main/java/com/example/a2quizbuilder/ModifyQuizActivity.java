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
            goToPreviousActivity();
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

    //function to get and set the quiz name and seconds values in the text fields
    private void populateInfo() {

        checkEditing();
        getQuizInfo();
        quizNameET.setText(quizName);
        numSecondsTV.setText(quizSeconds);
    }

    //function to get the quiz name and seconds
    private void getQuizInfo() {

        quizId = quizIntent.getStringExtra("quizId");

        //if editing, get quiz info from the database
        if (editing) {
            db.open();
            Cursor c = db.getSingleQuiz(Long.parseLong(quizId));
            if (c.moveToFirst()) {
                quizName = c.getString(0);
                quizSeconds = c.getString(1);
            }
            db.close();
        }
        //else, sue the default values
        else {
            quizId = defaultId;
            quizName = defaultQuizName;
            quizSeconds = defaultSeconds;
        }
    }

    //function to check if an existing quiz is being edited
    private void checkEditing() {

        editing = quizIntent.getBooleanExtra("editing", true);

        //if a new quiz is being made, hide the delete button
        if (!editing) {
            deleteBtn.setVisibility(View.INVISIBLE);
        }
    }

    //function to increase the seconds value by 1
    private void increaseSeconds() {

        int sec = Integer.parseInt(numSecondsTV.getText().toString());
        int maxSec = 99;
        //do not increase seconds past 99
        if (sec < maxSec) {
            sec++;
        } else {
            Toast.makeText(ModifyQuizActivity.this,
                    "Maximum time is 99 seconds", Toast.LENGTH_LONG).show();
        }
        numSecondsTV.setText(String.valueOf(sec));
    }

    //function to decrease the seconds value by 1
    private void decreaseSeconds() {

        int sec = Integer.parseInt(numSecondsTV.getText().toString());

        //do not decrease seconds below 1
        if (sec > 1) {
            sec--;
        } else {
            Toast.makeText(ModifyQuizActivity.this,
                    "Minimum time is 1 second", Toast.LENGTH_LONG).show();
        }
        numSecondsTV.setText(String.valueOf(sec));
    }

    //function to save the new information to the database
    private void saveChanges() {

        quizName = quizNameET.getText().toString();
        quizSeconds = numSecondsTV.getText().toString();

        //if editing an existing quiz, call the update function
        if (editing) {
            updateQuiz();
        }
        //if its a new quiz, call the create quiz function
        else {
            createQuiz();
        }
    }

    //function to update the existing quiz with new information in the database
    private void updateQuiz() {

        //validate the quiz name
        if (validateQuizName()) {
            db.open();
            //try to update the quiz
            if (db.updateQuiz(Long.parseLong(quizId), quizName, quizSeconds)) {
                Toast.makeText(ModifyQuizActivity.this,
                        "Quiz Updated", Toast.LENGTH_LONG).show();
            }
            //inform the user if it fails
            else {
                Toast.makeText(ModifyQuizActivity.this,
                        "Error updating quiz, please try again", Toast.LENGTH_LONG).show();
            }
            db.close();
            goToViewQuestions();
        }
    }

    //function to add the new quiz to the database
    private void createQuiz() {

        //validate the quiz name
        if (validateQuizName()) {

            //try to create the quiz
            try {
                db.open();
                long id = db.addNewQuiz(quizName, quizSeconds);
                db.close();
                Toast.makeText(ModifyQuizActivity.this,
                        "Quiz Added", Toast.LENGTH_LONG).show();
            }
            //inform the user if it fails
            catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ModifyQuizActivity.this,
                        "Error adding quiz, please try again", Toast.LENGTH_LONG).show();
            }
            goToViewQuizzes();
        }
    }

    //function to delete the quiz
    private void deleteQuiz() {

        db.open();
        //try to delete the quiz
        if (db.deleteQuiz(Long.parseLong(quizId))) {
            Toast.makeText(ModifyQuizActivity.this,
                    "Quiz Deleted", Toast.LENGTH_LONG).show();
        }
        //inform the user if it fails
        else {
            Toast.makeText(ModifyQuizActivity.this,
                    "Error deleting quiz, please try again", Toast.LENGTH_LONG).show();
        }
        db.close();
        goToViewQuizzes();
    }

    //function to determine which activity the user came from
    private void goToPreviousActivity() {

        //if editing, the suer came from the view questions activity
        if (editing) {
            goToViewQuestions();
        }
        //else, the came from the view quizzes activity
        else {
            goToViewQuizzes();
        }
    }

    //function to go to the view questions activity
    private void goToViewQuestions() {

        Intent intent = new Intent(ModifyQuizActivity.this, ViewQuestionsActivity.class);
        intent.putExtras(quizInfo);
        startActivity(intent);
    }

    //function to go to the view quizzes activity
    private void goToViewQuizzes() {

        Intent intent = new Intent(ModifyQuizActivity.this, ViewQuizzesActivity.class);
        startActivity(intent);
    }

    //function to ensure the quiz name is not empty
    private boolean validateQuizName() {

        //check if the quiz name is empty
        if (!quizName.isEmpty()) {
            return true;
        }
        //if it is, tell the user
        else {
            Toast.makeText(ModifyQuizActivity.this,
                    "There must be text in the name field", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}