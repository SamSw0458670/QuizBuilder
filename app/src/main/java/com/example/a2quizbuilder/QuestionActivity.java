package com.example.a2quizbuilder;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class QuestionActivity extends AppCompatActivity {

    //initialize class variables
    final int btnOne = 1, btnTwo = 2, btnThree = 3, btnFour = 4;
    int correct = 0, currentQuestion = 0, numQuestions;

    String quizId;
    Intent intent;
    Bundle quizInfo;

    Button optOneBtn, optTwoBtn, optThreeBtn, optFourBtn, nextBtn, backBtn;
    ArrayList<String> options = new ArrayList<>();
    ArrayList<Question> questions = new ArrayList<>();

    DBAdapter db;

    TextView questionTextView, qProgress, corNum, timerTV;

    CountDownTimer timer;
    boolean timerRunning;
    long timerLeft;
    long timerAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        intent = getIntent();
        quizInfo = intent.getExtras();

        optOneBtn = findViewById(R.id.optionOneBtn);
        optTwoBtn = findViewById(R.id.optionTwoBtn);
        optThreeBtn = findViewById(R.id.optionThreeBtn);
        optFourBtn = findViewById(R.id.optionFourBtn);
        nextBtn = findViewById(R.id.nextBtn);
        questionTextView = findViewById(R.id.questionPromptTextView);
        qProgress = findViewById(R.id.qFractionTextView);
        corNum = findViewById(R.id.correctValueTextView);
        backBtn = findViewById(R.id.questionBackBtn);
        timerTV = findViewById(R.id.timerTV);

        optOneBtn.setOnClickListener(onOptionClicked);
        optTwoBtn.setOnClickListener(onOptionClicked);
        optThreeBtn.setOnClickListener(onOptionClicked);
        optFourBtn.setOnClickListener(onOptionClicked);
        nextBtn.setOnClickListener(onOptionClicked);
        backBtn.setOnClickListener(onBackClicked);

        db = new DBAdapter(this);

        runSetup();
        populateHeader();
        displayQAndOpts();
        startTimer();
    }

    //listener for the four option buttons
    public View.OnClickListener onOptionClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.optionOneBtn:
                    answerGiven(btnOne);
                    break;
                case R.id.optionTwoBtn:
                    answerGiven(btnTwo);
                    break;
                case R.id.optionThreeBtn:
                    answerGiven(btnThree);
                    break;
                case R.id.optionFourBtn:
                    answerGiven(btnFour);
                    break;
                case R.id.nextBtn:
                    nextQuestion();
                    break;
            }
        }
    };

    public View.OnClickListener onBackClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(QuestionActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };

    //function to determine if its the first question or not
    public void runSetup() {

        quizId = intent.getStringExtra("quizId");
        currentQuestion = intent.getIntExtra("currQ", 0);

        if (currentQuestion > 0) {
            getInfo();
        } else {
            populateQuestions();
            populateTimer();
        }
        setNumQuestions();
    }

    //function to get data from the bundle from the previous question
    public void getInfo() {

        questions = intent.getParcelableArrayListExtra("QOs");
        correct = intent.getIntExtra("correct", 0);
        timerAmount = intent.getLongExtra("timerAmt", 10000);
    }

    //function to get ArrayList and hashmap data, and shuffle the questions
    public void populateQuestions() {

        populateQAndA();
        shuffleQuestions();
    }

    //function to fill the questions and answers array list
    public void populateQAndA() {

        db.open();
        Cursor c = db.getAllQuestions(Integer.parseInt(quizId));

        if (c.moveToFirst()) {
            do {
                Question question = new Question(c.getString(0), c.getString(1), c.getString(2));
                questions.add(question);
            } while (c.moveToNext());
        }
        db.close();
    }

    //function to set the number of question from the questions array list size
    public void setNumQuestions() {
        numQuestions = questions.size();
    }

    //function to get the timer amount from the database
    public void populateTimer(){
        db.open();
        Cursor c = db.getSingleQuiz(Integer.parseInt(quizId));

        if (c.moveToFirst()) {
            timerAmount = (long) c.getInt(1) * 1000;
        }
        db.close();
    }

    //function to randomize the order of the questions
    public void shuffleQuestions() {
        long seed = System.nanoTime();
        Collections.shuffle(questions, new Random(seed));
    }

    //function to get the options for the possible answers to the displayed question
    public void getOptionsO() {

        //create randomizer
        Random r = new Random();
        int limit = questions.size() - 1;

        //create options
        String optOne = questions.get(currentQuestion).getAnswer();
        String optTwo = questions.get(r.nextInt(limit)).getAnswer();
        String optThree = questions.get(r.nextInt(limit)).getAnswer();
        String optFour = questions.get(r.nextInt(limit)).getAnswer();

        //make sure option 2 is unique
        while (optOne.equals(optTwo)) {
            optTwo = questions.get(r.nextInt(limit)).getAnswer();
        }

        //make sure option 3 is unique
        while (optThree.equals(optOne) || optThree.equals(optTwo)) {
            optThree = questions.get(r.nextInt(limit)).getAnswer();
        }

        //make sure option 4 is unique
        while (optFour.equals(optOne) || optFour.equals(optTwo) || optFour.equals(optThree)) {
            optFour = questions.get(r.nextInt(limit)).getAnswer();
        }

        //add to options arraylist
        options.add(optOne);
        options.add(optTwo);
        options.add(optThree);
        options.add(optFour);
    }

    //function to display the questions and options to the user
    public void displayQAndOpts() {

        //set question
        String q = questions.get(currentQuestion).getQuestion();
        questionTextView.setText(q);

        //set options
        getOptionsO();
        shuffleOptions();
        optOneBtn.setText(options.get(0));
        optTwoBtn.setText(options.get(1));
        optThreeBtn.setText(options.get(2));
        optFourBtn.setText(options.get(3));

    }

    //function to randomize the order of the options, so that btn 1 is not always correct
    public void shuffleOptions() {

        long seed = System.nanoTime();
        Collections.shuffle(options, new Random(seed));
    }

    //function to add correct numbers to the header
    public void populateHeader() {

        populateQuestionProgress();
        populateCorrectNumber();
    }

    //function to fill question fraction
    public void populateQuestionProgress() {

        String progress = " " + (currentQuestion + 1) + " / "
                + numQuestions;
        qProgress.setText(progress);
    }

    //function to display number of questions answered correctly so far
    public void populateCorrectNumber() {

        String cor = " " + correct;
        corNum.setText(cor);
    }

    //function to check if answer given was correct
    public void answerGiven(int btn) {

        if (btn == getCorrectBtn()) {
            correct++;
        }
        stopTimer();
        revealCorrectAnswer();
        revealNextBtn();

    }

    //function to get the button the correct answer is on
    public int getCorrectBtn() {

        String correct = questions.get(currentQuestion).getAnswer();

        if (correct.equals(String.valueOf(optOneBtn.getText()))) {
            return btnOne;
        } else if (correct.equals(String.valueOf(optTwoBtn.getText()))) {
            return btnTwo;
        } else if (correct.equals(String.valueOf(optThreeBtn.getText()))) {
            return btnThree;
        } else {
            return btnFour;
        }
    }

    //function to change color of buttons and disable them once user has given answer
    public void revealCorrectAnswer() {

        String incorrect = "red";
        String correct = "green";

        switch (getCorrectBtn()) {
            case btnOne:
                setColor(btnOne, correct);
                setColor(btnTwo, incorrect);
                setColor(btnThree, incorrect);
                setColor(btnFour, incorrect);
                break;
            case btnTwo:
                setColor(btnOne, incorrect);
                setColor(btnTwo, correct);
                setColor(btnThree, incorrect);
                setColor(btnFour, incorrect);
                break;
            case btnThree:
                setColor(btnOne, incorrect);
                setColor(btnTwo, incorrect);
                setColor(btnThree, correct);
                setColor(btnFour, incorrect);
                break;
            case btnFour:
                setColor(btnOne, incorrect);
                setColor(btnTwo, incorrect);
                setColor(btnThree, incorrect);
                setColor(btnFour, correct);
                break;
        }

        disableButtons();
    }

    //function to change incorrect buttons to red and correct to green
    public void setColor(int btn, String color) {

        switch (btn) {
            case btnOne:
                optOneBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
                break;
            case btnTwo:
                optTwoBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
                break;
            case btnThree:
                optThreeBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
                break;
            case btnFour:
                optFourBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
                break;
        }
    }

    //function to disable option buttons
    public void disableButtons() {

        optOneBtn.setClickable(false);
        optTwoBtn.setClickable(false);
        optThreeBtn.setClickable(false);
        optFourBtn.setClickable(false);
    }

    //function to display the next question button
    public void revealNextBtn() {

        if ((currentQuestion + 1) == numQuestions) {
            String fin = "Finish";
            nextBtn.setText(fin);
        }
        nextBtn.setVisibility(VISIBLE);
    }

    //function to take user to the next question
    public void nextQuestion() {

        //go to end screen if on last question
        if ((currentQuestion + 1) == numQuestions) {
            Intent i = new Intent(getApplicationContext(), EndActivity.class);
            Bundle finals = new Bundle();
            finals.putInt("correct", correct);
            finals.putInt("totalQs", numQuestions);
            finals.putString("quizId", quizId);
            i.putExtras(finals);
            startActivity(i);
        }
        //if not last question, go to next question
        else {
            currentQuestion++;
            Intent i = new Intent(getApplicationContext(), QuestionActivity.class);
            Bundle extras = new Bundle();
            extras.putInt("correct", correct);
            extras.putInt("currQ", currentQuestion);
            extras.putString("quizId", quizId);
            extras.putParcelableArrayList("QOs", questions);
            extras.putLong("timerAmt", timerAmount);
            i.putExtras(extras);
            startActivity(i);
        }
    }

    //function to create and start a timer
    public void startTimer(){
        timer = new CountDownTimer(timerAmount, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerLeft = millisUntilFinished;
                updateTimerDisplay();
            }

            @Override
            public void onFinish() {
                timerRunning = false;

                //5 is not an existing button so it will always be incorrect
                answerGiven(5);
                timerTV.setText("0");
            }
        }.start();
        timerRunning = true;
    }

    //function to stop the timer
    private void stopTimer() {
        if(timerRunning){
            timer.cancel();
        }
        timerRunning = false;
        updateTimerDisplay();
    }

    //function to update the timer display in the UI
    private void updateTimerDisplay(){

        int seconds = (int) (timerLeft / 1000) + 1;
        timerTV.setText(String.valueOf(seconds));
    }
}