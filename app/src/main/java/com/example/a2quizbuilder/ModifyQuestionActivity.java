package com.example.a2quizbuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class ModifyQuestionActivity extends AppCompatActivity {

    Intent questionIntent;
    Bundle questionInfo;

    DBAdapter db;

    Button backBtn, deleteBtn, saveBtn, cancelBtn;

    EditText questionET, answerET;

    boolean editing = true;

    String question, answer, questionId, quizId;
    String defaultQuestion = "Enter Question", defaultAnswer = "Enter Answer", defaultId = "-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_question);

        questionIntent = getIntent();
        questionInfo = questionIntent.getExtras();
        db = new DBAdapter(this);

        backBtn = findViewById(R.id.EQuestionBackBtn);
        deleteBtn = findViewById(R.id.EQuestionDeleteBtn);
        saveBtn = findViewById(R.id.EQuestionSaveBtn);
        cancelBtn = findViewById(R.id.EQuestionCancelBtn);
        questionET = findViewById(R.id.EQuestionQuestionEditText);
        answerET = findViewById(R.id.EQuestionAnswerEditText);

        backBtn.setOnClickListener(onBackClicked);
        deleteBtn.setOnClickListener(onDeleteClicked);
        saveBtn.setOnClickListener(onSaveClicked);
        cancelBtn.setOnClickListener(onBackClicked);

        populateInfo();
    }

    public View.OnClickListener onBackClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goToViewQuestions();
        }
    };

    public View.OnClickListener onDeleteClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            deleteQuestion();
        }
    };

    public View.OnClickListener onSaveClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveChanges();
        }
    };

    //function to call functions to get and fill the question and answer edit texts
    private void populateInfo() {
        checkEditing();
        getQuestionInfo();
        questionET.setText(question);
        answerET.setText(answer);

    }

    //function to get the question information
    private void getQuestionInfo() {

        quizId = questionIntent.getStringExtra("quizId");

        //if updating a question, get info from the database
        if (editing) {
            questionId = questionIntent.getStringExtra("id");

            db.open();
            Cursor c = db.getSingleQuestion(Long.parseLong(questionId));
            if (c.moveToFirst()) {
                question = c.getString(0);
                answer = c.getString(1);
            }
            db.close();
        }
        //else, its a new question. Use the default information
        else {
            questionId = defaultId;
            question = defaultQuestion;
            answer = defaultAnswer;
        }
    }

    //function to check if the user is editing an
    // existing question or making a new one
    private void checkEditing() {

        editing = questionIntent.getBooleanExtra("editing", true);

        //if a new question is being made, hide the delete button
        if (!editing) {
            deleteBtn.setVisibility(View.INVISIBLE);
        }
    }

    //function to save the question to the database
    private void saveChanges() {

        question = questionET.getText().toString();
        answer = answerET.getText().toString();

        //if editing, call the update question function
        if (editing) {
            updateQuestion();
        }
        //else, call the create question function
        else {
            createQuestion();
        }
    }

    //function to update the existing question with the new information
    private void updateQuestion() {

        //validate the text fields
        if (validateInput()) {
            db.open();

            //try to update the question
            if (db.updateQuestion(Long.parseLong(questionId), question, answer)) {
                Toast.makeText(ModifyQuestionActivity.this,
                        "Question Updated", Toast.LENGTH_LONG).show();
            }
            //inform the user if it fails
            else {
                Toast.makeText(ModifyQuestionActivity.this,
                        "Something Went Wrong", Toast.LENGTH_LONG).show();
            }
            db.close();
            goToViewQuestions();
        }
    }

    //function to save the new question to the database
    private void createQuestion() {

        //validate the text fields
        if (validateInput()) {

            //try to create teh question
            try {
                db.open();
                long id = db.addNewQuestion(quizId, question, answer);
                db.close();
                Toast.makeText(ModifyQuestionActivity.this,
                        "Question Created", Toast.LENGTH_LONG).show();
            }
            //inform the user if it fails
            catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ModifyQuestionActivity.this,
                        "Error creating question, please try again", Toast.LENGTH_LONG).show();
            }
            goToViewQuestions();
        }
    }

    //function to delete the question from the database
    private void deleteQuestion() {

        db.open();

        //try to delete the question
        if (db.deleteQuestion(Long.parseLong(questionId))) {
            Toast.makeText(ModifyQuestionActivity.this,
                    "Question Deleted", Toast.LENGTH_LONG).show();
        }
        //inform the user if it fails
        else {
            Toast.makeText(ModifyQuestionActivity.this,
                    "Error deleting question, please try again", Toast.LENGTH_LONG).show();
        }
        db.close();
        goToViewQuestions();
    }

    //function to go to the view questions activity
    private void goToViewQuestions() {

        Intent intent = new Intent(ModifyQuestionActivity.this, ViewQuestionsActivity.class);
        intent.putExtras(questionInfo);
        startActivity(intent);
    }

    //function to check if question and answer fields have text in them
    private boolean validateInput() {

        //if they arent empty, give the result of the test for same answer function
        if (!question.isEmpty() && !answer.isEmpty()) {
            return !testForSameAnswer();
        }

        //tell the user why their input isn't valid
        Toast.makeText(ModifyQuestionActivity.this,
                "There must be text in both fields", Toast.LENGTH_LONG).show();
        return false;
    }

    //function to check if the given answer is the same as another answer in the same quiz
    private boolean testForSameAnswer() {

        db.open();
        Cursor c = db.getAllAnswers(Long.parseLong(quizId));
        if (c.moveToFirst()) {
            do {
                //if the answer is the same as another, tell the user
                if (Objects.equals(answer, c.getString(0))) {
                    Toast.makeText(ModifyQuestionActivity.this,
                            "Answer cannot be the same as another answer in this quiz",
                            Toast.LENGTH_LONG).show();
                    return true;
                }
            } while (c.moveToNext());
        }
        db.close();
        return false;
    }
}