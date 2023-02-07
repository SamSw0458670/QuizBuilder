package com.example.a2quizbuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

    private void populateInfo(){
        getQuestionInfo();
        questionET.setText(question);
        answerET.setText(answer);
    }

    private void getQuestionInfo() {
        quizId = questionIntent.getStringExtra("quizId");
        questionId = questionIntent.getStringExtra("id");
        if(questionId != null){
            question = questionIntent.getStringExtra("question");
            answer = questionIntent.getStringExtra("answer");
        }
        else{
            questionId = defaultId;
            question = defaultQuestion;
            answer = defaultAnswer;
        }
    }

    private void saveChanges(){
        question = questionET.getText().toString();
        answer = answerET.getText().toString();

        if(!Objects.equals(questionId, defaultId)){
            updateQuestion();
        }
        else {
            createQuestion();
        }
        goToViewQuestions();
    }

    private void updateQuestion(){
        if(db.updateQuestion(Long.parseLong(questionId), question, answer)){
            Toast.makeText(ModifyQuestionActivity.this,
                    "Question Updated",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(ModifyQuestionActivity.this,
                    "Something Went Wrong",Toast.LENGTH_LONG).show();
        }
    }

    private void createQuestion(){
        db.addNewQuestion(quizId, question, answer);
        Toast.makeText(ModifyQuestionActivity.this,
                "Question Created", Toast.LENGTH_LONG).show();
    }

    private void deleteQuestion(){
        if(db.deleteQuestion(Long.parseLong(questionId))){
            Toast.makeText(ModifyQuestionActivity.this,
                    "Question Deleted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ModifyQuestionActivity.this,
                    "Something Went Wrong", Toast.LENGTH_LONG).show();
        }
        goToViewQuestions();
    }

    private void goToViewQuestions(){
        Intent intent = new Intent(ModifyQuestionActivity.this, ViewQuestionsActivity.class);
        //intent.putExtras(questionInfo);
        startActivity(intent);
    }

}