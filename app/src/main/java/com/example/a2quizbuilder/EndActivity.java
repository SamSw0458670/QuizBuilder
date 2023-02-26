package com.example.a2quizbuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity {

    Button MenuBtn, retryBtn;
    int numCorrect, numQuestions;

    TextView scoreValue, message;

    Bundle quizInfo;
    Intent quizIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        quizIntent = getIntent();
        quizInfo = quizIntent.getExtras();

        MenuBtn = findViewById(R.id.backToMenuBtn);
        retryBtn = findViewById(R.id.endRetryBtn);
        scoreValue = findViewById(R.id.scoreValueTextView);
        message = findViewById(R.id.messageTextView);

        MenuBtn.setOnClickListener(onMenuClicked);
        retryBtn.setOnClickListener(onRetryClicked);

        getScore();
        setMessage();
        setScore();

    }

    private final View.OnClickListener onMenuClicked = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
    };

    private final View.OnClickListener onRetryClicked = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            String quizId = quizInfo.getString("quizId");
            Intent i = new Intent(getApplicationContext(), QuestionActivity.class);
            Bundle id = new Bundle();
            id.putString("quizId", quizId);
            i.putExtras(id);
            startActivity(i);
        }
    };

    //function to get the number of correct answers and total questions from the bundle
    private void getScore() {

        numCorrect = quizInfo.getInt("correct");
        numQuestions = quizInfo.getInt("totalQs");
    }

    //function to determine and set which message top display based on the users score
    private void setMessage() {

        double percentage = numCorrect * 100 / numQuestions;
        String msg;
        if (percentage >= 80) {
            msg = "Great Job!";

        } else if (percentage >= 60) {
            msg = "Good Job";
        } else {
            msg = "Try Again";

        }
        message.setText(msg);
    }

    //function to populate the score onto the screen
    private void setScore() {

        String score = numCorrect + " / " + numQuestions;
        scoreValue.setText(score);
    }
}