package com.example.a2quizbuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity {

    Button backToM;
    int numCorrect, numQuestions;

    TextView scoreValue, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        backToM = findViewById(R.id.backToMenuBtn);
        scoreValue = findViewById(R.id.scoreValueTextView);
        message = findViewById(R.id.messageTextView);

        backToM.setOnClickListener(onMenuClicked);

        getScore();
        setMessage();
        setScore();

    }

    public View.OnClickListener onMenuClicked = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
    };

    //function to get the number of correct answers and total questions from the bundle
    public void getScore(){
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        numCorrect = extras.getInt("correct");
        numQuestions = extras.getInt("totalQs");
    }//end getScore

    //function to determine and set which message top display based on the users score
    public void setMessage(){
         double percentage = numCorrect * 100 / numQuestions ;
         String msg;
         if(percentage >= 80){
             msg = "Great Job!";

         }
         else if(percentage >= 60 ){
             msg = "Good Job";
         }
         else{
             msg = "Try Again";

         }
        message.setText(msg);
    }//end of setMessage

    //function to populate the score onto the screen
    public void setScore(){

        String score = numCorrect + " / " + numQuestions;
        scoreValue.setText(score);
    }//end setScore
}