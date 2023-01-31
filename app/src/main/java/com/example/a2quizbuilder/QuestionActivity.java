package com.example.a2quizbuilder;

import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class QuestionActivity extends AppCompatActivity {

    //initialize class variables
    final int btnOne = 1, btnTwo = 2, btnThree = 3, btnFour = 4;
    int correct = 0;
    Intent intent;
    Bundle extras;

    Button optOneBtn, optTwoBtn, optThreeBtn, optFourBtn, nextBtn;

    ArrayList<String> questions = new ArrayList<>();
    ArrayList<String> answers = new ArrayList<>();
    ArrayList<String> options = new ArrayList<>();

    HashMap<String,String> map = new HashMap<String,String>();

    TextView questionTextView, qProgress, corNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        intent = getIntent();
        extras = intent.getExtras();

        //assign ids
        optOneBtn = findViewById(R.id.optionOneBtn);
        optTwoBtn = findViewById(R.id.optionTwoBtn);
        optThreeBtn = findViewById(R.id.optionThreeBtn);
        optFourBtn = findViewById(R.id.optionFourBtn);
        nextBtn = findViewById(R.id.nextBtn);
        questionTextView = findViewById(R.id.questionPromptTextView);
        qProgress = findViewById(R.id.qFractionTextView);
        corNum = findViewById(R.id.correctValueTextView);

        //set Listeners
        optOneBtn.setOnClickListener(onOptionClicked);
        optTwoBtn.setOnClickListener(onOptionClicked);
        optThreeBtn.setOnClickListener(onOptionClicked);
        optFourBtn.setOnClickListener(onOptionClicked);
        nextBtn.setOnClickListener(onOptionClicked);

        //setup arraylists and hashmaps
        runSetup();
        //fill header with proper data
        populateHeader();
        //display the question and options for user to answer
        displayQAndOpts();
        //change next question to finish if user is on the last question
        toggleFinishBtn();

    }

    public View.OnClickListener onOptionClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
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

    //function to determine if its the first question or not
    public void runSetup(){
        if(extras != null){
            notFirstRun();
        }
        else{
            firstRunSetup();
        }
    }//end runSetup

    //function to get data from bundle
    public void notFirstRun(){

        questions = (ArrayList<String>) intent.getSerializableExtra("Qs");
        answers = (ArrayList<String>) intent.getSerializableExtra("As");
        map = (HashMap<String, String>) intent.getSerializableExtra("map");
        correct = intent.getIntExtra("correct", 0);

    }//end of notFirstRun

    //function to get ArrayList and hashmap data, and shuffle the questions
    public void firstRunSetup(){
        populateQAndA();
        createHashMap();
        shuffleQuestions();
    }//end firstRunSetup

    //function to fill the questions and answers array list
    public void populateQAndA(){
        String str = null;
        BufferedReader br = null;
        try{

            InputStream is = getResources().openRawResource(R.raw.quizdata);
            br = new BufferedReader(new InputStreamReader(is));

            //read in file and split using delimiter
            while ((str = br.readLine()) != null){
                String[] QA = str.split("#");
                questions.add(QA[0]);
                answers.add(QA[1]);
            }
            is.close();
        }
        catch(IOException e) {
            e.printStackTrace();
            Log.e("QuestionActivity", "Error with opening input file");
        }
        catch(Exception e) {
            e.printStackTrace();
            Log.e("QuestionActivity", "Something went wrong");
        }
    }//end of populateQAndA

    //function to create hashmap between questions and answers
    public void createHashMap(){
        for(int i = 0; i < questions.size(); i++){
            map.put(questions.get(i), answers.get(i));
        }
    }//end of createHashMap

    //function to randomize the order of the questions
    public void shuffleQuestions(){
        long seed = System.nanoTime();
        Collections.shuffle(questions, new Random(seed));
    }//end shuffleQuestions

    //function to get the questions being currently displayed
    public String getActiveQuestion(){
        return questions.get(0);
    }

    //function to get the options for the possible answers to the displayed question
    public void getOptions(String q){

        //create randomizer
        Random r = new Random();
        int limit = answers.size();

        //create options
        String optOne = map.get(q);
        String optTwo = answers.get(r.nextInt(limit));
        String optThree = answers.get(r.nextInt(limit));
        String optFour = answers.get(r.nextInt(limit));

        //make sure none of the options are the same
        while(true) {
            if(optOne.equals(optTwo) || optOne.equals(optThree) || optOne.equals(optFour) ||
            optTwo.equals(optThree) || optTwo.equals(optFour) || optThree.equals(optFour)){
                optTwo = answers.get(r.nextInt(limit));
                optThree = answers.get(r.nextInt(limit));
                optFour = answers.get(r.nextInt(limit));
            }
            else{
                break;
            }

        }
        //add to options arraylist
        options.add(optOne);
        options.add(optTwo);
        options.add(optThree);
        options.add(optFour);

        }//end of getOptions

    //function to display the questions and options to the user
    public void displayQAndOpts(){

        //set question
        String q = getActiveQuestion();
        questionTextView.setText(q);

        //set options
        getOptions(q);
        shuffleOptions();
        optOneBtn.setText(options.get(0));
        optTwoBtn.setText(options.get(1));
        optThreeBtn.setText(options.get(2));
        optFourBtn.setText(options.get(3));

    }//end displayQAndOpts

    //function to randomize the order of the options, so that btn 1 is not always correct
    public void shuffleOptions(){
        long seed = System.nanoTime();
        Collections.shuffle(options, new Random(seed));
    }//end shuffleOptions

    //function to add correct numbers to the header
    public void populateHeader(){
        populateQuestionProgress();
        populateCorrectNumber();
    }//end of populateHeader

    //function to fill question fraction
    public void populateQuestionProgress(){
        String progress =" " + (11 - questions.size()) + " / "
                + answers.size();
        qProgress.setText(progress);
    }//end of populateQuestionProgress

    //function to display number of questions answered correctly so far
    public void populateCorrectNumber(){
        String cor = " " + correct;
        corNum.setText(cor);
    }//end of populateCorrectNumber

    //function to check if answer given was correct
    public void answerGiven(int btn){
        if(btn == getCorrectBtn()){
            correct++;
        }
        revealCorrectAnswer();
        revealNextBtn();

    }//end of answerGiven

    //function to get the button the correct answer is on
    public int getCorrectBtn(){
        String correct = map.get(getActiveQuestion());
        if(correct.equals(String.valueOf(optOneBtn.getText()))){
            return btnOne;
        }
        else if(correct.equals(String.valueOf(optTwoBtn.getText()))){
            return btnTwo;
        }
        else if(correct.equals(String.valueOf(optThreeBtn.getText()))){
            return btnThree;
        }
        else{
            return btnFour;
        }
    }//end of getCorrectBtn

    //function to change color of buttons and disable them once user has given answer
    public void revealCorrectAnswer(){
        String red = "red";
        String green = "green";
        switch(getCorrectBtn()){
            case btnOne:
                setColor(btnOne, green);
                setColor(btnTwo, red);
                setColor(btnThree, red);
                setColor(btnFour, red);
                break;
            case btnTwo:
                setColor(btnOne, red);
                setColor(btnTwo, green);
                setColor(btnThree, red);
                setColor(btnFour, red);
                break;
            case btnThree:
                setColor(btnOne, red);
                setColor(btnTwo, red);
                setColor(btnThree, green);
                setColor(btnFour, red);
                break;
            case btnFour:
                setColor(btnOne, red);
                setColor(btnTwo, red);
                setColor(btnThree, red);
                setColor(btnFour, green);
                break;
        }
        disableButtons();

    }//end of revealCorrectAnswer

    //function to change incorrect buttons to red and correct to green
    public void setColor(int btn, String color){
        switch(btn){
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
    }//end of setColor

    //function to disable option buttons
    public void disableButtons(){
        optOneBtn.setClickable(false);
        optTwoBtn.setClickable(false);
        optThreeBtn.setClickable(false);
        optFourBtn.setClickable(false);
    }//end of disableButtons

    //function to display the next question button
    public void revealNextBtn(){
        nextBtn.setVisibility(VISIBLE);
    }

    //function to take user to the next question
    public void nextQuestion(){

        //go to end screen if on last question
        if(questions.size() == 1){
            Intent i = new Intent(getApplicationContext(), EndActivity.class);
            Bundle finals = new Bundle();
            finals.putInt("correct", correct);
            finals.putInt("totalQs", answers.size());
            i.putExtras(finals);
            startActivity(i);
        }
        //if not last question, go to next question
        else {
            nextQPrep();
            Intent i = new Intent(getApplicationContext(), QuestionActivity.class);
            Bundle extras = new Bundle();
            extras.putInt("correct", correct);
            extras.putSerializable("Qs", questions);
            extras.putSerializable("As", answers);
            extras.putSerializable("map", map);
            i.putExtras(extras);
            startActivity(i);
        }
    }//end of nextQuestion

    //end of nextQuestion remove the the question that was just
    // answered from the questions array list
    public void nextQPrep(){

        questions.remove(0);
    }//end of nextQPrep

    //function to change text of next question button to finish
    public void toggleFinishBtn(){
        if(questions.size() == 1){
            String fin = "Finish";
            nextBtn.setText(fin);
        }
    }//end of toggleFinishBtn


}